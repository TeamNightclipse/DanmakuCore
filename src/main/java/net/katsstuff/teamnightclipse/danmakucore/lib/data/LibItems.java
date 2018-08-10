/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.lib.data;

import net.katsstuff.teamnightclipse.danmakucore.lib.LibItemName;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibModJ;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public class LibItems {

	@ObjectHolder(LibItemName.DANMAKU)
	public static final Item DANMAKU = new Item();
	@ObjectHolder(LibItemName.SPELLCARD)
	public static final Item SPELLCARD = new Item();
}
