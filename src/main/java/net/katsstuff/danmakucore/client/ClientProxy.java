/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client;

import net.katsstuff.danmakucore.CommonProxy;
import net.katsstuff.danmakucore.client.handler.BossBarHandler;
import net.katsstuff.danmakucore.client.handler.HUDHandler;
import net.katsstuff.danmakucore.client.handler.SpellcardHandler;
import net.katsstuff.danmakucore.client.helper.RenderHelper;
import net.katsstuff.danmakucore.client.particle.GlowTexture;
import net.katsstuff.danmakucore.client.particle.IGlowParticle;
import net.katsstuff.danmakucore.client.particle.ParticleRenderer;
import net.katsstuff.danmakucore.client.particle.ParticleUtil;
import net.katsstuff.danmakucore.client.render.RenderDanmaku;
import net.katsstuff.danmakucore.client.render.RenderFallingData;
import net.katsstuff.danmakucore.client.render.RenderSpellcard;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.EntityFallingData;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.living.boss.EntityDanmakuBoss;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.helper.ItemNBTHelper;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.item.ItemDanmaku;
import net.katsstuff.danmakucore.item.ItemSpellcard;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.network.SpellcardInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	private final BossBarHandler bossBarHandler = new BossBarHandler();
	private final SpellcardHandler spellcardHandler = new SpellcardHandler();
	public final ParticleRenderer particleRenderer = new ParticleRenderer();

	@Override
	public void bakeDanmakuVariant(DanmakuVariant variant) {
		ModelBakery.registerItemVariants(LibItems.DANMAKU, variant.getItemModel());
	}

	@Override
	public void bakeDanmakuForm(Form form) {
		ModelBakery.registerItemVariants(LibItems.DANMAKU, form.getItemModel());
	}

	@Override
	public void bakeSpellcard(Spellcard spellcard) {
		ModelBakery.registerItemVariants(LibItems.SPELLCARD, spellcard.getItemModel());
	}

	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityDanmaku.class, RenderDanmaku::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellcard.class, RenderSpellcard::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingData.class, RenderFallingData::new);

		MinecraftForge.EVENT_BUS.register(new HUDHandler());
		MinecraftForge.EVENT_BUS.register(bossBarHandler);
		MinecraftForge.EVENT_BUS.register(spellcardHandler);
		MinecraftForge.EVENT_BUS.register(particleRenderer);
	}

	@Override
	public void bakeRenderModels() {
		RenderHelper.bakeModels();
	}

	@Override
	public void registerItemColors() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		itemColors.registerItemColorHandler((stack, pass) -> {
			if(!ItemNBTHelper.hasTag(stack, ShotData.NbtShotData(), Constants.NBT.TAG_COMPOUND) || pass == 1) {
				return 0xFFFFFF;
			}
			else return ShotData.fromNBTItemStack(stack).color();
		}, LibItems.DANMAKU);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomMeshDefinition(LibItems.DANMAKU, stack -> ItemDanmaku.getController(stack).getItemModel());
		ModelLoader.setCustomMeshDefinition(LibItems.SPELLCARD, stack -> ItemSpellcard.getSpellcard(stack).getItemModel());
	}

	@Override
	public void addDanmakuBoss(EntityDanmakuBoss boss) {
		bossBarHandler.danmakuBosses.add(boss);
	}

	@Override
	public void removeDanmakuBoss(EntityDanmakuBoss boss) {
		bossBarHandler.danmakuBosses.remove(boss);
	}

	@Override
	public void handleSpellcardInfo(SpellcardInfoPacket.Message packet) {
		spellcardHandler.handlePacket(packet);
	}

	@Override
	public <T extends IGlowParticle> void addParticle(T particle) {
		particleRenderer.addParticle(particle);
	}

	@Override
	public void createParticleGlow(World world, Vector3 pos, Vector3 motion, float r, float g, float b, float scale, int lifetime, GlowTexture
			type) {
		ParticleUtil.spawnParticleGlow(world, pos, motion, r, g, b, scale, lifetime, type);
	}

	@Override
	public void createChargeSphere(Entity entity, int amount, double offset, double divSpeed, float r, float g, float b, int lifetime) {
		TouhouHelper.createChargeSphere(entity, amount, offset, divSpeed, r, g, b, lifetime);
	}
}
