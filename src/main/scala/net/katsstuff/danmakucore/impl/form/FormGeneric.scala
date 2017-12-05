/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.entity.danmaku.form.{Form, IRenderForm}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

abstract class FormGeneric extends Form {

  final private val texture = DanmakuCore.resource("textures/white.png")

  @SideOnly(Side.CLIENT)
  protected var renderer: IRenderForm = _

  def this(name: String) {
    this()
    setRegistryName(name)
    DanmakuCore.proxy.bakeDanmakuForm(this)
  }

  override def getTexture(danmaku: DanmakuState): ResourceLocation = texture

  @SideOnly(Side.CLIENT)
  override def getRenderer(danmaku: DanmakuState): IRenderForm = {
    if (renderer == null) renderer = createRenderer
    renderer
  }

  @SideOnly(Side.CLIENT)
  protected def createRenderer: IRenderForm
}
