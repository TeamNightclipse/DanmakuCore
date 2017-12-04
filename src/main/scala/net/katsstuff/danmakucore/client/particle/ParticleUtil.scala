/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.particle

import java.util.Random

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.network.scalachannel.TargetPoint
import net.katsstuff.danmakucore.network.{DanCorePacketHandler, ParticlePacket}
import net.minecraft.client.Minecraft
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object ParticleUtil {
  private val random  = new Random
  private var counter = 0

  @SideOnly(Side.CLIENT)
  def spawnParticleGlow(
      world: World,
      pos: Vector3,
      motion: Vector3,
      r: Float,
      g: Float,
      b: Float,
      scale: Float,
      lifetime: Int,
      texture: GlowTexture
  ): Unit = {
    counter += random.nextInt(3)
    val particleSetting = Minecraft.getMinecraft.gameSettings.particleSetting
    val particleComp = if (particleSetting == 0) 1 else 2 * particleSetting
    if (counter % particleComp == 0) {
      DanmakuCore.proxy.addParticle(new ParticleGlow(world, pos, motion, r, g, b, scale, lifetime, texture))
    }
  }

  def spawnParticleGlowPacket(
      world: World,
      pos: Vector3,
      motion: Vector3,
      r: Float,
      g: Float,
      b: Float,
      scale: Float,
      lifetime: Int,
      texture: GlowTexture,
      range: Int
  ): Unit =
    DanCorePacketHandler
      .sendToAllAround(
        new ParticlePacket(pos, motion, r, g, b, scale, lifetime, texture),
        TargetPoint.around(world.provider.getDimension, pos, range)
      )
}
