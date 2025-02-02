package net.katsstuff.danmakucore

import com.mojang.logging.LogUtils
import net.katsstuff.danmakucore.blocks.DanCoreBlocks
import net.katsstuff.danmakucore.client.DanCoreShaders
import net.katsstuff.danmakucore.client.danmaku.DanmakuRenderer
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler.DanmakuSpawnData
import net.katsstuff.danmakucore.danmaku.form.DanCoreForms
import net.katsstuff.danmakucore.danmaku.{DanmakuSystem, DanmakuSystems, TopDanmakuBehaviorsHandler}
import net.katsstuff.danmakucore.items.DanCoreItems
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.{IEventBus, SubscribeEvent}
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.{FMLClientSetupEvent, FMLCommonSetupEvent}
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.DataPackRegistryEvent

@Mod(DanmakuCore.ModId)
object DanmakuCore {
  final val ModId    = "danmakucore"
  private val Logger = LogUtils.getLogger

  val modEventBus: IEventBus = FMLJavaModLoadingContext.get.getModEventBus

  modEventBus.addListener(onNewDatapackRegistryEvent)

  DanCoreBlocks.registry.register(modEventBus)
  DanCoreItems.registry.register(modEventBus)
  DanCoreForms.registry.register(modEventBus)
  DanCoreTabs.registry.register(modEventBus)

  ModLoadingContext.get.registerConfig(ModConfig.Type.COMMON, DanCoreCommonConfig.forgeConfig)

  MinecraftForge.EVENT_BUS.register(this)

  private val danmakuHandler = new TopDanmakuBehaviorsHandler
  MinecraftForge.EVENT_BUS.register(danmakuHandler)
  modEventBus.register(DanCoreShaders)

  def resource(name: String): ResourceLocation = new ResourceLocation(ModId, name)

  def spawnDanmaku(danmaku: Seq[DanmakuSpawnData]): Unit = danmakuHandler.addDanmaku(danmaku)

  private def commonSetup(event: FMLCommonSetupEvent): Unit = ()

  @SubscribeEvent
  def onNewDatapackRegistryEvent(event: DataPackRegistryEvent.NewRegistry): Unit =
    event.dataPackRegistry(DanmakuSystems.registryKey, DanmakuSystem.codec, DanmakuSystem.codec)

  @SubscribeEvent
  def onServerStarting(event: ServerStartingEvent): Unit = ()

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DanmakuCore.ModId)
  object ClientModEvents {
    private val Logger          = LogUtils.getLogger
    private val danmakuRenderer = new DanmakuRenderer(danmakuHandler)
    MinecraftForge.EVENT_BUS.register(danmakuRenderer)
    FMLJavaModLoadingContext.get.getModEventBus.register(this)

    @SubscribeEvent def onClientSetup(event: FMLClientSetupEvent): Unit = ()
  }
}
