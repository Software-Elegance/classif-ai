
import java.io.*

plugins {
    java
    id ("jacoco")
    id("org.springframework.boot") version "2.6.4" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id ("com.google.cloud.tools.jib") version "3.1.1" apply false
    kotlin("jvm") version "1.5.20" apply false
    kotlin("plugin.spring") version "1.5.20" apply false
    id ("org.sonarqube") version "3.4.0.2513"
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
}

//tasks.test {
//    finalizedBy("jacocoTestReport")
//    useJUnitPlatform()
//}
//
//tasks.jacocoTestReport {
//    classDirectories.setFrom(
//        files(classDirectories.files.map {
//            fileTree(it) {
//                exclude(
//                    "com/example/integration/**",
//                    "com/example/application/*Ext*"
//                )
//            }
//        })
//    )
//}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {}
    finalizedBy("jacocoTestReport")
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.jacocoTestReport {
    reports {
        html.isEnabled = true
        xml.isEnabled = false
        csv.isEnabled = false
        html.destination = file("$buildDir/jacocoHtml")
    }

    finalizedBy("jacocoTestCoverageVerification")
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.30".toBigDecimal()
            }
        }

        rule {
            enabled = true
            element = "CLASS"
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
            }
            excludes = listOf(
                    "*.test.*",
                    "*.Kotlin*",
                    "*.config/**"
            )
        }
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage"

    dependsOn(":test",
        ":jacocoTestReport",
        ":jacocoTestCoverageVerification")

    tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
    tasks["jacocoTestCoverageVerification"].mustRunAfter(tasks["jacocoTestReport"])
}


sonarqube {
    properties {

        property("sonar.projectKey", "Software-Elegance_classif-ai")
        property("sonar.organization", "software-elegance")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.links.issue", "https://github.com/Software-Elegance/classif-ai/issues")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/jacoco/test/jacocoTestReport.xml")
        property("sonar.exclusions", "**/build/**/*")
        property("sonar.exclusions", "**/config/**/*")
        property("sonar.exclusions", "**/models/**/*")
        property("sonar.exclusions", "**/ClassifySpringBootApplication.java")
    }
}

repositories {
    mavenCentral()
}

allprojects {
    ext.set("commitHash", getCommitHash())
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin  = "java")
    group = "com.aws.samples"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    java.sourceCompatibility=JavaVersion.VERSION_11
    java.targetCompatibility=JavaVersion.VERSION_11

    dependencyManagement {
        imports {
            mavenBom("org.springframework:spring-framework-bom:5.3.16")
        }
    }
}

fun getCommitHash() : String  {
    return Runtime
            .getRuntime()
            .exec("git rev-parse --short HEAD")
            .let<Process, String> { process ->
                process.waitFor()
                val output : String  = process.inputStream.use {
                    it.bufferedReader().use(BufferedReader::readText)
                }
                process.destroy()
                output.trim()
            }
}
