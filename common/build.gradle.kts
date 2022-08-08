
dependencies {
	implementation("org.slf4j:slf4j-api:1.7.36")
	implementation("org.springframework:spring-core")
	implementation("org.springframework:spring-context")
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.261")
	implementation("com.amazonaws:aws-java-sdk-sts:1.12.239")
}
repositories {
	mavenCentral()
}

tasks.withType<Test> {
	useJUnitPlatform()
}
