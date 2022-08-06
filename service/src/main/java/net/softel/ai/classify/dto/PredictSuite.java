package net.softel.ai.classify.dto;

import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import ai.djl.engine.Engine;

// import javax.persistence.EnumType;
// import javax.persistence.Enumerated;
// import javax.persistence.GeneratedValue;
// import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
// import io.swagger.v3.oas.annotations.media.Schema;

import net.softel.ai.classify.enums.NNet;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Data
@Builder
@ToString
public class PredictSuite{

    @NotNull
    @Size(min = 5, max = 50)
    String title;       //for logging purposes

    //NNet neuralNetwork;
    @NotNull
    @Size(min = 5, max = 50)
    String neuralNetwork;

    @NotNull
    @Size(min = 5, max = 50)
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