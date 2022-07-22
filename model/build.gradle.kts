
plugins {
    id ("io.freefair.lombok") version "6.4.3"
}

dependencies {
	implementation("jakarta.validation:jakarta.validation-api:2.0.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
