/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.item;

import java.util.List;
import java.util.stream.Collectors;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.LibItemName;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.registry.Registry;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("WeakerAccess")
public class ItemSpellcard extends ItemBase {

	public ItemSpellcard() {
		super(LibItemName.SPELLCARD);
		setHasSubtypes(true);
		maxStackSize = 1;
		setMaxDamage(0);
		setCreativeTab(DanmakuCore.SPELLCARD_CREATIVE_TAB);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		Spellcard type = DanmakuRegistry.INSTANCE.spellcard.get(stack.getItemDamage());
		if(type.onRightClick(stack, world, player, hand)) {
			boolean result = TouhouHelper.declareSpellcardPlayer(player, type, true, false);
			return result ? ActionResult.newResult(EnumActionResult.SUCCESS, stack) : ActionResult.newResult(EnumActionResult.FAIL, stack);
		}
		else return super.onItemRightClick(stack, world, player, hand);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + DanmakuRegistry.INSTANCE.spellcard.get(stack.getItemDamage()).getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
		Registry<Spellcard> spellcard = DanmakuRegistry.INSTANCE.spellcard;
		list.addAll(spellcard.getValues().stream().sorted()
				.map(type -> new ItemStack(DanmakuCoreItem.spellcard, 1, spellcard.getId(type))).collect(Collectors.toList()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool) {
		super.addInformation(stack, player, list, bool);

		Spellcard type = DanmakuRegistry.INSTANCE.spellcard.get(stack.getItemDamage());
		String item = "item.spellcard";
		list.add(I18n.format(item + ".level") + " " + type.getNeededLevel() + " " + I18n.format(item + ".spellcard"));
		list.add(I18n.format(item + ".user") + " : " + I18n.format(item + ".userName." + type.getOriginalUser().getName()));
		list.add(I18n.format(item + ".removeTime") + " : " + type.getRemoveTime());
		list.add(I18n.format(item + ".endTime") + " : " + type.getEndTime());
	}
}
