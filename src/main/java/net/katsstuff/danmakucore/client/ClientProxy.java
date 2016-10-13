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
import net.katsstuff.danmakucore.client.helper.RenderHelper;
import net.katsstuff.danmakucore.client.render.RenderDanmaku;
import net.katsstuff.danmakucore.client.render.RenderSpellcard;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.helper.ItemNBTHelper;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.item.ItemDanmaku;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ClientProxy extends CommonProxy {

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
		ModelLoader.setCustomModelResourceLocation(LibItems.SPELLCARD, DanmakuRegistry.SPELLCARD.getId(spellcard), spellcard.getItemModel());
	}

	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityDanmaku.class, RenderDanmaku::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellcard.class, RenderSpellcard::new);

		MinecraftForge.EVENT_BUS.register(new HUDHandler());
	}

	@Override
	public void bakeRenderModels() {
		RenderHelper.bakeModels();
	}

	@Override
	public void registerItemColors() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		itemColors.registerItemColorHandler((stack, pass) -> {
			if(!ItemNBTHelper.verifyExistance(stack, ShotData.NbtShotData()) || pass == 1) {
				return 0xFFFFFF;
			}

			int color = ShotData.fromNBTItemStack(stack).color();

			if(color == 0) {
				return 0xFFFFFF;
			}
			else {
				return color;
			}
		}, LibItems.DANMAKU);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomMeshDefinition(LibItems.DANMAKU, stack -> {
			if(ItemNBTHelper.getBoolean(stack, ItemDanmaku.NBT_CUSTOM, false)) {
				Form form = ShotData.fromNBTItemStack(stack).form();
				return new ModelResourceLocation(new ResourceLocation(form.getModId(), "danmaku/custom/" + form.getName()), "inventory");
			}
			else {
				DanmakuVariant variant = DanmakuRegistry.DANMAKU_VARIANT.getObjectById(stack.getItemDamage());
				return new ModelResourceLocation(new ResourceLocation(variant.getModId(), "danmaku/" + variant.getName()), "inventory");
			}
		});
	}
}
