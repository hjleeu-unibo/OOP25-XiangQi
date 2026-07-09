plugins { 
    java
    application
    id("com.gradleup.shadow") version "9.5.1"
    id("org.danilopianini.gradle-java-qa") version "1.188.0"
}

application {
    mainClass.set("it.unibo.xiangqi.app.XiangqiApplication")
}

tasks.javadoc {
    options.memberLevel = JavadocMemberLevel.PUBLIC
}

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
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")

    compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.7")
}

tasks.test {
    useJUnitPlatform()
}
