/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.subentity;

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.minecraft.world.World;

/**
 * Where you define special behavior for danmaku. The most used methods ones are provided already,
 * but it's entirely possible to create new ones, and then call those methods from elsewhere.
 */
public abstract class SubEntity {

	protected final World world;
	protected final EntityDanmaku danmaku;

	public SubEntity(World world, EntityDanmaku danmaku) {
		this.world = world;
		this.danmaku = danmaku;
	}

	/**
	 * Called each tick as long as the danmaku is alive, and it's delay is 0.
	 */
	public abstract void subEntityTick();

}
