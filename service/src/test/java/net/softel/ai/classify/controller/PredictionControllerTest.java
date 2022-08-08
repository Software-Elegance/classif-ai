package net.softel.ai.classify.controller;

import net.softel.ai.classify.common.TestObjects;
import net.softel.ai.classify.dto.PredictSuite;
import net.softel.ai.classify.service.IPredict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class PredictionControllerTest {

    @InjectMocks
    PredictionController predictionController;

    @Mock
    IPredict predictService;

    @BeforeEach
    void setUp(){
        openMocks(this);
    }

    @Test
    @DisplayName("test predict classes controller")
    void testPredictClassesController(){
        PredictSuite suite = TestObjects.getPredictSuite();
        when(predictService.predictClass(any())).thenReturn("");
        ResponseEntity<String> test = predictionController.predictClasses(suite);
        assertTrue(test.hasBody());
        assertEquals(200,test.getStatusCode().value());
    }

    @Test
    @DisplayName("test predict best selection controller")
    void testPredictBestController(){
        PredictSuite suite = TestObjects.getPredictSuite();
        when(predictService.predictClass(any())).thenReturn("");
        ResponseEntity<String> test = predictionController.predictBest(suite);
        assertEquals(200,test.getStatusCode().value());
    }

    @Test
    @DisplayName("test predict classes on s3")
    void testPredictClassesOnS3Controller(){
        PredictSuite suite = TestObjects.getPredictSuite();
        when(predictService.predictClass(any())).thenReturn("");
        ResponseEntity<String> test = predictionController.predictClasses("/tmp");
        assertEquals(200,test.getStatusCode().value());
    }
}
