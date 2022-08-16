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


import com.google.gson.Gson
import net.softel.ai.classify.backend.ImageClassificationClient
import net.softel.ai.classify.common.S3ImageDownloader
import net.softel.ai.classify.common.S3ImageUploader
import net.softel.ai.classify.dto.ClassificationResponse
import net.softel.ai.classify.form.ImageClassificationForm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.lang.reflect.Type
import java.time.Duration
import java.util.*


@Controller
class ImageClassificationController(private val uploader: S3ImageUploader,
                                private val downloader: S3ImageDownloader,
                                private val apiClient: ImageClassificationClient
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(ObjectDetectionController::class.java)
    }

    @RequestMapping("/image-classification/inbox")
    fun listFiles(model: Model): String {
        model.addAttribute("files", downloader.listFolder("inbox"))
        return "image-classification-files"
    }

    @RequestMapping("/image-classification/inbox/new-image-classification")
    fun newObjectDetection(model: Model): String  {
        model.addAttribute("ImageClassificationForm", ImageClassificationForm())
        return "new-image-classification"
    }

    @RequestMapping("/image-classification/inbox", method = [RequestMethod.POST])
    fun classifyImage(@ModelAttribute form: ImageClassificationForm, model: Model): String {
        if(form.file?.originalFilename == null) {
            return "image-classification-inbox"
        }
        var accuracyCheck = false
        val fileName = form.file?.originalFilename ?: ""
        uploader.upload(form.file?.inputStream, form.file?.size ?:0, fileName)
        val results = apiClient.classifyS3(fileName).block(Duration.ofSeconds(30))
        val response: Array<ClassificationResponse> = Gson().fromJson(results, Array<ClassificationResponse>::class.java)
        if(response.toList().isNotEmpty()){
            accuracyCheck = true
        }
        model.addAttribute("files", downloader.listFolder("inbox"))
        model.addAttribute("results", response.toList())
        model.addAttribute("accuracyCheck", accuracyCheck)
        model.addAttribute("originalFile", form.file?.originalFilename)
        return "image-classification-files"
    }

    @RequestMapping("/image-classification/images/inbox/{file-name}")
    @ResponseBody
    fun getInboxImage(@PathVariable("file-name") fileName: String) : Resource {
        return InputStreamResource(downloader.downloadStream("inbox/".plus(fileName)))
    }

    @RequestMapping("/image-classification/images/outbox/{file-name}")
    @ResponseBody
    fun getOutboxImage(@PathVariable("file-name") fileName: String) : Resource {
        return InputStreamResource(downloader.downloadStream("outbox/".plus(fileName)))
    }
}
