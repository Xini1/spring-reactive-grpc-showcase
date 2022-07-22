plugins {
    java
}

dependencies {
    implementation(project("::common"))
    implementation(libs.spring.webflux)

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
}

tasks.test {
    useJUnitPlatform()
}