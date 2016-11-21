/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase;

import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.misc.IItemStackConvertible;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class PhaseTypeSpellcard extends PhaseType {

	@Override
	public Phase instantiate(PhaseManager manager) {
		return new PhaseSpellcard(manager, this, DanmakuRegistry.SPELLCARD.getRandomObject(manager.entity.getRNG()));
	}

	public PhaseSpellcard instantiate(PhaseManager manager, Spellcard spellcard) {
		return new PhaseSpellcard(manager, this, spellcard);
	}

	public static class PhaseSpellcard extends Phase implements IItemStackConvertible {

		public static final String NBT_SPELLCARD = "spellcard";
		public static final String NBT_FIRST_ATTACK = "firstAttack";

		private Spellcard spellcard;
		private final PhaseTypeSpellcard type;
		private boolean firstAttack;

		public PhaseSpellcard(PhaseManager manager, PhaseTypeSpellcard type, Spellcard spellcard) {
			super(manager);
			this.type = type;
			this.spellcard = spellcard;
		}

		@Override
		public void init() {
			super.init();
			interval = spellcard.getEndTime();
			firstAttack = true;
		}

		@Override
		public void serverUpdate() {
			super.serverUpdate();
			if(counter == 0) {
				EntityMob entity = getEntity();
				TouhouHelper.declareSpellcard(entity, entity.getAttackTarget(), spellcard, firstAttack);
				firstAttack = false;
			}
		}

		@Override
		protected PhaseType getType() {
			return type;
		}

		public Spellcard getSpellcard() {
			return spellcard;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = super.serializeNBT();
			compound.setString(NBT_SPELLCARD, spellcard.getFullName().toString());
			compound.setBoolean(NBT_FIRST_ATTACK, firstAttack);
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			super.deserializeNBT(nbt);
			spellcard = DanmakuRegistry.SPELLCARD.getObject(new ResourceLocation(nbt.getString(NBT_SPELLCARD)));
			firstAttack = nbt.getBoolean(NBT_FIRST_ATTACK);
		}

		@Override
		public ItemStack getItemStack() {
			return new ItemStack(LibItems.SPELLCARD, 1, DanmakuRegistry.SPELLCARD.getId(spellcard));
		}
	}
}
