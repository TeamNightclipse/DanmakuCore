/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai.pathfinding;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

//Some code taken from SwimNodeProcessor
public class FlyNodeProcessor extends NodeProcessor {

	@Override
	public PathPoint getStart() {
		return this.openPoint(MathHelper.floor(this.entity.getEntityBoundingBox().minX),
				MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getEntityBoundingBox().minZ));
	}

	@Override
	public PathPoint getPathPointToCoords(double x, double y, double z) {
		return this.openPoint(MathHelper.floor(x - this.entity.width / 2.0F), MathHelper.floor(y + 0.5D),
				MathHelper.floor(z - this.entity.width / 2.0F));
	}

	@Override
	public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
		int i = 0;

		for(EnumFacing enumfacing : EnumFacing.values()) {
			PathPoint pathpoint = this.getAirNode(currentPoint.x + enumfacing.getFrontOffsetX(),
					currentPoint.y + enumfacing.getFrontOffsetY(), currentPoint.z + enumfacing.getFrontOffsetZ());

			if(pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
				pathOptions[i++] = pathpoint;
			}
		}

		return i;
	}

	@Nullable
	private PathPoint getAirNode(int startX, int startY, int startZ) {
		PathNodeType pathnodetype = isFree(startX, startY, startZ);
		return pathnodetype == PathNodeType.OPEN ? openPoint(startX, startY, startZ) : null;
	}

	private PathNodeType isFree(int startX, int startY, int startZ) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for(int x = startX; x < startX + entitySizeX; ++x) {
			for(int y = startY; y < startY + entitySizeY; ++y) {
				for(int z = startZ; z < startZ + entitySizeZ; ++z) {
					IBlockState iblockstate = blockaccess.getBlockState(pos.setPos(x, y, z));

					if(!iblockstate.getBlock().isAir(iblockstate, blockaccess, pos)) {
						return PathNodeType.BLOCKED;
					}
				}
			}
		}

		return PathNodeType.OPEN;
	}

	public PathNodeType getPathNodeType(IBlockAccess blockAccess, int x, int y, int z, EntityLiving living, int xSize, int ySize, int zSize,
			boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
		return PathNodeType.OPEN;
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess x, int y, int z, int p_186330_4_) {
		return PathNodeType.OPEN;
	}
}
