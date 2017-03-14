/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.particle;

import net.katsstuff.danmakucore.client.lib.LibParticleTexures;
import net.minecraft.util.ResourceLocation;

public enum  GlowTexture {

	GLINT(LibParticleTexures.PARTICLE_GLINT),
	GLOW(LibParticleTexures.PARTICLE_GLOW),
	MOTE(LibParticleTexures.PARTICLE_MOTE),
	STAR(LibParticleTexures.PARTICLE_STAR);

	private final ResourceLocation texture;

	GlowTexture(ResourceLocation texture) {
		this.texture = texture;
	}

	public ResourceLocation getTexture() {
		return texture;
	}
}
