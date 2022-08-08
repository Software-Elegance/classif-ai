package net.softel.ai.classify.dto;

import ai.djl.engine.Engine;

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
public class TrainingSuite{

    @NotNull
    @Size(min = 5, max = 100)
    String title;       

    @NotNull
    @Size(min = 3, max = 50)
    String neuralNetwork;

    @NotNull
    @Size(min = 5, max = 200)
    String classification;

    @Min(32)
    @Max(64)
    Integer batchSize;

    @Min(1)
    @Max(50)
    Integer epochs;

    final Integer maxGpus = Engine.getInstance().getGpuCount();
    final Integer deviceCount = Engine.getInstance().getDevices().length;

    @NotNull
    @Size(min = 5, max = 50)
    String modelName;

    @NotNull
    @Size(min = 5, max = 200)
    String modelOutputDir;

    @NotNull
    @Size(min = 5, max = 200)
    String trainingDataset;     //image folder

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

