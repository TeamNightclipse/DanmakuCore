/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.form.danobj

import net.minecraft.client.renderer.VertexBuffer

case class PositionData(x: Double, y: Double, z: Double)
case class ColorData(red: Float, green: Float, blue: Float, alpha: Float) {

	def this(color: Int, alpha: Float) {
		this((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, alpha)
	}
}
case class UVData(u: Double, v: Double)
case class NormalData(x: Float, y: Float, z: Float)

/**
	* Raw vertex data.
	* Should be optimized before it can be used.
	*/
case class VertexData(pos: PositionData, tex: UVData, color: ColorData, normal: NormalData) {

	def optimize(danmakuMarkerColor: ColorData): OptimizedVertexData = {
		val isDanmakuColor = color == danmakuMarkerColor
		OptimizedVertexData(pos, tex, color, normal, isDanmakuColor)
	}
}

/**
	* Raw triangle data.
	* Should be optimized before it can be used.
	*/
case class TriangleData(first: VertexData, second: VertexData, third: VertexData) {

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

/**
	* Optimized vertex data.
	* Knows if it should use danmaku color or not.
	*/
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

/**
	* Optimized triangle data.
	* The vertex data inside is optimized, in addition to being modified for glow if that should be used.
	*/
case class OptimizedTriangleData(first: OptimizedVertexData, second: OptimizedVertexData, third: OptimizedVertexData, useGlow: Boolean) {

	/**
		* Draws this triangle
		* The states are changed from the outside if glow should be used.
		* The Danmaku color is changed from the outside if glow should be used
		*/
	def draw(vb: VertexBuffer, danmakuColor: ColorData): Unit = {
		first.draw(vb, danmakuColor)
		second.draw(vb, danmakuColor)
		third.draw(vb, danmakuColor)
	}
}