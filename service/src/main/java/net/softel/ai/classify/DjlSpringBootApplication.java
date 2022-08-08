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

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
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
import net.softel.ai.classify.train.ResNetTrainer;
// import com.aws.samples.djlspringboot.util.inference.ImageClassification;
// import com.aws.samples.djlspringboot.util.inference.ContainerDamageDetection;
// import com.aws.samples.djlspringboot.util.inference.container.MLPDamageDetection;
import net.softel.ai.classify.inference.ResNetDamageDetection;

// import com.aws.samples.djlspringboot.util.preprocess.GrayScale;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.output.DetectedObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@Import(AmazonClientConfiguration.class)
public class DjlSpringBootApplication {

    private static final Logger logger = LoggerFactory.getLogger(DjlSpringBootApplication.class);

    public static void main(String[] args) {

        logger.info("Starting... args = {}", (Object) args);
        SpringApplication.run(DjlSpringBootApplication.class, args);

        //try{

        // 1. PREPARATION Preprocess. 
            //a. convert to grayscale
            // String filePath = "/Users/zeguru/Pictures/Containers/RAW images Basic classification/dent/";
            // File path = new File(filePath);
            // File [] files = path.listFiles();
            // for (int i = 0; i < files.length; i++){
            //     if (files[i].isFile()){ //this line weeds out other directories/folders
            //         //System.out.println(files[i]);
            //         GrayScale.convertToGrayScale(filePath, files[i].getName());
            //         }
            //     else{ 
            //         System.out.println("Not a file... skipping");
            //         }
            // }


        // 2. TRAINING 
            // 1. Mnist
            // logger.info("Training Mnist...");
            // TrainMnist.runExample(args);

            // 2. Container Damage :: Image folder
            //logger.info("\n\n*************Training Container Damage...******************\n\n");
            // MLPTrainer.trainMlp("build/models/mlpContainerModel");
            //ResNetTrainer.trainResNet("build/models/resNetModel");


        // 3. INFERENCE
            //2. container Damage
            // logger.info("\n\n*************Predicting container damage...******************\n\n");
            // // Classifications classifications = ContainerDamageDetection.predict("burnt_closeup.jpeg");
            // // logger.info("{}", classifications);

            // String testDir = "/Users/zeguru/Pictures/Containers/Dataset/testing/hole/";
            // File testPath = new File(testDir);
            // File [] testFiles = testPath.listFiles();
            // for (int i = 0; i < testFiles.length; i++){
            //     if (testFiles[i].isFile()){ //this line weeds out other directories/folders
            //         //Classifications classifications = MLPDamageDetection.predict("build/models/mlpContainerModel",testDir, testFiles[i].getName());
            //         Classifications classifications = ResNetDamageDetection.predict("build/models/resNetContainerModel",testDir, testFiles[i].getName());
            //         logger.info("{}", classifications);
            //         }
            //     else{ 
            //         System.out.println("Not a file... skipping");
            //         }
            // }

            // Classifications classifications = MLPDamageDetection.predict("build/models/mlpContainerModel","burnt_closeup.jpeg");
            // logger.info("{}", classifications);

            // Classifications classifications = ImageClassification.predict();
            // logger.info("{}", classifications);

            // Classifications classifications = SentimentAnalysis.predict();
            // logger.info(classifications.toString());

            // DetectedObjects detection = ObjectDetectionWithTensorflowSavedModel.predict();
            // logger.info("{}", detection);

            // DetectedObjects detection = KichukuObjectDetection.predict();
            // logger.info("{}", detection);




        //     }
        // catch(IOException ioe) {
        //     System.out.println(ioe.getMessage());
        //     }
        // catch(TranslateException te) {
        //     System.out.println(te.getMessage());
        //     }
        // catch(ModelException me) {
        //     System.out.println(me.getMessage());
        //     }

        }
}
