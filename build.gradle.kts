plugins { 
    java 
    application
}

application{
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
}
