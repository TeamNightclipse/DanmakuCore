/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai.pathfinding;

import java.util.function.Predicate;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;

public class DualNodeProcessor extends NodeProcessor {

	private final NodeProcessor first;
	private final NodeProcessor second;
	private final Predicate<EntityLiving> decide;

	@SuppressWarnings("WeakerAccess")
	public DualNodeProcessor(NodeProcessor first, NodeProcessor second, Predicate<EntityLiving> decide) {
		this.first = first;
		this.second = second;
		this.decide = decide;
	}

	@Override
	public void initProcessor(IBlockAccess sourceIn, EntityLiving mob) {
		super.initProcessor(sourceIn, mob);
		if(decide.test(entity)) first.initProcessor(sourceIn, mob);
		else second.initProcessor(sourceIn, mob);
	}

	@Override
	public void postProcess() {
		if(decide.test(entity)) first.postProcess();
		else second.postProcess();
		super.postProcess();
	}

	@Override
	public PathPoint getStart() {
		return decide.test(entity) ? first.getStart() : second.getStart();
	}

	@Override
	public PathPoint getPathPointToCoords(double x, double y, double z) {
		return decide.test(entity) ? first.getPathPointToCoords(x, y, z) : second.getPathPointToCoords(x, y, z);
	}

	@Override
	public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
		return decide.test(entity) ? first.findPathOptions(pathOptions, currentPoint, targetPoint, maxDistance) : second.findPathOptions(pathOptions,
				currentPoint, targetPoint, maxDistance);
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockAccess, int x, int y, int z, EntityLiving living, int xSize, int ySize, int zSize,
			boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
		return decide.test(entity) ? first.getPathNodeType(blockAccess, x, y, z, living, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn) :
				second.getPathNodeType(blockAccess, x, y, z, living, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockAccess, int x, int y, int z) {
		return decide.test(entity) ? first.getPathNodeType(blockAccess, x, y, z) : second.getPathNodeType(blockAccess, x, y, z);
	}

	public void setCanEnterDoors(boolean canEnterDoorsIn) {
		if(decide.test(entity)) first.setCanEnterDoors(canEnterDoorsIn);
		else second.setCanEnterDoors(canEnterDoorsIn);
	}

	public void setCanBreakDoors(boolean canBreakDoorsIn) {
		if(decide.test(entity)) first.setCanBreakDoors(canBreakDoorsIn);
		else second.setCanBreakDoors(canBreakDoorsIn);
	}

	public void setCanSwim(boolean canSwimIn) {
		if(decide.test(entity)) first.setCanSwim(canSwimIn);
		else second.setCanSwim(canSwimIn);
	}

	public boolean getCanEnterDoors() {
		return decide.test(entity) ? first.getCanEnterDoors() : second.getCanEnterDoors();
	}

	public boolean getCanBreakDoors() {
		return decide.test(entity) ? first.getCanBreakDoors() : second.getCanBreakDoors();
	}

	public boolean getCanSwim() {
		return decide.test(entity) ? first.getCanSwim() : second.getCanSwim();
	}
}
