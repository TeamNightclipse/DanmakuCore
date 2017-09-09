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
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.EntityFallingData;
import net.katsstuff.danmakucore.handler.PlayerChangeHandler;
import net.katsstuff.danmakucore.helper.DanmakuHelper;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.katsstuff.danmakucore.item.ItemDanmaku;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.network.DanmakuCorePacketHandler;
import net.katsstuff.danmakucore.server.commands.DanmakuCoreCmd;
import net.katsstuff.danmakucore.shape.ShapeHandler;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = LibMod.MODID, name = LibMod.NAME, version = LibMod.VERSION)
public class DanmakuCore {

	public static final DanmakuCoreCreativeTab DANMAKU_CREATIVE_TAB = new DanmakuCoreCreativeTab("danmaku") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(LibItems.DANMAKU);
		}
	};
	public static final DanmakuCoreCreativeTab SPELLCARD_CREATIVE_TAB = new DanmakuCoreCreativeTab("spellcard") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(LibItems.SPELLCARD);
		}
	};

	@Mod.Instance
	public static DanmakuCore instance;

	@SidedProxy(clientSide = LibMod.CLIENT_PROXY, serverSide = LibMod.COMMON_PROXY, modId = LibMod.MODID)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LogHelper.setLog(event.getModLog());
		DataSerializers.registerSerializer(CoreDataSerializers.SHOTDATA);
		DataSerializers.registerSerializer(CoreDataSerializers.VECTOR_3);
		DataSerializers.registerSerializer(EntityFallingData.DATA_TYPE_SERIALIZER);

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
		MinecraftForge.EVENT_BUS.register(PlayerChangeHandler.class);

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(LibItems.DANMAKU, ((source, stack) -> {
			IPosition iposition = BlockDispenser.getDispensePosition(source);

			EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
			Vec3i directionVec = facing.getDirectionVec();

			Vector3 pos = new Vector3(iposition.getX(), iposition.getY(), iposition.getZ());
			Vector3 direction = new Vector3(directionVec.getX(), directionVec.getY(), directionVec.getZ());

			if(ItemDanmaku.shootDanmaku(stack, source.getWorld(), null, false, pos.asImmutable(), direction, 0D)) {
				stack.shrink(1);
			}
			DanmakuHelper.playShotSound(source.getWorld(), pos);

			return stack;
		}));
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.registerEntities();
		proxy.registerItemColors();
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new DanmakuCoreCmd());
	}
}
