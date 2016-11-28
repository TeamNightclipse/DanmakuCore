/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai.pathfinding;

import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

//Some code taken from PathNavigateSwimmer
public class PathNavigateFlyer extends PathNavigateGround {

	private final EntityDanmakuMob danmakuMob;

	public PathNavigateFlyer(EntityDanmakuMob entityDanmakuIn, World worldIn) {
		super(entityDanmakuIn, worldIn);
		danmakuMob = entityDanmakuIn;
	}

	@Override
	protected PathFinder getPathFinder() {
		WalkNodeProcessor walkNodeProcessor = new WalkNodeProcessor();
		walkNodeProcessor.setCanEnterDoors(true);
		nodeProcessor = new DualNodeProcessor(new FlyNodeProcessor(), walkNodeProcessor,
				living -> living instanceof EntityDanmakuMob && ((EntityDanmakuMob)living).isFlying());
		return new PathFinder(new DualNodeProcessor(new FlyNodeProcessor(), walkNodeProcessor,
				living -> living instanceof EntityDanmakuMob && ((EntityDanmakuMob)living).isFlying()));
	}

	@Override
	protected boolean canNavigate() {
		return danmakuMob.isFlying() || super.canNavigate();
	}

	@Override
	protected Vec3d getEntityPosition() {
		if(danmakuMob.isFlying()) {
			return new Vec3d(this.theEntity.posX, this.theEntity.posY + this.theEntity.height * 0.5D, this.theEntity.posZ);
		}
		else {
			return super.getEntityPosition();
		}
	}

	@Override
	protected void pathFollow() {
		if(danmakuMob.isFlying()) {
			Vec3d vec3d = this.getEntityPosition();
			float f = this.theEntity.width * this.theEntity.width;

			if(vec3d.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < f) {
				this.currentPath.incrementPathIndex();
			}

			for(int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1);
					j > this.currentPath.getCurrentPathIndex(); --j) {
				Vec3d vec3d1 = this.currentPath.getVectorFromIndex(this.theEntity, j);

				if(vec3d1.squareDistanceTo(vec3d) <= 36.0D && this.isDirectPathBetweenPoints(vec3d, vec3d1, 0, 0, 0)) {
					this.currentPath.setCurrentPathIndex(j);
					break;
				}
			}

			this.checkForStuck(vec3d);
		}
		else {
			super.pathFollow();
		}
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
		if(danmakuMob.isFlying()) {
			RayTraceResult raytraceresult = this.worldObj.rayTraceBlocks(posVec31,
					new Vec3d(posVec32.xCoord, posVec32.yCoord + (double)this.theEntity.height * 0.5D, posVec32.zCoord), false, true, false);
			return raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS;
		}
		else {
			return super.isDirectPathBetweenPoints(posVec31, posVec32, sizeX, sizeY, sizeZ);
		}
	}

	@Override
	public boolean canEntityStandOnPos(BlockPos pos) {
		if(danmakuMob.isFlying()) {
			return !this.worldObj.getBlockState(pos).isFullBlock();
		}
		else {
			return super.canEntityStandOnPos(pos);
		}
	}
}
