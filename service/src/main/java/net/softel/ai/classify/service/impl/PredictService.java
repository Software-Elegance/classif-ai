package net.softel.ai.classify.service.impl;


import java.util.List;

// import com.github.dozermapper.core.Mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.nio.file.Path;
import java.nio.file.Paths;
import ai.djl.Model;

import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Block;

import ai.djl.translate.TranslateException;
import java.io.IOException;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.Classifications;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.djl.basicmodelzoo.cv.classification.ResNetV1;

import java.util.Arrays;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.Classifications.Classification;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.nn.Block;
import ai.djl.modality.cv.transform.Resize;

import net.softel.ai.classify.dto.PredictSuite;
import net.softel.ai.classify.common.S3ImageDownloader;

import net.softel.ai.classify.service.IPredict;

@Service("predictService")
public class PredictService implements IPredict {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    
    @Autowired
	private S3ImageDownloader downloader;

    public String predictClass(PredictSuite predictSuite){
        log.info("\nPredicting...{}******************\n\n", predictSuite.getTitle());
        Classifications classifications = predictClassification(predictSuite);
        log.info("{}", classifications);
        log.info("\nDone predicting...{}******************\n\n", predictSuite.getTitle());
        return classifications.getAsString();
        }

    
    public String predictBest(PredictSuite predictSuite){
        log.info("\nPredicting...{}*****************\n\n",predictSuite.getTitle());
        Classification best = predictClassification(predictSuite).best();
        log.info("{}", best);
        log.info("\nDone predicting...{}******************\n\n", predictSuite.getTitle());
        return best.toString();
        }

    private Classifications predictClassification(PredictSuite predictSuite)  {

        try{

            String modelLocation = predictSuite.getModelDirectory();

            String imagePath = predictSuite.getImagePath();

            log.info("Predicting {} ...", imagePath);

            Image img = null;
            if(predictSuite.getImageSource().equals("S3")){
                img = ImageFactory.getInstance().fromInputStream(downloader.downloadStream(imagePath));
                }
            else if(predictSuite.getImageSource().equals("URL")){
                img = ImageFactory.getInstance().fromUrl(imagePath);
                }
            else{
                Path imageFile = Paths.get(imagePath);
                img = ImageFactory.getInstance().fromFile(imageFile);
                }
            
            final int outputSize = predictSuite.getClasses().split(",").length;

            String modelName = predictSuite.getModelName();
            try (Model model = Model.newInstance(modelName)) {

                Block neuralNet = null;

                if(predictSuite.getNeuralNetwork().equals("RESNET_50")){
                    neuralNet = ResNetV1.builder()
                        .setImageShape(new Shape(predictSuite.getBatchSize(), 3, predictSuite.getImageHeight(), predictSuite.getImageWidth()))
                        .setNumLayers(50)      
                        .setOutSize(outputSize)
                        .build();
                    }

                model.setBlock(neuralNet);

                Path modelDir = Paths.get(modelLocation);
                model.load(modelDir);

                String[] classification = predictSuite.getClasses().split(",");
                List<String> classes = IntStream
                    .range(0, outputSize)
                    .mapToObj(i -> classification[i])
                    .collect(Collectors.toList());

                Translator<Image, Classifications> translator =
                        ImageClassificationTranslator.builder()
                                .addTransform(new Resize(predictSuite.getImageHeight(), predictSuite.getImageWidth()))
                                .addTransform(new ToTensor())
                                .optSynset(classes)
                                .optApplySoftmax(true)
                                .build();

                try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
                    return predictor.predict(img);
                    }
                }
            }
            
        catch(IOException ioe) {
            System.out.println(ioe.getMessage());
            return null;
            }
        catch(TranslateException te) {
            System.out.println(te.getMessage());
            return null;
            }
        catch(ModelException me) {
            System.out.println(me.getMessage());
            return null;
            }
        }

}