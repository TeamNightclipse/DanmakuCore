/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network.scalachannel

import java.lang.ref.WeakReference
import java.util

import scala.collection.mutable
import scala.reflect.ClassTag

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelDuplexHandler, ChannelHandlerContext, ChannelPromise}
import io.netty.handler.codec.{MessageToMessageDecoder, MessageToMessageEncoder}
import io.netty.util.AttributeKey
import net.katsstuff.danmakucore.helper.LogHelper
import net.katsstuff.danmakucore.lib.LibMod
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket

object ScalaIndexedMessageChannel {
  val InboundPacketTracker: AttributeKey[ThreadLocal[WeakReference[FMLProxyPacket]]] =
    AttributeKey.valueOf(s"${LibMod.Id}:inboundpacket")
}

@Sharable
class ScalaIndexedMessageChannel extends ChannelDuplexHandler {

  private val discriminatorToConverter = mutable.HashMap.empty[Byte, MessageConverter[_]]
  private val classToDiscriminator     = mutable.HashMap.empty[Class[_], Byte]

  private val encoder = new MessageToMessageEncoder[AnyRef]() {
    override def acceptOutboundMessage(msg: AnyRef): Boolean = classToDiscriminator.contains(msg.getClass)
    override def encode(ctx: ChannelHandlerContext, msg: AnyRef, out: util.List[AnyRef]): Unit = {
      val buf           = new PacketBuffer(Unpooled.buffer)
      val discriminator = classToDiscriminator(msg.getClass)
      buf.writeByte(discriminator)
      discriminatorToConverter(discriminator).asInstanceOf[MessageConverter[Any]].writeBytes(msg, buf)
      val proxy = new FMLProxyPacket(buf, ctx.channel.attr(NetworkRegistry.FML_CHANNEL).get)
      val ref   = ctx.attr(ScalaIndexedMessageChannel.InboundPacketTracker).get.get
      val old   = if (ref == null) null else ref.get
      if (old != null) proxy.setDispatcher(old.getDispatcher)
      out.add(proxy)
    }
  }

  private val decoder = new MessageToMessageDecoder[AnyRef]() {
    override def acceptInboundMessage(msg: AnyRef): Boolean = msg.isInstanceOf[FMLProxyPacket]
    override def decode(ctx: ChannelHandlerContext, msg: AnyRef, out: util.List[AnyRef]): Unit = {
      val fmlProxyPacket = msg.asInstanceOf[FMLProxyPacket]

      val payload = fmlProxyPacket.payload.duplicate
      if (payload.readableBytes < 1)
        LogHelper.error(
          s"The ScalaIndexedMessageChannel has received an empty buffer on channel ${ctx.channel
            .attr(NetworkRegistry.FML_CHANNEL)}, likely a result of a LAN server issue. Pipeline parts : ${ctx.pipeline.toString}"
        )
      val discriminator = payload.readByte
      discriminatorToConverter.get(discriminator) match {
        case Some(converter) =>
          ctx
            .attr(ScalaIndexedMessageChannel.InboundPacketTracker)
            .get
            .set(new WeakReference[FMLProxyPacket](fmlProxyPacket))
          val newMsg = converter.readBytes(payload.slice())
          out.add(newMsg.asInstanceOf[AnyRef])
        case None =>
          LogHelper.error(s"Undefined message for discriminator $discriminator in channel ${fmlProxyPacket.channel}")
      }
    }
  }

  def addDiscriminator[A](discriminator: Byte, converter: MessageConverter[A])(implicit classTag: ClassTag[A]): Unit = {
    discriminatorToConverter.put(discriminator, converter)
    classToDiscriminator.put(classTag.runtimeClass, discriminator)
  }

  override def handlerAdded(ctx: ChannelHandlerContext): Unit = {
    ctx.attr(ScalaIndexedMessageChannel.InboundPacketTracker).set(new ThreadLocal[WeakReference[FMLProxyPacket]])
    super.handlerAdded(ctx)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit =
    decoder.channelRead(ctx, msg)

  override def write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise): Unit =
    encoder.write(ctx, msg, promise)
}
