package net.softel.ai.classify.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import net.softel.ai.classify.service.IPredict;
import net.softel.ai.classify.dto.PredictSuite;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/predict")
@Tag(name = "Inference", description = "Inference controller")
public class PredictionController {

    @Autowired
    IPredict predictService;

    @PostMapping(path="/classes", produces = "application/json")
    public ResponseEntity<String> predictClasses(@RequestBody @Valid PredictSuite suite){
         return new ResponseEntity<>(predictService.predictClass(suite), HttpStatus.OK);
        }
    
    @PostMapping(path="/best", produces = "application/json")
    public ResponseEntity<String> predictBest(@RequestBody @Valid PredictSuite suite){
         return new ResponseEntity<>(predictService.predictBest(suite), HttpStatus.OK);
        }

    @GetMapping(path="/classes/s3", produces = "application/json")
    public ResponseEntity<String> predictClasses(@RequestParam("file") final String filePath ){ 

        PredictSuite suite = PredictSuite.builder()
            .imagePath(filePath)
            .imageSource("S3")
            .title("Testting at ...." + System.currentTimeMillis())
            .neuralNetwork("RESNET_50")
            .classification("buildings,forest,glacier,mountain,sea,street")
            .modelName("intelModel")
            .modelDirectory("/Users/zeguru/Downloads/MachineLearning/Models/intelModelDir")
            .batchSize(32)
            .numberOfChannels(3)
            .imageHeight(150)
            .imageWidth(150)
    
            .build();

        return new ResponseEntity<>(predictService.predictBest(suite), HttpStatus.OK);
        }
    }