package net.softel.ai.classify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
// @Tag(name = "Train", description = "Slack request and callbacks")
public class PredictionController {

    @Autowired
    IPredict predictService;

    @PostMapping(path="/classes", produces = "application/json")
    public ResponseEntity<String> predictClasses(@RequestBody @Valid PredictSuite suite){ 
         //predictService.predictClass(suite);
         return new ResponseEntity<String>(predictService.predictClass(suite), HttpStatus.OK);
        }
    
    @PostMapping(path="/best", produces = "application/json")
    public ResponseEntity<String> predictBest(@RequestBody @Valid PredictSuite suite){ 
         //predictService.predictClass(suite);
         return new ResponseEntity<String>(predictService.predictBest(suite), HttpStatus.OK);
        }

    }