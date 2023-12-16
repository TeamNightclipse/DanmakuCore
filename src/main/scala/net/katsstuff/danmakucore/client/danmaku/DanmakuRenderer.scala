package net.katsstuff.danmakucore.client.danmaku

import scala.util.Using
import net.katsstuff.danmakucore.client.mirrorshaders.ShaderManager
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler
import net.katsstuff.danmakucore.danmaku.form.DanCoreForms
import net.katsstuff.danmakucore.events.AfterEntityRenderEvent
import net.katsstuff.danmakucore.math.{Mat4, Vector3}
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.lwjgl.system.MemoryStack

class DanmakuRenderer(handler: TopDanmakuBehaviorsHandler) {

  private var hasRunInit = false

  def init(): Unit = {
    DanCoreForms.DanCoreForms.getEntries.forEach(f => f.get().clientForm.init())
    hasRunInit = true
  }

  // noinspection DuplicatedCode
  @SubscribeEvent def onRenderAfterLevel(event: AfterEntityRenderEvent): Unit = {
    if (!hasRunInit) {
      init()
    }

    val renderData = handler.renderData(event.getPartialTick)

    if (renderData.nonEmpty) {
      val cameraPos = event.getCamera.getPosition

      val poseStack = event.getPoseStack
      poseStack.pushPose()

      poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

      val modelViewMatrix = poseStack.last.pose // Make sure this is correct
      val projectionMatrix = event.getProjectionMatrix

      Using(MemoryStack.stackPush()) { stack =>
        val tempVec = Vector3.Zero.asMutable

        val buffer = stack.mallocFloat(16)
        modelViewMatrix.store(buffer)
        val modelViewMat = Mat4(
          m00 = buffer.get(),
          m01 = buffer.get(),
          m02 = buffer.get(),
          m03 = buffer.get(),
          m10 = buffer.get(),
          m11 = buffer.get(),
          m12 = buffer.get(),
          m13 = buffer.get(),
          m20 = buffer.get(),
          m21 = buffer.get(),
          m22 = buffer.get(),
          m23 = buffer.get(),
          m30 = buffer.get(),
          m31 = buffer.get(),
          m32 = buffer.get(),
          m33 = buffer.get()
        )

        renderData
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
          .view
          .map { data =>
            val dataModelViewMat = data.modelViewMat
            modelViewMat.multiplyMutableDest(data.modelMat, dataModelViewMat)
            tempVec.set(
              dataModelViewMat.m03,
              dataModelViewMat.m13,
              dataModelViewMat.m23
            )

            data.copy(distanceFromCamera = tempVec.lengthSquared)
          }
          .sortBy(_.distanceFromCamera)(implicitly[Ordering[Double]].reverse)
          .toSeq
          .groupBy(_.form.clientForm.shaderLoc)
          .flatMap { case (shaderLoc, renderData) =>
            ShaderManager.getProgram(shaderLoc).map(_ -> renderData)
          }
          .foreach { case (shader, formDanmaku) =>
            shader.begin()

            formDanmaku.groupBy(_.form.clientForm).foreach { case (form, danmaku) =>
              form.render(shader, danmaku, modelViewMatrix, projectionMatrix)
            }

            shader.end()
          }
      }.get

      poseStack.popPose()
    }
  }
}
