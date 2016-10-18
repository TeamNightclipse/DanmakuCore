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
import net.katsstuff.danmakucore.misc.ITranslatable;
import net.katsstuff.danmakucore.registry.RegistryValue;
import net.minecraft.world.World;

public abstract class SubEntityType extends RegistryValue<SubEntityType> implements ITranslatable {

	public SubEntityType(String name) {
		setRegistryName(name);
	}

	@SuppressWarnings("WeakerAccess")
	public SubEntityType() {}

	public abstract SubEntity instantiate(World world, EntityDanmaku entityDanmaku);

	@Override
	public String getUnlocalizedName() {
		return "subentity." + getModId() + "." + getName();
	}
}
