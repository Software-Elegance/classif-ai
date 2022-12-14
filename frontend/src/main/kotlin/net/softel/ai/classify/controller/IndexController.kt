/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.softel.ai.classify.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Index controller.
 */
@Controller
class IndexController {

    @RequestMapping("/")
    fun index(): String {
        return "index"
    }

    @RequestMapping("/video.ftlh")
    fun video(): String {
        return "video"
    }

    @RequestMapping("/rtsp.ftlh")
    fun rtsp(): String {
        return "rtsp"
    }

    @RequestMapping(*arrayOf("settings.ftlh","settings.html","settings"))
    fun settings(): String {
        return "settings"
    }
}