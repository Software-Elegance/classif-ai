package net.softel.ai.classify.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.openMocks;

public class SwaggerConfigTest {

    @InjectMocks
    SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp(){
        openMocks(this);
    }

    @Test
    @DisplayName("test swagger configuration class")
    void testSwaggerConfigurationClass(){
        OpenAPI openAPI = swaggerConfig.openApiInit();
        assertNotNull(openAPI);
    }
}
