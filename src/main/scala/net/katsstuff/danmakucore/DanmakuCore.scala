package net.katsstuff.danmakucore

import com.mojang.logging.LogUtils
import net.katsstuff.danmakucore.blocks.DanCoreBlocks
import net.katsstuff.danmakucore.client.DanCoreShaders
import net.katsstuff.danmakucore.client.danmaku.DanmakuRenderer
import net.katsstuff.danmakucore.client.gui.CursorShapes
import net.katsstuff.danmakucore.danmaku.*
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler.DanmakuSpawnData
import net.katsstuff.danmakucore.danmaku.form.{DanCoreForms, Form}
import net.katsstuff.danmakucore.items.DanCoreItems
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.{IEventBus, SubscribeEvent}
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.{DataPackRegistryEvent, NewRegistryEvent, RegistryBuilder}

import scala.annotation.static

@Mod(DanmakuCore.ModId)
object DanmakuCore {
  final val ModId    = "danmakucore"
  private val Logger = LogUtils.getLogger

  val modEventBus: IEventBus = FMLJavaModLoadingContext.get.getModEventBus

  modEventBus.register(this)
  modEventBus.register(DanCoreShaders)
  modEventBus.register(DanCoreItems)
  DanCoreBlocks.defRegistry.register(modEventBus)
  DanCoreItems.defRegistry.register(modEventBus)
  DanCoreForms.defRegistry.register(modEventBus)
  DanCoreTabs.defRegistry.register(modEventBus)

  ModLoadingContext.get.registerConfig(ModConfig.Type.COMMON, DanCoreCommonConfig.forgeConfig)

  private val danmakuHandler = new TopDanmakuBehaviorsHandler
  MinecraftForge.EVENT_BUS.register(danmakuHandler)

  def resource(name: String): ResourceLocation = new ResourceLocation(ModId, name)

  def spawnDanmaku(danmaku: Seq[DanmakuSpawnData]): Unit = danmakuHandler.addDanmaku(danmaku)

  @SubscribeEvent
  def onRegisterRegistries(event: NewRegistryEvent): Unit =
    DanCoreForms._registry = event.create(RegistryBuilder.of[Form](DanCoreForms.formsRegistryKey.location()))

  @SubscribeEvent
  def onNewDatapackRegistryEvent(event: DataPackRegistryEvent.NewRegistry): Unit =
    event.dataPackRegistry(DanmakuSystems.registryKey, DanmakuSystem.codec, DanmakuSystem.codec)
    event.dataPackRegistry(DanmakuInstantiations.registryKey, DanmakuInstantiation.codec, DanmakuInstantiation.codec)


  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DanmakuCore.ModId, value = Array(Dist.CLIENT))
  object ClientModEvents {
    private val Logger          = LogUtils.getLogger
    private val danmakuRenderer = new DanmakuRenderer(danmakuHandler)
    MinecraftForge.EVENT_BUS.register(danmakuRenderer)
    MinecraftForge.EVENT_BUS.register(CursorShapes)
    modEventBus.register(this)

    @SubscribeEvent def onClientSetup(event: FMLClientSetupEvent): Unit = {
      CursorShapes.init()
      Runtime.getRuntime.addShutdownHook(new Thread(() => CursorShapes.destroy()))
    }
  }
}
