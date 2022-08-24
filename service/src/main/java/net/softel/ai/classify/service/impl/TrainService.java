package net.softel.ai.classify.service.impl;



import net.softel.ai.classify.train.Training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.util.List;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import net.softel.ai.classify.dto.TrainingSuite;

import ai.djl.training.listener.SaveModelTrainingListener;
import ai.djl.training.loss.SingleShotDetectionLoss;


import java.util.List;
import net.softel.ai.classify.service.ITrain;

@Service("trainService")
public class TrainService implements ITrain {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Async
    public void runTraining(Training training, TrainingSuite trainingSuite){
        log.info("\nDIP Starting training...{}******************\n\n",trainingSuite.getTitle());
        training.train(trainingSuite);
        log.info("\nDIP Finished training...{}******************\n\n",trainingSuite.getTitle());
        }

}