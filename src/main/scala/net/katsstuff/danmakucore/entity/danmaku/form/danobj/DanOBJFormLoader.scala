/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.form.danobj

import scala.io.Source

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object DanOBJFormLoader {

	@SideOnly(Side.CLIENT)
	def createIRenderForm(location: ResourceLocation): Either[String, (IRenderForm, ResourceLocation)] = {
		val url = getClass.getResource(s"assets/${location.getResourceDomain}/${location.getResourcePath}")
		val textFromFile = Source.fromURL(url).mkString

		DanOBJParser.read(textFromFile).right.map{case (seq, texture) =>

			val form: IRenderForm = new IRenderForm {
				override def renderForm(danmaku: EntityDanmaku, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float,
					renderManager: RenderManager): Unit = {
					val tes = Tessellator.getInstance
					val vb = tes.getBuffer
					val shotData = danmaku.getShotData
					val sizeX = shotData.getSizeX
					val sizeY = shotData.getSizeY
					val sizeZ = shotData.getSizeZ
					val color = shotData.getColor
					val pitch = danmaku.rotationPitch
					val yaw = danmaku.rotationYaw
					val roll = danmaku.getRoll

					GlStateManager.scale(sizeX, sizeY, sizeZ)
					GlStateManager.rotate(-yaw - 180, 0F, 1F, 0F)
					GlStateManager.rotate(pitch, 1F, 0F, 0F)
					GlStateManager.rotate(roll, 0F, 0F, 1F)

					val danmakuColorData = new ColorData(color, 1F)
					val glowDanmakuColorData = new ColorData(color, 0.3F)

					seq.foreach(data => {
						vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL)
						if(data.useGlow) data.draw(vb, glowDanmakuColorData) else data.draw(vb, danmakuColorData)
						tes.draw()
					})
				}
			}

			(form, texture)
		}
	}
}
