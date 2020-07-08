import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import groovy.util.ConfigObject
import groovy.util.ConfigSlurper
import org.gradle.jvm.tasks.Jar
import java.io.File
import java.util.Properties
import java.time.LocalDateTime

buildscript {
    repositories {
        maven("https://files.minecraftforge.net/maven")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    scala
    //We apply these to get pretty build script
    java
    idea
    maven
    signing
    id("com.github.johnrengelman.shadow").version("2.0.4")
    id("net.minecraftforge.gradle").version("3.0.179")
}

val scaladoc: ScalaDoc by tasks
val compileJava: JavaCompile by tasks
val compileScala: ScalaCompile by tasks
val jar: Jar by tasks
val shadowJar: ShadowJar by tasks

val config = parseConfig(file("build.properties"))

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

compileJava.options.encoding = "UTF-8"
compileScala.scalaCompileOptions.additionalParameters = listOf("-Xexperimental")
scaladoc.scalaDocOptions.additionalParameters = listOf("-Xexperimental")

version = "${config["mc_version"]}-${config["version"]}"
group = "net.katsstuff.teamnightclipse"
base.archivesBaseName = "danmakucore"

sourceSets["main"].apply {
    java {
        setSrcDirs(listOf<File>())
    }
    withConvention(ScalaSourceSet::class) {
        scala {
            srcDir("src/main/java")
        }
    }
}

minecraft {
    mappings("snapshot", "20171003-1.12")

    accessTransformer(file("src/main/resources/danmakucore_at.cfg"))

    runs {
        create("client") {
            workingDirectory(if (file("../run1.12").exists()) "../run1.12" else "run")

            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")
        }

        create("server") {
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")
        }
    }
}

repositories {
    maven {
        name = "TeamNightclipse Bintray"
        setUrl("https://dl.bintray.com/team-nightclipse/maven/")
    }
    maven {
        name = "ilexiconn"
        setUrl("https://maven.mcmoddev.com")
    }
    jcenter()
    mavenLocal()
}

dependencies {
    minecraft("net.minecraftforge:forge:${config["mc_version"]}-${config["forge_version"]}")

    compileOnly("net.katsstuff.teamnightclipse:mirror:1.12.2-0.6.0-SNAPSHOT")
    runtimeOnly("net.katsstuff.teamnightclipse:mirror:1.12.2-0.6.0-SNAPSHOT:dev")
    compile("org.scala-lang:scala-library:2.11.4") //Gets ourself a better compiler
}

shadowJar.apply {
    classifier = "shaded"
    configurations = listOf()

    relocate("shapeless", "net.katsstuff.teamnightclipse.mirror.shade.shapeless")
}


jar.apply {
    exclude("**/*.psd")
    manifest {
        attributes(
                mapOf(
                        "FMLAT" to "danmakucore_at.cfg",
                        "Specification-Title" to "DanmakuCore",
                        "Specification-Vendor" to "TeamNightclipse",
                        "Specification-Version" to "1", // We are version 1 of ourselves
                        "Implementation-Title" to project.name,
                        "Implementation-Version" to version,
                        "Implementation-Vendor" to "TeamNightclipse",
                        "Implementation-Timestamp" to LocalDateTime.now().toString()
                )
        )
    }
}

tasks.withType<ProcessResources> {
    inputs.property("version", project.version)
    inputs.property("mcversion", config["mc_version"])

    from(sourceSets["main"].resources.srcDirs) {
        include("mcmod.info")
        expand(mapOf("version" to project.version, "mcversion" to config["mc_version"]))
    }

    from(sourceSets["main"].resources.srcDirs) {
        exclude("mcmod.info")
    }
}

fun parseConfig(config: File): ConfigObject {
    val prop = Properties()
    prop.load(config.reader())
    return ConfigSlurper().parse(prop)
}

idea.module.inheritOutputDirs = true

reobf {
    create("shadowJar") {
        mappings = tasks.getByName<net.minecraftforge.gradle.mcp.task.GenerateSRG>("createMcpToSrg").output
    }
}

tasks["build"].dependsOn(shadowJar)

val deobfJar by tasks.creating(Jar::class) {
    classifier = "dev"
    from(sourceSets["main"].output)
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    classifier = "javadoc"
    dependsOn(scaladoc)
    from(scaladoc.destinationDir)
}

artifacts {
    add("archives", shadowJar)
    add("archives", sourcesJar)
    add("archives", javadocJar)
    add("archives", deobfJar)
}

signing {
    useGpgCmd()
    sign(configurations.archives)
}

tasks {
    "uploadArchives"(Upload::class) {
        repositories {
            withConvention(MavenRepositoryHandlerConvention::class) {
                mavenDeployer {
                    beforeDeployment {
                        signing.signPom(this)
                    }

                    withGroovyBuilder {
                        val releasesUri = """https://api.bintray.com/maven/team-nightclipse/maven/DanmakuCore/;publish=1"""
                        "repository"("url" to uri(releasesUri)) {
                            "authentication"("userName" to properties["bintray.user"], "password" to properties["bintray.apikey"])
                        }
                        /*
                        "snapshotRepository"("url" to uri("TODO")) {
                            "authentication"("userName" to properties["bintray.user"], "password" to properties["bintray.apikey"])
                        }
                        */
                    }

                    pom.project {
                        withGroovyBuilder {
                            "description"("A library for Minecraft for creating Danmaku")

                            "licenses" {
                                "license" {
                                    "name"("GNU General Lesser Public License (LGPL) version 3.0")
                                    "url"("http://www.gnu.org/licenses/lgpl.txt")
                                    "distribution"("repo")
                                }
                            }

                            "scm" {
                                "url"("https://github.com/TeamNightclipse/DanmakuCore")
                                "connection"("scm:git:github.com/TeamNightclipse/DanmakuCore")
                                "developerConnection"("scm:git:github.com/TeamNightclipse/DanmakuCore")
                            }

                            "issueManagement" {
                                "system"("github")
                                "url"("https://github.com/TeamNightclipse/DanmakuCore/issues")
                            }

                            "developers" {
                                "developer" {
                                    "id"("Nikolai Frid")
                                    "email"("katrix97@hotmail.com")
                                    "url"("http://katsstuff.net/")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
