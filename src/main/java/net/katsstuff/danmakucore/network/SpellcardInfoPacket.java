/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.spellcard.spellcardbar.SpellcardInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpellcardInfoPacket {

	public enum Action {
		ADD,
		SET_NAME,
		SET_MIRROR,
		SET_POS,
		SET_POS_ABSOLUTE,
		REMOVE
	}

	public static class Message implements IMessage {

		private UUID uuid;
		private ITextComponent name;
		private Action action;

		private boolean mirror;

		private float posX;
		private float posY;

		private float prevPosX;
		private float prevPosY;


		public Message(SpellcardInfo info, Action action) {
			this.uuid = info.getUuid();
			this.name = info.getName();
			this.action = action;

			mirror = info.shouldMirrorText();

			posX = info.getPosX();
			posY = info.getPosY();
		}

		public Message() {}

		public UUID getUuid() {
			return uuid;
		}

		public ITextComponent getName() {
			return name;
		}

		public Action getAction() {
			return action;
		}

		public boolean shouldMirrorText() {
			return mirror;
		}

		public float getPosX() {
			return posX;
		}

		public float getPosY() {
			return posY;
		}

		public float getPrevPosX() {
			return prevPosX;
		}

		public float getPrevPosY() {
			return prevPosY;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			PacketBuffer packetBuffer = new PacketBuffer(buf);
			action = packetBuffer.readEnumValue(Action.class);
			uuid = packetBuffer.readUuid();

			switch(action) {
				case ADD:
					String json1 = ByteBufUtils.readUTF8String(buf);
					name = ITextComponent.Serializer.jsonToComponent(json1);

					mirror = packetBuffer.readBoolean();

					posX = packetBuffer.readFloat();
					posY = packetBuffer.readFloat();

					prevPosX = packetBuffer.readFloat();
					prevPosY = packetBuffer.readFloat();
					break;
				case SET_MIRROR:
					mirror = packetBuffer.readBoolean();
					break;
				case SET_NAME:
					String json2 = ByteBufUtils.readUTF8String(buf);
					name = ITextComponent.Serializer.jsonToComponent(json2);
					break;
				case SET_POS:
				case SET_POS_ABSOLUTE:
					posX = packetBuffer.readFloat();
					posY = packetBuffer.readFloat();
					break;
				case REMOVE:
				default:
					break;
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			PacketBuffer packetBuffer = new PacketBuffer(buf);
			packetBuffer.writeEnumValue(action);
			packetBuffer.writeUuid(uuid);

			switch(action) {
				case ADD:
					String json1 = ITextComponent.Serializer.componentToJson(name);
					packetBuffer.writeString(json1);

					packetBuffer.writeBoolean(mirror);

					packetBuffer.writeFloat(posX);
					packetBuffer.writeFloat(posY);

					packetBuffer.writeFloat(posX);
					packetBuffer.writeFloat(posY);
					break;
				case SET_MIRROR:
					packetBuffer.writeBoolean(mirror);
					break;
				case SET_NAME:
					String json2 = ITextComponent.Serializer.componentToJson(name);
					packetBuffer.writeString(json2);
					break;
				case SET_POS:
					packetBuffer.writeFloat(posX);
					packetBuffer.writeFloat(posY);
					break;
				case SET_POS_ABSOLUTE:
					packetBuffer.writeFloat(posX);
					packetBuffer.writeFloat(posY);

					packetBuffer.writeFloat(prevPosX);
					packetBuffer.writeFloat(prevPosY);
					break;
				case REMOVE:
				default:
					break;
			}
		}
	}

	public static class Handler implements IMessageHandler<SpellcardInfoPacket.Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> DanmakuCore.proxy.handleSpellcardInfo(message));
			return null;
		}
	}
}
