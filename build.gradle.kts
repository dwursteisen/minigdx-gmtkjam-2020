plugins {
    kotlin("multiplatform") version "1.3.70"
    id("com.github.dwursteisen.gltf") version "1.0.0-alpha7"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven(
        url = uri("https://dl.bintray.com/dwursteisen/minigdx")
    )
    jcenter()
    mavenCentral()
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    js {
        this.useCommonJs()
        this.browser {
            this.webpackTask {
                this.compilation.kotlinOptions {
                    this.sourceMap = true
                    this.sourceMapEmbedSources = "always"
                }
            }
        }
        this.nodejs
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.github.dwursteisen.minigdx:minigdx:1.0.0-alpha0")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
