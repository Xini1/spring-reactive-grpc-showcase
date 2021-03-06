import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    alias(libs.plugins.protobuf)
    idea
    `java-library`
    `java-test-fixtures`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    compileOnly(libs.spring.mongodb)
    api(libs.grpc)
    implementation(libs.annotation.api)
}

protobuf {
    protoc {
        artifact = libs.protoc.compiler.get().toString()
    }
    plugins {
        id("grpc") {
            artifact = libs.protoc.plugin.get().toString()
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
            }
        }
    }
}