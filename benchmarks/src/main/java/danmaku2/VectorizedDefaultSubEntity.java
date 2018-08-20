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

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import danmaku.MathUtil;
import danmaku.Quat;
import danmaku.ShotData;

@SuppressWarnings("Duplicates")
@OperationsPerInvocation(DanmakuCluster.DANMAKUS_PER_CLUSTER)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1,
	  jvmArgsAppend = {"-XX:+UseSuperWord", "-XX:+UnlockDiagnosticVMOptions", "-XX:CompileCommand=print,*VectorizedDefaultSubEntity.updatePos",
			  "-XX:-TieredCompilation", "-XX:PrintAssemblyOptions=intel"})
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class VectorizedDefaultSubEntity {

	@Benchmark
	public final void markDead(DanmakuCluster cluster, Blackhole bh) {
		int back = cluster.back;
		int size = cluster.size[back];
		int[] tickExisted = cluster.ticksExisted[back];
		boolean[] isDead = cluster.isDead[back];
		int[] deadCount = cluster.deadCount;
		ShotData[] shot = cluster.shot;

		for(int i = 0; i < size; i++) {
			if(tickExisted[i] > shot[i].end()) {
				isDead[i] = true;
				deadCount[back]++;
			}
		}

		bh.consume(isDead);
		bh.consume(deadCount[back]);
	}

	@Benchmark
	public final void rotate(DanmakuCluster cluster, Blackhole bh) {
		int front = cluster.front;
		int back = cluster.back;
		int size = cluster.size[back];

		double[] dirXFront = cluster.dirX[front];
		double[] dirYFront = cluster.dirY[front];
		double[] dirZFront = cluster.dirZ[front];
		double[] dirXBack = cluster.dirX[back];
		double[] dirYBack = cluster.dirY[back];
		double[] dirZBack = cluster.dirZ[back];
		Quat[] orientFront = cluster.orient[front];
		Quat[] orientBack = cluster.orient[back];

		boolean[] rotEnabled = cluster.rotEnabled;
		Quat[] rotQuat = cluster.rotQuat;
		int[] rotTime = cluster.rotTime;
		int[] ticksExisted = cluster.ticksExisted[back];

		for(int i = 0; i < size; i++) {
			if(rotEnabled[i] && rotTime[i] > ticksExisted[i]) {
				Quat rotation = rotQuat[i];

				double rotX = rotation.x();
				double rotY = rotation.y();
				double rotZ = rotation.z();
				double rotW = rotation.w();

				{
					Quat orient = orientFront[i];

					double ox = orient.x();
					double oy = orient.y();
					double oz = orient.z();
					double ow = orient.w();

					double newX = ow * rotX + ox * rotW + oy * rotZ - oz * rotY;
					double newY = ow * rotY + oy * rotW + oz * rotX - ox * rotZ;
					double newZ = ow * rotZ + oz * rotW + ox * rotY - oy * rotX;
					double newW = ow * rotW - ox * rotX - oy * rotY - oz * rotZ;

					orientBack[i] = new Quat(newX, newY, newZ, newW);
				}

				double x = dirXFront[i];
				double y = dirYFront[i];
				double z = dirZFront[i];

				double tx = 2 * (rotY * z - rotZ * y);
				double ty = 2 * (rotZ * x - rotX * z);
				double tz = 2 * (rotX * y - rotY * x);

				double cx = rotY * tz - rotZ * ty;
				double cy = rotZ * tx - rotX * tz;
				double cz = rotX * ty - rotY * tx;

				dirXBack[i] = x + rotW * tx + cx;
				dirYBack[i] = y + rotW * ty + cy;
				dirZBack[i] = z + rotW * tz + cz;
			}
		}

		bh.consume(dirXBack);
		bh.consume(dirYBack);
		bh.consume(dirZBack);
		bh.consume(orientBack);
	}

	@Benchmark
	public final void calcCurrentSpeed(DanmakuCluster cluster, Blackhole bh) {
		int back = cluster.back;
		int front = cluster.front;
		int size = cluster.size[back];
		double[] motX = cluster.motX[front];
		double[] motY = cluster.motY[front];
		double[] motZ = cluster.motZ[front];

		double[] currentSpeedSq = cluster.currentSpeedSq;

		for(int i = 0; i < size; i++) {
			double x = motX[i];
			double y = motY[i];
			double z = motZ[i];
			currentSpeedSq[i] = x * x + y * y + z * z;
		}

		bh.consume(currentSpeedSq);
	}

	@Benchmark
	public final void calcSpeed(DanmakuCluster cluster, Blackhole bh) {
		calcCurrentSpeed(cluster, bh);
		int front = cluster.front;
		int back = cluster.back;

		double[] speedAccelArr = cluster.speedAcceleration;
		double[] upperSpeedLimitArr = cluster.speedUpperLimit;
		double[] lowerSpeedLimitArr = cluster.speedLowerLimit;
		double[] upperSpeedLimitSqArr = cluster.speedLowerLimitSq;
		double[] lowerSpeedLimitSqArr = cluster.speedLowerLimitSq;

		int size = cluster.size[back];
		double[] currentSpeedSqArr = cluster.currentSpeedSq;
		double[] dirX = cluster.dirX[back];
		double[] dirY = cluster.dirY[back];
		double[] dirZ = cluster.dirZ[back];
		double[] motXBack = cluster.motX[back];
		double[] motYBack = cluster.motY[back];
		double[] motZBack = cluster.motZ[back];
		double[] motXFront = cluster.motX[front];
		double[] motYFront = cluster.motY[front];
		double[] motZFront = cluster.motZ[front];


		for(int i = 0; i < size; i++) {
			double currentSpeedSq = currentSpeedSqArr[i];
			double dx = dirX[i];
			double dy = dirY[i];
			double dz = dirZ[i];

			double speedAccel = speedAccelArr[i];
			double upperSpeedLimit = upperSpeedLimitArr[i];
			double lowerSpeedLimit = lowerSpeedLimitArr[i];
			double upperSpeedLimitSq = upperSpeedLimitSqArr[i];
			double lowerSpeedLimitSq = lowerSpeedLimitSqArr[i];

			if(MathUtil.fuzzyCompare(currentSpeedSq, upperSpeedLimitSq) >= 0 && speedAccel >= 0D) {
				motXBack[i] = dx * upperSpeedLimit;
				motYBack[i] = dy * upperSpeedLimit;
				motZBack[i] = dz * upperSpeedLimit;
			}
			else if(MathUtil.fuzzyCompare(currentSpeedSq, lowerSpeedLimitSq) <= 0 && speedAccel <= 0D) {
				motXBack[i] = dx * lowerSpeedLimit;
				motYBack[i] = dy * lowerSpeedLimit;
				motZBack[i] = dz * lowerSpeedLimit;
			}
			else {
				double x = motXFront[i] + dx * speedAccel;
				double y = motYFront[i] + dy * speedAccel;
				double z = motZFront[i] + dz * speedAccel;

				currentSpeedSq = x * x + y * y + z * z;

				if(MathUtil.fuzzyCompare(currentSpeedSq, upperSpeedLimitSq) > 0) {
					motXBack[i] = dx * upperSpeedLimit;
					motYBack[i] = dy * upperSpeedLimit;
					motZBack[i] = dz * upperSpeedLimit;
				}
				else if(MathUtil.fuzzyCompare(currentSpeedSq, lowerSpeedLimitSq) < 0) {
					motXBack[i] = dx * lowerSpeedLimit;
					motYBack[i] = dy * lowerSpeedLimit;
					motZBack[i] = dz * lowerSpeedLimit;
				}
				else {
					motXBack[i] = x;
					motYBack[i] = y;
					motZBack[i] = z;
				}
			}
		}

		bh.consume(motXBack);
		bh.consume(motYBack);
		bh.consume(motZBack);
	}

	@Benchmark
	public final void addGravity(DanmakuCluster cluster, Blackhole bh) {
		int back = cluster.back;
		double[] motX = cluster.motX[back];
		double[] motY = cluster.motY[back];
		double[] motZ = cluster.motZ[back];
		double[] gravX = cluster.gravX;
		double[] gravY = cluster.gravY;
		double[] gravZ = cluster.gravZ;

		for(int i = 0; i < cluster.size[back]; i++) {
			motX[i] += gravX[i];
			motY[i] += gravY[i];
			motZ[i] += gravZ[i];
		}

		bh.consume(motX);
		bh.consume(motY);
		bh.consume(motZ);
	}

	@Benchmark
	public final void updatePos(DanmakuCluster cluster, Blackhole bh) {
		int front = cluster.front;
		int back = cluster.back;
		int size = cluster.size[back];
		double[] posXBack = cluster.posX[back];
		double[] posYBack = cluster.posY[back];
		double[] posZBack = cluster.posZ[back];

		double[] posXFront = cluster.posX[front];
		double[] posYFront = cluster.posY[front];
		double[] posZFront = cluster.posZ[front];

		double[] motX = cluster.motX[back];
		double[] motY = cluster.motY[back];
		double[] motZ = cluster.motZ[back];

		for(int i = 0; i < size; i++) {
			posXBack[i] = posXFront[i] + motX[i];
			posYBack[i] = posYFront[i] + motY[i];
			posZBack[i] = posZFront[i] + motZ[i];
		}

		bh.consume(posXBack);
		bh.consume(posYBack);
		bh.consume(posZBack);
	}

	//@Benchmark
	public final void updateMovement(DanmakuCluster cluster, Blackhole bh) {
		markDead(cluster, bh);
		rotate(cluster, bh);
		calcSpeed(cluster, bh);
		addGravity(cluster, bh);
		updatePos(cluster, bh);

		int back = cluster.back;
		int size = cluster.size[back];
		int[] ticksExisted = cluster.ticksExisted[back];
		for(int i = 0; i < size; i++) {
			ticksExisted[i]++;
		}

		bh.consume(ticksExisted);
	}

}
