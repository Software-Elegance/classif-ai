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
@ToString@Schema(name = "SummarizeVideo", description = "SummarizeVideo options")
public class SummarizeVideo{

    @NotNull
    @Size(min = 5, max = 100)
    @Schema(name = "title", description = "Title of this task", example = "Summarize video clip")
    String title;       //for logging purposes

    @NotNull
    @Size(min = 5, max = 100)
    @Schema(name = "filePath", description = "filePath", example = "/Users/zeguru/Projects/Softel/ML/classif-ai/service/src/main/resources/mp4/sample_960x540.mp4")
    String filePath;       //for logging purposes

    @NotNull
    @Schema(name = "stepInSeconds", description = "Rate of sampling. Eg 5 to sample every 5 seconds", example = "5")
    Integer stepInSeconds;

    @NotNull
    @Schema(name = "showImage", description = "Return the base64 image", example = "false")
    Boolean showImage;

    }

