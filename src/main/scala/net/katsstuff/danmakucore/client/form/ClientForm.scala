package net.katsstuff.danmakucore.client.form

import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler
import net.minecraft.client.renderer.{RenderType, ShaderInstance}
import org.joml.Matrix4f

abstract class ClientForm {
  def renderType: RenderType
  
  def render(
      shader: ShaderInstance,
      danmaku: Seq[TopDanmakuBehaviorsHandler.RenderData],
      modelViewMatrix: Matrix4f,
      projectionMatrix: Matrix4f
  ): Unit

  def init(): Unit

  def defaultAttributeValues: Map[String, RenderingProperty]
}
