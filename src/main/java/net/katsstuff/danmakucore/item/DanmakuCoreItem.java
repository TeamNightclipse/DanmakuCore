/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.item;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.lib.LibItemName;
import net.minecraft.item.Item;

public class DanmakuCoreItem {

	public static Item danmaku;
	public static Item spellcard;
	public static Item bombItem;
	public static Item scoreItem;
	public static Item extendItem;

	public static void preInit() {
		danmaku = new ItemDanmaku();
		spellcard = new ItemSpellcard();
		bombItem = new ItemBase(LibItemName.BOMB).setCreativeTab(DanmakuCore.GENERAL_CREATIVE_TAB);
		scoreItem = new ItemBase(LibItemName.SCORE).setCreativeTab(DanmakuCore.GENERAL_CREATIVE_TAB);
		extendItem = new ItemBase(LibItemName.EXTEND).setMaxStackSize(8).setCreativeTab(DanmakuCore.GENERAL_CREATIVE_TAB);
	}
}
