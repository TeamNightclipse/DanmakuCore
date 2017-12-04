/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.particle

import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.entity.Entity

trait IGlowParticle {
  //These methods need to delegate to the respective minecraft methods
  def onUpdateGlow(): Unit
  def renderParticleGlow(buffer: BufferBuilder, entityIn: Entity, partialTicks: Float, rotationX: Float,
      rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float): Unit

  def isAdditive: Boolean
  def ignoreDepth: Boolean
  def alive: Boolean
}