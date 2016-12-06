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
import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpellcardNamePacket {

	public static class Message implements IMessage {

		private ITextComponent name;
		private boolean add;

		public Message(ITextComponent message, boolean add) {
			this.name = message;
			this.add = add;
		}

		public Message() {}

		@Override
		public void fromBytes(ByteBuf buf) {
			String json = ByteBufUtils.readUTF8String(buf);
			name = ITextComponent.Serializer.jsonToComponent(json);
			add = buf.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			String json = ITextComponent.Serializer.componentToJson(name);
			ByteBufUtils.writeUTF8String(buf, json);
			buf.writeBoolean(add);
		}
	}

	public static class Handler implements IMessageHandler<SpellcardNamePacket.Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask( () -> {
				if(message.add) {
					DanmakuCore.proxy.addSpellcard(message.name);
				}
				else {
					DanmakuCore.proxy.removeSpellcard(message.name);
				}
			});
			return null;
		}
	}
}
