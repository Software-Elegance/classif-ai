package net.softel.ai.classify.common;

import net.softel.ai.classify.dto.PredictSuite;

public class TestObjects {

    public static PredictSuite getPredictSuite(){
        return  PredictSuite.builder()
                    .imagePath("filePath")
                    .imageSource("S3")
                    .title("Testting at ...." + System.currentTimeMillis())
                    .neuralNetwork("RESNET_50")
                    .classes("buildings,forest,glacier,mountain,sea,street")
                    .modelName("intelModel")
                    .modelDirectory("/Users/zeguru/Downloads/MachineLearning/Models/intelModelDir")
                    .batchSize(32)
                    .imageHeight(150)
                    .imageWidth(150)
                .build();
    }

}
