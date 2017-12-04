/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.particle

import java.util.concurrent.ThreadLocalRandom

import net.katsstuff.danmakucore.data.Vector3
import net.minecraft.client.Minecraft
import net.minecraft.world.World

class ParticleGlow(
    _world: World,
    val pos: Vector3,
    val motion: Vector3,
    val r: Float,
    val g: Float,
    val b: Float,
    val scale: Float,
    val lifetime: Int,
    val `type`: GlowTexture
) extends AbstractParticleGlow(_world, pos.x, pos.y, pos.z, 0, 0, 0)
    with IGlowParticle {

  private val initScale = scale

  {
    val colorR = if (r > 1.0) r / 255.0f else r
    val colorG = if (g > 1.0) g / 255.0f else g
    val colorB = if (b > 1.0) b / 255.0f else b
    setRBGColorF(colorR, colorG, colorB)
  }
  particleMaxAge = lifetime
  particleScale = scale
  motionX = motion.x
  motionY = motion.y
  motionZ = motion.z
  particleAngle = 2.0f * Math.PI.toFloat

  setParticleTexture(Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(`type`.getTexture.toString))

  override def getBrightnessForRender(pTicks: Float) = 255

  override def shouldDisableDepth = true

  override def getFXLayer = 1

  override def onUpdateGlow(): Unit = {
    super.onUpdateGlow()
    if (ThreadLocalRandom.current.nextInt(6) == 0) particleAge += 1
    val lifeCoeff = particleAge.toFloat / particleMaxAge.toFloat
    this.particleScale = initScale - initScale * lifeCoeff
    this.particleAlpha = 1.0f - lifeCoeff
    this.prevParticleAngle = particleAngle
    particleAngle += 1.0f
  }

  override def alive: Boolean = particleAge < particleMaxAge

  override def isAdditive = true

  override def ignoreDepth = false
}
