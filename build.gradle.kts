plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}

group = "com.github.HuberHaYu.Xiaomi-SuperIsland-Playground"
version = "1.0.0"

android {
    namespace = "com.lab.island.sdk"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 31
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.hidden.api.bypass)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group.toString()
            artifactId = "island-super-island"
            version = project.version.toString()
            afterEvaluate { from(components["release"]) }
            pom {
                name.set("Island Super Island SDK")
                description.set("Android library for publishing Xiaomi HyperOS Super Island notifications")
                url.set("https://github.com/HuberHaYu/Xiaomi-SuperIsland-Playground")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                scm {
                    url.set("https://github.com/HuberHaYu/Xiaomi-SuperIsland-Playground")
                    connection.set("scm:git:https://github.com/HuberHaYu/Xiaomi-SuperIsland-Playground.git")
                    developerConnection.set("scm:git:ssh://git@github.com/HuberHaYu/Xiaomi-SuperIsland-Playground.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "localBuild"
            url = layout.buildDirectory.dir("repo").get().asFile.toURI()
        }
    }
}
