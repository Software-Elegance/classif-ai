package net.softel.ai.classify.util;

import ai.djl.training.dataset.Dataset;

import java.util.List;
import java.util.Random;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import net.softel.ai.classify.dto.TrainingSuite;

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

import ai.djl.translate.TranslateException;
import java.io.IOException;
import ai.djl.translate.Pipeline;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrainingUtils {

    private static Logger log = LoggerFactory.getLogger(TrainingUtils.class);

    public static RandomAccessDataset getDataset(Dataset.Usage usage,TrainingSuite trainingSuite) throws IOException {

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

}