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
package net.softel.ai.classify.config.inference;

import ai.djl.Application;
import ai.djl.training.util.ProgressBar;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Supplier;

import ai.djl.modality.Classifications;

@Configuration
public class ActionRecognitionConfig {

   
    @Bean(name="actionRecognitionCriteria")
    public Criteria<Image, Classifications> actionRecognitionCriteria() {

        return  Criteria.builder()
            .optApplication(Application.CV.ACTION_RECOGNITION)
            .setTypes(Image.class, Classifications.class)
            .optFilter("backbone", "inceptionv3")
            .optFilter("dataset", "ucf101")
            .optEngine("MXNet")
            .optProgress(new ProgressBar())
            .build();

        }

    @Bean(name="actionRecognitionModel")
    public ZooModel<Image, Classifications> actionRecognitionModel(
        @Qualifier("actionRecognitionCriteria") 
        Criteria<Image, Classifications> criteria
        )throws MalformedModelException, ModelNotFoundException, IOException {

        return ModelZoo.loadModel(criteria);
        }


    /**
     * Scoped proxy is one way to have a predictor configured and closed.
     * @param model object for which predictor is expected to be returned
     * @return predictor object that can be used for inference
     */
    @Bean(name="actionRecognitionPredictor", destroyMethod = "close")
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
    public Predictor<Image, Classifications> actionRecognitionPredictor(
        @Qualifier("actionRecognitionModel")
        ZooModel<Image, Classifications> model) {
            return model.newPredictor();
            }

    @Bean("actionRecognitionPredictorProvider")
    public Supplier<Predictor<Image, Classifications>> actionRecognitionPredictorProvider(
        @Qualifier("actionRecognitionModel")
        ZooModel<Image, Classifications> model) {
            return model::newPredictor;
            }
}
