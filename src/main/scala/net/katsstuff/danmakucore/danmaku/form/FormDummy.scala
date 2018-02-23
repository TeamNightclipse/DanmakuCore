/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku.form

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.mirror.data.Quat
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object FormDummy extends Form {

  //JAVA-API
  def instance: FormDummy.type = this

  override def getTexture(danmaku: DanmakuState): ResourceLocation = DanmakuCore.resource("textures/white.png")

  @SideOnly(Side.CLIENT)
  override def getRenderer(danmaku: DanmakuState): IRenderForm =
    (_: DanmakuState, _: Double, _: Double, _: Double, _: Quat, _: Float, _: RenderManager) => () //NO-OP
}
