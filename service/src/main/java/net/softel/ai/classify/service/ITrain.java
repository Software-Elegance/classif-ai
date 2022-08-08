package net.softel.ai.classify.service;

import ai.djl.training.TrainingResult;
import net.softel.ai.classify.train.Training;

import net.softel.ai.classify.dto.TrainingSuite;

public interface ITrain {
    // public void trainClassifier(TrainingSuite suite);
    // public void trainDetector(TrainingSuite suite);
    public void runTraining(Training train, TrainingSuite suite);
    }


