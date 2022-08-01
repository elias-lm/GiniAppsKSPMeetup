plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":libAnnotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.0-1.0.6")
    implementation("com.squareup:kotlinpoet-ksp:1.12.0")
}