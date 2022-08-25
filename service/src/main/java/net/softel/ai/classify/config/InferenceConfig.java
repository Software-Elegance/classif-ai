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
package net.softel.ai.classify.config;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.io.IOException;
import java.util.function.Supplier;

@Configuration
public class InferenceConfig {

    @Bean
    public ImageFactory imageFactory() {
        return ImageFactory.getInstance();
        }

    @Bean
    public Criteria<Image, DetectedObjects> criteria() {

        return Criteria.builder()
                .setTypes(Image.class, DetectedObjects.class)
                .optApplication(Application.CV.OBJECT_DETECTION)

                .optArtifactId("ssd")
                .optProgress(new ProgressBar())

                .build();
        }


    @Bean
    public ZooModel<Image, DetectedObjects> model(
            @Qualifier("criteria") Criteria<Image, DetectedObjects> criteria)
            throws MalformedModelException, ModelNotFoundException, IOException {
        return ModelZoo.loadModel(criteria);
    }

    /**
     * Scoped proxy is one way to have a predictor configured and closed.
     * @param model object for which predictor is expected to be returned
     * @return predictor object that can be used for inference
     */
    @Bean(destroyMethod = "close")
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
    public Predictor<Image, DetectedObjects> predictor(ZooModel<Image, DetectedObjects> model) {
        return model.newPredictor();
    }

    /**
     * Inject with @Resource or autowired. Only safe to be used in the try with resources.
     * @param model object for which predictor is expected to be returned
     * @return supplier of predictor for thread-safe inference
     */
    @Bean
    public Supplier<Predictor<Image, DetectedObjects>> predictorProvider(ZooModel<Image, DetectedObjects> model) {
        return model::newPredictor;
    }
}
