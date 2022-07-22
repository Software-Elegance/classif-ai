package net.softel.ai.classify.service;


import java.util.List;

// import com.github.dozermapper.core.Mapper;

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


@Service("trainService")
public class TrainService implements ITrain {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    // @Autowired
    // GISClient gisClient;

  
    // @Autowired
    // Mapper mapper;
    
    @Async
    public void trainClassifier(TrainingSuite trainingSuite){
        log.info("\nStarting training..." + trainingSuite.getTitle() + "******************\n\n");
        trainModel(trainingSuite);
        log.info("\nFinished training..." + trainingSuite.getTitle() + "******************\n\n");
        }

    private TrainingResult trainModel(TrainingSuite trainingSuite){

        log.info("Training using : {}", trainingSuite.toString());

        //final int outPutSize = trainingSuite.getClassification().size();
        final int outPutSize = trainingSuite.getClassification().split(",").length;

        log.info("Build the network block...");
        Block neuralNet;
        if(trainingSuite.getNeuralNetwork().equals("RESNET_50")){

            neuralNet = ResNetV1.builder()
                .setImageShape(new Shape(trainingSuite.getBatchSize(), trainingSuite.getNumberOfChannels(), trainingSuite.getImageHeight(), trainingSuite.getImageWidth()))
                .setNumLayers(50)       
                .setOutSize(outPutSize)
                .build();

            log.info("Preparing to start training...");

            try (Model model = Model.newInstance(trainingSuite.getModelName())) {
                model.setBlock(neuralNet);

                // String trainingImages = trainingSuite.getTrainingDataset() + "/training/";
                // String validationImages = trainingSuite.getTrainingDataset() + "/validation/";

                    RandomAccessDataset trainingSet = null;
                    RandomAccessDataset validateSet = null;
                try{
                    trainingSet = getDataset(Dataset.Usage.TRAIN, trainingSuite);
                    validateSet = getDataset(Dataset.Usage.TEST, trainingSuite);

                    DefaultTrainingConfig config = setupTrainingConfig(trainingSuite);

                    try (Trainer trainer = model.newTrainer(config)) {
                        log.info("Changing the world...");
                        trainer.setMetrics(new Metrics());
                        Shape inputShape = new Shape(trainingSuite.getBatchSize(), trainingSuite.getNumberOfChannels(), trainingSuite.getImageHeight(), trainingSuite.getImageWidth());          //ResNet shape
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
        log.info("GPU count: {}", trainingSuite.getMaxGpus());
        log.info("Devices available: {}", trainingSuite.getDeviceCount());
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
                // .optDevices(Device.gpu())
                .addTrainingListeners(TrainingListener.Defaults.logging(trainingSuite.getModelOutputDir()))
                .addTrainingListeners(listener);
        }

    private RandomAccessDataset getDataset(Dataset.Usage usage,TrainingSuite trainingSuite) throws IOException {

        log.info("Getting training dataset");
        Random rand = new Random();
        float brightness = rand.nextFloat();
        float contrast = rand.nextFloat();
        float saturation= rand.nextFloat();
        float hue = rand.nextFloat();

        String datasetRoot = "";
        if(usage.equals(Dataset.Usage.TRAIN)){
            datasetRoot = trainingSuite.getTrainingDataset() + "/training/";
            }
        else{
            datasetRoot = trainingSuite.getTrainingDataset() + "/validation/";
            }

        log.info("loading dataset {}", datasetRoot);


        ImageFolder dataset = ImageFolder.builder()
            .setRepositoryPath(Paths.get(datasetRoot)) // set root of image folder
            .setSampling(trainingSuite.getBatchSize(), true) // random sampling; don't process the data in order
            .optMaxDepth(2) // set the max depth of the sub folders
            .addTransform(new RandomColorJitter(brightness, contrast, saturation, hue))
            .addTransform(new RandomFlipLeftRight())
            .addTransform(new RandomFlipTopBottom())            // .addTransform(new ToTensor())       //Convert to  an N-Dimensional Array ? (tensor ?)
            .optPipeline(
                new Pipeline()
                    .add(new Resize(trainingSuite.getImageWidth(), trainingSuite.getImageHeight()))
                    .add(new ToTensor())
                )
            .build();

            return dataset;

            }

}