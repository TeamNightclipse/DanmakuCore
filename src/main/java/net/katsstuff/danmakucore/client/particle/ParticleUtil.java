/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.particle;

import java.util.Random;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.network.DanmakuCorePacketHandler;
import net.katsstuff.danmakucore.network.ParticlePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ParticleUtil {

	private static Random random = new Random();
	private static int counter = 0;

	@SideOnly(Side.CLIENT)
	public static void spawnParticleGlow(World world, Vector3 pos, Vector3 motion, float r, float g, float b, float scale, int lifetime,
			GlowTexture type) {
		counter += random.nextInt(3);
		if(counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting)
				== 0) {
			DanmakuCore.proxy.addParticle(new ParticleGlow(world, pos, motion, r, g, b, scale, lifetime, type));
		}
	}

	public static void spawnParticleGlowPacket(World world, Vector3 pos, Vector3 motion, float r, float g, float b, float scale, int lifetime,
			GlowTexture type, int range) {
		ParticlePacket.Message message = new ParticlePacket.Message(pos, motion, r, g, b, scale, lifetime, type);
		NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.x(), pos.y(), pos.z(), range);

		DanmakuCorePacketHandler.INSTANCE.sendToAllAround(message, point);
	}
}