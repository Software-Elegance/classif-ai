# Stage 2
FROM nginx

COPY ./nginx-selfsigned.crt /etc/nginx/ssl/nginx-selfsigned.crt
COPY ./nginx-selfsigned.key /etc/nginx/ssl/nginx-selfsigned.key

#RUN echo yes | keytool -importcert -alias startssl -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -file smart.crt

# copy the nginx configurations to the docker image
COPY ./nginx.build.conf /etc/nginx/nginx.conf



#Aftermath
#========================================

#cd classif-ai
#sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout nginx/nginx-selfsigned.key -out nginx/nginx-selfsigned.crt
#18.221.25.21

#Dockerize
#cd nginx
#docker build -t zeguru/classifai-nginx:0.7 .

#Push
#docker login
#docker push zeguru/classifai-nginx:0.7