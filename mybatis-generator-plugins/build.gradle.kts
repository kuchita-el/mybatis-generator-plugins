import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    `maven-publish`
}

java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
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

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dkurata38/maven")
            credentials {
                username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")).toString()
                password = (project.findProperty("gpr.token") ?: System.getenv("PASSWORD")).toString()
            }
        }
    }
    publications {
        create<MavenPublication>("GithubPackageRegistry") {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}
