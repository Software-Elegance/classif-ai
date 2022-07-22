# ROADMAP

# PROJECT GOAL

A plug and play microservice for image classification using Deep Learning. 
Avail both a REST API and a web front end, eventually a maven library

## TODO

We will use [`eksctl` tool](https://eksctl.io/introduction/#installation) to provision an EKS cluster for 
demonstration purposes.
You also need to install [kubectl](https://docs.aws.amazon.com/eks/latest/userguide/install-kubectl.html) – A
command line tool for working with Kubernetes clusters.

Before you start, make sure you have AWS CLI installed and proper credentials to access your AWS account.
Steps 2, 4 and 5 are only needed if you would like to protect access to your S3 bucket properly (it is NOT a good 
idea to open up an bucket with read/write access to the world). 

1. API
    - Training: Pass the dataset folder, type (S3/file/http), modelName, and epocs
    - Inference: Pass image path (S3/file/http) and modelName
    - Response: json with probabilities

2. Web
    - Training progress
    - Reactive

3. Documentation
    - Swagger
    - Postman Collection

4. Pretrained Models
   for outgoing processed images with detected objects. 
   The bucket name must be unique so in this example we add a suffix for your AWS account number which should 
   replace the placeholder <YOUR_AWS_ACCOUNT>.

5. Transfer Learning

    - Create a copy of the `docs/AccesDjlBucketPolicyTemplate.json` as `AccessDjlBucketPolicy.json` **replacing 
    <YOUR_BUCKET_NAME> with your actual bucket name**. 
    
    
   
5. Docker / Compose 
   
   **Note:** Adjust the namespace if you plan to deploy to a namespace different from default.
   
    ```bash
    eksctl create iamserviceaccount \
        --name djl-backend-account \
        --namespace default \
        --cluster djl-demo \
        --attach-policy-arn <ARN_OF_POLICY_OBTAINED_IN_STEP_4> \
        --approve \
        --override-existing-serviceaccounts
    ```

6. In this example we will use Amazon ECR private repository to push images. The repository will need to be created 
   upfront. 

    ```bash
    aws ecr create-repository --repository-name djl-app
    ```

7. Build and push the container image for the API app to an accessible container (Docker) registry.
   
    - You must be properly authenticated to push images to Amazon ECR. For more info see this
      [doc](https://docs.aws.amazon.com/AmazonECR/latest/userguide/docker-push-ecr-image.html).
      
   ```bash
   aws ecr get-login-password --region <YOUR_REGION> | docker login --username AWS --password-stdin <YOUR_AWS_ACCOUNT>.dkr.ecr.<YOUR_REGION>.amazonaws.com
   ```
   
    - Assuming you forked this repository, modify the jib section of the `djl-app/build.gradle.kts` to 
    reflect your settings (replace the placeholders):

    ```kotlin
    jib {
        from.image = "adoptopenjdk/openjdk13:debian"
        to.image = "<YOUR_AWS_ACCOUNT>.dkr.ecr.<YOUR_REGION>.amazonaws.com/djl-app"
        to.tags = versionTags
    } 
    ```

    - From the root directory:

    ```bash
    ./gradlew djl-app:bootjar
    ./gradlew djl-app:jib
    ```

    The above will push the image to the Amazon ECR container registry and output the image/tag pair that you will need 
    for the subsequent steps.


8. Deploy the application:
    
    - Modify the provided `docs/deployment-template.yaml` and specify your image:tag produced in step 7 

    - Run the command below to deploy the application and create a load balancer to access it over HTTP (don't use this 
   approach in production without TLS in place):
   
    ```bash
    # assuming you saved the modified deployment-template.yaml in the current directory as deployment.yaml
    kubectl apply -f deployment.yaml
    ``` 
   
    You can set the `-n YOUR_NAMESPACE` flag on the command if you created the service account in a different namespace.

    **Note:** this deployment is leveraging the service account `djl-backend-account` created in the previous steps. If you 
    don't need a service account (e.g. in case you modified the app to read from local storage), then remove the  
   service account association in the template (`serviceAccountName: djl-backend-account`).


## API
[Rest API](djl-web/README.md)

## Frontend Web Application
[Web Application](djl-web/README.md)

## License
This project is licensed under the Apache-2.0 License.


