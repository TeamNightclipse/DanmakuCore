/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.danmaku.form

import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.mirror.client.shaders.MirrorShaderProgram
import net.katsstuff.teamnightclipse.mirror.data.Quat
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.minecraft.util.ResourceLocation

/**
  * A interface used to render danmaku forms.
  * Only exists on the client to be safe.
  */
@SideOnly(Side.CLIENT)
trait IRenderForm {

  /**
    * Do your rendering like normal in here. Note that the texture is already
    * applied, lighting is disabled, and the entity is translated to it's
    * position. You do not need to call glPushMatrix or glPopMatrix.
    */
  def renderLegacy(
      danmaku: DanmakuState,
      x: Double,
      y: Double,
      z: Double,
      orientation: Quat,
      partialTicks: Float,
      manager: RenderManager
  ): Unit

  /**
    * Do more fancy and performant rendering using shaders and other good stuff.
    */
  def renderShaders(
      danmaku: DanmakuState,
      x: Double,
      y: Double,
      z: Double,
      orientation: Quat,
      partialTicks: Float,
      manager: RenderManager,
      shaderProgram: MirrorShaderProgram
  ): Unit = {
    val shot = danmaku.shot
    DanCoreRenderHelper.updateDanmakuShaderAttributes(shaderProgram, this, shot)
    renderLegacy(
      danmaku.copy(
        extra = danmaku.extra.copy(
          shot = shot.copy(
            edgeColor = DanCoreRenderHelper.OverwriteColorEdge,
            coreColor = DanCoreRenderHelper.OverwriteColorCore
          )
        )
      ),
      x,
      y,
      z,
      orientation,
      partialTicks,
      manager
    )
  }

  /**
    * The shader to use for renderShaders. The danmaku renderer will handle beginning and ending the shader program.
    */
  def shader(state: DanmakuState): ResourceLocation = DanCoreRenderHelper.baseDanmakuShaderLoc

  def defaultAttributeValues: Map[String, RenderingProperty] = Map.empty
}
