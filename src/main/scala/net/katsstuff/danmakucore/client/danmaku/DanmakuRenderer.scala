package net.katsstuff.danmakucore.client.danmaku

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.katsstuff.danmakucore.client.DanCoreShaders
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler
import net.katsstuff.danmakucore.danmaku.form.DanCoreForms
import net.katsstuff.danmakucore.math.{Mat4, Vector3}
import net.minecraft.client.renderer.{GameRenderer, LevelRenderer, RenderType}
import net.minecraft.client.{Camera, Minecraft}
import net.minecraftforge.client.event.{RenderGuiOverlayEvent, RenderLevelStageEvent}
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.RenderTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.joml.{AxisAngle4f, Matrix4f, Vector3f}

class DanmakuRenderer(handler: TopDanmakuBehaviorsHandler) {

  private var hasRunInit = false

  def init(): Unit = {
    DanCoreForms.defRegistry.getEntries.forEach(f => f.get().clientForm.init())
    hasRunInit = true
  }

  @SubscribeEvent def onRenderHud(event: RenderGuiOverlayEvent.Pre): Unit = {
    val minecraft = Minecraft.getInstance()
    if (event.getOverlay.id.getPath != "chat_panel") {
      return
    }

    val gameRenderer = minecraft.gameRenderer
    val camera       = gameRenderer.getMainCamera

    val pose = new PoseStack
    pose.mulPose(Axis.ZP.rotationDegrees(0))

    pose.mulPose(Axis.XP.rotationDegrees(camera.getXRot))
    pose.mulPose(Axis.YP.rotationDegrees(camera.getYRot + 180.0F))

    // renderDanmaku(camera, pose, gameRenderer.getProjectionMatrix(minecraft.options.fov.get().toInt))

    // val gui = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource())
    // gui.fillGradient(0, 0, 200, 200, 0xFF0000FF, 0x00FF00FF)

    // val bb = minecraft.renderBuffers().bufferSource().getBuffer(RenderType.gui())// Tesselator.getInstance().getBuilder
    // bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
    // bb.vertex(0, 0, 0).color(1F, 0, 0, 1F).endVertex()
    // bb.vertex(0, 300, 0).color(0, 1F, 0, 1F).endVertex()
    // bb.vertex(300, 300, 0).color(0, 0, 1F, 1F).endVertex()
    // bb.vertex(300, 0, 0).color(0, 0, 0, 1F).endVertex()
    // minecraft.renderBuffers().bufferSource().endLastBatch()
    // Tesselator.getInstance().end()

    // gui.drawCenteredString(minecraft.font, "Testing", 200, 200, 0xFFFFFFFF)
  }

  @SubscribeEvent def onRender(event: RenderTickEvent): Unit = {}

  @SubscribeEvent def onRenderAfterLevel(event: RenderLevelStageEvent): Unit = {
    // TODO: Change this to after_particles. Works better
    if (event.getStage != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
      return
    }

    renderDanmaku(event.getCamera, event.getPoseStack, event.getProjectionMatrix)
  }

  // renderDanmaku(event.getCamera, event.getPoseStack, event.getProjectionMatrix)

  // noinspection DuplicatedCode
  private def renderDanmaku(camera: Camera, pose: PoseStack, projMatrix: Matrix4f): Unit = {
    if (!hasRunInit) {
      init()
    }

    val scaleMat = new Matrix4f()
    scaleMat.scaling(1F)

    // val renderData = handler.renderData(event.getPartialTick)
    val renderData = Vector(
      TopDanmakuBehaviorsHandler.RenderData(
        form = DanCoreForms.SphereForm.get(),
        renderProperties =
          DanCoreForms.SphereForm.get().clientForm.defaultAttributeValues.map(t => t._1 -> t._2.default) ++ Map(
            "coreSize"     -> 0.75F,
            "coreHardness" -> 2.5F,
            "edgeHardness" -> 5.0F,
            "edgeGlow"     -> 3.0F
          ),
        modelMat = scaleMat.translate(2, -59, 2, new Matrix4f()),
        modelViewMat = new Matrix4f(),
        0xFFFFFFFF,
        0xFFFF0000,
        5,
        80,
        distanceFromCamera = 15
      ),
      TopDanmakuBehaviorsHandler.RenderData(
        form = DanCoreForms.SphereForm.get(),
        renderProperties =
          DanCoreForms.SphereForm.get().clientForm.defaultAttributeValues.map(t => t._1 -> t._2.default) ++ Map(
            "coreSize"     -> 0.75F,
            "coreHardness" -> 2.5F,
            "edgeHardness" -> 5.0F,
            "edgeGlow"     -> 3.0F
          ),
        modelMat = scaleMat.translate(-2, -59, -2, new Matrix4f()),
        modelViewMat = new Matrix4f(),
        0xFFFFFFFF,
        0xFF00FF00,
        5,
        80,
        distanceFromCamera = 15
      )
    )

    if (renderData.nonEmpty) {
      val cameraPos = camera.getPosition

      pose.pushPose()
      pose.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)
      val modelViewMatrix = pose.last.pose

      if (Math.random() > 0.99) {
        val pos = new Vector3f()
        modelViewMatrix.getTranslation(pos)
        println(s"Pos: $pos")
        val rot = new AxisAngle4f()
        modelViewMatrix.getRotation(rot)
        println(s"Rot: $rot")
        val scale = new Vector3f()
        modelViewMatrix.getScale(scale)
        println(s"Scale: $scale")
        println()
      }

      val originalShader = RenderSystem.getShader
      val tempVec        = new Vector3f()

      renderData.view
        .filter { data =>
          /*
            val pos = tempVec.set(
              data.modelMat.m03,
              data.modelMat.m13,
              data.modelMat.m23
            )
            val scaleX = data.modelMat.m00 / 2
            val scaleY = data.modelMat.m11 / 2
            val scaleZ = data.modelMat.m22 / 2

            val aabb = new AABB(
              pos.x - scaleX,
              pos.y - scaleY,
              pos.z - scaleZ,
              pos.x + scaleX,
              pos.y + scaleY,
              pos.z + scaleZ
            )
            frustum.isVisible(aabb)
           */

          true
        }
        .groupBy(_.form.clientForm.renderType)
        .foreach { case (renderType, danmaku) =>
          val minecraft = Minecraft.getInstance()

          renderType.setupRenderState()

          // TODO: Need to figure out how these two differ
          val modelViewMat = modelViewMatrix // RenderSystem.getModelViewMatrix

          danmaku
            .map { data =>
              val dataModelViewMat = data.modelViewMat
              modelViewMat.mul(data.modelMat, dataModelViewMat)
              dataModelViewMat.getColumn(3, tempVec)

              data.copy(distanceFromCamera = tempVec.lengthSquared)
            }
            .toSeq
            .sortBy(_.distanceFromCamera)(implicitly[Ordering[Double]].reverse)
            .groupBy(_.form.clientForm)
            .foreach { case (clientform, danmaku) =>
              clientform.render(
                RenderSystem.getShader,
                danmaku,
                modelViewMatrix,
                RenderSystem.getProjectionMatrix
              )
            }

          renderType.clearRenderState()

          // if Math.random() > 0.95 then println("Before: " + pose.last.pose)

          val m = danmaku.head.modelMat
          pose.pushPose()
          pose.mulPoseMatrix(m)
          // if Math.random() > 0.95 then println("After: " + pose.last.pose)

          LevelRenderer.renderLineBox(
            pose,
            minecraft.renderBuffers().bufferSource().getBuffer(RenderType.lines()),
            0,
            0,
            0,
            1,
            1,
            1,
            1F,
            0F,
            0F,
            1F,
            0F,
            1F,
            0F
          )
          pose.popPose()
        }

      originalShader.apply()
      pose.popPose()
    }
  }
}
