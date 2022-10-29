import org.gradle.configurationcache.extensions.capitalized

plugins {
    kotlin("jvm") version "1.7.20"
    application
    id("maven-publish")
}

group = "kr.heartpattern"
version = "1.0.0-SNAPSHOT"

repositories {
    maven("https://repo.heartpattern.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.ow2.asm:asm:9.4")
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation("me.tongfei:progressbar:0.9.5")
}

application {
    mainClass.set("kr.heartpattern.exhibitionism.AppKt")
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
val fatJar = task("fatJar", type = org.gradle.jvm.tasks.Jar::class) {
    archiveBaseName.set("${project.name}-fat")
    manifest {
        attributes["Implementation-Title"] = project.name.capitalized()
        attributes["Implementation-Version"] = project.version
        attributes["Main-Class"] = application.mainClass
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}
