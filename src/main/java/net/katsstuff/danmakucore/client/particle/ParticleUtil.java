package net.katsstuff.danmakucore.client.particle;

import java.util.Random;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ParticleUtil {

	private static Random random = new Random();
	private static int counter = 0;

	/*
	public static void spawnParticlesFromPacket(MessageParticle message, World world){
	}
	*/

	@SideOnly(Side.CLIENT)
	public static void spawnParticleGlow(World world, Vector3 pos, Vector3 motion, float r, float g, float b, float scale, int lifetime,
			GlowTexture type) {
		counter += random.nextInt(3);
		if(counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting)
				== 0) {
			DanmakuCore.proxy.addParticle(new ParticleGlow(world, pos, motion, r, g, b, scale, lifetime, type));
		}
	}
}