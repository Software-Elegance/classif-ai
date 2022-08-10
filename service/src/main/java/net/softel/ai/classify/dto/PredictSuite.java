package net.softel.ai.classify.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@ToString@Schema(name = "PredictSuite", description = "Inference options")
public class PredictSuite{

    @NotNull
    @Size(min = 5, max = 100)
    @Schema(name = "title", description = "Title of this inference", example = "Inferencce")
    String title;       //for logging purposes

    @NotNull
    @Size(min = 5, max = 50)
    @Schema(name = "neuralNetwork", description = "CNN architecture. RESNET_50, YOLO or SDD", example = "RESNET_50")
    String neuralNetwork;

    @NotNull
    @Size(min = 5, max = 200)
    @Schema(name = "classes", description = "Classes available in the model", example = "slipper,loafer,mocassin,sneakers,sport")
    String classes;

    @Min(32)
    @Max(64)
    @Schema(name = "batchSize", description = "batchSize", example = "32")
    Integer batchSize;

    @NotNull
    @Size(min = 5, max = 50)
    @Schema(name = "modelName", description = "modelName", example = "intelModel")
    String modelName;

    @NotNull
    @Size(min = 5, max = 200)
    @Schema(name = "modelOutputDir", description = "Where to save the generated model", example = "models/intelModelDir")
    String modelDirectory;

    @NotNull
    @Size(min = 5, max = 200)
    @Schema(name = "imagePath", description = "Path to the image you want to predict the classes for", example = "image1.jpeg")
    String imagePath;

    @NotNull
    @Size(min = 5, max = 50)
    @Schema(name = "imageSource", description = "Location of the image mentioned in the imagePath above. LOCAL, S3 or URL", example = "LOCAL")
    String imageSource;

    @Min(32)
    @Max(512)
    @Schema(name = "imageHeight", description = "imageHeight", example = "150")
    Integer imageHeight;

    @Min(32)
    @Max(512)
    @Schema(name = "imageWidth", description = "imageWidth", example = "150")
    Integer imageWidth;

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