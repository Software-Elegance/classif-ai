package net.softel.ai.classify.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@ToString@Schema(name = "LiveStream", description = "LiveStream options")
public class LiveStream{

    @NotNull
    @Size(min = 5, max = 50)
    @Schema(name = "title", description = "Title of this task", example = "Summarize video clip")
    String title;       

    @NotNull
    @Size(min = 5, max = 1000)
    @Schema(name = "rtspUrl", description = "rtspUrl", example = "rtsp://demo:demo@ipvmdemo.dyndns.org:5541/onvif-media/media.amp?profile=profile_1_h264&sessiontimeout=60&streamtype=unicast")
    String rtspUrl;      

    @NotNull
    @Schema(name = "frameRate", description = "frameRate", example = "0.5")
    Float frameRate;

    }

