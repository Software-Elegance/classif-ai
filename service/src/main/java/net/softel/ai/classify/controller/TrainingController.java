package net.softel.ai.classify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Qualifier;

import lombok.extern.slf4j.Slf4j;
import net.softel.ai.classify.train.Training;

import net.softel.ai.classify.service.ITrain;
import net.softel.ai.classify.dto.TrainingSuite;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/train")
public class TrainingController {

    @Autowired
    ITrain trainService;

    @Autowired
    @Qualifier("imageClassifierTrainer")
    Training imageClassifierTrainer;

    @PostMapping(path="/image/classifier", produces = "application/json")
    public ResponseEntity<String> trainClassifierAsync(@RequestBody @Valid TrainingSuite suite){ 
         trainService.runTraining(imageClassifierTrainer, suite);
         return new ResponseEntity<String>("Training classifier in the background", HttpStatus.OK);
        }
    
    }