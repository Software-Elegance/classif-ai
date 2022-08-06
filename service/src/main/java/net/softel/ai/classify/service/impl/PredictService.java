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
import ai.djl.modality.Classifications;

import ai.djl.Application;
import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.djl.translate.Pipeline;
import ai.djl.repository.SimpleRepository;
import ai.djl.basicmodelzoo.cv.classification.ResNetV1;
import ai.djl.training.optimizer.Sgd;

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
import net.softel.ai.classify.enums.NNet;
import net.softel.ai.classify.common.S3ImageDownloader;
import org.springframework.core.io.InputStreamResource;
// import org.springframework.core.io.Resource;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;

import javax.annotation.Resource;

@Service("predictService")
public class PredictService implements IPredict {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    

    // @Resource
    // private ImageFactory imageFactory;

    @Autowired
	private S3ImageDownloader downloader;

    //@Async
    public String predictClass(PredictSuite predictSuite){
        log.info("\nPredicting..." + predictSuite.getTitle() + "******************\n\n");
        Classifications classifications = predictClassification(predictSuite);
        log.info("{}", classifications);
        log.info("\nDone predicting..." + predictSuite.getTitle() + "******************\n\n");
        return classifications.getAsString();
        }

    
    public String predictBest(PredictSuite predictSuite){
        log.info("\nPredicting..." + predictSuite.getTitle() + "******************\n\n");
        Classification best = predictClassification(predictSuite).best();
        log.info("{}", best);
        log.info("\nDone predicting..." + predictSuite.getTitle() + "******************\n\n");
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
            

            final int outputSize = predictSuite.getClassification().split(",").length;

            String modelName = predictSuite.getModelName();
            try (Model model = Model.newInstance(modelName)) {

                Block neuralNet = null;

                if(predictSuite.getNeuralNetwork().equals("RESNET_50")){
                    neuralNet = ResNetV1.builder()
                        .setImageShape(new Shape(predictSuite.getBatchSize(), predictSuite.getNumberOfChannels(), predictSuite.getImageHeight(), predictSuite.getImageWidth()))
                        .setNumLayers(50)       //default was 50 !
                        .setOutSize(outputSize)
                        .build();
                    }

                model.setBlock(neuralNet);

                // Assume you have run TrainContainerDamage.java example, and saved model in build/containerModel folder.
                Path modelDir = Paths.get(modelLocation);
                model.load(modelDir);

                String[] classification = predictSuite.getClassification().split(",");
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