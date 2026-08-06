package com.tenure.domain.ootd.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.repository.CategoryRepository;
import com.tenure.global.config.GeminiProperties;
import com.tenure.global.config.StorageProperties;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Google Gemini 이미지 분석을 통해 OOTD 착장 태그 후보를 생성한다.
 * 분석 실패 시 예외를 전파하지 않고 빈 목록을 반환한다 (비동기 리스너의 흐름을 막지 않기 위함).
 * Gemini는 bbox 좌표를 0~1 정규화 지시와 무관하게 0~1000 스케일로 반환하는 경향이 있어,
 * 응답을 그대로 신뢰하지 않고 1000으로 나눠 0~1 스케일로 변환한다.
 */
@Slf4j
@Service
public class GeminiAiTagService implements AiTagService {

    private static final BigDecimal BBOX_SCALE = BigDecimal.valueOf(1000);
    private static final int BBOX_DECIMAL_SCALE = 5;
    private static final int CATEGORY_LARGE_DEPTH = 1;

    private static final String PROMPT_TEMPLATE = """
            이미지 속 착장(의류/패션 아이템)을 분석해서 각 아이템의 위치, 라벨, 카테고리를 JSON 배열로만 응답하세요.
            다른 설명 없이 아래 형식의 JSON 배열만 출력하세요.
            bbox 좌표(bboxX, bboxY, bboxWidth, bboxHeight)는 이미지 전체 기준 0~1000 사이의 정수값입니다.
            confidence는 0~1 사이의 신뢰도입니다.
            categoryLarge/categorySmall은 반드시 아래 카테고리 목록 중에서만 골라서 채우세요.

            [카테고리 목록]
            %s

            [응답 형식]
            [
              {"labelText": "아이템명", "categoryLarge": "상위카테고리", "categorySmall": "세부카테고리", "bboxX": 0, "bboxY": 0, "bboxWidth": 0, "bboxHeight": 0, "confidence": 0.0}
            ]
            """;

    private final RestClient restClient;
    private final GeminiProperties geminiProperties;
    private final StorageProperties storageProperties;
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;

    public GeminiAiTagService(
            RestClient.Builder restClientBuilder,
            GeminiProperties geminiProperties,
            StorageProperties storageProperties,
            ObjectMapper objectMapper,
            CategoryRepository categoryRepository
    ) {
        this.restClient = restClientBuilder.baseUrl(geminiProperties.endpoint()).build();
        this.geminiProperties = geminiProperties;
        this.storageProperties = storageProperties;
        this.objectMapper = objectMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<AiTagResult> analyze(String imageUrl) {
        try {
            String base64Image = readImageAsBase64(imageUrl);
            String responseBody = requestGemini(base64Image);
            return parseTagResults(responseBody);
        } catch (Exception e) {
            log.error("Gemini AI 태그 분석 실패 - imageUrl={}", imageUrl, e);
            return List.of();
        }
    }

    private String requestGemini(String base64Image) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(
                                Map.of("text", buildPrompt()),
                                Map.of("inline_data", Map.of(
                                        "mime_type", "image/jpeg",
                                        "data", base64Image
                                ))
                        )
                ))
        );

        return restClient.post()
                .uri("/{model}:generateContent?key={apiKey}", geminiProperties.model(), geminiProperties.apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }

    private String readImageAsBase64(String imageUrl) throws IOException {
        String baseUrl = storageProperties.baseUrl();
        String relativePath = imageUrl.startsWith(baseUrl) ? imageUrl.substring(baseUrl.length()) : imageUrl;
        Path path = Path.of(storageProperties.baseDir(), relativePath);
        return Base64.getEncoder().encodeToString(Files.readAllBytes(path));
    }

    private List<AiTagResult> parseTagResults(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        String text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();
        String json = extractJsonArray(text);
        List<AiTagResult> rawResults = objectMapper.readValue(json, new TypeReference<>() {
        });
        return rawResults.stream().map(this::normalizeBbox).toList();
    }

    private AiTagResult normalizeBbox(AiTagResult result) {
        return new AiTagResult(
                result.labelText(),
                result.categoryLarge(),
                result.categorySmall(),
                toUnitScale(result.bboxX()),
                toUnitScale(result.bboxY()),
                toUnitScale(result.bboxWidth()),
                toUnitScale(result.bboxHeight()),
                result.confidence()
        );
    }

    private String buildPrompt() {
        return PROMPT_TEMPLATE.formatted(buildCategoryGuide());
    }

    private String buildCategoryGuide() {
        List<Category> categories = categoryRepository.findAllByIsActiveTrueOrderByDepthAscSortOrderAsc();
        Map<String, List<String>> smallByLarge = categories.stream()
                .filter(category -> category.getDepth() != CATEGORY_LARGE_DEPTH)
                .collect(Collectors.groupingBy(
                        category -> category.getParent().getName(),
                        LinkedHashMap::new,
                        Collectors.mapping(Category::getName, Collectors.toList())
                ));

        return smallByLarge.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + String.join(", ", entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private BigDecimal toUnitScale(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.divide(BBOX_SCALE, BBOX_DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    private String extractJsonArray(String text) {
        int start = text.indexOf('[');
        int end = text.lastIndexOf(']');
        if (start == -1 || end == -1 || end < start) {
            return "[]";
        }
        return text.substring(start, end + 1);
    }
}
