package net.softel.ai.classify.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;
import java.util.concurrent.TimeUnit;
import org.springframework.http.MediaType;

@Service("videoService")
public class VideoStreamingService implements IVideo {

    private static final String FORMAT = "classpath:mp4/%s.mp4";

    @Autowired
    private ResourceLoader resourceLoader;

    public ResponseEntity<StreamingResponseBody> livestream(String rtspUrl){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(os -> {
                    FFmpeg.atPath()
                            .addArgument("-re")
                            .addArguments("-acodec", "pcm_s16le")
                            .addArguments("-rtsp_transport", "tcp")
                            .addArguments("-i", rtspUrl)
                            .addArguments("-vcodec", "copy")
                            .addArguments("-af", "asetrate=22050")
                            .addArguments("-acodec", "aac")
                            .addArguments("-b:a", "96k" )
                            .addOutput(PipeOutput.pumpTo(os)
                                    .disableStream(StreamType.AUDIO)
                                    .disableStream(StreamType.SUBTITLE)
                                    .disableStream(StreamType.DATA)
                                    .setFrameCount(StreamType.VIDEO, 100L)
                                    .setFrameRate(0.1)              //1 frame every 10 seconds
                                    .setDuration(1, TimeUnit.HOURS)
                                    .setFormat("ismv"))
                            .addArgument("-nostdin")
                            .execute();
                    });
        }

    public Mono<Resource> getVideo(String title) {
        return Mono.fromSupplier(() -> this.resourceLoader.getResource( String.format(FORMAT, title)));
    }

    public ResponseEntity<String> summarizeVideo(String title) {
       return ResponseEntity.ok("{}");
    }

}