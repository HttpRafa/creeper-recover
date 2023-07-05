plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation("org.bstats:bstats-bukkit:" + findProperty("bstats_version"))

    compileOnly("org.spigotmc:spigot-api:" + findProperty("spigot_version"))

    compileOnly("org.projectlombok:lombok:" + findProperty("lombok_version"))
    annotationProcessor("org.projectlombok:lombok:" + findProperty("lombok_version"))
}

tasks.shadowJar {
    relocate("org.bstats", "de.rafael.plugins.creeper.recover.utils")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}