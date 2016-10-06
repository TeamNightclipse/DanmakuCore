/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.katsstuff.danmakucore.data.AbstractShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DamageSourceDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.living.IAllyDanmaku;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.lib.data.LibShotData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class DanmakuHelper {

	/**
	 * Plays the iconic shot sound
	 */
	public static void playShotSound(Entity entity) {
		entity.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 2.0F, 1.2F);
	}

	public static void explosionEffect(World world, Vector3 pos, float explosionSize) {
		world.playSound(null, pos.toBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 2.0F, 3.0F);

		if(explosionSize >= 2.0F) {
			world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, pos.x(), pos.y(), pos.z(), 1.0D, 0.0D, 0.0D);
		}
		else {
			world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, pos.x(), pos.y(), pos.z(), 1.0D, 0.0D, 0.0D);
		}
	}

	public static void explosionEffect2(World world, Vector3 pos, float explosionSize) {
		world.playSound(null, pos.toBlockPos(), SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.HOSTILE, 2.0F, 3.0F);

		if(explosionSize >= 2.0F) {
			world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, pos.x(), pos.y(), pos.z(), 1.0D, 0.0D, 0.0D);
		}
		else {
			world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, pos.x(), pos.y(), pos.z(), 1.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Create a chain explosion, damaging any {@link IAllyDanmaku} in the vicinity.
	 */
	public static void chainExplosion(Entity deadEntity, double range, float maxDamage) {
		List<EntityLivingBase> list = deadEntity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
				deadEntity.getEntityBoundingBox().expandXyz(range), e -> e != deadEntity && e instanceof IAllyDanmaku);
		for(Entity entity : list) {
			double distance = entity.getDistanceToEntity(deadEntity);

			if(distance <= range) {
				float damage = (float)(maxDamage * (1.0F - (distance / range)));
				entity.attackEntityFrom(DamageSourceDanmaku.causeDanmakuDamage(entity, deadEntity), damage);
			}
		}
	}

	/**
	 * The types of removal for danmaku
	 * ALL is to remove all danmaku
	 * ENEMY removes all danmaku except that which is created by players
	 * PLAYER removes all danmaku created by players
	 * OTHER remove all danmaku that wasn't created by the user
	 */
	public enum DanmakuRemoveMode {
		ALL,
		ENEMY,
		PLAYER,
		OTHER
	}

	/**
	 * Remove danmaku in the specific area
	 * @param centerEntity Where to center the removal around
	 * @param range The range to remove in
	 * @param mode What should be removed and what should be left behind
	 * @param isDropBonus Should the danmaku drop bonus when removed
	 * @return The amount of danmaku removed
	 */
	public static int danmakuRemove(Entity centerEntity, double range, DanmakuRemoveMode mode, boolean isDropBonus) {
		int count = 0;
		List<EntityDanmaku> list = centerEntity.worldObj.getEntitiesWithinAABB(EntityDanmaku.class,
				centerEntity.getEntityBoundingBox().expandXyz(range), entity -> entity != centerEntity);

		for(EntityDanmaku entity : list) {
			Optional<EntityLivingBase> optUser = entity.getUser();
			if(optUser.isPresent()) {
				EntityLivingBase user = optUser.get();
				switch(mode) {
					case ALL:
						finishOrKillDanmaku(entity, isDropBonus);
						count++;
						break;
					case ENEMY:
						if(!(user instanceof EntityPlayer)) {
							finishOrKillDanmaku(entity, isDropBonus);
							count++;
						}
						break;
					case PLAYER:
						if(user instanceof EntityPlayer) {
							finishOrKillDanmaku(entity, isDropBonus);
							count++;
						}
						break;
					case OTHER:
						if(user != centerEntity) {
							finishOrKillDanmaku(entity, isDropBonus);
							count++;
						}
						break;
					default:
						break;
				}
			}
		}
		return count;
	}

	private static void finishOrKillDanmaku(EntityDanmaku entity, boolean dropBonus) {
		if(dropBonus) {
			entity.danmakuFinishBonus();
		}
		else {
			entity.setDead();
		}
	}

	/**
	 * Adjust shot damage according to difficulty
	 */
	public static float adjustDamageToDifficulty(AbstractShotData shot, EntityLivingBase user) {
		if(user instanceof EntityPlayer) return shot.damage();

		if(ConfigHandler.isOneHitKill()) return 999999F;

		switch(user.worldObj.getDifficulty()) {
			case PEACEFUL:
				return shot.damage() * 1.0F;
			case EASY:
				return shot.damage() * 0.7F;
			case NORMAL:
				return shot.damage();
			case HARD:
				return shot.damage() * 1.5F;
			default:
				return shot.damage();
		}
	}

	/**
	 * Check if a color is registered
	 */
	public static boolean isNormalColor(int color) {
		return LibShotData.getRegisteredColors().contains(color);
	}

	public static int randomSaturatedColor() {
		Random rand = new Random();
		int[] colors = {LibShotData.COLOR_SATURATED_BLUE, LibShotData.COLOR_SATURATED_CYAN, LibShotData.COLOR_SATURATED_GREEN,
				LibShotData.COLOR_SATURATED_MAGENTA, LibShotData.COLOR_SATURATED_ORANGE, LibShotData.COLOR_SATURATED_RED, LibShotData.COLOR_WHITE,
				LibShotData.COLOR_SATURATED_YELLOW};
		return colors[rand.nextInt(colors.length)];
	}

	public static int randomVanillaColor() {
		Random rand = new Random();
		int[] colors = {LibShotData.COLOR_VANILLA_WHITE, LibShotData.COLOR_VANILLA_ORANGE, LibShotData.COLOR_VANILLA_MAGENTA,
				LibShotData.COLOR_VANILLA_LIGHT_BLUE, LibShotData.COLOR_VANILLA_YELLOW, LibShotData.COLOR_VANILLA_LIME,
				LibShotData.COLOR_VANILLA_PINK, LibShotData.COLOR_VANILLA_GRAY, LibShotData.COLOR_VANILLA_SILVER, LibShotData.COLOR_VANILLA_CYAN,
				LibShotData.COLOR_VANILLA_PURPLE, LibShotData.COLOR_VANILLA_BLUE, LibShotData.COLOR_VANILLA_BROWN, LibShotData.COLOR_VANILLA_GREEN,
				LibShotData.COLOR_VANILLA_RED, LibShotData.COLOR_VANILLA_BLACK};
		return colors[rand.nextInt(colors.length)];
	}
}
