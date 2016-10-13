/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore;

import net.katsstuff.danmakucore.capability.CapabilityDanmakuCoreData;
import net.katsstuff.danmakucore.capability.DanmakuCoreDataHandler;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.network.DanmakuCorePacketHandler;
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
@Mod.EventBusSubscriber
public class DanmakuCore {

	public static final DanmakuCoreCreativeTab DANMAKU_CREATIVE_TAB = new DanmakuCoreCreativeTab("danmaku") {

		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(LibItems.DANMAKU);
		}
	};
	public static final DanmakuCoreCreativeTab SPELLCARD_CREATIVE_TAB = new DanmakuCoreCreativeTab("spellcard") {

		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(LibItems.SPELLCARD);
		}
	};

	@Mod.Instance
	public static DanmakuCore instance;

	@SidedProxy(clientSide = LibMod.CLIENT_PROXY, serverSide = LibMod.COMMON_PROXY, modId = LibMod.MODID)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.setConfig(event.getSuggestedConfigurationFile());

		DataSerializers.registerSerializer(CoreDataSerializers.SHOTDATA);
		DataSerializers.registerSerializer(CoreDataSerializers.VECTOR_3);

		proxy.registerColors();
		proxy.registerRenderers();

		CapabilityDanmakuCoreData.register();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.bakeRenderModels();
		DanmakuCorePacketHandler.init();
		MinecraftForge.EVENT_BUS.register(ShapeHandler.class);
		MinecraftForge.EVENT_BUS.register(new DanmakuCoreDataHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.registerEntities();
		proxy.registerItemColors();
	}
}
