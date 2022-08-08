
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
    // JaCoCo 버전
    toolVersion = "0.8.5"

//  테스트결과 리포트를 저장할 경로 변경
//  default는 "${project.reporting.baseDir}/jacoco"
//  reportsDir = file("$buildDir/customJacocoReportDir")
}

tasks.jacocoTestReport {
    reports {
        // 원하는 리포트를 켜고 끌 수 있다.
        html.isEnabled = true
        xml.isEnabled = false
        csv.isEnabled = false

//      각 리포트 타입 마다 리포트 저장 경로를 설정할 수 있다.
     html.destination = file("$buildDir/jacocoHtml")
//      xml.destination = file("$buildDir/jacoco.xml")
    }

    finalizedBy("jacocoTestCoverageVerification")
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            // 'element'가 없으면 프로젝트의 전체 파일을 합친 값을 기준으로 한다.
            limit {
                // 'counter'를 지정하지 않으면 default는 'INSTRUCTION'
                // 'value'를 지정하지 않으면 default는 'COVEREDRATIO'
                minimum = "0.30".toBigDecimal()
            }
        }

        rule {
            // 룰을 간단히 켜고 끌 수 있다.
            enabled = true

            // 룰을 체크할 단위는 클래스 단위
            element = "CLASS"

            // 브랜치 커버리지를 최소한 90% 만족시켜야 한다.
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }

            // 라인 커버리지를 최소한 80% 만족시켜야 한다.
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }

            // 빈 줄을 제외한 코드의 라인수를 최대 200라인으로 제한한다.
            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
//              maximum = "8".toBigDecimal()
            }

            // 커버리지 체크를 제외할 클래스들
            excludes = listOf(
//                    "*.test.*",
//                    "*.Kotlin*"
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
