package net.softel.ai.classify.service;

import ai.djl.training.TrainingResult;

import net.softel.ai.classify.dto.TrainingSuite;

public interface ITrain {
    public void trainClassifier(TrainingSuite suite);
    }


