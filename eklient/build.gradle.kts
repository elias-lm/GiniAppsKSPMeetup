plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {

    api(project(":eklientAnnotations"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    api("com.squareup.okhttp3:okhttp:4.10.0")

}