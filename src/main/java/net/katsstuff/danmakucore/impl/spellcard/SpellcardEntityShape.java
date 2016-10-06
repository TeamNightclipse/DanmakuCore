/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.spellcard;

import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.entity.spellcard.SpellcardEntity;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeHandler;
import net.minecraft.entity.EntityLivingBase;

public class SpellcardEntityShape extends SpellcardEntity {

	private IShape shape;
	private boolean sent = false;

	public SpellcardEntityShape(Spellcard type, EntitySpellcard card, EntityLivingBase target, IShape shape) {
		super(type, card, target);
		this.shape = shape;
	}

	public void setShape(IShape shape) {
		this.shape = shape;
	}

	@Override
	public void onSpellcardUpdate() {
		if(!sent) {
			ShapeHandler.createShape(shape, card);
			sent = true;
		}
	}
}
