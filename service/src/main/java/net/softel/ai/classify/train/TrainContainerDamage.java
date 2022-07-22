package net.softel.ai.classify.train;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.Mnist;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.engine.Engine;
import net.softel.ai.classify.util.Arguments;
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
import ai.djl.modality.cv.transform.ToTensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.djl.translate.Pipeline;
import ai.djl.repository.SimpleRepository;
import ai.djl.basicmodelzoo.cv.classification.ResNetV1;

/**
 * An example of training an image classification (MNIST) model.
 *
 * <p>See this <a
 * href="https://github.com/deepjavalibrary/djl/blob/master/examples/docs/train_mnist_mlp.md">doc</a>
 * for information about this example.
 */
public final class TrainContainerDamage {


private static final Logger logger = LoggerFactory.getLogger(TrainContainerDamage.class);

    //the number of classification labels:  hole, normal, open
    private static final int NUM_OF_OUTPUT = 2;

    //the height and width for pre-processing of the image
    private static final int NEW_HEIGHT = 256;
    private static final int NEW_WIDTH = 256;

    //represents number of training samples processed before the model is updated
    private static final int BATCH_SIZE = 32;   //was 32

    private static final int NUM_OF_CHANNELS = 3;   //


    //the number of passes over the complete dataset
    private static final int EPOCHS = 20;

    private TrainContainerDamage() {}

    // public static void main(String[] args) throws IOException, TranslateException {
    //     TrainContainerDamage.runExample(args);
    // }

    public static TrainingResult runExample(String[] args) throws IOException, TranslateException {

        Arguments arguments = new Arguments();
        String outputDir = "build/models/mlpContainerModel";
        // arguments.initialize("build/imageModel");
        
        // Arguments arguments = new Arguments().parseArgs(args);
        // if (arguments == null) {
        //     return null;
        // }

        /**
            Preparation ? 
            -  resize all photos to 256 by 256
            -  convert to gray scale
            */

        // MLP neural network
        int neuronsInInputLayer = NUM_OF_CHANNELS * NEW_HEIGHT * NEW_WIDTH;
        int neuronsInOutputLayer = NUM_OF_OUTPUT;       //number of classes
        int neuronsInHiddenLayer =  (int)Math.sqrt(neuronsInInputLayer * neuronsInOutputLayer) + 1;

        System.out.println("Constructing neural network...");
        Block mlpNet =
                new Mlp(
                        neuronsInInputLayer,      //input layer
                        neuronsInOutputLayer,              //output classes (normal vs damaged)
                        // new int[] {NEW_HEIGHT + 1});       //size of hidden layer
                        new int[] {neuronsInHiddenLayer});       //Size of hidden layer A number between input and output ? 2× N +1, where N is number of dataset features


        Block resNet50 =
                //construct the network
                ResNetV1.builder()
                        .setImageShape(new Shape(BATCH_SIZE, NUM_OF_CHANNELS, NEW_HEIGHT, NEW_WIDTH))
                        .setNumLayers(50)       //default was 50 !
                        .setOutSize(NUM_OF_OUTPUT)
                        .build();

        // a 4D Tensor in NCHW order - <batch, channels, width, height>
        //From NHWC to NCHW ???
 
        // N: number of images in the batch
        // H: height of the image
        // W: width of the image
        // C: number of channels of the image (ex: 3 for RGB, 1 for grayscale...)

        String modelName = "containers";
        try (Model model = Model.newInstance(modelName)) {
            model.setBlock(mlpNet);

            // get training and validation dataset
            String trainingImages = "/Users/zeguru/Pictures/Containers/training/";
            String validationImages = "/Users/zeguru/Pictures/Containers/validation/";

            RandomAccessDataset trainingSet = getDataset(Dataset.Usage.TRAIN, trainingImages);
            RandomAccessDataset validateSet = getDataset(Dataset.Usage.TEST, validationImages);
    
            // logger.info("Training Set: size:{} , availableSize:{}, array : {}", trainingSet.size(), trainingSet.toArray());
// ImageFolder dataset = ImageFolder.builder().setRepository(repository).setSampling(1, false).build();
//     RandomAccessDataset[] sets = dataset.randomSplit(75, 25);

            // setup training configuration
            DefaultTrainingConfig config = setupTrainingConfig(arguments, outputDir);

            try (Trainer trainer = model.newTrainer(config)) {
                trainer.setMetrics(new Metrics());

                /*
                 * MNIST is 28x28 grayscale image and pre processed into 28 * 28 NDArray.
                 * 1st axis is batch axis, we can use 1 for initialization.
                 * ???Does a gray scale image have 1 or 3 channels ??
                  pytorch shape is =>  [batch_size, 16 ,5 ,5], ??
                 */
                Shape inputShape = new Shape(BATCH_SIZE, NUM_OF_CHANNELS * NEW_HEIGHT * NEW_WIDTH);      //MLP shape
                // Shape inputShape = new Shape(BATCH_SIZE, NUM_OF_CHANNELS, NEW_HEIGHT, NEW_WIDTH);           //ResNet shape

                // initialize trainer with proper input shape
                trainer.initialize(inputShape);

                EasyTrain.fit(trainer, EPOCHS, trainingSet, validateSet);

                return trainer.getTrainingResult();
            }
        }
    }

    private static DefaultTrainingConfig setupTrainingConfig(Arguments arguments, String _outputDir) {
        //String outputDir = arguments.getOutputDir();
        //String outputDir = "build/models/mlpContainerModel";

        
        //epoch number to change learning rate
        // int[] epoch = {3, 5, 8};
        // int[] steps = Arrays.stream(epoch).map(k -> k * 60000 / BATCH_SIZE).toArray();

        //initialize neural network weights using Xavier initializer
        //weights dictate the importance of the input value
        //weights are random at first, then changed after each iteration to correct errors (uses learning rate)
        // Initializer initializer =
        //         new XavierInitializer(
        //                 XavierInitializer.RandomType.UNIFORM, XavierInitializer.FactorType.AVG, 2);

        //set the learning rate
        //the amount the weights are adjusted based on loss (ie errors)
        //dictates how much to change the model in response to errors
        //sometimes called the step size
        // MultiFactorTracker learningRateTracker =
        //         LearningRateTracker.multiFactorTracker()
        //                 .setSteps(steps)
        //                 .optBaseLearningRate(0.01f)
        //                 .optFactor(0.1f)
        //                 .optWarmUpBeginLearningRate(1e-3f)
        //                 .optWarmUpSteps(500)
        //                 .build();

        //set optimization technique, Stochastic Gradient Descent (SGD)
        //makes small adjustments to the network configuration to decrease errors
        //minimizes loss (i.e. errors) to produce better and faster results
        // Optimizer optimizer =
        //         Optimizer.sgd()
        //                 .setRescaleGrad(1.0f / BATCH_SIZE)
        //                 .setLearningRateTracker(learningRateTracker)
        //                 .optMomentum(0.9f)
        //                 .optWeightDecays(0.001f)
        //                 .optClipGrad(1f)
        //                 .build();


        SaveModelTrainingListener listener = new SaveModelTrainingListener(_outputDir);
        listener.setSaveModelCallback(
                trainer -> {
                    TrainingResult result = trainer.getTrainingResult();
                    Model model = trainer.getModel();
                    float accuracy = result.getValidateEvaluation("Accuracy");
                    model.setProperty("Accuracy", String.format("%.5f", accuracy));
                    model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
                });
        return new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                .addEvaluator(new Accuracy())
                .optDevices(Engine.getInstance().getDevices(arguments.getMaxGpus()))
                .addTrainingListeners(TrainingListener.Defaults.logging(_outputDir))
                .addTrainingListeners(listener);
    }

   

    private static RandomAccessDataset getDataset(Dataset.Usage usage, String datasetRoot) throws IOException {

        ImageFolder dataset = ImageFolder.builder()
            .setRepositoryPath(Paths.get(datasetRoot)) // set root of image folder

            .setSampling(BATCH_SIZE, true) // random sampling; don't process the data in order

            .optMaxDepth(2) // set the max depth of the sub folders
            // .optUsage(usage)
            //.addTransform(new Resize(Models.IMAGE_WIDTH, Models.IMAGE_HEIGHT))
            // .addTransform(new ToTensor())       //Convert to  an N-Dimensional Array ? (tensor ?)
            .optPipeline(
                //create preprocess pipeline
                new Pipeline()
                        .add(new Resize(NEW_WIDTH, NEW_HEIGHT))
                        // .add(new CenterCrop(NEW_WIDTH, NEW_HEIGHT))
                        // .add(new RandomFlipLeftRight())
                        // .add(new RandomFlipTopBottom())
                        //Crop(x,y,NEW_WIDTH, NEW_HEIGHT)
                        //Normalize             //A Transform that normalizes an image NDArray of shape CHW or NCHW.

                        //RandomBrightness
                        //RandomCollorJitter
                        //RandomFlipLeftRight
                        //RandomFlipTopBottom
                        //RandomHue
                        //RandomResizedCrop
                        .add(new ToTensor())

                        //reCenter ?
                )
            .build();

            dataset.prepare(new ProgressBar());

            //log it
            logger.info("Dataset: type:{}, size:{}, channels:{}", usage.toString(), dataset.size(), dataset.getImageChannels());

            return dataset;

            }


}