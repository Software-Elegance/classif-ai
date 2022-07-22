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

import net.softel.ai.classify.service.ITrain;
import net.softel.ai.classify.dto.TrainingSuite;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/train")
// @Tag(name = "Train", description = "Slack request and callbacks")
public class TrainingController {

    @Autowired
    ITrain trainService;

    @PostMapping(path="/classifier", produces = "application/json")
    public ResponseEntity<String> trainClassifierAsync(@RequestBody @Valid TrainingSuite suite){ 
         trainService.trainClassifier(suite);
         return new ResponseEntity<String>("Training in the background", HttpStatus.OK);
        }
    
    }