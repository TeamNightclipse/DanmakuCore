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
import net.katsstuff.danmakucore.entity.living.TouhouCharacter;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.helper.ItemNBTHelper;
import net.katsstuff.danmakucore.helper.StringNBTProperty;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.LibItemName;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.lib.data.LibSpellcards;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("WeakerAccess")
public class ItemSpellcard extends ItemBase implements IOwnedBy {

	private static final StringNBTProperty<ItemStack> SPELLCARD = StringNBTProperty.ofStack("spellcard", () -> LibSpellcards.DELUSION_OF_ENLIGHTENMENT.getFullNameString());

	public ItemSpellcard() {
		super(LibItemName.SPELLCARD);
		maxStackSize = 1;
		setMaxDamage(0);
		setCreativeTab(DanmakuCore.SPELLCARD_CREATIVE_TAB);
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		subItems.addAll(DanmakuRegistry.SPELLCARD.getValues().stream()
				.sorted()
				.map(ItemSpellcard::createStack)
				.collect(Collectors.toList()));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		Spellcard type = getSpellcard(stack);
		//noinspection ConstantConditions
		if(!world.isRemote && type.onRightClick(stack, world, player, hand)) {
			Optional<EntitySpellcard> result = TouhouHelper.declareSpellcardPlayer(player, type, true);
			return result.isPresent() ? new ActionResult<>(EnumActionResult.SUCCESS, stack) : new ActionResult<>(EnumActionResult.FAIL, stack);
		}
		else return super.onItemRightClick(world, player, hand);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + getSpellcard(stack).getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool) {
		super.addInformation(stack, player, list, bool);

		Spellcard type = getSpellcard(stack);
		String item = "item.spellcard";
		list.add(I18n.format(item + ".level") + " " + type.getLevel() + " " + I18n.format(item + ".spellcard"));
		list.add(I18n.format(item + ".user") + " : " + I18n.format(type.getUnlocalizedName()));
		list.add(I18n.format(item + ".removeTime") + " : " + type.getRemoveTime());
		list.add(I18n.format(item + ".endTime") + " : " + type.getEndTime());
	}

	@Override
	public TouhouCharacter character(ItemStack stack) {
		return getSpellcard(stack).getOriginalUser();
	}

	public static Spellcard getSpellcard(ItemStack stack) {
		Spellcard spellcard = DanmakuRegistry.SPELLCARD.getValue(new ResourceLocation(SPELLCARD.get(stack)));
		if(spellcard == null) {
			spellcard = LibSpellcards.DELUSION_OF_ENLIGHTENMENT;
			SPELLCARD.set(spellcard.getFullNameString(), stack);
		}

		return spellcard;
	}

	public static ItemStack createStack(Spellcard spellcard) {
		ItemStack stack = new ItemStack(LibItems.SPELLCARD, 1);
		SPELLCARD.set(spellcard.getFullNameString(), stack);
		return stack;
	}
}
