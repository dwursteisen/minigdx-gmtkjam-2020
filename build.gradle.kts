plugins {
    kotlin("multiplatform") version "1.3.70"
    id("com.github.dwursteisen.gltf") version "1.0.0-alpha8"
}

group = "com.github.dwursteisen.gmtkjam"
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
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math:1.0.0-alpha18")
                implementation("com.github.dwursteisen.minigdx:minigdx:1.1-SNAPSHOT")
                implementation("com.github.dwursteisen.gltf:gltf-api:1.0.0-alpha8")
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


gltfPlugin {
    create("assetsProtobuf") {
        this.gltfDirectory.set(project.projectDir.resolve("src/assets/"))
        this.target.set(project.projectDir.resolve("src/commonMain/resources/"))
        this.format.set(com.github.dwursteisen.gltf.Format.PROTOBUF)
    }
}

project.tasks.create("runJs").apply {
    group = "minigdx"
    dependsOn("jsBrowserDevelopmentRun")
}

// -- convenient task to create the documentation.
project.tasks.create("dist").apply {
    group = "minigdx"
    // package the application
    dependsOn("jsBrowserProductionWebpack")
}
