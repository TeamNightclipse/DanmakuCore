/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DamageSourceDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.katsstuff.danmakucore.entity.living.IAllyDanmaku;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SubEntityTypeDefault extends SubEntityTypeCore {

	public SubEntityTypeDefault(String name) {
		super(name);
	}

	@Override
	public SubEntity instantiate(World world, EntityDanmaku entityDanmaku) {
		return new SubEntityDefault(world, entityDanmaku);
	}

	@SuppressWarnings("WeakerAccess")
	public static class SubEntityDefault extends SubEntity {

		protected final Random rand = new Random();

		public SubEntityDefault(World world, EntityDanmaku danmaku) {
			super(world, danmaku);
		}

		@Override
		public void subEntityTick() {
			if(!world.isRemote) {
				if(danmaku.getRotationData().isEnabled()) {
					rotate();
				}

				if(!(Math.abs(danmaku.getCurrentSpeed() - danmaku.getMovementData().getSpeedLimit()) <= 0.0001)) {
					danmaku.accelerate();
				}
				else {
					danmaku.updateMotion();
				}

				updateMotionWithGravity();
				hitCheck(entity -> entity != danmaku.getUser().orElse(null) && entity != danmaku.getSource().orElse(null));
			}

			ProjectileHelper.rotateTowardsMovement(danmaku, 1F);

			if(danmaku.isInWater()) {
				waterMovement();
			}
		}

		/**
		 * Called when the danmaku hits a block.
		 */
		@SuppressWarnings("UnusedParameters")
		protected void impactBlock(RayTraceResult raytrace) {}

		/**
		 * Called when the danmaku hits an entity.
		 */
		protected void impactEntity(RayTraceResult raytrace) {
			Entity hitEntity = raytrace.entityHit;
			Entity indirect;
			Optional<EntityLivingBase> optUser = danmaku.getUser();
			if(optUser.isPresent()) indirect = optUser.get();
			else indirect = danmaku.getSource().orElse(null);

			if(hitEntity instanceof EntityLivingBase && !(hitEntity instanceof EntityAgeable)
					&& !(optUser.orElse(null) instanceof IAllyDanmaku && hitEntity instanceof IAllyDanmaku)) {
				EntityLivingBase living = (EntityLivingBase)hitEntity;
				living.attackEntityFrom(DamageSourceDanmaku.causeDanmakuDamage(danmaku, indirect), danmaku.getShotData().damage());
			}
			else if(hitEntity instanceof EntityDragonPart) {
				EntityDragonPart dragon = (EntityDragonPart)hitEntity;
				dragon.attackEntityFrom(DamageSourceDanmaku.causeDanmakuDamage(danmaku, indirect), danmaku.getShotData().damage());
			}
		}

		/**
		 * Called on any impact. Called after special_ImpactBlock and special_ImpactEntity.
		 */
		protected void impact(RayTraceResult raytrace) {
			danmaku.delete();
		}

		/**
		 * Called when the danmaku is in water.
		 */
		protected void waterMovement() {
			ShotData shot = danmaku.getShotData();
			double modifier = danmaku.getCurrentSpeed() * (shot.sizeX() * shot.sizeY() * shot.sizeZ() * 4);
			for(int i = 0; i < 2 * modifier; ++i) {
				float f4 = 0.25F;
				Vector3 posCenter = Vector3.fromEntityCenter(danmaku);
				double randX = rand.nextDouble() * shot.sizeX() - shot.sizeX() / 2;
				double randY = rand.nextDouble() * shot.sizeY() - shot.sizeY() / 2;
				double randZ = rand.nextDouble() * shot.sizeZ() - shot.sizeZ() / 2;
				world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posCenter.x() + randX - danmaku.motionX * f4,
						posCenter.y() + randY - danmaku.motionY * f4, posCenter.z() + randZ - danmaku.motionZ * f4, danmaku.motionX, danmaku.motionY,
						danmaku.motionZ);
			}
			if(danmaku.motionX < 0.01 && danmaku.motionY < 0.01 && danmaku.motionZ < 0.01) {
				danmaku.ticksExisted += 3;
			}

			danmaku.motionX *= 0.95F;
			danmaku.motionY *= 0.95F;
			danmaku.motionZ *= 0.95F;
		}

		/**
		 * Add the gravity to the motion.
		 */
		protected void updateMotionWithGravity() {
			if(!world.isRemote) {
				Entity entity = danmaku;
				Vector3 gravity = danmaku.getMovementData().getGravity();
				entity.motionX += gravity.x();
				entity.motionY += gravity.y();
				entity.motionZ += gravity.z();
			}
		}

		protected void hitCheck(Predicate<Entity> exclude) {
			Entity danEntity = danmaku;
			Vector3 start = new Vector3(danEntity);
			Vector3 end = start.add(danEntity.motionX, danEntity.motionY, danEntity.motionZ);
			RayTraceResult ray = world.rayTraceBlocks(start.toVec3d(), end.toVec3d(), false, true, false);

			if(ray != null) {
				end = new Vector3(ray.hitVec);
			}

			ShotData shot = danmaku.getShotData();

			Entity entity = null;
			List<Entity> list = world.getEntitiesInAABBexcluding(danEntity, danEntity.getEntityBoundingBox()
					.addCoord(danEntity.motionX, danEntity.motionY, danEntity.motionZ).expand(shot.sizeX(), shot.sizeY(), shot.sizeZ()), exclude::test);
			double d0 = 0.0D;

			for(Entity entity1 : list) {
				if(entity1.canBeCollidedWith() && !entity1.noClip) {
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(shot.sizeX(), shot.sizeY(), shot.sizeZ());
					RayTraceResult ray1 = axisalignedbb.calculateIntercept(start.toVec3d(), end.toVec3d());

					if(ray1 != null) {
						double d1 = start.distance(new Vector3(ray1.hitVec));
						if(d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if(entity != null) {
				ray = new RayTraceResult(entity);
			}

			if(ray != null) {
				if(ray.typeOfHit == RayTraceResult.Type.BLOCK) {
					if(world.getBlockState(ray.getBlockPos()).getBlock() == Blocks.PORTAL) {
						danEntity.setPortal(ray.getBlockPos());
					}
					impactBlock(ray);
				}
				else if(ray.typeOfHit == RayTraceResult.Type.ENTITY) {
					impactEntity(ray);
				}

				impact(ray);
			}
		}

		/**
		 * Sets the angle based on the rotation.
		 */
		protected void rotate() {
			if(!world.isRemote) {
				danmaku.setAngle(danmaku.getAngle().rotate(danmaku.getRotationData().getRotationQuat()));
			}
		}
	}
}
