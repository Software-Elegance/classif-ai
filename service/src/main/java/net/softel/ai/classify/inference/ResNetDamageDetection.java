package net.softel.ai.classify.inference;

import java.util.Arrays;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.nn.Block;
import ai.djl.modality.cv.transform.Resize;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.softel.ai.classify.preprocess.GrayScale;
import ai.djl.basicmodelzoo.cv.classification.ResNetV1;
import ai.djl.ndarray.types.Shape;


/**
 * An example of inference using an image classification model.
 *
 * <p>See this <a
 * href="https://github.com/deepjavalibrary/djl/blob/master/examples/docs/image_classification.md">doc</a>
 * for information about this example.
 */
public final class ResNetDamageDetection {

    private static final Logger logger = LoggerFactory.getLogger(ResNetDamageDetection.class);

    //the number of classification labels:  dent, hole, normal, open, rust
    private static List<String> classification = Arrays.asList("dent", "hole", "normal", "open", "rust");
    private static final int NUM_OF_OUTPUT = classification.size();

    //the height and width for pre-processing of the image
    private static final int NEW_HEIGHT = 256;
    private static final int NEW_WIDTH = 256;

    //represents number of training samples processed before the model is updated
    private static final int BATCH_SIZE = 5;   //was 32

    private static final int NUM_OF_CHANNELS = 3;   //was 32


    private ResNetDamageDetection() {}

    // public static void main(String[] args) throws IOException, ModelException, TranslateException {
    //     Classifications classifications = ContainerDamageDetection.predict("normal_gray.jpeg");
    //     logger.info("{}", classifications);
    // }

    public static Classifications predict(String modelLocation, String dir, String colorImageName) throws IOException, ModelException, TranslateException {
        // Path imageFile = Paths.get("src/test/resources/0.png");
        // String imagePath = "/Users/zeguru/Pictures/Containers/RAW images Basic classification/damaged/grayscale_img.jpeg";

        // String modelDir = modelLocation; 

        // String filePath = dir;
        // GrayScale.convertToGrayScale(dir, colorImageName);

        //String imagePath = dir + "grayscale_" + colorImageName;
        String imagePath = dir + colorImageName;
        logger.info("Predicting {} ...", imagePath);
        Path imageFile = Paths.get(imagePath);

        Image img = ImageFactory.getInstance().fromFile(imageFile);

        String modelName = "containers";
        try (Model model = Model.newInstance(modelName)) {

            Block resNet50 =
                    //construct the network
                    ResNetV1.builder()
                            .setImageShape(new Shape(BATCH_SIZE, NUM_OF_CHANNELS, NEW_HEIGHT, NEW_WIDTH))
                            .setNumLayers(50)       //default was 50 !
                            .setOutSize(NUM_OF_OUTPUT)
                            .build();

                model.setBlock(resNet50);

                // Assume you have run TrainContainerDamage.java example, and saved model in build/containerModel folder.
                Path modelDir = Paths.get(modelLocation);
                model.load(modelDir);

                List<String> classes = IntStream
                                        .range(0, NUM_OF_OUTPUT)
                                        .mapToObj(i -> classification.get(i))
                                        .collect(Collectors.toList());

                Translator<Image, Classifications> translator =
                        ImageClassificationTranslator.builder()
                                .addTransform(new Resize(NEW_HEIGHT, NEW_WIDTH))
                                .addTransform(new ToTensor())
                                .optSynset(classes)
                                .optApplySoftmax(true)
                                .build();

                try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
                    return predictor.predict(img);
                }

            }
    }
}