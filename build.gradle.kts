plugins {
    java
    signing
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.cfunicorn.reportsgui"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation("org.jetbrains:annotations:23.1.0")
    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")
    implementation("commons-io:commons-io:2.11.0")
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "name" to rootProject.name, "version" to rootProject.version
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }
}