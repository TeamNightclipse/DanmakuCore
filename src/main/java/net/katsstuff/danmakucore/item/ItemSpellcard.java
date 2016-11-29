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
import java.util.Optional;
import java.util.stream.Collectors;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.LibItemName;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
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
		Spellcard type = DanmakuRegistry.SPELLCARD.getObjectById(stack.getItemDamage());
		if(!world.isRemote && type.onRightClick(stack, world, player, hand)) {
			Optional<EntitySpellcard> result = TouhouHelper.declareSpellcardPlayer(player, type, true);
			return result.isPresent() ? new ActionResult<>(EnumActionResult.SUCCESS, stack) : new ActionResult<>(EnumActionResult.FAIL, stack);
		}
		else return super.onItemRightClick(stack, world, player, hand);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + DanmakuRegistry.SPELLCARD.getObjectById(stack.getItemDamage()).getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
		FMLControlledNamespacedRegistry<Spellcard> spellcard = DanmakuRegistry.SPELLCARD;
		list.addAll(spellcard.getValues()
				.stream()
				.sorted()
				.map(type -> new ItemStack(LibItems.SPELLCARD, 1, spellcard.getId(type)))
				.collect(Collectors.toList()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool) {
		super.addInformation(stack, player, list, bool);

		Spellcard type = DanmakuRegistry.SPELLCARD.getObjectById(stack.getItemDamage());
		String item = "item.spellcard";
		list.add(I18n.format(item + ".level") + " " + type.getLevel() + " " + I18n.format(item + ".spellcard"));
		list.add(I18n.format(item + ".user") + " : " + I18n.format(item + ".userName." + type.getOriginalUser().getName()));
		list.add(I18n.format(item + ".removeTime") + " : " + type.getRemoveTime());
		list.add(I18n.format(item + ".endTime") + " : " + type.getEndTime());
	}
}
