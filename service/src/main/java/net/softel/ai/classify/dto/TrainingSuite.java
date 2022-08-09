package net.softel.ai.classify.dto;

import ai.djl.engine.Engine;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(name = "TrainingSuite", description = "Training hyper parameters")
public class TrainingSuite{

    @NotNull
    @Size(min = 5, max = 150)
    @Schema(name = "title", description = "Title of this training", example = "Footwear classification")
    String title;       

    @NotNull
    @Size(min = 3, max = 50)
    @Schema(name = "neuralNetwork", description = "CNN architecture. RESNET_50, YOLO or SDD", example = "RESNET_50")
    String neuralNetwork;

    @NotNull
    @Size(min = 5, max = 200)
    @Schema(name = "classification", description = "Classes to train on", example = "slipper,loafer,mocassin,sneakers,sport")
    String classification;

    @Min(32)
    @Max(64)
    @Schema(name = "batchSize", description = "batchSize", example = "32")
    Integer batchSize;

    @Min(1)
    @Max(200)
    @Schema(name = "epochs", description = "Number of iterations", example = "10")
    Integer epochs;

    final Integer maxGpus = Engine.getInstance().getGpuCount();
    final Integer deviceCount = Engine.getInstance().getDevices().length;

    @NotNull
    @Size(min = 5, max = 50)
    @Schema(name = "modelName", description = "modelName", example = "myModel")
    String modelName;

    @NotNull
    @Size(min = 5, max = 200)
    @Schema(name = "modelOutputDir", description = "Where to save the generated model", example = "myModelDir")
    String modelOutputDir;

    @NotNull
    @Size(min = 5, max = 200)
    @Schema(name = "trainingDataset", description = "Path to the folder containing the training dataset (subdirectories named training and validation)", example = "/home/zeguru/Downloads/Dataset")
    String trainingDataset;     //image folder

    @Min(32)
    @Max(512)
    @Schema(name = "imageHeight", description = "imageHeight", example = "imageHeight")
    Integer imageHeight;

    @Min(32)
    @Max(512)
    @Schema(name = "imageWidth", description = "imageWidth", example = "150")
    Integer imageWidth;

    @Min(1)
    @Max(3)
    @Schema(name = "numberOfChannels", description = "Number of channels. 3 for colored (rgb) images", example = "3")
    Integer numberOfChannels;

    }

