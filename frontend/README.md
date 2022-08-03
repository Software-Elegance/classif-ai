# The Web App
To run the web application directly execute:

    gradlew :frontend:bootRun

To deploy to PCF, edit `manifest.yml` and supply the access key and secret key that allow access to the desired S3 bucket.

The bucket name is set to `djl-demo` in application.properties file. 

The bucket name must the same as the one used by the `djl-spring-boot-app` project, which acts as the backend. 
