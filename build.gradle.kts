import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    id("maven-publish")
}

group = "com.github.dkurata38"
version = "0.0.2"

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.5.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dkurata38/maven")
            credentials {
                username = project.findProperty("gpr.user").toString()  ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.token").toString() ?: System.getenv("PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("") {
            from(components["java"])
        }
    }
}
