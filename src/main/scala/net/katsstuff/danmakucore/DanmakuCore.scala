package net.katsstuff.danmakucore

import com.mojang.logging.LogUtils
import net.katsstuff.danmakucore.blocks.DanCoreBlocks
import net.katsstuff.danmakucore.client.danmaku.DanmakuRenderer
import net.katsstuff.danmakucore.client.mirrorshaders.ShaderManager
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler.DanmakuSpawnData
import net.katsstuff.danmakucore.danmaku.form.DanCoreForms
import net.katsstuff.danmakucore.items.DanCoreItems
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.{FMLClientSetupEvent, FMLCommonSetupEvent}
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(DanmakuCore.ModId)
object DanmakuCore {
  final val ModId    = "danmakucore"
  private val LOGGER = LogUtils.getLogger

  FMLJavaModLoadingContext.get.getModEventBus.addListener(commonSetup)

  DanCoreBlocks.BlocksReg.register(FMLJavaModLoadingContext.get.getModEventBus)
  DanCoreItems.DanCoreItems.register(FMLJavaModLoadingContext.get.getModEventBus)
  DanCoreForms.DanCoreForms.register(FMLJavaModLoadingContext.get.getModEventBus)

  ModLoadingContext.get.registerConfig(ModConfig.Type.COMMON, DanCoreCommonConfig.forgeConfig)

  MinecraftForge.EVENT_BUS.register(this)

  private val danmakuHandler = new TopDanmakuBehaviorsHandler
  MinecraftForge.EVENT_BUS.register(danmakuHandler)

  def resource(name: String): ResourceLocation = new ResourceLocation(ModId, name)

  def spawnDanmaku(danmaku: Seq[DanmakuSpawnData]): Unit = danmakuHandler.addDanmaku(danmaku)

  private def commonSetup(event: FMLCommonSetupEvent): Unit = ()

  @SubscribeEvent
  def onServerStarting(event: ServerStartingEvent): Unit = ()


  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DanmakuCore.ModId)
  object ClientModEvents {
    private val LOGGER = LogUtils.getLogger
    private val danmakuRenderer = new DanmakuRenderer(danmakuHandler)
    MinecraftForge.EVENT_BUS.register(danmakuRenderer)
    FMLJavaModLoadingContext.get.getModEventBus.register(this)

    @SubscribeEvent def onClientSetup(event: FMLClientSetupEvent): Unit = ()

    @SubscribeEvent def registerReloadListeners(event: RegisterClientReloadListenersEvent): Unit =
      event.registerReloadListener(ShaderManager.reloader)
  }
}
