plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":eklientAnnotations"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    api("com.google.devtools.ksp:symbol-processing-api:1.7.0-1.0.6")

//    implementation(kotlin("reflect", "1.7.0"))
    implementation("com.squareup:kotlinpoet-ksp:1.12.0")
    implementation("com.sealwu.jsontokotlin:library:3.6.1")

}