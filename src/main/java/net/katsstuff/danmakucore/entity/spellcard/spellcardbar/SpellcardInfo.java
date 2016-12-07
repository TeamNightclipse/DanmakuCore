/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard.spellcardbar;

import java.util.UUID;

import net.minecraft.util.text.ITextComponent;

public abstract class SpellcardInfo {

	private final UUID uuid;
	private ITextComponent name;
	private boolean mirrorText;

	public SpellcardInfo(UUID uuid, ITextComponent name, boolean mirrorText) {
		this.uuid = uuid;
		this.name = name;
		this.mirrorText = mirrorText;
	}

	public UUID getUuid() {
		return uuid;
	}

	public ITextComponent getName() {
		return name;
	}

	public void setName(ITextComponent name) {
		this.name = name;
	}

	public boolean shouldMirrorText() {
		return mirrorText;
	}

	public void setMirrorText(boolean mirrorText) {
		this.mirrorText = mirrorText;
	}

	public abstract float getPosX();

	public abstract float getPosY();

}
