package net.katsstuff.danmakucore.client.form

import java.nio.FloatBuffer

import com.mojang.blaze3d.platform.{GlConst, GlStateManager}
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Matrix4f
import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.mirrormodels.MirrorMesh
import net.katsstuff.danmakucore.client.mirrorshaders._
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler
import net.katsstuff.danmakucore.math.Vector3
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.lwjgl.opengl.{GL15, GL20}
import org.lwjgl.system.MemoryUtil

class SphereClientForm extends ClientForm(DanmakuCore.resource("mirrorshaders/simple")) {
  private var sphereLow: MirrorMesh  = _
  private var sphereMid: MirrorMesh  = _
  private var sphereHigh: MirrorMesh = _
  private var attributeBuf: Int      = _

  private var floatBuffer: FloatBuffer = _

  private def sizePerDanmaku: Int =
    (4 * 4) + 0 // ModelView matrix
  // 3 +     // Main color
  // 3 +     // Secondary color
  // 4       // Extra properties

  private def initModel(): Unit = {
    RenderSystem.glBindBuffer(GlConst.GL_ARRAY_BUFFER, () => attributeBuf)

    val matrixIdx         = 2
    val mainColorIdx      = 6
    val secondaryColorIdx = 7
    val coreSizeIdx       = 8
    val coreHardnessIdx   = 9
    val edgeHardnessIdx   = 10
    val edgeGlowIdx       = 11

    val stride = sizePerDanmaku * 4

    def instancedAttrib(idx: Int, size: Int, tpe: Int, normalized: Boolean, offset: Int): Unit = {
      GL20.glEnableVertexAttribArray(idx)
      GL20.glVertexAttribPointer(idx, size, tpe, normalized, stride, floatBuffer.position(offset))
      // GL33.glVertexAttribDivisor(idx, 1)
    }

    /*
    (0 until 4).foreach { i =>
      instancedAttrib(matrixIdx + i, 4, GlConst.GL_FLOAT, normalized = false, i * 4)
    }

    instancedAttrib(mainColorIdx, 3, GlConst.GL_FLOAT, normalized = false, 16)
    instancedAttrib(secondaryColorIdx, 3, GlConst.GL_FLOAT, normalized = false, 19)

    instancedAttrib(coreSizeIdx, 1, GlConst.GL_FLOAT, normalized = false, 22)
    instancedAttrib(coreHardnessIdx, 1, GlConst.GL_FLOAT, normalized = false, 23)
    instancedAttrib(edgeHardnessIdx, 1, GlConst.GL_FLOAT, normalized = false, 24)
    instancedAttrib(edgeGlowIdx, 1, GlConst.GL_FLOAT, normalized = false, 25)
     */
  }

  override def init(): Unit = {
    floatBuffer = MemoryUtil.memCallocFloat(sizePerDanmaku * 512)
    RenderSystem.glGenBuffers(i => attributeBuf = i)
    RenderSystem.glBindBuffer(GlConst.GL_ARRAY_BUFFER, () => attributeBuf)
    GL15.glBufferData(GlConst.GL_ARRAY_BUFFER, floatBuffer.capacity() * 4, GL15.GL_STREAM_DRAW)

    sphereLow = MirrorMesh.make(
      DanmakuCore.resource("mirrormodels/sphere_low.obj"),
      posIdx = 0,
      normalsIdx = Some(1),
      initExtra = () => initModel()
    ) // low  = 8,4
    sphereMid = MirrorMesh.make(
      DanmakuCore.resource("mirrormodels/sphere_mid.obj"),
      posIdx = 0,
      normalsIdx = Some(1),
      initExtra = () => initModel()
    ) // mid  = 16,8
    sphereHigh = MirrorMesh.make(
      DanmakuCore.resource("mirrormodels/sphere_high.obj"),
      posIdx = 0,
      normalsIdx = Some(1),
      initExtra = () => initModel()
    ) // high = 32,16

    /*
    ShaderManager.initProgram(
      DanCoreShaderLocs.fancyShaderLoc,
      Seq(ShaderType.Vertex, ShaderType.Fragment),
      Map(
        //"modelViewMatrix"  -> UniformBase(UniformType.Mat4, 1),
        "projectionMatrix" -> UniformBase(UniformType.Mat4, 1)
      ),
      _ => {},
      strictUniforms = false
    )
     */

    ShaderManager.initProgram(
      DanmakuCore.resource("mirrorshaders/simple"),
      Seq(ShaderType.Vertex, ShaderType.Fragment),
      Map(
        "modelMatrix"      -> UniformBase(UniformType.Mat4, 1),
        "modelViewMatrix"  -> UniformBase(UniformType.Mat4, 1),
        "projectionMatrix" -> UniformBase(UniformType.Mat4, 1),
        "ticks"            -> UniformBase(UniformType.unFloat, 1),
        "cameraPos"        -> UniformBase(UniformType.vec3, 1),
        "mainColor"        -> UniformBase(UniformType.vec3, 1),
        "secondaryColor"   -> UniformBase(UniformType.vec3, 1),
        "coreSize"         -> UniformBase(UniformType.unFloat, 1),
        "coreHardness"     -> UniformBase(UniformType.unFloat, 1),
        "edgeHardness"     -> UniformBase(UniformType.unFloat, 1),
        "edgeGlow"         -> UniformBase(UniformType.unFloat, 1)
      ),
      _ => {},
      strictUniforms = false
    )
  }

  private def colorToVec(color: Int): Vector3 = {
    val cr = (color >> 16 & 255) / 255F
    val cg = (color >> 8 & 255) / 255F
    val cb = (color & 255) / 255F

    Vector3(cr, cg, cb)
  }

  private def writeColorToBuff(color: Int, buffer: FloatBuffer): Unit = {
    val cr = (color >> 16 & 255) / 255F
    val cg = (color >> 8 & 255) / 255F
    val cb = (color & 255) / 255F

    buffer.put(cr)
    buffer.put(cg)
    buffer.put(cb)
  }

  private def renderWithMesh(
      shader: MirrorShaderProgram,
      danmaku: Seq[TopDanmakuBehaviorsHandler.RenderData],
      model: MirrorMesh,
      modelViewMatrix: Matrix4f,
      projectionMatrix: Matrix4f
  ): Unit = {
    /*
    if (floatBuffer.capacity() < danmaku.length * sizePerDanmaku) {
      MemoryUtil.memFree(floatBuffer)
      floatBuffer = MemoryUtil.memAllocFloat(danmaku.length * sizePerDanmaku)
    }

    RenderSystem.glBindBuffer(GlConst.GL_ARRAY_BUFFER, () => attributeBuf)

    // Orphan buffer
    GL15.glBufferData(GlConst.GL_ARRAY_BUFFER, floatBuffer.capacity() * 4, GL15.GL_STREAM_DRAW)

    floatBuffer.clear()
     */

    danmaku.foreach { data =>
      def extraValues(s: String): Float =
        defaultAttributeValues(s).asValue(data.renderProperties.get(s))

      // println(data)
      /*
      (0 until model.vertexCount).foreach { _ =>
        data.modelMat.addToBuffer(floatBuffer)
        writeColorToBuff(data.mainColor, floatBuffer)
        writeColorToBuff(data.secondaryColor, floatBuffer)

        def extraValues(s: String): Float =
          defaultAttributeValues(s).asValue(data.renderProperties.get(s))

        floatBuffer.put(extraValues("coreSize"))
        floatBuffer.put(extraValues("coreHardness"))
        floatBuffer.put(extraValues("edgeHardness"))
        floatBuffer.put(extraValues("edgeGlow"))
      }
       */

      assert(modelViewMatrix != null)
      assert(projectionMatrix != null)

      val mainColor      = colorToVec(data.mainColor)
      val secondaryColor = colorToVec(data.secondaryColor)

      shader.getUniformS("mainColor").get.set(mainColor.x.toFloat, mainColor.y.toFloat, mainColor.z.toFloat)
      shader
        .getUniformS("secondaryColor")
        .get
        .set(secondaryColor.x.toFloat, secondaryColor.y.toFloat, secondaryColor.z.toFloat)
      shader.getUniformS("coreSize").get.set(extraValues("coreSize"))
      shader.getUniformS("coreHardness").get.set(extraValues("coreHardness"))
      shader.getUniformS("edgeHardness").get.set(extraValues("edgeHardness"))
      shader.getUniformS("edgeGlow").get.set(extraValues("edgeGlow"))

      shader.getUniformS("modelMatrix").get.set(data.modelMat)
      shader.uploadUniforms()

      assert(model != null)
      // model.draw()

      Minecraft.getInstance().textureManager.getTexture(new ResourceLocation("textures/block/acacia_log.png")).bind()

      GlStateManager._enableDepthTest()
      GlStateManager._disableCull()

      val mesh = MirrorMesh.make(
        DanmakuCore.resource("mirrormodels/sphere_high.obj"),
        posIdx = 0,
        normalsIdx = Some(1),
        texCoordsIdx = Some(2)
      )
      mesh.draw()
      mesh.delete()
    }

    /*t
    floatBuffer.flip()
    GL15.glBufferSubData(GlConst.GL_ARRAY_BUFFER, 0, floatBuffer)

    RenderSystem.glBindBuffer(GlConst.GL_ARRAY_BUFFER, () => 0)

    model.drawInstanced(danmaku.length)
     */
  }

  override def render(
      shader: MirrorShaderProgram,
      danmaku: Seq[TopDanmakuBehaviorsHandler.RenderData],
      modelViewMatrix: Matrix4f,
      projectionMatrix: Matrix4f
  ): Unit = {
    val minecraft = Minecraft.getInstance()

    val gameTime     = minecraft.level.getGameTime
    val partialTicks = minecraft.getFrameTime

    val ticks = ((gameTime % 24000L).toFloat + partialTicks) / 24000.0F
    // println(ticks)

    val cameraPos = minecraft.gameRenderer.getMainCamera.getPosition

    shader.getUniformS("modelViewMatrix").get.set(modelViewMatrix)
    shader.getUniformS("projectionMatrix").get.set(projectionMatrix)
    shader.getUniformS("ticks").get.set(ticks)
    shader.getUniformS("cameraPos").get.set(cameraPos.x.toFloat, cameraPos.y.toFloat, cameraPos.z.toFloat)
    shader.uploadUniforms()

    RenderSystem.enableBlend()
    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

    val (highDanmaku, midLowDanmaku) = danmaku.span(t => t.distanceFromCamera < 8 * 8)
    val (midDanmaku, lowDanmaku)     = midLowDanmaku.span(t => t.distanceFromCamera < 32 * 32)

    renderWithMesh(shader, danmaku, sphereLow, modelViewMatrix, projectionMatrix)

    // renderWithMesh(shader, lowDanmaku, sphereLow, modelViewMatrix, projectionMatrix)
    // renderWithMesh(shader, midDanmaku, sphereMid, modelViewMatrix, projectionMatrix)
    // renderWithMesh(shader, highDanmaku, sphereHigh, modelViewMatrix, projectionMatrix)

    RenderSystem.defaultBlendFunc()
    RenderSystem.disableBlend()
  }

  override val defaultAttributeValues: Map[String, RenderingProperty] = Map(
    "coreSize"     -> RenderingProperty(1.1F, 0.5F, 10F),
    "coreHardness" -> RenderingProperty(2.5F, 0.5F, 10F),
    "edgeHardness" -> RenderingProperty(3F, 0.5F, 10F),
    "edgeGlow"     -> RenderingProperty(3F, 0.5F, 10F)
  )
}
