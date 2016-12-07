/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler;

import net.katsstuff.danmakucore.entity.spellcard.spellcardbar.SpellcardInfo;
import net.katsstuff.danmakucore.network.SpellcardInfoPacket;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SpellcardInfoClient extends SpellcardInfo {

	private float posX;
	private float posY;

	private float prevPosX;
	private float prevPosY;

	public SpellcardInfoClient(SpellcardInfoPacket.Message message) {
		super(message.getUuid(), message.getName(), message.shouldMirrorText());

		posX = message.getPosX();
		posY = message.getPosY();

		prevPosX = message.getPrevPosX();
		prevPosY = message.getPrevPosY();
	}

	@Override
	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		prevPosX = posX;
		this.posX = posX;
	}

	@Override
	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		prevPosY = posY;
		this.posY = posY;
	}

	public float getRenderPosX(ScaledResolution res, float partialTicks) {
		return (prevPosX + (posX - prevPosX) * partialTicks) * res.getScaledWidth();
	}

	public float getRenderPosY(ScaledResolution res, float partialTicks) {
		return (prevPosY + (posY - prevPosY) * partialTicks) * res.getScaledHeight();
	}

	public void handlePacket(SpellcardInfoPacket.Message message) {
		switch(message.getAction()) {
			case SET_NAME:
				setName(message.getName());
				break;
			case SET_MIRROR:
				setMirrorText(message.shouldMirrorText());
				break;
			case SET_POS:
				prevPosX = posX;
				prevPosY = posY;

				posX = message.getPosX();
				posY = message.getPosY();
				break;
			case SET_POS_ABSOLUTE:
				posX = message.getPosX();
				posY = message.getPosY();

				prevPosX = posX;
				prevPosY = posY;
				break;
			default:
				break;
		}
	}
}
