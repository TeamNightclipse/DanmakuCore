/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.item;

import net.minecraft.item.Item;

@SuppressWarnings("WeakerAccess")
public class ItemBase extends Item {

	public ItemBase(String name) {
		setRegistryName(name);
		setUnlocalizedName(name);
	}
}
