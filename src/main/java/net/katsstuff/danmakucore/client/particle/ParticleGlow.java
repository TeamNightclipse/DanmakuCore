package net.katsstuff.danmakucore.client.particle;

import java.util.concurrent.ThreadLocalRandom;

import net.katsstuff.danmakucore.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

public class ParticleGlow extends Particle implements IGlowParticle {

	private float initScale = 0;

	public ParticleGlow(World worldIn, Vector3 pos, Vector3 motion, float r, float g, float b, float scale, int lifetime, GlowTexture type) {
		super(worldIn, pos.x(), pos.y(), pos.z(), 0, 0, 0);
		float colorR = r;
		float colorG = g;
		float colorB = b;
		if(colorR > 1.0) {
			colorR = colorR / 255.0f;
		}
		if(colorG > 1.0) {
			colorG = colorG / 255.0f;
		}
		if(colorB > 1.0) {
			colorB = colorB / 255.0f;
		}
		this.setRBGColorF(colorR, colorG, colorB);
		this.particleMaxAge = lifetime;
		this.particleScale = scale;
		this.initScale = scale;
		this.motionX = motion.x();
		this.motionY = motion.y();
		this.motionZ = motion.z();
		this.particleAngle = 2.0f * (float)Math.PI;
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(type.getTexture().toString());
		this.setParticleTexture(sprite);
	}

	@Override
	public int getBrightnessForRender(float pTicks) {
		return 255;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(ThreadLocalRandom.current().nextInt(6) == 0) {
			this.particleAge++;
		}
		float lifeCoeff = (float)this.particleAge / (float)this.particleMaxAge;
		this.particleScale = initScale - initScale * lifeCoeff;
		this.particleAlpha = 1.0f - lifeCoeff;
		this.prevParticleAngle = particleAngle;
		particleAngle += 1.0f;
	}

	@Override
	public boolean alive() {
		return this.particleAge < this.particleMaxAge;
	}

	@Override
	public boolean isAdditive() {
		return true;
	}
}