/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.data.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class SpellcardEntity implements INBTSerializable<NBTTagCompound> {

	private static final String NBT_TIME = "time";

	protected final Spellcard type;
	protected final EntitySpellcard card;
	@SuppressWarnings("CanBeFinal")
	protected EntityLivingBase target;
	protected int time;
	protected final Random rand = new Random();

	public SpellcardEntity(Spellcard type, EntitySpellcard card, @Nullable EntityLivingBase target) {
		this.type = type;
		this.card = card;
		this.target = target;
	}

	public void onUpdate() {
		time++;
		onSpellcardUpdate();
	}

	public abstract void onSpellcardUpdate();

	public EntitySpellcard getCard() {
		return card;
	}

	public EntityLivingBase getUser() {
		return card.getUser();
	}

	public Optional<EntityLivingBase> getTarget() {
		return Optional.ofNullable(target);
	}

	public int getTime() {
		return time;
	}

	public Random getRand() {
		return rand;
	}

	public World getWorld() {
		return card.worldObj;
	}

	public Vector3 posUser() {
		return new Vector3(getUser());
	}

	public Vector3 posCard() {
		return new Vector3(card);
	}

	public Optional<Vector3> posTarget() {
		return getTarget().map(Vector3::new);
	}

	public Optional<Vector3> angleUserToTarget() {
		return posTarget().map(v -> v.subtract(posUser()).normalize());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(NBT_TIME, time);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		time = tag.getInteger(NBT_TIME);
	}
}
