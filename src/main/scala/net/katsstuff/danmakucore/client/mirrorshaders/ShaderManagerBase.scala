package net.katsstuff.danmakucore.client.mirrorshaders

import java.io.IOException
import java.util
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.logging.LogUtils
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.{PreparableReloadListener, ResourceManager, SimplePreparableReloadListener}
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.{CrashReport, ReportedException}

/**
  * A class used for managing shaders. Similar to Minecraft,
  * [[net.minecraft.client.renderer.texture.TextureManager]].
  * @param resourceManager
  *   The [[ResourceManager]] to use for loading shaders.
  */
class ShaderManagerBase(resourceManager: ResourceManager) {
  private val LOGGER = LogUtils.getLogger

  private val shaderPrograms = mutable.Map.empty[ResourceLocation, MirrorShaderProgram]
  private val shaderProgramsInits = {
    type ShaderInit = MirrorShaderProgram => Unit
    new mutable.HashMap[ResourceLocation, mutable.Set[ShaderInit]] with mutable.MultiMap[ResourceLocation, ShaderInit]
  }
  private val shaderObjects = mutable.Map.empty[ResourceLocation, MirrorShader]

  /**
    * Gets a shader of a given type, or compiles it if it's missing.
    * @param location
    *   Where the shader is located.
    * @param shaderType
    *   The shader type.
    */
  def getShader(location: ResourceLocation, shaderType: ShaderType): MirrorShader =
    shaderObjects.getOrElseUpdate(location, compileShader(location, shaderType))

  /**
    * Compiles a shader, or logs warnings if it couldn't.
    * @param location
    *   Where the shader is located.
    * @param shaderType
    *   The shader type.
    * @return
    *   The compiled shader, or a dummy shader if an error occurred.
    */
  def compileShader(location: ResourceLocation, shaderType: ShaderType): MirrorShader = {
    try {
      MirrorShader.compile(location, shaderType, resourceManager)
    } catch {
      case e: IOException =>
        LOGGER.warn(s"Failed to load shader: $location", e)
        MirrorShader.missingShader(shaderType)
      case e: ShaderException =>
        LOGGER.warn(s"Failed to compile shader: $location", e)
        MirrorShader.missingShader(shaderType)
    }
  }

  /**
    * Creates a shader program. Java-API.
    * @param location
    *   The location where the shaders can be found.
    * @param shaderTypes
    *   The shader types to compile.
    * @param uniforms
    *   The uniforms to use.
    * @param strictUniforms
    *   If the uniforms should be strict (error on missing uniform).
    */
  def createProgram(
      location: ResourceLocation,
      shaderTypes: util.List[ShaderType],
      uniforms: util.Map[String, UniformBase[_ <: UniformType]],
      strictUniforms: Boolean
  ): MirrorShaderProgram = createProgram(location, shaderTypes.asScala.toSeq, uniforms.asScala.toMap, strictUniforms)

  /**
    * Creates a shader program. Scala-API.
    * @param location
    *   The location where the shaders can be found.
    * @param shaderTypes
    *   The shader types to compile.
    * @param uniforms
    *   The uniforms to use.
    * @param strictUniforms
    *   If the uniforms should be strict (error on missing uniform).
    */
  def createProgram(
      location: ResourceLocation,
      shaderTypes: Seq[ShaderType],
      uniforms: Map[String, UniformBase[_ <: UniformType]],
      strictUniforms: Boolean = true
  ): MirrorShaderProgram = {
    val shaders = shaderTypes.map(_ -> location).toMap
    createComplexProgram(shaders, uniforms, strictUniforms)
  }

  /**
    * Creates a complex shader program where the shader files are located in
    * different places. Java-API.
    * @param shaders
    *   A map from the shader type to their location.
    * @param uniforms
    *   The uniforms to use.
    * @param strictUniforms
    *   If the uniforms should be strict (error on missing uniform).
    */
  def createComplexProgram(
      shaders: util.Map[ShaderType, ResourceLocation],
      uniforms: util.Map[String, UniformBase[_ <: UniformType]],
      strictUniforms: Boolean
  ): MirrorShaderProgram = createComplexProgram(shaders.asScala.toMap, uniforms.asScala.toMap, strictUniforms)

  /**
    * Creates a complex shader program where the shader files are located in
    * different places. Scala-API.
    * @param shaders
    *   A map from the shader type to their location.
    * @param uniforms
    *   The uniforms to use.
    * @param strictUniforms
    *   If the uniforms should be strict (error on missing uniform).
    */
  def createComplexProgram(
      shaders: Map[ShaderType, ResourceLocation],
      uniforms: Map[String, UniformBase[_ <: UniformType]],
      strictUniforms: Boolean
  ): MirrorShaderProgram = {
    val newShaders = shaders.map { case (tpe, location) =>
      val shaderLocation = new ResourceLocation(s"$location.${tpe.extension}")
      val shader         = getShader(shaderLocation, tpe)
      shaderLocation -> shader
    }

    try {
      MirrorShaderProgram.create(newShaders, uniforms, strictUniforms)
    } catch {
      case e: ShaderException =>
        LOGGER.warn(s"Failed to create shader: $shaders", e)
        MirrorShaderProgram.missingShaderProgram(newShaders.values.toSeq, uniforms)
      case NonFatal(throwable) =>
        val crashReport         = CrashReport.forThrowable(throwable, "Registering shaders")
        val crashReportCategory = crashReport.addCategory("Resource locations being registered")
        crashReportCategory.setDetail("Resource locations", shaders.values)
        throw new ReportedException(crashReport)
    }
  }

  // noinspection SameParameterValue
  private def getOrElseAddProgram(
      key: ResourceLocation,
      shaderTypes: Seq[ShaderType],
      uniforms: Map[String, UniformBase[_ <: UniformType]],
      strictUniforms: Boolean
  ) = {
    shaderPrograms.getOrElseUpdate(
      key, {
        val program = createProgram(key, shaderTypes, uniforms, strictUniforms)
        shaderPrograms.put(key, program)
        program
      }
    )
  }

  /**
    * Initializes a shader for use. This should be called as part of the startup
    * loop, before a shader program is gotten. Java-API.
    * @param shaderLocation
    *   The shader location.
    * @param shaderTypes
    *   The shader types to compile.
    * @param uniforms
    *   The uniforms to use.
    * @param init
    *   The initialization function. This will also be called on reloads.
    */
  def initProgram(
      shaderLocation: ResourceLocation,
      shaderTypes: util.List[ShaderType],
      uniforms: util.Map[String, UniformBase[_ <: UniformType]],
      init: Consumer[MirrorShaderProgram],
      strictUniforms: Boolean
  ): Unit =
    initProgram(
      shaderLocation,
      shaderTypes.asScala.toSeq,
      uniforms.asScala.toMap,
      program => init.accept(program),
      strictUniforms
    )

  /**
    * Initializes a shader for use. This should be called as part of the startup
    * loop, before a shader program is gotten. Scala-API.
    * @param shaderLocation
    *   The shader location.
    * @param shaderTypes
    *   The shader types to compile.
    * @param uniforms
    *   The uniforms to use.
    * @param init
    *   The initialization function. This will also be called on reloads.
    */
  def initProgram(
      shaderLocation: ResourceLocation,
      shaderTypes: Seq[ShaderType],
      uniforms: Map[String, UniformBase[_ <: UniformType]],
      init: MirrorShaderProgram => Unit,
      strictUniforms: Boolean = true
  ): Unit = {
    shaderProgramsInits.addBinding(shaderLocation, init)
    init(getOrElseAddProgram(shaderLocation, shaderTypes, uniforms, strictUniforms))
  }

  /** Gets a initialized program. */
  def getProgram(shaderLocation: ResourceLocation): Option[MirrorShaderProgram] =
    shaderPrograms.get(shaderLocation)

  def reloader: PreparableReloadListener = {
    new SimplePreparableReloadListener[Map[ResourceLocation, (ShaderType, Seq[String])]] {
      override def prepare(
          resourceManager: ResourceManager,
          preparationsProfiler: ProfilerFiller
      ): Map[ResourceLocation, (ShaderType, Seq[String])] =
        shaderObjects.map(t => t._1 -> (t._2.shaderType, MirrorShader.parseShaderSource(t._1, resourceManager))).toMap

      override def apply(
          data: Map[ResourceLocation, (ShaderType, Seq[String])],
          resourceManager: ResourceManager,
          reloadProfiler: ProfilerFiller
      ): Unit = {
        shaderObjects.foreach(t => t._2.delete())
        shaderObjects.clear()

        shaderObjects ++= data.map { case (loc, (shaderType, shaderSource)) =>
          try {
            loc -> MirrorShader.compileFromShaderSource(loc, shaderSource, shaderType)
          } catch {
            case e: IOException =>
              LOGGER.warn(s"Failed to load shader: $loc", e)
              loc -> MirrorShader.missingShader(shaderType)
            case e: ShaderException =>
              LOGGER.warn(s"Failed to compile shader: $loc", e)
              loc -> MirrorShader.missingShader(shaderType)
          }
        }

        val res = for ((key, program) <- shaderPrograms) yield {
          program.delete()
          val newProgram = createProgram(
            key,
            program.shaders.map(_.shaderType),
            program.uniformMap.map { case (name, uniform) =>
              name -> UniformBase(uniform.tpe, uniform.count)
            },
            strictUniforms = program.strictUniforms
          )
          shaderProgramsInits.get(key).foreach(_.foreach(init => init(newProgram)))

          key -> newProgram
        }

        shaderPrograms.clear()
        shaderPrograms ++= res
      }
    }
  }

  def reloadBlocking(): Unit = {
    def doReloadBlocking(): Unit = {
      val newObjs = shaderObjects.map { case (loc, shader) =>
        shader.delete()
        loc -> compileShader(loc, shader.shaderType)
      }
      shaderObjects.clear()
      shaderObjects ++= newObjs

      val res = for ((key, program) <- shaderPrograms) yield {
        program.delete()
        val newProgram = createProgram(
          key,
          program.shaders.map(_.shaderType),
          program.uniformMap.map { case (name, uniform) =>
            name -> UniformBase(uniform.tpe, uniform.count)
          },
          strictUniforms = program.strictUniforms
        )
        shaderProgramsInits.get(key).foreach(_.foreach(init => init(newProgram)))

        key -> newProgram
      }

      shaderPrograms.clear()
      shaderPrograms ++= res
    }

    if (RenderSystem.isOnRenderThread) {
      doReloadBlocking()
    } else {
      val latch = new CountDownLatch(1)

      RenderSystem.recordRenderCall(() => {
        try {
          doReloadBlocking()
        } finally {
          latch.countDown()
        }
      })

      latch.await()
    }
  }
}

/** The default shader manager to use. */
object ShaderManager extends ShaderManagerBase(Minecraft.getInstance().getResourceManager)
