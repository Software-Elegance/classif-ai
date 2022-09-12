package net.softel.ai.classify.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class WebcamController {

    @RequestMapping(*arrayOf("webcam.ftlh","webcam.html","webcam"))
    fun webcam(): String {
        return "webcam"
    }
}