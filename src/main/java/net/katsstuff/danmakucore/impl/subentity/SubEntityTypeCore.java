/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity;

import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.lib.LibMod;

public abstract class SubEntityTypeCore extends SubEntityType {

	private final String name;

	SubEntityTypeCore(String name) {
		setRegistryName(name);
		this.name = name;
	}

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName() + "." + LibMod.MODID +  "." + name;
	}
}
