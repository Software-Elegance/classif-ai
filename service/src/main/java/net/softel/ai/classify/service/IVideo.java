package net.softel.ai.classify.service;

import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import net.softel.ai.classify.dto.response.VideoSummary;
import net.softel.ai.classify.dto.SummarizeVideo;

public interface IVideo {
    public Mono<Resource> getVideo(String title);
    public ResponseEntity<StreamingResponseBody> liveStream(String rtspUrl);
    public ResponseEntity<VideoSummary> summarizeVideo(SummarizeVideo summary);
    public ResponseEntity<StreamingResponseBody> liveStreamFrameOutput(String rtspUrl);

    }


