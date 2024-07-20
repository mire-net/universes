plugins {
    kotlin("jvm") version "2.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    `maven-publish`
}

group = "net.radstevee"
version = "0.3.4"

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("org.incendo:cloud-paper:2.0.0-beta.7")
    implementation("org.incendo:cloud-kotlin-extensions:2.0.0-rc.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.18.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.18.0")

    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}

publishing {
    repositories {
        maven {
            name = "radPublic"
            url = uri("https://maven.radsteve.net/public")

            credentials {
                username = System.getenv("RAD_MAVEN_USER")
                password = System.getenv("RAD_MAVEN_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
