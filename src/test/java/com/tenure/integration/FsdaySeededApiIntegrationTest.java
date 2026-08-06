package com.tenure.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.ootd.ai.AiTagResult;
import com.tenure.domain.ootd.ai.AiTagService;
import com.tenure.domain.ootd.ai.RegionAnalysisResult;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class FsdaySeededApiIntegrationTest {

    private static final String PASSWORD = "TenureTest!2026";
    private static final String BUYER_EMAIL = "fsday.buyer01@test.tenure";
    private static final String SELLER_EMAIL = "fsday.seller01@test.tenure";
    private static final long BUYER_ID = 900001L;
    private static final long SELLER_ID = 900101L;
    private static final long BUYER_ADDRESS_ID = 901001L;
    private static final long BUYER_TAG_ITEM_ID = 903001L;
    private static final long SELLER_OOTD_ID = 905001L;
    private static final long SALE_ITEM_ID = 902011L;
    private static final long SALE_PRODUCT_ID = 904011L;
    private static final long OFFER_ITEM_ID = 902013L;
    private static final long CHAT_ROOM_ID = 911001L;
    private static final long BUYER_NOTIFICATION_ID = 912012L;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("Tenure_DB")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void testProperties(DynamicPropertyRegistry registry) {
        registry.add("tenure.storage.local.base-dir", () -> System.getProperty("java.io.tmpdir") + "/tenure-test-uploads");
        registry.add("tenure.storage.local.base-url", () -> "/files");
        registry.add("tenure.ai.gemini.api-key", () -> "test-api-key");
    }

    @Test
    @DisplayName("FSDAY seed users can exercise OOTD, commerce, history, and exploration APIs")
    void fsdaySeededAccountsSupportDemoFlowsAndImplementedApis() throws Exception {
        Session buyer = login(BUYER_EMAIL);
        Session seller = login(SELLER_EMAIL);

        assertThat(buyer.userId()).isEqualTo(BUYER_ID);
        assertThat(seller.userId()).isEqualTo(SELLER_ID);

        assertSeedStaticResourceIsServed();
        assertBaseApiSurfaceIsUsable(buyer, seller);
        long createdOotdId = postOotdWithTaggedItemAndVerifyFeedAndMyPage(buyer);
        assertThat(createdOotdId).isPositive();

        completeDirectPurchaseFlowAndVerifyItemHistory(buyer, seller);
        createAndCancelPurchaseOfferForExploration(buyer);
    }

    private void assertSeedStaticResourceIsServed() throws Exception {
        mockMvc.perform(get("/files/seed/ootd-seller-08.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG));
    }

    private void assertBaseApiSurfaceIsUsable(Session buyer, Session seller) throws Exception {
        expectOkActions(get("/users/me").with(auth(buyer.token())));
        expectOkActions(get("/addresses").with(auth(buyer.token())));
        expectOkActions(get("/items").param("size", "10").with(auth(buyer.token())));
        expectOkActions(get("/items/{itemId}", BUYER_TAG_ITEM_ID).with(auth(buyer.token())));
        expectOkActions(get("/items/{itemId}", SALE_ITEM_ID).with(auth(buyer.token())))
                .andExpect(jsonPath("$.data.productId").value(SALE_PRODUCT_ID));
        expectOkActions(get("/products/{productId}", SALE_PRODUCT_ID).with(auth(buyer.token())));

        expectOkActions(get("/feed").param("size", "10").with(auth(buyer.token())));
        expectOkActions(get("/ootds/{ootdId}", SELLER_OOTD_ID).with(auth(buyer.token())))
                .andExpect(jsonPath("$.data.author.userId").value(SELLER_ID))
                .andExpect(jsonPath("$.data.tags[0].onSale").value(true));
        expectOkActions(get("/ootds/{ootdId}/related", SELLER_OOTD_ID).with(auth(buyer.token())));
        expectOkActions(get("/ootds/hearted").with(auth(buyer.token())));
        expectOkActions(get("/ootds/saved").with(auth(buyer.token())));

        expectOkActions(get("/search/recent").with(auth(buyer.token())));
        expectOkActions(get("/search/home").with(auth(buyer.token())));
        expectOkActions(get("/search/ootds").param("keyword", "denim").param("size", "5").with(auth(buyer.token())));
        expectOkActions(get("/search/users").param("keyword", "fs_seller").with(auth(buyer.token())));
        expectOkActions(post("/search/recent-ootds/{ootdId}", SELLER_OOTD_ID).with(auth(buyer.token())));
        expectOkActions(post("/search/recent-users/{userId}", SELLER_ID).with(auth(buyer.token())));

        expectOkActions(get("/wishes").with(auth(buyer.token())));
        expectOkActions(patch("/items/{itemId}/wish/notification", SALE_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("notificationEnabled", false)))
                .with(auth(buyer.token())));
        expectOkActions(get("/notifications").with(auth(buyer.token())));
        expectOkActions(post("/notifications/{notificationId}/read", BUYER_NOTIFICATION_ID).with(auth(buyer.token())));

        expectOkActions(get("/chats").with(auth(buyer.token())));
        expectOkActions(get("/chats/{chatRoomId}", CHAT_ROOM_ID).with(auth(buyer.token())));
        expectOkActions(get("/chats/{chatRoomId}/messages", CHAT_ROOM_ID).with(auth(buyer.token())));
        expectOkActions(post("/chats/{chatRoomId}/read", CHAT_ROOM_ID).with(auth(seller.token())));
    }

    private long postOotdWithTaggedItemAndVerifyFeedAndMyPage(Session buyer) throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "fsday-ootd.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                jpegBytes()
        );

        JsonNode createResponse = performOk(multipart("/ootds/auto-tag")
                .file(image)
                .param("source", "CAMERA")
                .with(auth(buyer.token())));
        long ootdId = createResponse.path("data").path("ootdId").asLong();

        String tagJson = json(Map.of(
                "itemId", BUYER_TAG_ITEM_ID,
                "bbox", Map.of(
                        "x", 0.12,
                        "y", 0.18,
                        "width", 0.35,
                        "height", 0.44
                ),
                "labelText", "FSDAY tagged tee",
                "status", "CONFIRMED"
        ));

        expectOkActions(post("/ootds/{ootdId}/tags", ootdId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tagJson)
                .with(auth(buyer.token())));

        expectOkActions(post("/ootds/{ootdId}/tags/confirm", ootdId).with(auth(buyer.token())))
                .andExpect(jsonPath("$.data.tagStatus").value("CONFIRMED"));

        JsonNode feed = performOk(get("/feed").param("size", "20").with(auth(buyer.token())));
        JsonNode myPosts = performOk(get("/ootds/me").param("size", "20").with(auth(buyer.token())));
        JsonNode myPage = performOk(get("/my-page").with(auth(buyer.token())));

        assertThat(containsLongField(feed, "ootdId", ootdId)).isTrue();
        assertThat(containsLongField(myPosts, "ootdId", ootdId)).isTrue();
        assertThat(myPage.path("data").path("feedCount").asLong()).isGreaterThanOrEqualTo(2);

        return ootdId;
    }

    private byte[] jpegBytes() throws Exception {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", output);
        return output.toByteArray();
    }

    private void completeDirectPurchaseFlowAndVerifyItemHistory(Session buyer, Session seller) throws Exception {
        expectOkActions(get("/ootds/{ootdId}", SELLER_OOTD_ID).with(auth(buyer.token())))
                .andExpect(jsonPath("$.data.tags[0].itemId").value(SALE_ITEM_ID))
                .andExpect(jsonPath("$.data.tags[0].onSale").value(true));

        expectOkActions(get("/items/{itemId}", SALE_ITEM_ID).with(auth(buyer.token())))
                .andExpect(jsonPath("$.data.productId").value(SALE_PRODUCT_ID))
                .andExpect(jsonPath("$.data.saleStatus").value("ON_SALE"));

        JsonNode intentResponse = performOk(post("/products/{productId}/purchase-intents", SALE_PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "deliveryAddressId", BUYER_ADDRESS_ID,
                        "paymentMethodId", "MOCK_CARD",
                        "agreement", true
                )))
                .with(auth(buyer.token())));
        long intentId = intentResponse.path("data").path("intentId").asLong();

        expectOkActions(get("/purchase-intents/sent").with(auth(buyer.token())));
        expectOkActions(get("/purchase-intents/received").with(auth(seller.token())));
        expectOkActions(get("/purchase-intents/{intentId}", intentId).with(auth(seller.token())));

        JsonNode acceptResponse = performOk(post("/purchase-intents/{intentId}/accept", intentId)
                .with(auth(seller.token())));
        long tradeId = acceptResponse.path("data").path("tradeId").asLong();
        assertThat(tradeId).isPositive();
        assertThat(acceptResponse.path("data").path("status").asText()).isEqualTo("PAID");

        expectOkActions(get("/trades/{tradeId}", tradeId).with(auth(buyer.token())))
                .andExpect(jsonPath("$.data.status").value("PAID"));

        expectOkActions(post("/trades/{tradeId}/status", tradeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "status", "SHIPPED",
                        "deliveryCarrier", "CJ_LOGISTICS",
                        "trackingNumber", "1234567890"
                )))
                .with(auth(seller.token())))
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));

        expectOkActions(post("/trades/{tradeId}/status", tradeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("status", "DELIVERED")))
                .with(auth(seller.token())))
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));

        expectOkActions(post("/trades/{tradeId}/status", tradeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("status", "PURCHASE_CONFIRMED")))
                .with(auth(buyer.token())))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        expectOkActions(get("/trades").param("role", "BUYER").param("status", "COMPLETED").with(auth(buyer.token())));
        JsonNode histories = performOk(get("/items/{itemId}/histories", SALE_ITEM_ID).with(auth(buyer.token())));

        assertThat(containsLongField(histories, "tradeId", tradeId)).isTrue();
        assertThat(containsLongField(histories, "ownerUserId", BUYER_ID)).isTrue();
        assertThat(containsTextValue(histories, "TENURE_TRADE")).isTrue();
    }

    private void createAndCancelPurchaseOfferForExploration(Session buyer) throws Exception {
        JsonNode offerResponse = performOk(post("/items/{itemId}/offers", OFFER_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "offerPrice", 25000,
                        "deliveryAddressId", BUYER_ADDRESS_ID,
                        "paymentMethodId", "MOCK_CARD",
                        "agreement", true
                )))
                .with(auth(buyer.token())));
        long offerId = offerResponse.path("data").path("offerId").asLong();

        expectOkActions(get("/purchase-offers/sent").with(auth(buyer.token())));
        expectOkActions(get("/purchase-offers/{offerId}", offerId).with(auth(buyer.token())));
        expectOkActions(post("/purchase-offers/{offerId}/cancel", offerId).with(auth(buyer.token())));
    }

    private Session login(String email) throws Exception {
        JsonNode response = performOk(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("email", email, "password", PASSWORD))));

        JsonNode data = response.path("data");
        return new Session(email, data.path("accessToken").asText(), data.path("userId").asLong());
    }

    private ResultActions expectOkActions(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder
    ) throws Exception {
        return mockMvc.perform(builder)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success").value(true));
    }

    private JsonNode performOk(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) throws Exception {
        String body = mockMvc.perform(builder)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(body);
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private RequestPostProcessor auth(String token) {
        return request -> {
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return request;
        };
    }

    private boolean containsLongField(JsonNode node, String fieldName, long expected) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return false;
        }
        if (node.isObject()) {
            JsonNode field = node.get(fieldName);
            if (field != null && field.canConvertToLong() && field.asLong() == expected) {
                return true;
            }
            for (JsonNode child : node) {
                if (containsLongField(child, fieldName, expected)) {
                    return true;
                }
            }
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                if (containsLongField(child, fieldName, expected)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsTextValue(JsonNode node, String expected) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return false;
        }
        if (node.isTextual()) {
            return expected.equals(node.asText());
        }
        for (JsonNode child : node) {
            if (containsTextValue(child, expected)) {
                return true;
            }
        }
        return false;
    }

    private record Session(String email, String token, long userId) {
    }

    @TestConfiguration
    static class NoopAiTagTestConfig {

        @Bean
        @Primary
        AiTagService aiTagService() {
            return new AiTagService() {
                @Override
                public List<AiTagResult> analyze(String imageUrl) {
                    return List.of();
                }

                @Override
                public RegionAnalysisResult analyzeRegion(
                        String imageUrl,
                        BigDecimal bboxX,
                        BigDecimal bboxY,
                        BigDecimal bboxWidth,
                        BigDecimal bboxHeight
                ) {
                    return RegionAnalysisResult.empty();
                }
            };
        }
    }
}
