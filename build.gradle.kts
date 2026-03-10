plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.12.0"
}

group = "com.x366"
version = "1.1.14"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2023.1")
        pluginVerifier()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "231"
            untilBuild = "999.*"
        }
    }
    buildSearchableOptions = false
}
