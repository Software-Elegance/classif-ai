
package net.softel.ai.classify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.util.concurrent.TimeUnit;
import org.springframework.http.MediaType;

import lombok.extern.slf4j.Slf4j;


import net.softel.ai.classify.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;

@Slf4j
@RestController
@RequestMapping("/rtsp")
public class RTSPController {

    @Autowired
    private VideoService service;

    @GetMapping(value = "/live.mp4")
    @ResponseBody
    // public ResponseEntity<StreamingResponseBody> livestream(@PathVariable("id") Long tipperId) throws Exception {
    public ResponseEntity<StreamingResponseBody> livestream() {


        //  String rtspUrl = "rtsp://demo:demo@ipvmdemo.dyndns.org:5541/onvif-media/media.amp?profile=profile_1_h264&sessiontimeout=60&streamtype=unicast";
        // String rtspUrl = "rtsp://ipcam.stream:8554/bars";
       String rtspUrl = "rtsp://rtsp.stream/movie";
        return service.liveStream(rtspUrl);
        // return service.liveStreamFrameOutput(rtspUrl);

        }
}