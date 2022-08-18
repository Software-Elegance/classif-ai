package net.softel.ai.classify.service;

import ai.djl.training.TrainingResult;
import net.softel.ai.classify.train.Training;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import net.softel.ai.classify.dto.TrainingSuite;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import net.softel.ai.classify.dto.response.VideoSummary;
import net.softel.ai.classify.dto.response.FrameIntel;
import net.softel.ai.classify.dto.SummarizeVideo;

public interface IVideo {
    public ResponseEntity<StreamingResponseBody> livestream(String rtspUrl);
    public Mono<Resource> getVideo(String title);
    public ResponseEntity<VideoSummary> summarizeVideo(SummarizeVideo summary)  ;
    }


