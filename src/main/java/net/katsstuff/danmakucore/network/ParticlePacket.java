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
import net.katsstuff.danmakucore.client.particle.GlowTexture;
import net.katsstuff.danmakucore.client.particle.ParticleUtil;
import net.katsstuff.danmakucore.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ParticlePacket {

	public static class Message implements IMessage {

		private Vector3 pos;
		private Vector3 motion;
		private float r;
		private float g;
		private float b;
		private float scale;
		private int lifetime;
		private GlowTexture type;

		public Message(Vector3 pos, Vector3 motion, float r, float g, float b, float scale, int lifetime, GlowTexture type) {
			this.pos = pos;
			this.motion = motion;
			this.r = r;
			this.g = g;
			this.b = b;
			this.scale = scale;
			this.lifetime = lifetime;
			this.type = type;
		}

		public Message() {}

		@Override
		public void fromBytes(ByteBuf byteBuf) {
			PacketBuffer buf = new PacketBuffer(byteBuf);
			pos = new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			motion = new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			r = buf.readFloat();
			g = buf.readFloat();
			b = buf.readFloat();
			scale = buf.readFloat();
			lifetime = buf.readInt();
			type = buf.readEnumValue(GlowTexture.class);
		}

		@Override
		public void toBytes(ByteBuf byteBuf) {
			PacketBuffer buf = new PacketBuffer(byteBuf);
			buf.writeDouble(pos.x());
			buf.writeDouble(pos.y());
			buf.writeDouble(pos.z());
			buf.writeDouble(motion.x());
			buf.writeDouble(motion.y());
			buf.writeDouble(motion.z());
			buf.writeFloat(r);
			buf.writeFloat(g);
			buf.writeFloat(b);
			buf.writeFloat(scale);
			buf.writeInt(lifetime);
			buf.writeEnumValue(type);
		}
	}

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(
					() -> ParticleUtil.spawnParticleGlow(Minecraft.getMinecraft().world, message.pos, message.motion, message.r, message.g,
							message.b,
							message.scale, message.lifetime, message.type));
			return null;
		}
	}
}
