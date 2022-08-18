
package net.softel.ai.classify.controller;

import net.softel.ai.classify.service.VideoStreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;

@RestController
public class VideoStreamingController {

    @Autowired
    private VideoStreamingService service;

    @GetMapping(value = "video/{title}", produces = "video/mp4")
    public Mono<Resource> getVideo(@PathVariable String title, @RequestHeader("Range") String range) {
          return service.getVideo(title);
        }

    
    @GetMapping(value = "summarize/{title}", produces = "application/json")
    public ResponseEntity<String> summarizeVideo(@PathVariable String title) {
          return service.summarizeVideo(title);
        }

}