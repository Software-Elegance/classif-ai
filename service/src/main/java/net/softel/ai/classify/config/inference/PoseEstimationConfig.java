/*
 * Pose estimation
 */
package net.softel.ai.classify.config.inference;

import ai.djl.Application;
import ai.djl.training.util.ProgressBar;

import ai.djl.modality.cv.Image;

import ai.djl.repository.zoo.Criteria;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.djl.modality.cv.output.Joints;


@Configuration
public class PoseEstimationConfig {

    //Ref: https://github.com/deepjavalibrary/djl/blob/master/examples/src/main/java/ai/djl/examples/inference/PoseEstimation.java

    @Bean(name="poseEstimationCriteria")
    public Criteria<Image, Joints> poseEstimationCriteria() {

        return  Criteria.builder()
            .optApplication(Application.CV.POSE_ESTIMATION)
            .setTypes(Image.class, Joints.class)
            .optFilter("backbone", "resnet18")
            .optFilter("flavor", "v1b")
            .optFilter("dataset", "imagenet")
            .optProgress(new ProgressBar())
            .build();

        }

    }
