/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danamkucore.form.objloader

import org.lwjgl.opengl.GL11

import net.minecraft.client.renderer.{GlStateManager, VertexBuffer}

case class PositionData(x: Double, y: Double, z: Double)
case class ColorData(red: Float, green: Float, blue: Float, alpha: Float) {

	def this(color: Int, alpha: Float) {
		this((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, alpha)
	}
}
case class UVData(u: Double, v: Double)
case class NormalData(x: Float, y: Float, z: Float)

case class VertexData(pos: PositionData, tex: UVData, color: ColorData, normal: NormalData) {

	def draw(vb: VertexBuffer, danmakuMarkerColor: ColorData, danmakuColor: ColorData): Unit = {
		val usedColor = if(color == danmakuMarkerColor) danmakuColor else color

		vb.pos(pos.x, pos.y, pos.z)
			.tex(tex.u, tex.v)
			.color(usedColor.red, usedColor.green, usedColor.blue, usedColor.alpha)
			.normal(normal.x, normal.y, normal.z)
			.endVertex()
	}

	def optimize(danmakuMarkerColor: ColorData): OptimizedVertexData = {
		val isDanmakuColor = color == danmakuMarkerColor
		OptimizedVertexData(pos, tex, color, normal, isDanmakuColor)
	}
}

case class TriangleData(first: VertexData, second: VertexData, third: VertexData) {

	def draw(vb: VertexBuffer, danmakuMarkerColor: ColorData, glowMarkerColor: ColorData, danmakuColor: ColorData): Unit = {
		val allEqual = first.color == second.color && second.color == third.color
		val useGlow = allEqual && first.color == glowMarkerColor

		val usedFirst = if(useGlow) first.copy(color = first.color.copy(alpha = 0.3F)) else first
		val usedSecond = if(useGlow) second.copy(color = second.color.copy(alpha = 0.3F)) else second
		val usedThird = if(useGlow) third.copy(color = third.color.copy(alpha = 0.3F)) else third

		val usedDanmakuColor = if(useGlow) danmakuColor.copy(alpha = 0.3F) else danmakuColor

		if(useGlow) {
			GlStateManager.enableBlend()
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
			GlStateManager.depthMask(false)
		}

		usedFirst.draw(vb, danmakuMarkerColor, usedDanmakuColor)
		usedSecond.draw(vb, danmakuMarkerColor, usedDanmakuColor)
		usedThird.draw(vb, danmakuMarkerColor, usedDanmakuColor)

		if(useGlow) {
			GlStateManager.depthMask(true)
			GlStateManager.disableBlend()
		}
	}

	def optimize(glowMarkerColor: Seq[ColorData], danmakuMarkerColor: ColorData): OptimizedTriangleData = {
		val optimizedFirst = first.optimize(danmakuMarkerColor)
		val optimizedSecond = second.optimize(danmakuMarkerColor)
		val optimizedThird = third.optimize(danmakuMarkerColor)

		val allEqual = optimizedFirst.color == optimizedSecond.color && optimizedSecond.color == optimizedThird.color
		val useGlow = allEqual && glowMarkerColor.contains(optimizedFirst.color)

		val glowFirst = if(useGlow) optimizedFirst.copy(color = optimizedFirst.color.copy(alpha = 0.3F)) else optimizedFirst
		val glowSecond = if(useGlow) optimizedSecond.copy(color = optimizedSecond.color.copy(alpha = 0.3F)) else optimizedSecond
		val glowThird = if(useGlow) optimizedThird.copy(color = optimizedThird.color.copy(alpha = 0.3F)) else  optimizedThird

		OptimizedTriangleData(glowFirst, glowSecond, glowThird, useGlow)
	}
}

case class OptimizedVertexData(pos: PositionData, tex: UVData, color: ColorData, normal: NormalData, isDanmakuColor: Boolean) {

	def draw(vb: VertexBuffer, danmakuColor: ColorData): Unit = {
		val usedColor = if(isDanmakuColor) danmakuColor else color

		vb.pos(pos.x, pos.y, pos.z)
			.tex(tex.u, tex.v)
			.color(usedColor.red, usedColor.green, usedColor.blue, usedColor.alpha)
			.normal(normal.x, normal.y, normal.z)
			.endVertex()
	}
}

case class OptimizedTriangleData(first: OptimizedVertexData, second: OptimizedVertexData, third: OptimizedVertexData, useGlow: Boolean) {

	def draw(vb: VertexBuffer, danmakuColor: ColorData): Unit = {

		//The danmakuColor is changed from the outside

		if(useGlow) {
			GlStateManager.enableBlend()
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
			GlStateManager.depthMask(false)
		}

		first.draw(vb, danmakuColor)
		second.draw(vb, danmakuColor)
		third.draw(vb, danmakuColor)

		if(useGlow) {
			GlStateManager.depthMask(true)
			GlStateManager.disableBlend()
		}
	}
}