package net.softel.ai.classify.dto.response;

import java.util.List;
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
@Schema(name = "VideoSummary", description = "Summary of the video")
public class VideoSummary {
    String title;       
    String fileName;

    // @ToString(callSuper=true, includeFieldNames=true)
    List<FrameIntel> frames;
    }

