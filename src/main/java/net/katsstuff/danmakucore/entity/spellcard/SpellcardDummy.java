/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.entity.living.boss.EnumTouhouCharacters;
import net.minecraft.entity.EntityLivingBase;

public class SpellcardDummy extends Spellcard {

	@Override
	public SpellcardEntity instantiate(EntitySpellcard card, @Nullable EntityLivingBase target) {
		return new SpellcardEntity(this, card, target) {

			@Override
			public void onSpellcardUpdate() {}
		};
	}

	@Override
	public int getNeededLevel() {
		return 0;
	}

	@Override
	public int getRemoveTime() {
		return 0;
	}

	@Override
	public int getEndTime() {
		return 0;
	}

	@Override
	public EnumTouhouCharacters getOriginalUser() {
		return EnumTouhouCharacters.OTHER;
	}
}
