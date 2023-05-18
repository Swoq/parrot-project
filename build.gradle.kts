plugins {
    id("idea")
    kotlin("jvm") version "1.8.20"
    id("org.springframework.boot") version "3.0.6"
}

repositories {
    mavenCentral()
}

idea {
    module {
        outputDir = file("build/idea/classes/main")
        testOutputDir = file("build/idea/classes/test")
        excludeDirs = setOf(file(".gradle"), file("build"))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation(kotlin("stdlib", "1.8.20"))
    implementation("info.picocli:picocli:4.7.3")
    // aws kotlin sdk
    implementation("aws.sdk.kotlin:cloudformation-jvm:0.21.3-beta")
    implementation("aws.sdk.kotlin:sso:0.19.2-beta")
    implementation("aws.sdk.kotlin:ssooidc:0.19.2-beta")
    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

    withType<org.springframework.boot.gradle.tasks.bundling.BootJar>().configureEach {
        launchScript()
    }

    jar {
        manifest.attributes["Main-Class"] = "com.swoqe.parrot.commands.ParrotCommandKt"
        from(configurations.runtimeClasspath.get().map(::zipTree))
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
