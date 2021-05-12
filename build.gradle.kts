plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    id("application")
    id("maven-publish")
}

group = "kr.heartpattern"
version = "1.0.0-SNAPSHOT"

repositories {
    maven("https://repo.heartpattern.io/repository/maven-public/")
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.ow2.asm:asm:9.1")
    compile("com.github.ajalt:clikt:2.1.0")
    compile("me.tongfei:progressbar:0.8.1")
}

application {
    mainClassName = "kr.heartpattern.exhibitionism.AppKt"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "Exhibitionism"
            from(components["java"])
        }
    }
    repositories {
        maven(
            if (version.toString().endsWith("SNAPSHOT"))
                "https://maven.heartpattern.kr/repository/maven-public-snapshots/"
            else
                "https://maven.heartpattern.kr/repository/maven-public-releases/"
        ) {
            credentials {
                username = project.properties["nexusUser"] as String?
                password = project.properties["nexusPassword"] as String?
            }
        }
    }
}
