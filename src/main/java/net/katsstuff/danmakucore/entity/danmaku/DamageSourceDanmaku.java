/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

public class DamageSourceDanmaku extends EntityDamageSourceIndirect {

	private DamageSourceDanmaku(Entity entity, Entity indirectEntityIn) {
		super("danmaku", entity, indirectEntityIn);
		setProjectile().setDamageBypassesArmor().setDamageIsAbsolute().setMagicDamage();
	}

	public static DamageSource causeDanmakuDamage(Entity entity, Entity indirectEntityIn) {
		return new DamageSourceDanmaku(entity, indirectEntityIn);
	}

	/**
	 * Gets the death message that is displayed when the player dies
	 */
	@SuppressWarnings("ConstantConditions")
	@Override
	public ITextComponent getDeathMessage(EntityLivingBase target) {
		Entity indirect = getImmediateSource();
		ITextComponent iTextComponent = indirect == null ? getTrueSource().getDisplayName() : indirect.getDisplayName();
		ItemStack itemstack = indirect instanceof EntityLivingBase ? ((EntityLivingBase)indirect).getHeldItemMainhand() : ItemStack.EMPTY;
		String s = "death.attack." + damageType;
		String s1 = s + ".item";
		return !itemstack.isEmpty() && itemstack.hasDisplayName() && I18n.canTranslate(s1) ? new TextComponentTranslation(s1, target.getDisplayName(),
				iTextComponent, itemstack.getTextComponent()) : new TextComponentTranslation(s, target.getDisplayName(), iTextComponent);
	}
}
