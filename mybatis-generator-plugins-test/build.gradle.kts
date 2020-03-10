import com.thinkimi.gradle.MybatisGeneratorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.thinkimi.gradle.MybatisGenerator") version "2.1.2"
    kotlin("jvm") version "1.3.61"
}

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/dkurata38/maven")
        credentials {
            username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")).toString()
            password = (project.findProperty("gpr.token") ?: System.getenv("PASSWORD")).toString()
        }
    }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.1")
    implementation("org.mybatis.dynamic-sql:mybatis-dynamic-sql:1.1.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
}

tasks.withType<MybatisGeneratorTask> {
    verbose = true
    val configFileName = "generatorConfig.xml"
    configFile = sourceSets.getByName("main").resources.find { (it.name == configFileName) }
    targetDir = projectDir.toPath().toString()
    overwrite = true

    dependencies {
        mybatisGenerator("org.hsqldb:hsqldb:2.4.1")
//        mybatisGenerator("org.mybatis.generator:mybatis-generator-core:1.4.0")
        mybatisGenerator("com.github.dkurata38:mybatis-generator-plugins:0.1.1")
    }
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
