package net.katsstuff.danmakucore.client.particle;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.katsstuff.danmakucore.client.lib.LibParticleTexures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleRenderer {

	private ArrayList<Particle> particles = new ArrayList<>();

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent event) {
		event.getMap().registerSprite(LibParticleTexures.PARTICLE_GLINT);
		event.getMap().registerSprite(LibParticleTexures.PARTICLE_GLOW);
		event.getMap().registerSprite(LibParticleTexures.PARTICLE_MOTE);
		event.getMap().registerSprite(LibParticleTexures.PARTICLE_STAR);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTick(TickEvent.ClientTickEvent event) {
		if(event.side == Side.CLIENT) {
			updateParticles();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderAfterWorld(RenderWorldLastEvent event) {
		GlStateManager.pushMatrix();
		renderParticles(event.getPartialTicks());
		GlStateManager.popMatrix();
	}


	private void updateParticles() {
		boolean doRemove;
		for(int i = 0; i < particles.size(); i++) {
			doRemove = true;
			Particle particle = particles.get(i);
			if(particle != null) {
				if(particle instanceof IGlowParticle) {
					if(((IGlowParticle)particle).alive()) {
						particle.onUpdate();
						doRemove = false;
					}
				}
			}
			if(doRemove) {
				particles.remove(i);
			}
		}
	}

	private void renderParticles(float partialTicks) {
		float f = ActiveRenderInfo.getRotationX();
		float f1 = ActiveRenderInfo.getRotationZ();
		float f2 = ActiveRenderInfo.getRotationYZ();
		float f3 = ActiveRenderInfo.getRotationXY();
		float f4 = ActiveRenderInfo.getRotationXZ();
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null) {
			Particle.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			Particle.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			Particle.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			Particle.cameraViewDir = player.getLook(partialTicks);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.alphaFunc(516, 0.003921569F);
			GlStateManager.disableCull();

			GlStateManager.depthMask(false);

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Tessellator tess = Tessellator.getInstance();
			VertexBuffer buffer = tess.getBuffer();

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			//noinspection ForLoopReplaceableByForEach
			for(int i = 0; i < particles.size(); i++) {
				Particle particle = particles.get(i);
				if(!((IGlowParticle)particle).isAdditive()) {
					particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
				}
			}
			tess.draw();

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			//noinspection ForLoopReplaceableByForEach
			for(int i = 0; i < particles.size(); i++) {
				Particle particle = particles.get(i);
				if(((IGlowParticle)particle).isAdditive()) {
					particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
				}
			}
			tess.draw();

			GlStateManager.enableCull();
			GlStateManager.depthMask(true);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
		}
	}

	public <T extends IGlowParticle> void addParticle(T particle) {
		particles.add((Particle)particle);
	}
}