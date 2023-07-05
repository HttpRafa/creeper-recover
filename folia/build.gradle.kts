plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public")
}

dependencies {
    implementation("org.bstats:bstats-bukkit:" + findProperty("bstats_version"))

    compileOnly("dev.folia:folia-api:" + findProperty("folia_version"))

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