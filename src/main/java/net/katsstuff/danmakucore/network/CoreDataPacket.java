/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network;

import java.util.Optional;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.katsstuff.danmakucore.capability.BoundlessDanmakuCoreData;
import net.katsstuff.danmakucore.capability.IDanmakuCoreData;
import net.katsstuff.danmakucore.helper.NBTHelper;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CoreDataPacket {

	public static class CoreDataMessage implements IMessage {

		private final IDanmakuCoreData data;
		private UUID target;

		public CoreDataMessage(IDanmakuCoreData data, Entity target) {
			this.data = data;
			this.target = target.getUniqueID();
		}

		public CoreDataMessage() {
			data = new BoundlessDanmakuCoreData();
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			data.setPower(buf.readFloat());
			data.setScore(buf.readInt());
			data.setLives(buf.readInt());
			data.setBombs(buf.readInt());

			target = new UUID(buf.readLong(), buf.readLong());
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeFloat(data.getPower());
			buf.writeInt(data.getScore());
			buf.writeInt(data.getLives());
			buf.writeInt(data.getBombs());

			buf.writeLong(target.getMostSignificantBits());
			buf.writeLong(target.getLeastSignificantBits());
		}
	}

	public static class CoreDataMessageHandler implements IMessageHandler<CoreDataMessage, IMessage> {

		@Override
		public IMessage onMessage(CoreDataMessage message, MessageContext ctx) {
			Runnable runnable = () -> {
				WorldClient world = Minecraft.getMinecraft().theWorld;
				Entity entityTarget = world.getPlayerEntityByUUID(message.target);
				if(entityTarget == null) {
					entityTarget = NBTHelper.getEntityByUUID(message.target, world).orElse(null);
				}

				if(entityTarget != null) {
					Optional<IDanmakuCoreData> optData = TouhouHelper.getDanmakuCoreData(entityTarget);
					if(optData.isPresent()) {
						IDanmakuCoreData data = optData.get();
						data.setPower(message.data.getPower());
						data.setScore(message.data.getScore());
						data.setLives(message.data.getLives());
						data.setBombs(message.data.getBombs());
					}
				}
			};
			Minecraft.getMinecraft().addScheduledTask(runnable);

			return null;
		}
	}
}
