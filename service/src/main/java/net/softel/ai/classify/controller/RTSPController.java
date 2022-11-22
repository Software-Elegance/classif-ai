
package net.softel.ai.classify.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.extern.slf4j.Slf4j;

import net.softel.ai.classify.dto.LiveStream;
import net.softel.ai.classify.service.IVideo;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RestController
@RequestMapping("/rtsp")
public class RTSPController {

    @Autowired
    private IVideo service;

    @GetMapping(value = "/live.mp4")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> livestream(HttpSession session) {
        String rtspUrl = "rtsp://demo:demo@ipvmdemo.dyndns.org:5541/onvif-media/media.amp?profile=profile_1_h264&sessiontimeout=60&streamtype=unicast";
        if(session.getAttribute("rtspUrl") != null){
            rtspUrl = (String)session.getAttribute("rtspUrl");
            }
        log.info("rtspUrl = {}", rtspUrl);
        return service.liveStream(rtspUrl);
        }

    @PostMapping(value = "/stream")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> rtspStream(@RequestBody @Valid LiveStream stream) {
        return service.liveStream(stream.getRtspUrl());
        }

    @PostMapping(value = "/set/stream")
    @ResponseBody
    public String setStream(@RequestBody @Valid LiveStream stream, HttpSession session) {
        session.setAttribute("rtspUrl", stream.getRtspUrl());
        return "OK";
        }
}