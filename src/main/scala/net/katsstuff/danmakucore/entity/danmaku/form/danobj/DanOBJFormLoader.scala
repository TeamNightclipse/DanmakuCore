/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.form.danobj

import java.io.IOException

import scala.io.Source
import scala.util.{Failure, Success, Try}

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object DanOBJFormLoader {

  @SideOnly(Side.CLIENT)
  @throws[DanmakuParseException]
  @throws[IOException]
  def createIRenderForm(location: ResourceLocation): (IRenderForm, ResourceLocation) = {
    val url          = getClass.getResource(s"assets/${location.getResourceDomain}/${location.getResourcePath}")
    val source       = Source.fromURL(url)
    val textFromFile = source.getLines().mkString
    source.close()

    val res = DanOBJParser
      .read(textFromFile)
      .left
      .map(msg => Failure(new DanmakuParseException(msg)))
      .right
      .map {
        case (triangleData, texture) =>
          val (glowTriangles, noGlowTriangles) = triangleData.partition(_.useGlow)

          //noinspection ConvertExpressionToSAM
          val form: IRenderForm = new IRenderForm {
            override def renderForm(
                danmaku: EntityDanmaku,
                x: Double,
                y: Double,
                z: Double,
                entityYaw: Float,
                partialTicks: Float,
                renderManager: RenderManager
            ): Unit = {
              val tes      = Tessellator.getInstance
              val vb       = tes.getBuffer
              val shotData = danmaku.getShotData
              val sizeX    = shotData.sizeX
              val sizeY    = shotData.sizeY
              val sizeZ    = shotData.sizeZ
              val color    = shotData.color
              val pitch    = danmaku.rotationPitch
              val yaw      = danmaku.rotationYaw
              val roll     = danmaku.getRoll

              GlStateManager.scale(sizeX, sizeY, sizeZ)
              GlStateManager.rotate(-yaw - 180, 0F, 1F, 0F)
              GlStateManager.rotate(pitch, 1F, 0F, 0F)
              GlStateManager.rotate(roll, 0F, 0F, 1F)

              val danmakuColorData     = new ColorData(color, 1F)
              val glowDanmakuColorData = new ColorData(color, 0.3F)

              vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL)
              noGlowTriangles.foreach(_.draw(vb, danmakuColorData))
              tes.draw()

              GlStateManager.enableBlend()
              GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
              GlStateManager.depthMask(false)

              vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL)
              glowTriangles.foreach(_.draw(vb, glowDanmakuColorData))
              tes.draw()

              GlStateManager.depthMask(true)
              GlStateManager.disableBlend()
            }
          }

          Success((form, texture))
      }
      .merge

    res match {
      case Success(ret) => ret
      case Failure(e)   => throw e
    }
  }
}
