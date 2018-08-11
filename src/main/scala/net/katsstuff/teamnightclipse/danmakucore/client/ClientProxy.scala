/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.client

import scala.reflect.ClassTag

import net.katsstuff.teamnightclipse.danmakucore.CommonProxy
import net.katsstuff.teamnightclipse.danmakucore.client.handler.{BossBarHandler, DanmakuRenderer, HUDHandler, SpellcardHandler}
import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.danmakucore.client.render.{RenderFallingData, RenderSpellcard}
import net.katsstuff.teamnightclipse.danmakucore.danmaku._
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.Form
import net.katsstuff.teamnightclipse.danmakucore.data.ShotData
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.Spellcard
import net.katsstuff.teamnightclipse.danmakucore.helper.ItemNBTHelper
import net.katsstuff.teamnightclipse.danmakucore.item.{ItemDanmaku, ItemSpellcard}
import net.katsstuff.teamnightclipse.danmakucore.network.SpellcardInfoPacket
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.TouhouHelper
import net.katsstuff.teamnightclipse.danmakucore.entity.living.boss.EntityDanmakuBoss
import net.katsstuff.teamnightclipse.danmakucore.lib.data.LibItems
import net.katsstuff.teamnightclipse.mirror.client.particles.{GlowTexture, ParticleUtil}
import net.katsstuff.teamnightclipse.mirror.data.Vector3
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

  private val bossBarHandler                             = new BossBarHandler
  private val spellcardHandler                           = new SpellcardHandler
  private var clientDanmakuHandler: ClientDanmakuHandler = _
  private var danmakuRenderer: DanmakuRenderer           = _

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
    if (clientDanmakuHandler == null) {
      clientDanmakuHandler = new ClientDanmakuHandler
      danmakuRenderer = new DanmakuRenderer(clientDanmakuHandler)
      MinecraftForge.EVENT_BUS.register(clientDanmakuHandler)
      MinecraftForge.EVENT_BUS.register(danmakuRenderer)
    }
  }

  private def unregisterDanmaku(): Unit = {
    if (clientDanmakuHandler != null) {
      MinecraftForge.EVENT_BUS.unregister(clientDanmakuHandler)
      MinecraftForge.EVENT_BUS.unregister(danmakuRenderer)
      clientDanmakuHandler = null
      danmakuRenderer = null
    }
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
    registerEntityRenderer(new RenderSpellcard(_))
    registerEntityRenderer(new RenderFallingData(_))
    MinecraftForge.EVENT_BUS.register(new HUDHandler)
    MinecraftForge.EVENT_BUS.register(bossBarHandler)
    MinecraftForge.EVENT_BUS.register(spellcardHandler)
  }

  private def registerEntityRenderer[A <: Entity: ClassTag](f: RenderManager => Render[A]): Unit = {
    val factory: IRenderFactory[A] = manager => f(manager)
    RenderingRegistry.registerEntityRenderingHandler(
      implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]],
      factory
    )
  }

  override private[danmakucore] def bakeRenderModels(): Unit =
    DanCoreRenderHelper.initialize()

  override private[danmakucore] def registerItemColors(): Unit = {
    val itemColors = Minecraft.getMinecraft.getItemColors
    val f: IItemColor = { (stack: ItemStack, pass: Int) =>
      if (!ItemNBTHelper.hasTag(stack, ShotData.NbtShotData, Constants.NBT.TAG_COMPOUND)) 0xFFFFFF
      else {
        val shot = ShotData.fromNBTItemStack(stack)
        if (pass == 0) shot.edgeColor
        else shot.coreColor
      }
    }
    itemColors.registerItemColorHandler(f, LibItems.DANMAKU)
  }

  override private[danmakucore] def addDanmakuBoss(boss: EntityDanmakuBoss): Unit =
    bossBarHandler.danmakuBosses += boss

  override private[danmakucore] def removeDanmakuBoss(boss: EntityDanmakuBoss): Unit =
    bossBarHandler.danmakuBosses -= boss

  override private[danmakucore] def handleSpellcardInfo(packet: SpellcardInfoPacket): Unit =
    spellcardHandler.handlePacket(packet)

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

  override def danmakuHandler: DanmakuHandler = clientDanmakuHandler
}
