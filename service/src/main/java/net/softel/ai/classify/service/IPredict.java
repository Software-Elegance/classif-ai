package net.softel.ai.classify.service;

import ai.djl.training.TrainingResult;

import net.softel.ai.classify.dto.PredictSuite;
import ai.djl.modality.Classifications;

public interface IPredict {
    public String predictClass(PredictSuite suite);
    public String predictBest(PredictSuite suite);

    // public Classifications predictClassification(PredictSuite predictSuite);
    }


