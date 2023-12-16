package net.katsstuff.danmakucore.client.form

import com.mojang.math.Matrix4f
import net.katsstuff.danmakucore.client.mirrorshaders.MirrorShaderProgram
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler
import net.minecraft.resources.ResourceLocation

abstract class ClientForm(val shaderLoc: ResourceLocation) {
  def render(
      shader: MirrorShaderProgram,
      danmaku: Seq[TopDanmakuBehaviorsHandler.RenderData],
      modelViewMatrix: Matrix4f,
      projectionMatrix: Matrix4f
  ): Unit

  def init(): Unit

  def defaultAttributeValues: Map[String, RenderingProperty]
}
