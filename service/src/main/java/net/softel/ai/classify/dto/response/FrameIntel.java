package net.softel.ai.classify.dto.response;

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
@ToString
@Schema(name = "FrameIntel", description = "Predictions on the grabbed frame")
public class FrameIntel {
    String imageName;       
    String imageBase64;         //for logging purposes
    String timestamp;           //hh:mm:ss.SSS
    String classPredictions;    //for logging purposes   
    }
