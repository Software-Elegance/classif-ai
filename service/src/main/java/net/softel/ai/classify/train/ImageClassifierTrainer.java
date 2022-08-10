package net.softel.ai.classify.train;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import net.softel.ai.classify.train.Training;

import net.softel.ai.classify.util.TrainingUtils;

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

import ai.djl.basicmodelzoo.cv.object_detection.ssd.SingleShotDetection;

import ai.djl.modality.cv.translator.SingleShotDetectionTranslator;

import ai.djl.nn.SequentialBlock;
import ai.djl.training.evaluator.BoundingBoxError;
import ai.djl.training.evaluator.SingleShotDetectionAccuracy;
import ai.djl.training.listener.SaveModelTrainingListener;
import ai.djl.training.loss.SingleShotDetectionLoss;

import java.util.ArrayList;
import java.util.List;

@Service("imageClassifierTrainer")
public class ImageClassifierTrainer implements Training {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Async
    public void train(TrainingSuite trainingSuite){
        log.info("\nStarting training..." + trainingSuite.getTitle() + "******************\n\n");
        trainClassifierModel(trainingSuite);
        log.info("\nFinished training..." + trainingSuite.getTitle() + "******************\n\n");
        }


    private TrainingResult trainClassifierModel(TrainingSuite trainingSuite){

        log.info("Training using : {}", trainingSuite.toString());

        final int outPutSize = trainingSuite.getClasses().split(",").length;

        log.info("Build the network block...");
        Block neuralNet;
        if(trainingSuite.getNeuralNetwork().equals("RESNET_50")){

            neuralNet = ResNetV1.builder()
                .setImageShape(new Shape(trainingSuite.getBatchSize(), 3, trainingSuite.getImageHeight(), trainingSuite.getImageWidth()))
                .setNumLayers(50)       
                .setOutSize(outPutSize)
                .build();

            log.info("Preparing to start training...");

            try (Model model = Model.newInstance(trainingSuite.getModelName())) {
                model.setBlock(neuralNet);

                RandomAccessDataset trainingSet = null;
                RandomAccessDataset validateSet = null;

                try{
                    trainingSet = TrainingUtils.getDataset(Dataset.Usage.TRAIN, trainingSuite);
                    validateSet = TrainingUtils.getDataset(Dataset.Usage.TEST, trainingSuite);

                    DefaultTrainingConfig config = setupTrainingConfig(trainingSuite);

                    try (Trainer trainer = model.newTrainer(config)) {
                        log.info("Changing the world...");
                        trainer.setMetrics(new Metrics());
                        Shape inputShape = new Shape(trainingSuite.getBatchSize(), 3, trainingSuite.getImageHeight(), trainingSuite.getImageWidth());          //ResNet shape
                        trainer.initialize(inputShape);
                        EasyTrain.fit(trainer, trainingSuite.getEpochs(), trainingSet, validateSet);
                        return trainer.getTrainingResult();
                        }
                        
                    }
                 catch(IOException ioe) {
                    log.info(ioe.getMessage());
                    }
                catch(TranslateException te) {
                    log.info(te.getMessage());
                    }
          
                }
            }

            return null;
        
        }


    private DefaultTrainingConfig setupTrainingConfig(TrainingSuite trainingSuite) {

        log.info("Setting up training config");
        log.info("Devices available: {}", trainingSuite.getDeviceCount());
        log.info("GPU count: {}", trainingSuite.getMaxGpus());
        SaveModelTrainingListener listener = new SaveModelTrainingListener(trainingSuite.getModelOutputDir());
        listener.setSaveModelCallback(
                trainer -> {
                    log.info("in callback ....");
                    TrainingResult result = trainer.getTrainingResult();
                    Model model = trainer.getModel();
                    float accuracy = result.getValidateEvaluation("Accuracy");
                    model.setProperty("Accuracy", String.format("%.5f", accuracy));
                    model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
                });
        return new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                .addEvaluator(new Accuracy())
                //.optDevices(Engine.getInstance().getDevices(trainingSuite.getMaxGpus()))
                .addTrainingListeners(TrainingListener.Defaults.logging(trainingSuite.getModelOutputDir()))
                .addTrainingListeners(listener);
        }


    


}