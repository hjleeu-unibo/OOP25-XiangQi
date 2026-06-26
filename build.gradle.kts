plugins { java }

repositories { // Where to search for dependencies
    mavenCentral()
}

dependencies {
    // JUnit API and testing engine
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Mockito
    testImplementation("org.mockito:mockito-core:5.15.2")
    testRuntimeOnly("org.mockito:mockito-junit-jupiter:5.15.2")
}