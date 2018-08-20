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
package danmaku2;

import java.util.Random;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import danmaku.MovementData;
import danmaku.Quat;
import danmaku.RotationData;
import danmaku.ShotData;
import danmaku.Vector3;

@SuppressWarnings("WeakerAccess")
@State(Scope.Thread)
public class DanmakuCluster {

	public static final int DANMAKUS_PER_CLUSTER = 128;
	private static final int BUFFER_AMOUNT = 3;

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

	public final double[] currentSpeedSq = new double[DANMAKUS_PER_CLUSTER];
	public final int[][] ticksExisted = new int[BUFFER_AMOUNT][DANMAKUS_PER_CLUSTER];

	public final double[] speedOrig = new double[DANMAKUS_PER_CLUSTER];
	public final double[] speedLowerLimit = new double[DANMAKUS_PER_CLUSTER];
	public final double[] speedUpperLimit = new double[DANMAKUS_PER_CLUSTER];
	public final double[] speedAcceleration = new double[DANMAKUS_PER_CLUSTER];

	public final double[] speedLowerLimitSq = new double[DANMAKUS_PER_CLUSTER];
	public final double[] speedUpperLimitSq = new double[DANMAKUS_PER_CLUSTER];

	public final double[] gravX = new double[DANMAKUS_PER_CLUSTER];
	public final double[] gravY = new double[DANMAKUS_PER_CLUSTER];
	public final double[] gravZ = new double[DANMAKUS_PER_CLUSTER];

	public final boolean[] rotEnabled = new boolean[DANMAKUS_PER_CLUSTER];
	public final Quat[] rotQuat = new Quat[DANMAKUS_PER_CLUSTER];
	public final int[] rotTime = new int[DANMAKUS_PER_CLUSTER];

	public final ShotData[] shot = new ShotData[DANMAKUS_PER_CLUSTER];

	@Setup
	public void setup() {
		Random rand = new Random();
		ShotData shot = new ShotData(0xFF0000, 0xFFFFFF, 0.4F, 0.5F, 0.5F, 0.5F, 0, 80);
		for(int i = 0; i < DANMAKUS_PER_CLUSTER; i++) {
			Vector3 pos       = Vector3.randomDirection().multiply(rand.nextInt(), rand.nextInt(), rand.nextInt());
			Vector3 direction = Vector3.randomDirection().multiply(rand.nextInt(), rand.nextInt(), rand.nextInt());

			MovementData movement = new MovementData(0.4D, 0.2D, 0.6D, 0.01D, new Vector3(0D, 0D, 0D));
			RotationData rotation = new RotationData(true, Quat.fromEuler(5F, 10F, 0F), 9999);

			this.shot[i] = shot;

			speedOrig[i] = movement.speedOriginal();
			speedLowerLimit[i] = movement.lowerSpeedLimit();
			speedUpperLimit[i] = movement.upperSpeedLimit();
			speedAcceleration[i] = movement.speedAcceleration();

			speedUpperLimitSq[i] = speedUpperLimit[i] * speedUpperLimit[i];
			speedLowerLimitSq[i] = speedLowerLimit[i] * speedLowerLimit[i];

			Vector3 gravity = movement.gravity();
			gravX[i] = gravity.x();
			gravY[i] = gravity.y();
			gravZ[i] = gravity.z();

			rotEnabled[i] = rotation.enabled();
			rotQuat[i] = rotation.rotationQuat();
			rotTime[i] = rotation.endTime();

			for(int j = 0; j < BUFFER_AMOUNT; j++) {
				posX[j][i] = pos.x();
				posY[j][i] = pos.y();
				posZ[j][i] = pos.z();

				prevPosX[j][i] = pos.x();
				prevPosY[j][i] = pos.y();
				prevPosZ[j][i] = pos.z();

				dirX[j][i] = direction.x();
				dirY[j][i] = direction.y();
				dirZ[j][i] = direction.z();

				motX[j][i] = direction.x() * 0.4D;
				motY[j][i] = direction.y() * 0.4D;
				motZ[j][i] = direction.z() * 0.4D;

				ticksExisted[j][i] = i % 85;

				orient[j][i] = Quat.fromEulerRad((float)direction.yawRad(), (float)direction.pitchRad(), 0F);
			}
		}

		for(int i = 0; i < BUFFER_AMOUNT; i++) {
			size[i] = DANMAKUS_PER_CLUSTER;
			sizeWithNew[i] = DANMAKUS_PER_CLUSTER;
		}
	}
}
