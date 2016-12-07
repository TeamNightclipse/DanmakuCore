/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard.spellcardbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.katsstuff.danmakucore.network.DanmakuCorePacketHandler;
import net.katsstuff.danmakucore.network.SpellcardInfoPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class SpellcardInfoServer extends SpellcardInfo {

	private float position = 1F;
	private final float destinationHeight = 0.02F;

	private List<EntityPlayerMP> sentTo = new ArrayList<>();

	public SpellcardInfoServer(UUID uuid, ITextComponent name, boolean mirrorText) {
		super(uuid, name, mirrorText);
	}

	public SpellcardInfoServer(ITextComponent name) {
		this(MathHelper.getRandomUUID(), name, true);
	}

	public void setNoAnimation() {
		position = destinationHeight;
		sendPacketAll(SpellcardInfoPacket.Action.SET_POS_ABSOLUTE);
	}

	public void tick() {
		if(position > destinationHeight) {
			position = (float)(position - 0.35F * getAcceleration());

			if(position < destinationHeight) {
				position = destinationHeight;
				sendPacketAll(SpellcardInfoPacket.Action.SET_POS_ABSOLUTE);
			}
			else {
				sendPacketAll(SpellcardInfoPacket.Action.SET_POS);
			}
		}
	}

	@Override
	public void setName(ITextComponent name) {
		super.setName(name);
		sendPacketAll(SpellcardInfoPacket.Action.SET_NAME);
	}

	private double getAcceleration() {
		return (position - 1F) * -1 + 0.01;
	}

	@Override
	public void setMirrorText(boolean mirrorText) {
		super.setMirrorText(mirrorText);
		sendPacketAll(SpellcardInfoPacket.Action.SET_MIRROR);
	}

	@Override
	public float getPosX() {
		return 1F;
	}

	@Override
	public float getPosY() {
		return position;
	}

	public void addPlayer(EntityPlayerMP player) {
		sentTo.add(player);
		sendPacket(SpellcardInfoPacket.Action.ADD, player);
	}

	public void removePlayer(EntityPlayerMP player) {
		sentTo.remove(player);
		sendPacket(SpellcardInfoPacket.Action.REMOVE, player);
	}

	public void clear() {
		sendPacketAll(SpellcardInfoPacket.Action.REMOVE);
		sentTo.clear();
	}

	private void sendPacket(SpellcardInfoPacket.Action action, EntityPlayerMP player) {
		DanmakuCorePacketHandler.INSTANCE.sendTo(new SpellcardInfoPacket.Message(this, action), player);
	}

	private void sendPacketAll(SpellcardInfoPacket.Action action) {
		SpellcardInfoPacket.Message message = new SpellcardInfoPacket.Message(this, action);
		sentTo.forEach(p -> DanmakuCorePacketHandler.INSTANCE.sendTo(message, p));
	}
}
