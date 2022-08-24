/*
 * The action categories can be divided into five types: 
 * 1)Human-Object Interaction 
 * 2) Body-Motion Only 
 * 3) Human-Human Interaction 
 * 4) Playing Musical Instruments 
 * 5) Sports.
 */
package net.softel.ai.classify.config.inference;

import ai.djl.Application;
import ai.djl.training.util.ProgressBar;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
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
