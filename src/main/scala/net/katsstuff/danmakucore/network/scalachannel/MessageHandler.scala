/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network.scalachannel

import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.network.{INetHandler, NetHandlerPlayServer}
import net.minecraft.util.IThreadListener
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

sealed trait MessageHandler[-A, Reply] {

  def handle(netHandler: INetHandler, a: A): Option[Reply]

  def side: Side
  def scheduler: IThreadListener
}

trait ClientMessageHandler[A, Reply] extends MessageHandler[A, Reply] with HasClientHandler[A] {
  @SideOnly(Side.CLIENT)
  def handle(netHandler: NetHandlerPlayClient, a: A): Option[Reply]
  override def handle(netHandler: INetHandler, a: A): Option[Reply] = handle(netHandler.asInstanceOf[NetHandlerPlayClient], a)
  override def side: Side = Side.CLIENT
  override def scheduler: IThreadListener = Minecraft.getMinecraft
}

trait ServerMessageHandler[A, Reply] extends MessageHandler[A, Reply] with HasServerHandler[A] {
  def handle(netHandler: NetHandlerPlayServer, a: A): Option[Reply]
  override def handle(netHandler: INetHandler, a: A): Option[Reply] = handle(netHandler.asInstanceOf[NetHandlerPlayServer], a)
  override def side: Side = Side.SERVER
  override def scheduler: IThreadListener = FMLCommonHandler.instance().getMinecraftServerInstance
}