

plugins {
    id("com.google.osdetector") version "1.6.2"
    id ("com.google.cloud.tools.jib") apply true
    id("org.springframework.boot")
    id ("io.freefair.lombok") version "6.4.3"

}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

val osclassifier : String? by project
val inferredClassifier: String = osclassifier?: osdetector.classifier
val timestamp = System.currentTimeMillis()
val commitHash = ext.get("commitHash")
val versionTags = generateVersionTag()
val djlStarterVersion = 0.15

jib {
    from.image = "adoptopenjdk/openjdk13:debian"
    to.image = "929819487611.dkr.ecr.us-west1-1.amazonaws.com/service"
    to.tags = versionTags
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.2")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.9")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.9")

    // implementation("ai.djl.spring:djl-spring-boot-starter-autoconfigure:${djlStarterVersion}")
    implementation("ai.djl.spring:djl-spring-boot-starter-pytorch-auto:0.15")
    // implementation("ai.djl.spring:djl-spring-boot-starter-tensorflow-auto:0.15")
        
    implementation("ai.djl:api:0.17.0")
    implementation("ai.djl:model-zoo:0.17.0")
    implementation("ai.djl:basicdataset:0.17.0")

    compileOnly("commons-cli:commons-cli:1.5.0")

    implementation(project(":common"))
    implementation(project(":model"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")


    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    // See: https://github.com/awslabs/djl/blob/master/mxnet/mxnet-engine/README.md for MXNet library selection
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveClassifier.set(inferredClassifier)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

fun generateVersionTag() : Set<String> {
    project.logger.lifecycle("Version tag: ".plus(commitHash))
    return  setOf(version.toString().plus("-").plus(inferredClassifier).plus("-").plus(commitHash))
}

