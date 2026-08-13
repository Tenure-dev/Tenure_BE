package com.tenure.domain.ootd.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.item.repository.CategoryRepository;
import com.tenure.global.config.GeminiProperties;
import com.tenure.global.storage.ImageStorageService;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

class GeminiAiTagServiceTest {

    @Test
    void readImageBytes_fallsBackToClasspathStaticFileWhenStorageCannotReadSeedObject() throws IOException {
        ImageStorageService imageStorageService = mock(ImageStorageService.class);
        String objectKey = "seed/ian/ootds/ootd_001.jpg";
        when(imageStorageService.readBytes(objectKey)).thenThrow(new IOException("missing storage object"));

        GeminiAiTagService service = new GeminiAiTagService(
                RestClient.builder(),
                new GeminiProperties("test-key", "http://localhost", "test-model"),
                imageStorageService,
                new ObjectMapper(),
                mock(CategoryRepository.class)
        );

        byte[] imageBytes = ReflectionTestUtils.invokeMethod(
                service,
                "readImageBytes",
                "/files/seed/ian/ootds/ootd_001.jpg",
                objectKey
        );

        assertThat(imageBytes).isNotEmpty();
        assertThat(imageBytes[0] & 0xFF).isEqualTo(0xFF);
        assertThat(imageBytes[1] & 0xFF).isEqualTo(0xD8);
        assertThat(imageBytes[2] & 0xFF).isEqualTo(0xFF);
    }
}
