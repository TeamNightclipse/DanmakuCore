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
import net.katsstuff.danmakucore.item.DanmakuCoreItem;
import net.katsstuff.danmakucore.item.ItemDanmaku;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void bakeDanmakuVariant(DanmakuVariant variant) {
		ModelBakery.registerItemVariants(DanmakuCoreItem.danmaku,
				new ModelResourceLocation(new ResourceLocation(variant.getModId(), "danmaku/" + variant.getName()), "inventory"));
	}

	@Override
	public void bakeDanmakuForm(Form form) {
		ModelBakery.registerItemVariants(DanmakuCoreItem.danmaku,
				new ModelResourceLocation(new ResourceLocation(form.getModId(), "danmaku/custom/" + form.getName()), "inventory"));
	}

	@Override
	public void bakeSpellcard(Spellcard spellcard) {
		ModelLoader.setCustomModelResourceLocation(DanmakuCoreItem.spellcard, DanmakuRegistry.INSTANCE.spellcard.getId(spellcard),
				new ModelResourceLocation(new ResourceLocation(spellcard.getModId(), "spellcard/" + spellcard.getName()), "inventory"));
	}

	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityDanmaku.class, RenderDanmaku::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellcard.class, RenderSpellcard::new);
	}

	@Override
	public void bakeRenderModels() {
		RenderHelper.bakeModels();
	}

	@Override
	public void registerModels() {
		//Sets the item model to use depending on if the danmaku item is custom or not.
		ModelLoader.setCustomMeshDefinition(DanmakuCoreItem.danmaku, stack -> {
			if(ItemNBTHelper.getBoolean(stack, ItemDanmaku.NBT_CUSTOM, false)) {
				ShotData shot = ShotData.fromNBTItemStack(stack);
				Form registration = shot.form();
				return new ModelResourceLocation(new ResourceLocation(registration.getModId(), "danmaku/custom/" + registration.getName()),
						"inventory");
			}
			else {
				DanmakuVariant registration = DanmakuRegistry.INSTANCE.danmakuVariant.get(stack.getItemDamage());
				return new ModelResourceLocation(new ResourceLocation(registration.getModId(), "danmaku/" + registration.getName()), "inventory");
			}
		});

		//Normal textures
		registerItem(DanmakuCoreItem.scoreItem, 0);
		registerItem(DanmakuCoreItem.bombItem, 0);
		registerItem(DanmakuCoreItem.extendItem, 0);
	}

	public void registerItemColors() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		itemColors.registerItemColorHandler((stack, pass) -> {
			if(!ItemNBTHelper.verifyExistance(stack, ShotData.NbtShotData()) || pass == 1) {
				return 0xFFFFFF;
			}

			int color = ShotData.fromNBTItemStack(stack).color();
			//LogHelper.info(color);

			if(color == 0) {
				return 0xFFFFFF;
			}
			else {
				return color;
			}
		}, DanmakuCoreItem.danmaku);
	}

	private void registerItem(Item item, int damage) {
		ModelLoader.setCustomModelResourceLocation(item, damage, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
