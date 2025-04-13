plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.wahid.SpringInit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.1.5")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}
dependencies {
    implementation("com.google.cloud:google-cloud-aiplatform:3.35.0")
    intellij{
        plugins = listOf("com.intellij.gradle")
    }
    intellij{
        plugins = listOf("gradle")
    }

}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    register("generateCleanArchStructure") {
        val baseDir = projectDir.resolve("src")
        listOf(
            "domain/entities", "domain/usecases", "domain/repositories",
            "application", "adapters/controllers", "adapters/presenters",
            "infrastructure/persistence", "infrastructure/remote"
        )
            .forEach { dir ->
                val folder = baseDir.resolve(dir)
                if (!folder.exists()) {
                    folder.mkdirs()
                    println("Created folder: ${folder.absolutePath}")
                }
            }

    }


    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("241.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }


}
