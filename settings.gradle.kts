pluginManagement {
    repositories {
        jcenter()
        mavenCentral()
        gradlePluginPortal()
        maven("http://files.minecraftforge.net/maven")
    }
    resolutionStrategy {
        eachPlugin {
            if(requested.id.namespace == "net.minecraftforge") {
                useModule("net.minecraftforge.gradle:ForgeGradle:${requested.version}")
            }
        }
    }
}

