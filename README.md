# Classif-AI [pronounced *classify*]

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/bd71877636f546a584b89dbf978270d9)](https://app.codacy.com/gh/Software-Elegance/classif-ai?utm_source=github.com&utm_medium=referral&utm_content=Software-Elegance/classif-ai&utm_campaign=Badge_Grade_Settings)
[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=Software-Elegance_classif-ai)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) 

[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai) [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Software-Elegance_classif-ai&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Software-Elegance_classif-ai)

# VISION

A plug and play microservice for image classification using Deep Learning. 
Based on the Deep Learning framework, DJL.



## Setting Up

This is a multi-project build using gradle.
Before building, ensure that the DJL Spring Boot starter BOM is in your local maven repository. 
For more information, see the [DJL Spring Boot Starter repo](https://github.com/awslabs/djl-starter). 
You need to check out this repository and run `./mvnw install`.


## Building the service

To build the Classif-AI microservice, run the following command:

    ./gradlew :service:bootJar


This command detects the operating system on the system where the build is running and uses it for platform dependency resolution.
  
The produced artifacts will have the classifier in the name of the spring boot fat jar, e.g.

    service/build/libs/service-0.0.1-SNAPSHOT-linux-x86_64.jar
    service/build/libs/service-0.0.1-SNAPSHOT-osx-x86_64.jar

## Quick Start

Run the following command to start the backend based on the created JAR file, sample command for macOS:

    java -jar service/build/libs/service-0.0.1-SNAPSHOT-linux-x86_64.jar


Alternatively you can run with gradle, sample command for Linux:   
  
    gradlew :service:bootRun -P osclassifier=linux-x86_64



## Test run 

### Download Sample Pretrained Model

    Download and extract the model zip file (intelModelDir.zip) from the following url: https://drive.google.com/file/d/1QSKiiM5aVpzfpnDsyHPg_EfBUVR_cSLu/view?usp=sharing

    NB: This is a model trained to classify images into any of the following classes:
        buildings,forest,glacier,mountain,sea,street

### Predict Classification for image calm_ocean.jpeg


```
curl --location --request POST 'localhost:8080/classif-ai/predict/classes' \
--header 'Content-Type: application/json' \
--data-raw '{
    "title": "Intel Challenge :: predicting natural photo",
    "neuralNetwork": "RESNET_50",
    "classification": "buildings,forest,glacier,mountain,sea,street",
    "batchSize": 32,
    "modelName": "intelModel",
    "modelDirectory": "/Users/zeguru/Downloads/MachineLearning/Models/intelModelDir",
    "imagePath":"/Users/zeguru/Downloads/MachineLearning/Datasets/IntelChallenge/testing/calm_ocean.jpeg",
    "imageHeight": 150,
    "imageWidth": 150,
    "numberOfChannels": 3
    }'
```

Sample Output

    [
        {
            "className": "sea",
            "probability": 0.9999996423721313
        },
        {
            "className": "mountain",
            "probability": 1.4910899892583984E-7
        },
        {
            "className": "glacier",
            "probability": 1.2292046847051097E-7
        },
        {
            "className": "buildings",
            "probability": 1.0650427384462091E-7
        },
        {
            "className": "forest",
            "probability": 1.657191717185924E-8
        }
    ]

### Predict the best class for the same image calm_ocean.jpeg


```
curl --location --request POST 'localhost:8080/classif-ai/predict/best' \
--header 'Content-Type: application/json' \
--data-raw '{
    "title": "Intel Challenge :: prediction natural photo",
    "neuralNetwork": "RESNET_50",
    "classification": "buildings,forest,glacier,mountain,sea,street",
    "batchSize": 32,
    "modelName": "intelModel",
    "modelDirectory": "/Users/zeguru/Downloads/MachineLearning/Models/intelModelDir", "imagePath":"/Users/zeguru/Downloads/MachineLearning/Datasets/IntelChallenge/testing/calm_ocean.jpeg",
    "imageHeight": 150,
    "imageWidth": 150,
    "numberOfChannels": 3
    }'
```

Response:

```
    class: "sea", probability: 0.99998
```


## Training a classification model

### Step 1 :: Problem statement

    What do you want to solve using image classification DL model ?

        Step 1: Define Problem.
        Step 2: Prepare Data.
        Step 3: Evaluate Models.
        Step 4: Finalize Model.

### Step 2 :: Prepare your dataset


    (Link) [https://bugfender.com/blog/how-to-gather-data-for-machine-learning%EF%BB%BF/]

    Collect and organize your images into the following structure :

        Training
            Used to train the model and must be in a subfolder named `training`
            This should comprise approximately 70% of the dataset. This is just a rule of thumb

        Validating
            Used to validate the model during training and must be in a subfolder named `validation`
            This should comprise approximately 20% of the dataset. This is just a rule of thumb

        Testing (optional)
            This contains data the model has never seen (ie, during training) and is very useful when evaluating your model(s) 


    NB: You can download a ready to use and free training dataset from:

        Kaggle
        Amazon
        UCI Machine Learning Repository
        Google’s Datasets Search Engine
        Microsoft
        Lionbridge AI
        Dataset for this tutorial: https://drive.google.com/drive/folders/1fLI97l4ir44M70MOCTCaVCchUdsiK_-h?usp=sharing


### Step 3 :: Run the trainer

    ```
    curl --location --request POST 'localhost:8080/classif-ai/train/classifier' \
        --header 'Content-Type: application/json' \
        --data-raw '{
        "title": "Intel Challenge",
        "neuralNetwork": "RESNET_50",
        "classification": "buildings,forest,glacier,mountain,sea,street",
        "batchSize": 32,
        "epochs": 20,
        "modelName": "intelModel",
        "modelOutputDir": "/Users/zeguru/Download/MachineLearning/Models/intelModelDir",
        "trainingDataset":"/Users/zeguru/Pictures/MachineLearning/Datasets/IntelChallenge",
        "imageHeight": 150,
        "imageWidth": 150,
        "numberOfChannels": 3
        }'
    ```

## Go change the world
