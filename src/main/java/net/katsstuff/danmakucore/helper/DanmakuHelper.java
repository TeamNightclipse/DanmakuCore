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

import javax.annotation.Nullable;

import com.google.common.math.DoubleMath;

import net.katsstuff.danmakucore.EnumDanmakuLevel;
import net.katsstuff.danmakucore.capability.IDanmakuCoreData;
import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DamageSourceDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.living.IAllyDanmaku;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.lib.LibColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DanmakuHelper {

	/**
	 * Plays the iconic shot sound
	 */
	public static void playShotSound(Entity entity) {
		entity.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 2.0F, 1.2F);
	}

	/**
	 * Plays the iconic shot sound
	 */
	public static void playShotSound(World world, Vector3 pos) {
		world.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 2F, 1.2F);
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
	@SuppressWarnings("SameParameterValue")
	public static void chainExplosion(Entity deadEntity, double range, float maxDamage) {
		List<EntityLivingBase> list = deadEntity.world.getEntitiesWithinAABB(EntityLivingBase.class,
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
	 * @param dropBonus Should the danmaku drop bonus when removed
	 * @return The amount of danmaku removed
	 */
	@SuppressWarnings("SameParameterValue")
	public static int danmakuRemove(Entity centerEntity, double range, DanmakuRemoveMode mode, boolean dropBonus) {
		int count = 0;
		List<EntityDanmaku> list = centerEntity.world.getEntitiesWithinAABB(EntityDanmaku.class,
				centerEntity.getEntityBoundingBox().expandXyz(range), entity -> entity != centerEntity);

		for(EntityDanmaku entity : list) {
			Optional<EntityLivingBase> optUser = entity.getUser();
			if(optUser.isPresent()) {
				EntityLivingBase user = optUser.get();
				switch(mode) {
					case ALL:
						finishOrKillDanmaku(entity, dropBonus);
						count++;
						break;
					case ENEMY:
						if(!(user instanceof EntityPlayer)) {
							finishOrKillDanmaku(entity, dropBonus);
							count++;
						}
						break;
					case PLAYER:
						if(user instanceof EntityPlayer) {
							finishOrKillDanmaku(entity, dropBonus);
							count++;
						}
						break;
					case OTHER:
						if(user != centerEntity) {
							finishOrKillDanmaku(entity, dropBonus);
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
	public static float adjustDamageToDifficulty(float base, @Nullable EntityLivingBase user, EnumDanmakuLevel level) {
		if(user instanceof EntityPlayer) return base;

		if(ConfigHandler.danmaku.oneHitKill) return 999999F;

		switch(level) {
			case PEACEFUL:
				return base;
			case EASY:
				return base * 0.7F;
			case NORMAL:
				return base;
			case HARD:
				return base * 1.3F;
			case LUNATIC:
				return base * 1.5F;
			case EXTRA:
				return base * 2F;
			case LAST_SPELL:
				return base * 2.5F;
			case LAST_WORD:
				return base * 3F;
			default:
				return base;
		}
	}

	public static float adjustDamageCoreData(@Nullable EntityLivingBase user, float base) {
		if(user == null) return base;

		Optional<IDanmakuCoreData> optCoreData = TouhouHelper.getDanmakuCoreData(user);
		if(optCoreData.isPresent()) {
			IDanmakuCoreData coreData = optCoreData.get();
			float full = 4F;
			float power = coreData.getPower();
			float bonus = power / full;
			return base + ((base / 2) * bonus);
		}
		else return base;
	}

	public static float adjustDamageTarget(float base, EntityLivingBase target) {
		return target instanceof EntityPlayer ? base * 3.5F : base;
	}

	public static float adjustDanmakuDamage(@Nullable EntityLivingBase user, EntityLivingBase target, float base, EnumDanmakuLevel level) {
		float withData = adjustDamageCoreData(user, base);
		float againstTarget = adjustDamageTarget(withData, target);
		return adjustDamageToDifficulty(againstTarget, user, level);
	}

	public static final double GRAVITY_DEFAULT = -0.03D;

	/**
	 * Check if a color is registered
	 */
	public static boolean isNormalColor(int color) {
		return LibColor.getRegisteredColors().contains(color);
	}

	public static final int[] SATURATED_COLORS =
			{LibColor.COLOR_SATURATED_BLUE, LibColor.COLOR_SATURATED_CYAN, LibColor.COLOR_SATURATED_GREEN, LibColor.COLOR_SATURATED_MAGENTA,
					LibColor.COLOR_SATURATED_ORANGE, LibColor.COLOR_SATURATED_RED, LibColor.COLOR_WHITE, LibColor.COLOR_SATURATED_YELLOW};

	public static final int[] VANILLA_COLORS =
			{LibColor.COLOR_VANILLA_WHITE, LibColor.COLOR_VANILLA_ORANGE, LibColor.COLOR_VANILLA_MAGENTA, LibColor.COLOR_VANILLA_LIGHT_BLUE,
					LibColor.COLOR_VANILLA_YELLOW, LibColor.COLOR_VANILLA_LIME, LibColor.COLOR_VANILLA_PINK, LibColor.COLOR_VANILLA_GRAY,
					LibColor.COLOR_VANILLA_SILVER, LibColor.COLOR_VANILLA_CYAN, LibColor.COLOR_VANILLA_PURPLE, LibColor.COLOR_VANILLA_BLUE,
					LibColor.COLOR_VANILLA_BROWN, LibColor.COLOR_VANILLA_GREEN, LibColor.COLOR_VANILLA_RED, LibColor.COLOR_VANILLA_BLACK};

	private final static Random RAND = new Random();

	public static int randomSaturatedColor() {
		return SATURATED_COLORS[RAND.nextInt(SATURATED_COLORS.length)];
	}

	public static int randomVanillaColor() {
		return VANILLA_COLORS[RAND.nextInt(VANILLA_COLORS.length)];
	}

	//TODO: For simpler cases (no acceleration), there has to be a simpler way to do this
	public static Vector3 simulateRotation(Vector3 pos, Vector3 direction, MovementData movement, RotationData rotation) {
		direction = direction.negate(); //Need this for it to work correctly?
		Quat quat = rotation.getRotationQuat();
		Vector3 origin = pos;
		double last1 = 0D;
		double last2 = 0D;
		int counter = 0;
		Vector3 motion = simulateSetSpeed(direction, movement.speedOriginal());

		while(last1 >= last2 && counter < 999) {
			direction = direction.rotate(quat);
			motion = simulateAccelerate(movement, motion, direction);
			pos = pos.add(motion);
			last2 = last1;
			last1 = pos.distanceSquared(origin);
			counter++;
		}

		return origin.add(pos.subtract(origin).divide(2));
	}

	public static Vector3 simulateAccelerate(MovementData movement, Vector3 currentMotion, Vector3 direction) {
		double speedAccel = movement.getSpeedAcceleration();
		double upperSpeedLimit = movement.getUpperSpeedLimit();
		double lowerSpeedLimit = movement.getLowerSpeedLimit();
		double currentSpeed = currentMotion.length();

		if(DoubleMath.fuzzyCompare(currentSpeed, upperSpeedLimit, EntityDanmaku.EPSILON) >= 0 && speedAccel >= 0D) {
			return simulateSetSpeed(direction, upperSpeedLimit);
		}
		else if(DoubleMath.fuzzyCompare(currentSpeed, lowerSpeedLimit, EntityDanmaku.EPSILON) <= 0 && speedAccel <= 0D) {
			return simulateSetSpeed(direction, lowerSpeedLimit);
		}
		else {
			Vector3 newMotion = simulateAddSpeed(direction, speedAccel, currentMotion);

			double newCurrentSpeed = newMotion.length();
			if(DoubleMath.fuzzyCompare(newCurrentSpeed, upperSpeedLimit, EntityDanmaku.EPSILON) > 0) {
				return simulateSetSpeed(direction, upperSpeedLimit);
			}
			else if(DoubleMath.fuzzyCompare(newCurrentSpeed, lowerSpeedLimit, EntityDanmaku.EPSILON) < 0) {
				return simulateSetSpeed(direction, lowerSpeedLimit);
			}
			else return newMotion;
		}
	}

	public static Vector3 simulateSetSpeed(Vector3 direction, double speed) {
		return direction.multiply(speed);
	}

	public static Vector3 simulateAddSpeed(Vector3 direction, double speed, Vector3 currentMotion) {
		return currentMotion.offset(direction, speed);
	}
}
