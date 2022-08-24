/*
 * Image classification with a pretrained model. Not in use yet
 */
package net.softel.ai.classify.config.inference;

import ai.djl.Application;
import ai.djl.training.util.ProgressBar;

import ai.djl.modality.cv.Image;

import ai.djl.repository.zoo.Criteria;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import ai.djl.modality.cv.output.Joints;
import ai.djl.modality.Classifications;


@Configuration
public class ImageClassificationConfig {

    //Ref: https://github.com/deepjavalibrary/djl/blob/master/examples/src/main/java/ai/djl/examples/inference/imageClassification.java

    @Bean(name="imageClassificationCriteria")
    public Criteria<Image, Classifications> imageClassificationCriteria() {

        return  Criteria.builder()
            .optApplication(Application.CV.OBJECT_DETECTION)
            .setTypes(Image.class, Classifications.class)
            .optFilter("layer", "50")
            .optFilter("flavor", "v1")
            .optFilter("dataset", "cifar10")
            .optProgress(new ProgressBar())
            .build();
        }

    }
