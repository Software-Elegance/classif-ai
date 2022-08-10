plugins {
	id ("io.freefair.lombok") version "6.4.3"
}

dependencies {
	implementation("org.slf4j:slf4j-api:1.7.36")
	implementation("org.springframework:spring-core")
	implementation("org.springframework:spring-context")
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.261")
	implementation("com.amazonaws:aws-java-sdk-sts:1.12.239")
	implementation("jakarta.validation:jakarta.validation-api:2.0.2")
	implementation("org.jetbrains:annotations:20.1.0")
}

repositories {
	mavenCentral()
}

tasks.withType<Test> {
	useJUnitPlatform()
}
