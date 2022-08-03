# Classif-AI [pronounced *classify*]

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

    ```
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
```

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
    "modelDirectory": "/Users/zeguru/Downloads/MachineLearning/Models/intelModelDir",
    "imagePath":"/Users/zeguru/Downloads/MachineLearning/Datasets/IntelChallenge/testing/calm_ocean.jpeg",
    "imageHeight": 150,
    "imageWidth": 150,
    "numberOfChannels": 3
    }'
```

Response:

```
    class: "sea", probability: 0.99998
```


## Go change the world