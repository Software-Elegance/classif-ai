package net.softel.ai.classify.train;


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

import net.softel.ai.classify.util.TrainingUtils;

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

@Service("objectDetector")
public class ObjectDetectorTrainer implements Training {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Async
    public void train(TrainingSuite trainingSuite){
        log.info("\nStarting training..." + trainingSuite.getTitle() + "******************\n\n");
        trainDetectorModel(trainingSuite);
        log.info("\nFinished training..." + trainingSuite.getTitle() + "******************\n\n");
        }



//DETECTOR

    private TrainingResult trainDetectorModel(TrainingSuite trainingSuite) {

        log.info("Training using : {}", trainingSuite.toString());

        // Arguments arguments = new Arguments();
        String outputDir = trainingSuite.getModelOutputDir();

        RandomAccessDataset trainingSet = null;
        RandomAccessDataset validateSet = null;
        
        try (Model model = Model.newInstance(trainingSuite.getModelName())) {
            model.setBlock(getSsdTrainBlock(trainingSuite));

            trainingSet = TrainingUtils.getDataset(Dataset.Usage.TRAIN, trainingSuite);
            validateSet = TrainingUtils.getDataset(Dataset.Usage.TEST, trainingSuite);

            DefaultTrainingConfig config = setupSsdTrainingConfig(trainingSuite);

            try (Trainer trainer = model.newTrainer(config)) {
                trainer.setMetrics(new Metrics());

                //Shape inputShape = new Shape(arguments.getBatchSize(), 3, 256, 256);
                //Shape inputShape = new Shape(1, 3, 256, 256);
                Shape inputShape = new Shape(trainingSuite.getBatchSize(), 3, trainingSuite.getImageHeight(), trainingSuite.getImageWidth());          //ResNet shape

                trainer.initialize(inputShape);

                //EasyTrain.fit(trainer, arguments.getEpoch(), trainingSet, validateSet);
                EasyTrain.fit(trainer, 5, trainingSet, validateSet);
                return trainer.getTrainingResult();
                }
        
            }
        catch(IOException ioe) {
            log.info(ioe.getMessage());
            }
        catch(TranslateException te) {
            log.info(te.getMessage());
            }

        return null;

    }


    public static Block getSsdTrainBlock(TrainingSuite trainingSuite) {
            
            final int outPutSize = trainingSuite.getClasses().split(",").length;

            int[] numFilters = {16, 32, 64};        //need more clarification on filters...
            SequentialBlock baseBlock = new SequentialBlock();
            for (int numFilter : numFilters) {
                baseBlock.add(SingleShotDetection.getDownSamplingBlock(numFilter)); //Creates a Block that reduces the size of a convolutional block by half
            }

            List<List<Float>> sizes = new ArrayList<>();
            List<List<Float>> ratios = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ratios.add(Arrays.asList(1f, 2f, 0.5f));
            }
            sizes.add(Arrays.asList(0.2f, 0.272f));
            sizes.add(Arrays.asList(0.37f, 0.447f));
            sizes.add(Arrays.asList(0.54f, 0.619f));
            sizes.add(Arrays.asList(0.71f, 0.79f));
            sizes.add(Arrays.asList(0.88f, 0.961f));

            return SingleShotDetection.builder()
                    .setNumClasses(outPutSize)           //Sets the number of classes of objects to be detected
                    .setNumFeatures(3)          //Sets the number of down sampling blocks to be applied
                    .optGlobalPool(true)        //Sets the boolean whether to attach a global average pooling layer as the last output layer.
                    .setRatios(ratios)          //Sets the list of aspect ratios of generated anchor boxes.
                    .setSizes(sizes)            //Sets the list of sizes of generated anchor boxes.
                    .setBaseNetwork(baseBlock)
                    .build();
        }


    private static DefaultTrainingConfig setupSsdTrainingConfig(TrainingSuite trainingSuite) {
        // String outputDir = arguments.getOutputDir();
        String outputDir = trainingSuite.getModelOutputDir();
        SaveModelTrainingListener listener = new SaveModelTrainingListener(outputDir);
        listener.setSaveModelCallback(
                trainer -> {
                    TrainingResult result = trainer.getTrainingResult();
                    Model model = trainer.getModel();
                    float accuracy = result.getValidateEvaluation("classAccuracy");
                    model.setProperty("ClassAccuracy", String.format("%.5f", accuracy));
                    model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
                });

        return new DefaultTrainingConfig(new SingleShotDetectionLoss())
                .addEvaluator(new SingleShotDetectionAccuracy("classAccuracy"))
                .addEvaluator(new BoundingBoxError("boundingBoxError"))
                //.optDevices(Engine.getInstance().getDevices(arguments.getMaxGpus()))
                .addTrainingListeners(TrainingListener.Defaults.logging(outputDir))
                .addTrainingListeners(listener);
    }

}