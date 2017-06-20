/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore;

import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class DanmakuCoreCreativeTab extends CreativeTabs {

	DanmakuCoreCreativeTab(String label) {
		super(LibMod.MODID + "." + label);
		setNoTitle();
		setBackgroundImageName("item_search.png");
	}

	@Override
	public boolean hasSearchBar() {
		return true;
	}
}
