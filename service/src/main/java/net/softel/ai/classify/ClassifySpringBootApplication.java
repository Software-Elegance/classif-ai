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
package net.softel.ai.classify;

import net.softel.ai.classify.common.AmazonClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
// import com.aws.samples.djlspringboot.util.inference.SentimentAnalysis;
// import com.aws.samples.djlspringboot.util.inference.ObjectDetectionWithTensorflowSavedModel;
// import com.aws.samples.djlspringboot.util.inference.KichukuObjectDetection;
// import com.aws.samples.djlspringboot.util.train.TrainMnist;
// import com.aws.samples.djlspringboot.util.train.TrainContainerDamage;
// import com.aws.samples.djlspringboot.util.train.container.MLPTrainer;
// import net.softel.ai.classify.train.ResNetTrainer;
// import com.aws.samples.djlspringboot.util.inference.ImageClassification;
// import com.aws.samples.djlspringboot.util.inference.ContainerDamageDetection;
// import com.aws.samples.djlspringboot.util.inference.container.MLPDamageDetection;
// import net.softel.ai.classify.inference.ResNetDamageDetection;

// import com.aws.samples.djlspringboot.util.preprocess.GrayScale;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.output.DetectedObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import java.io.IOException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@Import(AmazonClientConfiguration.class)
public class ClassifySpringBootApplication {

    public static void main(String[] args) {

        SpringApplication.run(ClassifySpringBootApplication.class, args);

        }
}
