import java.time.Instant

plugins {
    scala
    java
    idea
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"
}

val modId = "danmakucore"
val minecraftVersion = "1.20.1"
val forgeVersion = "47.3.0"
val mixinVersion = "0.8.5"

base {
    archivesName.set("$modId-${project.version}")
    group = "net.katsstuff"
    version = "0.9"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "Azure-SLP"
        url = uri("https://pkgs.dev.azure.com/Kotori316/minecraft/_packaging/mods/maven/v1")
        content {
            includeGroup("com.kotori316")
            includeGroup("org.typelevel")
        }
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    minecraft("net.minecraftforge", "forge", version = "$minecraftVersion-$forgeVersion")
    annotationProcessor("org.spongepowered:mixin:${mixinVersion}:processor")

    compileOnly(group = "org.scala-lang", name = "scala-library", version = "2.13.11")
    compileOnly(group = "org.scala-lang", name = "scala3-library_3", version = "3.3.0")
    compileOnly(group = "org.typelevel", name = "cats-core_3", version = "2.9.2-kotori")
    runtimeOnly(group = "com.kotori316", name = "ScalableCatsForce".lowercase(), version = "3.3.0-build-2", classifier = "with-library").setTransitive(false)

    // JUnit
    // There is not a testImplementation-like configuration, AFAIK, that is available at minecraft runtime, so we use minecraftLibrary
    //minecraftLibrary("org.junit.jupiter:junit-jupiter-api:5.9.2")
    //minecraftLibrary("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

minecraft {
    mappings("parchment", "2023.09.03-${minecraftVersion}")
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
    copyIdeResources.set(true)
    generateRunFolders.set(true)
    enableIdeaPrepareRuns.set(false)

    runs {
        all {
            args("-mixin.config=$modId.mixins.json")

            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modId)

            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")

            jvmArgs("-ea", "-Xmx4G", "-Xms4G")

            ideaModule("${project.name}.test")

            mods.create(modId) {
                source(sourceSets.main.get())
                //source(sourceSets.test.get())
            }
        }

        register("client") {
            workingDirectory(project.file("run/client"))
        }

        register("server") {
            workingDirectory(project.file("run/server"))
            arg("--nogui")
        }

        register("gameTestServer") {
            workingDirectory(project.file("run/gametest"))
            arg("--nogui")
        }

        register("data") {
            workingDirectory(project.file("run/data"))
            args("--mod", modId, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
        }
    }
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources/")
    }
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("danmakucore.mixins.json")
}

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.WARN
        manifest {
            attributes["Specification-Title"] = modId
            attributes["Specification-Vendor"] = "Katrix"
            attributes["Specification-Version"] = "1" // We are version 1 of ourselves
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Vendor"] = "Katrix"
            attributes["Implementation-Version"] = project.version
            attributes["Implementation-Timestamp"] = Instant.now().toString()
        }

        finalizedBy("reobfJar")
    }
}

tasks.withType(Copy::class).all {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.register<Exec>("run + RenderDoc") {
    val javaExecTask = tasks.withType<JavaExec>().named("runClient").get()
    val javaHome = javaExecTask.javaLauncher.get().metadata.installationPath.asFile.absolutePath

    commandLine = listOf(
        "C:\\Program Files\\RenderDoc\\renderdoccmd.exe",
        "capture",
        "--opt-hook-children",
        "--wait-for-exit",
        "--working-dir",
        ".",
        "$javaHome/bin/java.exe",
        "-Xmx64m",
        "-Xms64m",
        "-Dorg.gradle.appname=gradlew",
        "-Dorg.gradle.java.home=$javaHome",
        "-classpath",
        "gradle/wrapper/gradle-wrapper.jar",
        "org.gradle.wrapper.GradleWrapperMain",
        "runClient"
    )
}
