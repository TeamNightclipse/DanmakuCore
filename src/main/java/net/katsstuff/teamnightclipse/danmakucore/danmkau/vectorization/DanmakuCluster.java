/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version BUFFER_AMOUNT of the License, or
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

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.katsstuff.teamnightclipse.danmakucore.danmaku.vectorization.VectorizedSubEntity;
import net.katsstuff.teamnightclipse.danmakucore.data.MovementData;
import net.katsstuff.teamnightclipse.danmakucore.data.OrientedBoundingBox;
import net.katsstuff.teamnightclipse.danmakucore.data.RotationData;
import net.katsstuff.teamnightclipse.danmakucore.data.ShotData;
import net.katsstuff.teamnightclipse.mirror.data.Quat;
import net.katsstuff.teamnightclipse.mirror.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DanmakuCluster {

	public static final int DANMAKUS_PER_CLUSTER = 256;
	private static final int BUFFER_AMOUNT = 3;

	private int updates = 0;

	public int back = 0;
	public int hitCheck = 0;
	public int front = 0;

	public int[] size = new int[BUFFER_AMOUNT];
	public int[] sizeWithNew = new int[BUFFER_AMOUNT];

	public final double[][] posX = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];
	public final double[][] posY = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];
	public final double[][] posZ = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];

	public final double[][] prevPosX = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];
	public final double[][] prevPosY = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];
	public final double[][] prevPosZ = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];

	public final double[][] dirX = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];
	public final double[][] dirY = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];
	public final double[][] dirZ = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];

	public final double[][] motX = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];
	public final double[][] motY = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];
	public final double[][] motZ = new double[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];

	public int[] deadCount = new int[BUFFER_AMOUNT];
	public final boolean[][] isDead = new boolean[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];

	public final Quat[][] orient = new Quat[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];

	public final double[] currentSpeedSq = new double[BUFFER_AMOUNT];
	public final int[][] ticksExisted = new int[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];

	public final OrientedBoundingBox[][] rawBoundingBoxes = new OrientedBoundingBox[DANMAKUS_PER_CLUSTER][];
	public final AxisAlignedBB[] rawEncompassingAABB = new AxisAlignedBB[DANMAKUS_PER_CLUSTER];

	public final double speedOrig;
	public final double speedLowerLimit;
	public final double speedUpperLimit;
	public final double speedAcceleration;

	public final double speedLowerLimitSq;
	public final double speedUpperLimitSq;

	public final double gravX;
	public final double gravY;
	public final double gravZ;

	public final boolean rotEnabled;
	public final Quat rot;
	public final int rotTime;

	public final World world;
	@Nullable
	public final EntityLivingBase user;
	@Nullable
	public final Entity source;
	public final MovementData movement;
	public final RotationData rotation;
	public final ShotData shot;
	public final VectorizedSubEntity subEntity;

	private final ArrayList<Runnable> callbacks = new ArrayList<>();

	public DanmakuCluster(World world, @Nullable EntityLivingBase user, @Nullable Entity source, MovementData movement, RotationData rotation,
			ShotData shot, VectorizedSubEntity subEntity) {
		Preconditions.checkArgument(shot.delay() == 0 && subEntity instanceof DefaultVectorizedSubEntity, "Delay is not allowed");
		this.world = world;
		this.user = user;
		this.source = source;
		this.movement = movement;
		this.rotation = rotation;
		this.shot = shot;
		this.subEntity = subEntity;

		speedOrig = movement.speedOriginal();
		speedLowerLimit = movement.lowerSpeedLimit();
		speedUpperLimit = movement.upperSpeedLimit();
		speedAcceleration = movement.speedAcceleration();

		speedUpperLimitSq = speedUpperLimit * speedUpperLimit;
		speedLowerLimitSq = speedLowerLimit * speedLowerLimit;

		Vector3 gravity = movement.gravity();
		gravX = gravity.x();
		gravY = gravity.y();
		gravZ = gravity.z();

		rotEnabled = rotation.enabled();
		rot = rotation.rotationQuat();
		rotTime = rotation.endTime();
	}

	public void tickStarted() {
		if(updates == 0) {
			updateMovement();
			back = 1;
			doHitCheck();
			hitCheck = 1;

			updates++;
		}
		else {
			doHitCheck();
			swapBuffers();
		}
	}

	public void tickEnded() {
		if(updates == 1) {
			updateMovement();
			back = 2;
		}
		else {
			updateMovement();
			swapBuffers();
		}
		updates++;
	}

	private void updateMovement() {
		subEntity.updateMovement(this);
	}

	private void doHitCheck() {
		subEntity.hitCheck(this);
	}

	private void swapBuffers() {
		back = (back + 1) % BUFFER_AMOUNT;
		hitCheck = (hitCheck + 1) % BUFFER_AMOUNT;
		front = (front + 1) % BUFFER_AMOUNT;
	}

	public void addCallback(Runnable runnable) {
		callbacks.add(runnable);
	}

	public void handleCallbacks() {
		for(Runnable callback : callbacks) {
			callback.run();
		}
		callbacks.clear();
	}
}
