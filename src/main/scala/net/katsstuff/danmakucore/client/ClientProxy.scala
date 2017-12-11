/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client

import scala.reflect.ClassTag

import net.katsstuff.danmakucore.CommonProxy
import net.katsstuff.danmakucore.client.handler.{BossBarHandler, DanmakuRenderer, HUDHandler, SpellcardHandler}
import net.katsstuff.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.danmakucore.client.particle.{GlowTexture, IGlowParticle, ParticleRenderer, ParticleUtil}
import net.katsstuff.danmakucore.client.render.{RenderDanmaku, RenderFallingData, RenderSpellcard}
import net.katsstuff.danmakucore.danmaku.{ClientDanmakuHandler, DanmakuChanges, DanmakuState}
import net.katsstuff.danmakucore.data.{ShotData, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant
import net.katsstuff.danmakucore.entity.danmaku.form.Form
import net.katsstuff.danmakucore.entity.living.boss.EntityDanmakuBoss
import net.katsstuff.danmakucore.entity.spellcard.Spellcard
import net.katsstuff.danmakucore.helper.ItemNBTHelper
import net.katsstuff.danmakucore.item.{ItemDanmaku, ItemSpellcard}
import net.katsstuff.danmakucore.lib.data.LibItems
import net.katsstuff.danmakucore.network.SpellcardInfoPacket
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.IThreadListener
import net.minecraft.world.World
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.client.registry.{IRenderFactory, RenderingRegistry}
import net.minecraftforge.fml.common.event.{FMLServerStartingEvent, FMLServerStoppedEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.{ClientConnectedToServerEvent, ClientDisconnectionFromServerEvent}

object ClientProxy {

  @SubscribeEvent
  def registerModels(event: ModelRegistryEvent): Unit = {
    ModelLoader
      .setCustomMeshDefinition(LibItems.DANMAKU, (stack: ItemStack) => ItemDanmaku.getController(stack).itemModel)
    ModelLoader
      .setCustomMeshDefinition(LibItems.SPELLCARD, (stack: ItemStack) => ItemSpellcard.getSpellcard(stack).itemModel)
  }
}
class ClientProxy extends CommonProxy {
  MinecraftForge.EVENT_BUS.register(this)

  private val bossBarHandler   = new BossBarHandler
  private val spellcardHandler = new SpellcardHandler
  private val particleRenderer = new ParticleRenderer
  private var clientDanmakuHandler: ClientDanmakuHandler = _
  private var danmakuRenderer:      DanmakuRenderer      = _

  override def defaultWorld: World = Minecraft.getMinecraft.world

  override def scheduler: IThreadListener = Minecraft.getMinecraft

  override def serverStarting(event: FMLServerStartingEvent): Unit = {
    super.serverStarting(event)
    registerDanmaku()
  }

  override def serverStopped(event: FMLServerStoppedEvent): Unit = {
    super.serverStopped(event)
    unregisterDanmaku()
  }

  @SubscribeEvent
  def onJoined(event: ClientConnectedToServerEvent): Unit =
    registerDanmaku()

  @SubscribeEvent
  def onQuit(event: ClientDisconnectionFromServerEvent): Unit =
    unregisterDanmaku()

  private def registerDanmaku(): Unit = {
    clientDanmakuHandler = new ClientDanmakuHandler
    danmakuRenderer = new DanmakuRenderer(clientDanmakuHandler)
    MinecraftForge.EVENT_BUS.register(clientDanmakuHandler)
    MinecraftForge.EVENT_BUS.register(danmakuRenderer)
  }

  private def unregisterDanmaku(): Unit = {
    MinecraftForge.EVENT_BUS.unregister(clientDanmakuHandler)
    MinecraftForge.EVENT_BUS.unregister(danmakuRenderer)
    clientDanmakuHandler = null
    danmakuRenderer = null
  }

  override private[danmakucore] def bakeDanmakuVariant(variant: DanmakuVariant): Unit =
    ModelBakery.registerItemVariants(LibItems.DANMAKU, variant.itemModel)

  override private[danmakucore] def initForm(form: Form): Unit = {
    ModelBakery.registerItemVariants(LibItems.DANMAKU, form.itemModel)
    form.initClient()
  }

  override private[danmakucore] def bakeSpellcard(spellcard: Spellcard): Unit =
    ModelBakery.registerItemVariants(LibItems.SPELLCARD, spellcard.itemModel)

  override private[danmakucore] def registerRenderers(): Unit = {
    registerEntityRenderer(new RenderDanmaku(_))
    registerEntityRenderer(new RenderSpellcard(_))
    registerEntityRenderer(new RenderFallingData(_))
    MinecraftForge.EVENT_BUS.register(new HUDHandler)
    MinecraftForge.EVENT_BUS.register(bossBarHandler)
    MinecraftForge.EVENT_BUS.register(spellcardHandler)
    MinecraftForge.EVENT_BUS.register(particleRenderer)
  }

  private def registerEntityRenderer[A <: Entity: ClassTag](f: RenderManager => Render[A]): Unit = {
    val factory: IRenderFactory[A] = manager => f(manager)
    RenderingRegistry.registerEntityRenderingHandler(
      implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]],
      factory
    )

    MinecraftForge.EVENT_BUS.register(particleRenderer)
  }

  override private[danmakucore] def bakeRenderModels(): Unit =
    DanCoreRenderHelper.bakeModels()

  override private[danmakucore] def registerItemColors(): Unit = {
    val itemColors = Minecraft.getMinecraft.getItemColors
    val f: IItemColor = { (stack: ItemStack, pass: Int) =>
      if (!ItemNBTHelper.hasTag(stack, ShotData.NbtShotData, Constants.NBT.TAG_COMPOUND) || pass == 1) 0xFFFFFF
      else ShotData.fromNBTItemStack(stack).color
    }
    itemColors.registerItemColorHandler(f, LibItems.DANMAKU)
  }

  override private[danmakucore] def addDanmakuBoss(boss: EntityDanmakuBoss): Unit =
    bossBarHandler.danmakuBosses += boss

  override private[danmakucore] def removeDanmakuBoss(boss: EntityDanmakuBoss): Unit =
    bossBarHandler.danmakuBosses -= boss

  override private[danmakucore] def handleSpellcardInfo(packet: SpellcardInfoPacket): Unit =
    spellcardHandler.handlePacket(packet)

  override def addParticle[T <: IGlowParticle](particle: T): Unit =
    particleRenderer.addParticle(particle)

  override def createParticleGlow(
      world: World,
      pos: Vector3,
      motion: Vector3,
      r: Float,
      g: Float,
      b: Float,
      scale: Float,
      lifetime: Int,
      texture: GlowTexture
  ): Unit = ParticleUtil.spawnParticleGlow(world, pos, motion, r, g, b, scale, lifetime, texture)

  override def createChargeSphere(
      entity: Entity,
      amount: Int,
      offset: Double,
      divSpeed: Double,
      r: Float,
      g: Float,
      b: Float,
      lifetime: Int
  ): Unit = TouhouHelper.createChargeSphere(entity, amount, offset, divSpeed, r, g, b, lifetime)

  override def forceUpdateDanmakuClient(state: DanmakuState): Unit =
    clientDanmakuHandler.forceUpdateDanmaku(
      state.copy(entity = state.entity.copy(world = Minecraft.getMinecraft.world))
    )

  override private[danmakucore] def updateDanmakuClient(changes: DanmakuChanges): Unit =
    clientDanmakuHandler.updateDanmaku(changes)

  override private[danmakucore] def spawnDanmakuClient(states: Seq[DanmakuState]): Unit = {
    val clientWorld = Minecraft.getMinecraft.world
    clientDanmakuHandler.spawnDanmaku(states.map(state => state.copy(entity = state.entity.copy(world = clientWorld))))
  }
}
