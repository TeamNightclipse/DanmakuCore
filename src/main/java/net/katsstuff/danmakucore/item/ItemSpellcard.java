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
import net.katsstuff.danmakucore.entity.living.boss.EnumTouhouCharacters;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.helper.ItemNBTHelper;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.LibItemName;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.lib.data.LibSpellcards;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("WeakerAccess")
public class ItemSpellcard extends ItemBase implements IOwnedBy {

	private static final String SPELLCARD = "spellcard";

	public ItemSpellcard() {
		super(LibItemName.SPELLCARD);
		maxStackSize = 1;
		setMaxDamage(0);
		setCreativeTab(DanmakuCore.SPELLCARD_CREATIVE_TAB);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		migrateFromLegacy(stack);
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		Spellcard type = getSpellcard(stack);
		//noinspection ConstantConditions
		if(!world.isRemote && type != null && type.onRightClick(stack, world, player, hand)) {
			Optional<EntitySpellcard> result = TouhouHelper.declareSpellcardPlayer(player, type, true);
			return result.isPresent() ? new ActionResult<>(EnumActionResult.SUCCESS, stack) : new ActionResult<>(EnumActionResult.FAIL, stack);
		}
		else return super.onItemRightClick(stack, world, player, hand);
	}

	private void migrateFromLegacy(ItemStack stack) {
		if(!ItemNBTHelper.verifyExistance(stack, SPELLCARD)) {
			int id = stack.getItemDamage();
			Spellcard spellcard = DanmakuRegistry.getObjById(Spellcard.class, id);
			//noinspection ConstantConditions
			if(spellcard == null) {
				spellcard = LibSpellcards.DELUSION_OF_ENLIGHTENMENT;
				LogHelper.warn("Found null spellcard. Fixing");
			}

			ItemNBTHelper.setString(stack, SPELLCARD, spellcard.getFullName().toString());
			stack.setItemDamage(0);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + getSpellcard(stack).getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
		list.addAll(DanmakuRegistry.SPELLCARD.getValues().stream()
				.sorted()
				.map(ItemSpellcard::createStack)
				.collect(Collectors.toList()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool) {
		super.addInformation(stack, player, list, bool);

		Spellcard type = getSpellcard(stack);
		String item = "item.spellcard";
		list.add(I18n.format(item + ".level") + " " + type.getLevel() + " " + I18n.format(item + ".spellcard"));
		list.add(I18n.format(item + ".user") + " : " + I18n.format(item + ".userName." + type.getOriginalUser().getName()));
		list.add(I18n.format(item + ".removeTime") + " : " + type.getRemoveTime());
		list.add(I18n.format(item + ".endTime") + " : " + type.getEndTime());
	}

	@Override
	public EnumTouhouCharacters character(ItemStack stack) {
		return getSpellcard(stack).getOriginalUser();
	}

	public static Spellcard getSpellcard(ItemStack stack) {
		Spellcard spellcard = DanmakuRegistry.SPELLCARD.getValue(
				new ResourceLocation(ItemNBTHelper.getString(stack, SPELLCARD, LibSpellcards.DELUSION_OF_ENLIGHTENMENT.getFullName().toString())));
		if(spellcard == null) {
			spellcard = LibSpellcards.DELUSION_OF_ENLIGHTENMENT;
			ItemNBTHelper.setString(stack, SPELLCARD, spellcard.getFullName().toString());
		}

		return spellcard;
	}

	public static ItemStack createStack(Spellcard spellcard) {
		ItemStack stack = new ItemStack(LibItems.SPELLCARD, 1);
		ItemNBTHelper.setString(stack, SPELLCARD, spellcard.getFullName().toString());
		return stack;
	}
}
