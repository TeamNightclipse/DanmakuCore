import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import groovy.util.ConfigObject
import groovy.util.ConfigSlurper
import net.minecraftforge.gradle.user.IReobfuscator
import net.minecraftforge.gradle.user.ReobfMappingType
import net.minecraftforge.gradle.user.ReobfTaskFactory
import net.minecraftforge.gradle.user.patcherUser.forge.ForgeExtension
import org.gradle.api.internal.HasConvention
import org.gradle.jvm.tasks.Jar
import java.util.Properties

buildscript {
    repositories {
        jcenter()
        maven {
            name = "forge"
            setUrl("http://files.minecraftforge.net/maven")
        }
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    }
}

apply {
    plugin("net.minecraftforge.gradle.forge")
}

plugins {
    scala
    //We apply these to get pretty build script
    java
    idea
    maven
    signing
    id("com.github.johnrengelman.shadow").version("2.0.4")
}

val scaladoc: ScalaDoc by tasks
val compileJava: JavaCompile by tasks
val compileScala: ScalaCompile by tasks
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

java.sourceSets {
    "main" {
        //Join compilation
        java {
            setSrcDirs(listOf<File>())
        }
        withConvention(ScalaSourceSet::class) {
            scala {
                srcDir("src/main/java")
            }
        }
    }
}

val minecraft = the<ForgeExtension>()
minecraft.apply {
    version = "${config["mc_version"]}-${config["forge_version"]}"
    runDir = if (file("../run1.12").exists()) "../run1.12" else "run"
    mappings = "snapshot_20180810"
    // makeObfSourceJar = false // an Srg named sources jar is made by default. uncomment this to disable.

    replace("@VERSION@", project.version)
    replaceIn("LibMod.scala")
}

repositories {
    maven {
        name = "TeamNightclipse Bintray"
        setUrl("https://dl.bintray.com/team-nightclipse/maven/")
    }
}

dependencies {
    compile("net.katsstuff.teamnightclipse:mirror:1.12.2-0.3.0")
}

shadowJar.apply {
    classifier = "shaded"
    dependencies {
        exclude(dependency("com.chuusai:shapeless_2.11:2.3.3"))
        exclude(dependency("net.katsstuff.teamnightclipse:mirror:1.12.2-0.3.0"))
    }
    exclude("dummyThing")
    relocate("shapeless", "net.katsstuff.teamnightclipse.mirror.shade.shapeless")
}

tasks.withType<Jar> {
    exclude("**/*.psd")
}

tasks.withType<ProcessResources> {
    inputs.property("version", project.version)
    inputs.property("mcversion", minecraft.version)

    from(java.sourceSets["main"].resources.srcDirs) {
        include("mcmod.info")
        expand(mapOf("version" to project.version, "mcversion" to minecraft.version))
    }

    from(java.sourceSets["main"].resources.srcDirs) {
        exclude("mcmod.info")
    }
}

fun parseConfig(config: File): ConfigObject {
    val prop = Properties()
    prop.load(config.reader())
    return ConfigSlurper().parse(prop)
}

idea.module.inheritOutputDirs = true

val reobf: NamedDomainObjectContainer<IReobfuscator> by extensions

tasks["build"].dependsOn(shadowJar)

reobf {
    "shadowJar" {
        mappingType = ReobfMappingType.SEARGE
    }
}

tasks["reobfShadowJar"].mustRunAfter(shadowJar)
tasks["build"].dependsOn("reobfShadowJar")

val deobfJar by tasks.creating(Jar::class) {
    classifier = "dev"
    from(java.sourceSets["main"].output)
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
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
