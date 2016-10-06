/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore;

import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.item.DanmakuCoreItem;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.shape.ShapeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LibMod.MODID, name = LibMod.NAME, version = LibMod.VERSION)
public class DanmakuCore {

	public static final DanmakuCoreCreativeTab DANMAKU_CREATIVE_TAB = new DanmakuCoreCreativeTab("danmaku") {

		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(DanmakuCoreItem.danmaku);
		}
	};
	public static final DanmakuCoreCreativeTab SPELLCARD_CREATIVE_TAB = new DanmakuCoreCreativeTab("spellcard") {

		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(DanmakuCoreItem.spellcard);
		}
	};
	public static final DanmakuCoreCreativeTab GENERAL_CREATIVE_TAB = new DanmakuCoreCreativeTab("general") {

		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(DanmakuCoreItem.scoreItem);
		}
	};

	@Mod.Instance
	public static DanmakuCore instance;

	@SidedProxy(clientSide = LibMod.CLIENT_PROXY, serverSide = LibMod.COMMON_PROXY, modId = LibMod.MODID)
	public static CommonProxy proxy;

	public static boolean registriesInitialized = false;
	public static boolean stuffRegistered = false;
	public static boolean variantsRegistered = false;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.setConfig(event.getSuggestedConfigurationFile());

		DataSerializers.registerSerializer(CoreDataSerializers.SHOTDATA);
		DataSerializers.registerSerializer(CoreDataSerializers.VECTOR_3);

		DanmakuRegistry.INSTANCE.init();
		registriesInitialized = true;

		DanmakuCoreItem.preInit();

		proxy.registerStuff();
		stuffRegistered = true;

		proxy.registerVariants();
		variantsRegistered = true;

		proxy.registerColors();
		proxy.registerModels();
		proxy.registerRenderers();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(ShapeHandler.INSTANCE);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.registerEntities();
		proxy.registerItemColors();
	}
}
