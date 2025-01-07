plugins {
    id("application")
    id("checkstyle")
    id("jacoco")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass = "hexlet.code.App"
}

dependencies {

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    implementation("io.javalin:javalin-bundle:6.4.0")
    implementation("io.javalin:javalin-rendering:6.1.6")
    implementation("gg.jte:jte:3.1.15")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.2.220")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.konghq:unirest-java-core:4.4.5")
    implementation("com.konghq:unirest-java-bom:4.4.5")
    implementation("org.jsoup:jsoup:1.18.3")

    testCompileOnly("org.projectlombok:lombok:1.18.36")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.slf4j:slf4j-simple:2.0.16")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}