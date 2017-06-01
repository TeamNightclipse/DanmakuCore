/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PhaseDataPacket {

	public static class Message implements IMessage {

		private int entityId;
		private NBTTagCompound phaseTag;

		public Message(EntityDanmakuMob entity, NBTTagCompound phaseTag) {
			this.entityId = entity.getEntityId();
			this.phaseTag = phaseTag;
		}

		@Override
		public void fromBytes(ByteBuf byteBuf) {
			PacketBuffer buf = new PacketBuffer(byteBuf);
			buf.writeInt(entityId);
			buf.writeNBTTagCompoundToBuffer(phaseTag);
		}

		@Override
		public void toBytes(ByteBuf byteBuf) {
			PacketBuffer buf = new PacketBuffer(byteBuf);
			entityId = buf.readInt();
			try {
				phaseTag = buf.readNBTTagCompoundFromBuffer();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
				if(entity != null && message.phaseTag != null && entity instanceof EntityDanmakuMob) {
					((EntityDanmakuMob)entity).getPhaseManager().deserializeNBT(message.phaseTag);
				}
			});
			return null;
		}
	}
}
