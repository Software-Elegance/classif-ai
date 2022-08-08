package net.softel.ai.classify.dto;



import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Data
@Builder
@ToString
public class PredictSuite{

    @NotNull
    @Size(min = 5, max = 100)
    String title;       //for logging purposes

    @NotNull
    @Size(min = 5, max = 50)
    String neuralNetwork;

    @NotNull
    @Size(min = 5, max = 200)
    String classification;

    @Min(32)
    @Max(64)
    Integer batchSize;

    @NotNull
    @Size(min = 5, max = 50)
    String modelName;

    @NotNull
    @Size(min = 5, max = 200)
    String modelDirectory;

    @NotNull
    @Size(min = 5, max = 200)
    String imagePath;

    @NotNull
    @Size(min = 5, max = 200)
    String imageSource;

    @Min(32)
    @Max(512)
    Integer imageHeight;

    @Min(32)
    @Max(512)
    Integer imageWidth;

    @Min(1)
    @Max(3)
    Integer numberOfChannels;

    }


// http://localhost:8080/classif-ai/preedict/now

// {
//   "title": "Container damage prediction",
//   "neuralNetwork": "RESNET_50",
//   "classification": "dent,hole,normal,open,rust",
//   "batchSize": 32,
//   "modelName": "helloModel",
//   "modelDirectory": "build/models/helloModelDir",
//   "imagePath":"/Users/zeguru/Pictures/Containers/Dataset/testing/normal/white.jpeg",
//   "imageHeight": 256,
//   "imageWidth": 256,
//   "numberOfChannels": 3
// }