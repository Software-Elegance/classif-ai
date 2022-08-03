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
    public void trainClassifier(TrainingSuite trainingSuite){
        log.info("\nStarting training..." + trainingSuite.getTitle() + "******************\n\n");
        trainClassifierModel(trainingSuite);
        log.info("\nFinished training..." + trainingSuite.getTitle() + "******************\n\n");
        }


    @Async
    public void trainDetector(TrainingSuite trainingSuite){
        log.info("\nStarting training..." + trainingSuite.getTitle() + "******************\n\n");
        trainDetectorModel(trainingSuite);
        log.info("\nFinished training..." + trainingSuite.getTitle() + "******************\n\n");
        }

    //CLASSIFIER
    private TrainingResult trainClassifierModel(TrainingSuite trainingSuite){

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

            try{
                // to get the synset or label names
                List<String> synset = dataset.getSynset();
                log.info("Synset: " + synset);
                }
            catch(TranslateException te) {
                log.info(te.getMessage());
                }
        

            return dataset;

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

            trainingSet = getDataset(Dataset.Usage.TRAIN, trainingSuite);
            validateSet = getDataset(Dataset.Usage.TEST, trainingSuite);

            DefaultTrainingConfig config = setupSsdTrainingConfig(trainingSuite);

            try (Trainer trainer = model.newTrainer(config)) {
                trainer.setMetrics(new Metrics());

                //Shape inputShape = new Shape(arguments.getBatchSize(), 3, 256, 256);
                //Shape inputShape = new Shape(1, 3, 256, 256);
                Shape inputShape = new Shape(trainingSuite.getBatchSize(), trainingSuite.getNumberOfChannels(), trainingSuite.getImageHeight(), trainingSuite.getImageWidth());          //ResNet shape

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
            
            final int outPutSize = trainingSuite.getClassification().split(",").length;

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