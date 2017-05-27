/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */

package net.katsstuff.danmakucore.impl.shape;

import java.util.HashSet;
import java.util.Set;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;

@SuppressWarnings("unused")
public class ShapeSingle implements IShape {

	private final DanmakuTemplate danmaku;
	private final Set<EntityDanmaku> set = new HashSet<>(1);

	public ShapeSingle(DanmakuTemplate danmaku) {
		this.danmaku = danmaku;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Quat orientation, int tick) {
		if(!danmaku.world.isRemote) {
			EntityDanmaku created = danmaku.asEntity();
			set.add(created);
			danmaku.world.spawnEntityInWorld(created);
		}
		return new Tuple<>(true, set);
	}
}
