package net.softel.ai.classify.service;


import java.util.List;

import net.softel.ai.classify.train.Training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.Mnist;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.engine.Engine;
import ai.djl.Device;

// import net.softel.ai.classify.util.Arguments;
import ai.djl.metric.Metrics;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Block;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.SaveModelTrainingListener;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import java.io.IOException;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.Crop;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.RandomFlipTopBottom;
import ai.djl.modality.cv.transform.RandomFlipLeftRight;
import ai.djl.modality.cv.transform.RandomResizedCrop;
import ai.djl.modality.cv.transform.RandomBrightness;
import ai.djl.modality.cv.transform.RandomColorJitter;
import ai.djl.modality.cv.transform.RandomHue;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.ToTensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.djl.translate.Pipeline;
import ai.djl.repository.SimpleRepository;
import ai.djl.basicmodelzoo.cv.classification.ResNetV1;
import ai.djl.training.optimizer.Sgd;


import net.softel.ai.classify.dto.TrainingSuite;
import net.softel.ai.classify.enums.NNet;



// import ai.djl.MalformedModelException;
// import ai.djl.Model;
// import ai.djl.basicdataset.cv.PikachuDetection;
import ai.djl.basicmodelzoo.cv.object_detection.ssd.SingleShotDetection;
// import ai.djl.engine.Engine;
// import ai.djl.examples.training.util.Arguments;
// import ai.djl.inference.Predictor;
// import ai.djl.metric.Metrics;
// import ai.djl.modality.cv.Image;
// import ai.djl.modality.cv.ImageFactory;
// import ai.djl.modality.cv.MultiBoxDetection;
// import ai.djl.modality.cv.output.DetectedObjects;
// import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.SingleShotDetectionTranslator;
// import ai.djl.ndarray.NDArray;
// import ai.djl.ndarray.NDList;
// import ai.djl.ndarray.types.Shape;
// import ai.djl.nn.Block;
// import ai.djl.nn.LambdaBlock;
import ai.djl.nn.SequentialBlock;
// import ai.djl.training.DefaultTrainingConfig;
// import ai.djl.training.EasyTrain;
// import ai.djl.training.Trainer;
// import ai.djl.training.TrainingResult;
// import ai.djl.training.dataset.Dataset;
// import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.evaluator.BoundingBoxError;
import ai.djl.training.evaluator.SingleShotDetectionAccuracy;
import ai.djl.training.listener.SaveModelTrainingListener;
// import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.SingleShotDetectionLoss;
// import ai.djl.training.util.ProgressBar;
// import ai.djl.translate.Pipeline;
// import ai.djl.translate.TranslateException;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
import java.util.List;

@Service("trainService")
public class TrainService implements ITrain {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Async
    public void runTraining(Training training, TrainingSuite trainingSuite){
        log.info("\nDIP Starting training..." + trainingSuite.getTitle() + "******************\n\n");
        training.train(trainingSuite);
        log.info("\nDIP Finished training..." + trainingSuite.getTitle() + "******************\n\n");
        }

}