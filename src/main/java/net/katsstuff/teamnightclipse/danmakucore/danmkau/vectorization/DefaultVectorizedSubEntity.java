/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.danmkau.vectorization;

import net.katsstuff.teamnightclipse.danmakucore.capability.danmakuhit.CapabilityDanmakuHitBehaviorJ;
import net.katsstuff.teamnightclipse.danmakucore.capability.danmakuhit.DanmakuHitBehavior;
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DamageSourceDanmaku;
import net.katsstuff.teamnightclipse.danmakucore.danmaku.vectorization.VectorizedSubEntity;
import net.katsstuff.teamnightclipse.danmakucore.data.ShotData;
import net.katsstuff.teamnightclipse.danmakucore.handler.ConfigHandler;
import net.katsstuff.teamnightclipse.danmakucore.helper.MathUtil;
import net.katsstuff.teamnightclipse.danmakucore.javastuff.DanmakuHelper;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibSounds;
import net.katsstuff.teamnightclipse.mirror.data.Quat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.math.RayTraceResult;
import scala.collection.mutable.ArrayBuffer;
import scala.runtime.BoxedUnit;

public class DefaultVectorizedSubEntity extends VectorizedSubEntity {

	private void markDead(DanmakuCluster cluster) {
		int back = cluster.back;
		for(int i = 0; i < cluster.size[back]; i++) {
			if(cluster.ticksExisted[back] > cluster.shot.end()) {
				cluster.isDead[back][i] = true;
				cluster.deadCount[back] += 1;
			}
		}
	}

	private void rotate(DanmakuCluster cluster) {
		int front = cluster.front;
		int back = cluster.back;
		for(int i = 0; i < cluster.size[back]; i++) {
			Quat rotation = cluster.rot;
			if(!cluster.isDead[back][i] && rotation != Quat.Identity()) {
				Quat pure = new Quat(cluster.dirX[front][i], cluster.dirY[front][i], cluster.dirZ[front][i], 0);
				Quat multiplied = rotation.multiply(pure).multiply(rotation.conjugate());

				cluster.dirX[back][i] = multiplied.x();
				cluster.dirY[back][i] = multiplied.y();
				cluster.dirZ[back][i] = multiplied.z();
			}
		}
	}

	private void calcCurrentSpeed(DanmakuCluster cluster) {
		int back = cluster.back;
		int front = cluster.front;
		for(int i = 0; i < cluster.size[back]; i++) {
			if(!cluster.isDead[back][i]) {
				double x = cluster.motX[front][i];
				double y = cluster.motY[front][i];
				double z = cluster.motZ[front][i];
				cluster.currentSpeedSq[i] = x * x + y * y + z * z;
			}
		}
	}

	private void calcSpeed(DanmakuCluster cluster) {
		calcCurrentSpeed(cluster);
		int front = cluster.front;
		int back = cluster.back;

		double speedAccel = cluster.speedAcceleration;
		double upperSpeedLimit = cluster.speedUpperLimit;
		double lowerSpeedLimit = cluster.speedLowerLimit;
		double upperSpeedLimitSq = cluster.speedLowerLimitSq;
		double lowerSpeedLimitSq = cluster.speedLowerLimitSq;

		for(int i = 0; i < cluster.size[back]; i++) {
			if(!cluster.isDead[back][i]) {
				double currentSpeedSq = cluster.currentSpeedSq[i];
				double dx = cluster.dirX[back][i];
				double dy = cluster.dirY[back][i];
				double dz = cluster.dirZ[back][i];

				if(MathUtil.fuzzyCompare(currentSpeedSq, upperSpeedLimitSq) >= 0 && speedAccel >= 0D) {
					cluster.motX[back][i] = dx * upperSpeedLimit;
					cluster.motY[back][i] = dy * upperSpeedLimit;
					cluster.motZ[back][i] = dz * upperSpeedLimit;
				}
				else if(MathUtil.fuzzyCompare(currentSpeedSq, lowerSpeedLimitSq) <= 0 && speedAccel <= 0D) {
					cluster.motX[back][i] = dx * lowerSpeedLimit;
					cluster.motY[back][i] = dy * lowerSpeedLimit;
					cluster.motZ[back][i] = dz * lowerSpeedLimit;
				}
				else {
					double x = cluster.motX[front][i] + dx * speedAccel;
					double y = cluster.motY[front][i] + dy * speedAccel;
					double z = cluster.motZ[front][i] + dz * speedAccel;

					currentSpeedSq = x * x + y * y + z * z;

					if(MathUtil.fuzzyCompare(currentSpeedSq, upperSpeedLimitSq) > 0) {
						cluster.motX[back][i] = dx * upperSpeedLimit;
						cluster.motY[back][i] = dy * upperSpeedLimit;
						cluster.motZ[back][i] = dz * upperSpeedLimit;
					}
					else if(MathUtil.fuzzyCompare(currentSpeedSq, lowerSpeedLimitSq) < 0) {
						cluster.motX[back][i] = dx * lowerSpeedLimit;
						cluster.motY[back][i] = dy * lowerSpeedLimit;
						cluster.motZ[back][i] = dz * lowerSpeedLimit;
					}
					else {
						cluster.motX[back][i] = x;
						cluster.motY[back][i] = y;
						cluster.motZ[back][i] = z;
					}
				}
			}
		}
	}

	private void addGravity(DanmakuCluster cluster) {
		int back = cluster.back;
		for(int i = 0; i < cluster.size[back]; i++) {
			if(!cluster.isDead[back][i]) {
				cluster.motX[back][i] += cluster.gravX;
				cluster.motX[back][i] += cluster.gravX;
				cluster.motX[back][i] += cluster.gravX;
			}
		}
	}

	private void updatePos(DanmakuCluster cluster) {
		int front = cluster.front;
		int back = cluster.back;
		for(int i = 0; i < cluster.size[back]; i++) {
			if(!cluster.isDead[back][i]) {
				cluster.posX[back][i] = cluster.posX[front][i] + cluster.motX[back][i];
				cluster.posY[back][i] = cluster.posY[front][i] + cluster.motY[back][i];
				cluster.posZ[back][i] = cluster.posZ[front][i] + cluster.motZ[back][i];
			}
		}
	}

	@Override
	public final void updateMovement(DanmakuCluster cluster) {
		markDead(cluster);
		rotate(cluster);
		calcSpeed(cluster);
		addGravity(cluster);
		updatePos(cluster);
		cluster.ticksExisted[cluster.back]++;
	}

	@Override
	public void hitCheck(DanmakuCluster cluster) {
		VectorizedSubEntity.defaultHitCheck(cluster, this, entity -> cluster.user != entity && cluster.source != entity);
	}

	@Override
	public void handleImpacts(DanmakuCluster cluster, ArrayBuffer<RayTraceResult>[] impacts, RayTraceResult[] groundHits) {
		for(int i = 0; i < cluster.size[cluster.hitCheck]; i++) {
			int idx = i;
			ArrayBuffer<RayTraceResult> entityImpacts = impacts[i];
			if(entityImpacts != null) {
				entityImpacts.foreach(res -> {
					attackEntity(cluster, idx, res);
					return BoxedUnit.UNIT;
				});
			}

			RayTraceResult groundHit = groundHits[i];
			if(groundHit != null) {
				impactBlock(cluster, idx, groundHit);
			}
		}

	}

	protected boolean hasLittleHealth(Entity entity) {
		int safeguard = 0;
		while(safeguard++ < 10) {
			if(entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase)entity;
				return living.getHealth() / living.getMaxHealth() < 0.1;
			}
			else if(entity instanceof MultiPartEntityPart) {
				IEntityMultiPart parent = ((MultiPartEntityPart)entity).parent;
				if(parent instanceof Entity) {
					entity = (Entity)parent;
				}
				else {
					return false;
				}
			}
			else return false;
		}

		return false;
	}

	protected void attackEntity(DanmakuCluster cluster, int idx, RayTraceResult result) {
		Entity entity = result.entityHit;
		ShotData shot = cluster.shot;
		float averageSize = (shot.sizeY() + shot.sizeX() + shot.sizeZ()) / 3;

		if(averageSize < 0.7F) {
			cluster.isDead[cluster.hitCheck][idx] = true;
			cluster.deadCount[cluster.hitCheck] += 1;
		}

		if(!cluster.world.isRemote) {
			cluster.addCallback(() -> {
				DamageSourceDanmaku source = DamageSourceDanmaku.create(null); //TODO null
				float damage = DanmakuHelper.adjustDanmakuDamage(cluster.user, entity, shot.damage(), ConfigHandler.danmaku.danmakuLevel);

				DanmakuHitBehavior hitBehavior = entity.getCapability(CapabilityDanmakuHitBehaviorJ.HIT_BEHAVIOR, null);
				if(hitBehavior != null) {
					hitBehavior.onHit(null, entity, damage, source); //TODO null
				}
				else {
					entity.attackEntityFrom(source, damage);
				}

				if(hasLittleHealth(entity)) {
					entity.playSound(LibSounds.DAMAGE_LOW, 1F, 1F);
				}
				else {
					entity.playSound(LibSounds.DAMAGE, 1F, 1F);
				}

			});
		}
	}

	protected void impactBlock(DanmakuCluster cluster, int idx, RayTraceResult raytrace) {
		ShotData shot = cluster.shot;
		if(shot.sizeZ() <= 1F || shot.sizeZ() / shot.sizeX() <= 3 || shot.sizeZ() / shot.sizeY() <= 3) {
			cluster.isDead[cluster.hitCheck][idx] = true;
			cluster.deadCount[cluster.hitCheck] += 1;
		}
	}
}
