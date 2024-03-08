plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

repositories {
    mavenCentral()
}

apply(plugin = "com.vanniktech.maven.publish")

dependencies {
    // Kotlin Dependencies
    implementation(Dependencies.Kotlin.KOTLIN)
    implementation(Dependencies.Kotlin.KSP)
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}

publishing {
    repositories {
        maven {
            val VERSION_NAME: String by project
            val nexusPersonalRepositorySnapshotUrl: String by project
            val nexusPersonalRepositoryReleaseUrl: String by project
            val nexusPersonalRepositoryUrl = if (VERSION_NAME.endsWith("SNAPSHOT")) {
                nexusPersonalRepositorySnapshotUrl
            } else {
                nexusPersonalRepositoryReleaseUrl
            }
            name = "nexusPersonal"
            url = uri(nexusPersonalRepositoryUrl)
            // `nexusPersonalUsername` and `nexusPersonalPassword` should be specified as Gradle properties
            credentials(PasswordCredentials::class)
        }
    }
}
