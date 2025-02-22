package net.katsstuff.danmakucore.client.form

import scala.jdk.CollectionConverters.*
import scala.util.Using

import com.mojang.blaze3d.platform.{GlConst, GlStateManager}
import com.mojang.blaze3d.vertex.{BufferBuilder, Tesselator, VertexFormat}
import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.mirrormodels.MirrorMesh
import net.katsstuff.danmakucore.client.{DanCoreShaders, DanCoreVertexFormat}
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler
import net.minecraft.client.renderer.{RenderType, ShaderInstance}
import org.joml.Matrix4f
import org.lwjgl.opengl.{GL15, GL33}

class SphereClientForm extends ClientForm {
  private var sphereLow: MirrorMesh  = _
  private var sphereMid: MirrorMesh  = _
  private var sphereHigh: MirrorMesh = _
  private var attributeBuf: Int      = _

  override def renderType: RenderType = DanCoreShaders.danmakuFancyRenderType

  override def init(): Unit = {
    attributeBuf = GlStateManager._glGenBuffers()
    GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, attributeBuf)

    def initInstancedAttributes(): Unit = {
      GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, attributeBuf)

      val vertexFormat = DanCoreShaders.danmakuFancyVertexFormat
      vertexFormat.getElements.asScala.zipWithIndex.foreach { case (el, i) =>
        el.setupBufferState(i + 2, vertexFormat.getOffset(i), vertexFormat.getVertexSize)
        GL33.glVertexAttribDivisor(i + 2, 1)
      }
    }

    sphereLow = MirrorMesh.make(
      DanmakuCore.resource("mirrormodels/sphere_low.obj"),
      DanCoreVertexFormat.positionNormal,
      initExtra = () => initInstancedAttributes()
    ) // low  = 8,4
    sphereMid = MirrorMesh.make(
      DanmakuCore.resource("mirrormodels/sphere_mid.obj"),
      DanCoreVertexFormat.positionNormal,
      initExtra = () => initInstancedAttributes()
    ) // mid  = 16,8
    sphereHigh = MirrorMesh.make(
      DanmakuCore.resource("mirrormodels/sphere_high.obj"),
      DanCoreVertexFormat.positionNormal,
      initExtra = () => initInstancedAttributes()
    ) // high = 32,16
  }

  private def renderWithMesh(
      shader: ShaderInstance,
      danmaku: Seq[TopDanmakuBehaviorsHandler.RenderData],
      model: MirrorMesh,
      modelViewMatrix: Matrix4f,
      projectionMatrix: Matrix4f
  ): Unit =
    val bb = Tesselator.getInstance().getBuilder
    bb.begin(VertexFormat.Mode.TRIANGLES, DanCoreShaders.danmakuFancyVertexFormat)

    danmaku.foreach { data =>
      def extraValues(s: String): Float =
        defaultAttributeValues(s).asValue(data.renderProperties.get(s))

      bb.putFloat(0, data.modelViewMat.m00)
      bb.putFloat(4, data.modelViewMat.m01)
      bb.putFloat(8, data.modelViewMat.m02)
      bb.putFloat(12, data.modelViewMat.m03)
      bb.nextElement()

      bb.putFloat(0, data.modelViewMat.m10)
      bb.putFloat(4, data.modelViewMat.m11)
      bb.putFloat(8, data.modelViewMat.m12)
      bb.putFloat(12, data.modelViewMat.m13)
      bb.nextElement()

      bb.putFloat(0, data.modelViewMat.m20)
      bb.putFloat(4, data.modelViewMat.m21)
      bb.putFloat(8, data.modelViewMat.m22)
      bb.putFloat(12, data.modelViewMat.m23)
      bb.nextElement()

      bb.putFloat(0, data.modelViewMat.m30)
      bb.putFloat(4, data.modelViewMat.m31)
      bb.putFloat(8, data.modelViewMat.m32)
      bb.putFloat(12, data.modelViewMat.m33)
      bb.nextElement()

      bb.color(data.mainColor)
      bb.color(data.secondaryColor)

      extension (bb: BufferBuilder)
        private def float(f: Float): Unit = {
          bb.putFloat(0, f)
          bb.nextElement()
        }

      bb.float(extraValues("coreSize"))
      bb.float(extraValues("coreHardness"))
      bb.float(extraValues("edgeHardness"))
      bb.float(extraValues("edgeGlow"))

      bb.endVertex()
    }
    Using.resource(bb.end()) { rendered =>
      GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, attributeBuf)

      // Orphan buffer
      // GL15.glBufferData(GlConst.GL_ARRAY_BUFFER, rendered.vertexBuffer().capacity(), GL15.GL_STREAM_DRAW)

      GlStateManager._glBufferData(GlConst.GL_ARRAY_BUFFER, rendered.vertexBuffer(), GL15.GL_STREAM_DRAW)
      GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, 0)
    }((resource: BufferBuilder#RenderedBuffer) => resource.release())

    // danmaku.foreach(_ => model.drawWithShader(modelViewMatrix, projectionMatrix, shader))
    model.drawInstancedWithShader(modelViewMatrix, projectionMatrix, shader, danmaku.length)

  override def render(
      shader: ShaderInstance,
      danmaku: Seq[TopDanmakuBehaviorsHandler.RenderData],
      modelViewMatrix: Matrix4f,
      projectionMatrix: Matrix4f
  ): Unit = {
    val (highDanmaku, midLowDanmaku) = danmaku.span(t => t.distanceFromCamera < 8 * 8)
    val (midDanmaku, lowDanmaku)     = midLowDanmaku.span(t => t.distanceFromCamera < 32 * 32)

    renderWithMesh(shader, danmaku, sphereHigh, modelViewMatrix, projectionMatrix)

    // renderWithMesh(shader, lowDanmaku, sphereLow, modelViewMatrix, projectionMatrix)
    // renderWithMesh(shader, midDanmaku, sphereMid, modelViewMatrix, projectionMatrix)
    // renderWithMesh(shader, highDanmaku, sphereHigh, modelViewMatrix, projectionMatrix)
  }

  override val defaultAttributeValues: Map[String, RenderingProperty] = Map(
    "coreSize"     -> RenderingProperty(1.1F, 0.5F, 10F),
    "coreHardness" -> RenderingProperty(2.5F, 0.5F, 10F),
    "edgeHardness" -> RenderingProperty(3F, 0.5F, 10F),
    "edgeGlow"     -> RenderingProperty(3F, 0.5F, 10F)
  )
}
