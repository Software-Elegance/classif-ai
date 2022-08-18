
package net.softel.ai.classify.controller;

import net.softel.ai.classify.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;
import net.softel.ai.classify.dto.response.VideoSummary;
import net.softel.ai.classify.dto.SummarizeVideo;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/predict")
@Tag(name = "Inference", description = "Inference controller")
public class VideoController {

    @Autowired
    private VideoService service;

    @GetMapping(value = "/{title}", produces = "video/mp4")
    public Mono<Resource> getVideo(@PathVariable String title, @RequestHeader("Range") String range) {
          return service.getVideo(title);
        }
    
    @PostMapping(path="/summarize", produces = "application/json")
    public ResponseEntity<VideoSummary> summarize(@RequestBody @Valid SummarizeVideo summary) throws Exception {
          return service.summarizeVideo(summary);
        }

}