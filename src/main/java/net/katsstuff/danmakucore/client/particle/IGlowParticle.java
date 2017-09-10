package net.katsstuff.danmakucore.client.particle;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;

public interface IGlowParticle {

	//These methods need to delegate to the respective minecraft methods
	void onUpdateGlow();
	void renderParticleGlow(VertexBuffer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ);

	boolean isAdditive();
	boolean ignoreDepth();
	boolean alive();
}