/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network;

import io.netty.buffer.ByteBuf;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChargeSpherePacket {

	public static class Message implements IMessage {

		private int entity;
		private int amount;
		private double offset;
		private double divSpeed;
		private float r;
		private float g;
		private float b;
		private int lifetime;

		public Message(Entity entity, int amount, double offset, double divSpeed, float r, float g, float b, int lifetime) {
			this.entity = entity.getEntityId();
			this.amount = amount;
			this.offset = offset;
			this.divSpeed = divSpeed;
			this.r = r;
			this.g = g;
			this.b = b;
			this.lifetime = lifetime;
		}

		public Message() {}

		@Override
		public void fromBytes(ByteBuf byteBuf) {
			PacketBuffer buf = new PacketBuffer(byteBuf);
			entity = buf.readInt();
			amount = buf.readInt();
			offset = buf.readDouble();
			divSpeed = buf.readDouble();
			r = buf.readFloat();
			g = buf.readFloat();
			b = buf.readFloat();
			lifetime = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf byteBuf) {
			PacketBuffer buf = new PacketBuffer(byteBuf);
			buf.writeInt(entity);
			buf.writeInt(amount);
			buf.writeDouble(offset);
			buf.writeDouble(divSpeed);
			buf.writeFloat(r);
			buf.writeFloat(g);
			buf.writeFloat(b);
			buf.writeInt(lifetime);
		}
	}

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entity);
				if(entity != null) {
					TouhouHelper.createChargeSphere(entity, message.amount, message.offset, message.divSpeed, message.r, message.g, message.b,
							message.lifetime);
				}
			});
			return null;
		}
	}
}
