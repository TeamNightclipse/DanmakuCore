package net.katsstuff.teamnightclipse.danmakucore

import java.util

import scala.collection.JavaConverters._

import net.katsstuff.teamnightclipse.danmakucore.capability.callableentity.CapabilityCallableEntityS
import net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata.{CapabilityDanCoreDataS, DanmakuCoreDataHandler}
import net.katsstuff.teamnightclipse.danmakucore.capability.danmakuhit.{CapabilityDanmakuHitBehaviorS, DanmakuHitBehaviorHandler}
import net.katsstuff.teamnightclipse.danmakucore.capability.owner.CapabilityHasOwnerS
import net.katsstuff.teamnightclipse.danmakucore.client.ClientProxy
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.data.ShotData
import net.katsstuff.teamnightclipse.danmakucore.entity.EntityFallingData
import net.katsstuff.teamnightclipse.danmakucore.handler.PlayerChangeHandler
import net.katsstuff.teamnightclipse.danmakucore.helper.LogHelper
import net.katsstuff.teamnightclipse.danmakucore.item.ItemDanmaku
import net.katsstuff.teamnightclipse.danmakucore.lib.LibMod
import net.katsstuff.teamnightclipse.danmakucore.lib.data.LibItems
import net.katsstuff.teamnightclipse.danmakucore.network.{DanCorePacketHandler, ShotDataSerializer}
import net.katsstuff.teamnightclipse.danmakucore.server.commands.DanmakuCoreCmd
import net.katsstuff.teamnightclipse.danmakucore.shape.ShapeHandler
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.block.BlockDispenser
import net.minecraft.dispenser.IBlockSource
import net.minecraft.item.ItemStack
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStartingEvent, FMLServerStoppedEvent}
import net.minecraftforge.fml.common.{FMLCommonHandler, Mod, SidedProxy}
import net.minecraftforge.fml.relauncher.Side

@Mod(
  modid = LibMod.Id,
  name = LibMod.Name,
  version = LibMod.Version,
  modLanguage = "scala",
  dependencies = "required-after:mirror@[0.2.0,);"
)
object DanmakuCore {
  MinecraftForge.EVENT_BUS.register(CommonProxy)

  if (FMLCommonHandler.instance().getSide == Side.CLIENT) {
    MinecraftForge.EVENT_BUS.register(ClientProxy)
  }

  def instance: DanmakuCore.type = this

  /**
    * Construct a ResourceLocation with DanmakuCore as the mod.
    */
  def resource(path: String) = new ResourceLocation(LibMod.Id, path)

  def spawnDanmaku(states: Seq[DanmakuState]): Unit = proxy.spawnDanmaku(states)

  def spawnDanmaku(states: util.List[DanmakuState]): Unit = proxy.spawnDanmaku(states.asScala)

  @SidedProxy(clientSide = LibMod.ClientProxy, serverSide = LibMod.CommonProxy, modId = LibMod.Id)
  var proxy: CommonProxy = _

  @Mod.EventHandler
  def preInit(event: FMLPreInitializationEvent): Unit = {
    LogHelper.setLog(event.getModLog)
    DataSerializers.registerSerializer(ShotDataSerializer)
    DataSerializers.registerSerializer(EntityFallingData.DataTypeSerializer)
    proxy.registerColors()
    proxy.registerRenderers()
    CapabilityDanCoreDataS.register()
    CapabilityDanmakuHitBehaviorS.register()
    CapabilityCallableEntityS.register()
    CapabilityHasOwnerS.register()
  }

  @Mod.EventHandler
  def init(event: FMLInitializationEvent): Unit = {
    proxy.bakeRenderModels()
    DanCorePacketHandler.load()
    MinecraftForge.EVENT_BUS.register(ShapeHandler)
    MinecraftForge.EVENT_BUS.register(DanmakuCoreDataHandler)
    MinecraftForge.EVENT_BUS.register(DanmakuHitBehaviorHandler)
    MinecraftForge.EVENT_BUS.register(PlayerChangeHandler)
    BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
      LibItems.DANMAKU,
      (source: IBlockSource, stack: ItemStack) => {
        val iPos         = BlockDispenser.getDispensePosition(source)
        val directionVec = source.getBlockState.getValue(BlockDispenser.FACING).getDirectionVec
        val pos          = Vector3(iPos.getX, iPos.getY, iPos.getZ)
        val direction    = Vector3(directionVec.getX, directionVec.getY, directionVec.getZ)
        if (ItemDanmaku.shootDanmaku(stack, source.getWorld, None, None, alternateMode = false, pos, direction, 0)) {
          stack.shrink(1)
        }
        val shot = ShotData.fromNBTItemStack(stack)
        shot.form.playShotSound(source.getWorld, pos, shot)
        stack
      }
    )
  }

  @Mod.EventHandler
  def postInit(event: FMLPostInitializationEvent): Unit =
    proxy.registerItemColors()

  @Mod.EventHandler
  def serverStarting(event: FMLServerStartingEvent): Unit = {
    proxy.serverStarting(event)
    event.registerServerCommand(new DanmakuCoreCmd)
  }

  @Mod.EventHandler
  def serverStopped(event: FMLServerStoppedEvent): Unit =
    proxy.serverStopped(event)
}
