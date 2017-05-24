/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.item;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.helper.DanmakuCreationHelper;
import net.katsstuff.danmakucore.helper.DanmakuHelper;
import net.katsstuff.danmakucore.helper.ItemNBTHelper;
import net.katsstuff.danmakucore.lib.LibItemName;
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.lib.data.LibSubEntities;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.registry.RegistryValueItemStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
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

@SuppressWarnings("unused")
public class ItemDanmaku extends ItemBase {

	private static final String NBT_GRAVITYX = "gravityX";
	private static final String NBT_GRAVITYY = "gravityY";
	private static final String NBT_GRAVITYZ = "gravityZ";
	private static final String NBT_PATTERN = "pattern";
	private static final String NBT_INFINITY = "infinity";
	private static final String NBT_AMOUNT = "amount";
	private static final String NBT_CUSTOM = "custom";
	private static final String NBT_SPEED = "speed";

	public ItemDanmaku() {
		super(LibItemName.DANMAKU);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(DanmakuCore.DANMAKU_CREATIVE_TAB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
		FMLControlledNamespacedRegistry<DanmakuVariant> variantRegistry = DanmakuRegistry.DANMAKU_VARIANT;
		list.addAll(variantRegistry.getValues().stream()
				.sorted()
				.map(variant -> {
					ShotData shot = variant.getShotData().setColor(DanmakuHelper.randomSaturatedColor());
					ItemStack stack = new ItemStack(LibItems.DANMAKU, 1, variantRegistry.getId(variant));
					return ShotData.serializeNBTItemStack(stack, shot);})
				.collect(Collectors.toList()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return getInfinity(stack);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String name = getCustom(stack) ?
				ShotData.fromNBTItemStack(stack).form().getUnlocalizedName() :
				DanmakuRegistry.DANMAKU_VARIANT.getObjectById(stack.getItemDamage()).getUnlocalizedName();
		return this.getUnlocalizedName() + "." + name;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		FMLControlledNamespacedRegistry<? extends RegistryValueItemStack<? extends RegistryValueItemStack<?>>> registry = getCustom(stack) ?
				DanmakuRegistry.FORM : DanmakuRegistry.DANMAKU_VARIANT;

		if(!registry.getObjectById(stack.getItemDamage()).onRightClick(stack, world, player, hand)) {
			return super.onItemRightClick(stack, world, player, hand);
		}

		boolean success = false;
		if(!world.isRemote) {
			if(player.capabilities.isCreativeMode) {
				setInfinity(stack, true);
			}

			success = shootDanmaku(stack, player.world, player, player.isSneaking(),
					new Vector3(player.posX, player.posY + player.eyeHeight - ShotData.fromNBTItemStack(stack).sizeY() / 2, player.posZ),
					new Vector3(player.getLookVec()), ShotData.fromNBTItemStack(stack).sizeZ() / 4);

			if(!getInfinity(stack) && success) {
				stack.stackSize--;
			}
		}
		DanmakuHelper.playShotSound(player);

		return new ActionResult<>(success | world.isRemote ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
	}

	public static boolean shootDanmaku(ItemStack stack, World world, @Nullable EntityLivingBase player, boolean alternateMode, Vector3 pos,
			Vector3 angle, double offset) {
		if(getCustom(stack)) {
			Form formType = DanmakuRegistry.FORM.getObjectById(stack.getItemDamage());
			//noinspection ConstantConditions
			if(formType == null) {
				LogHelper.warn("Found null form. Setting to default");
				stack.setItemDamage(DanmakuRegistry.FORM.getId(LibForms.SPHERE));
				return false;
			}

			if(!formType.onShootDanmaku(player, alternateMode, pos, angle)) return false;
		}
		else {
			DanmakuVariant danmakuVariant = DanmakuRegistry.DANMAKU_VARIANT.getObjectById(stack.getItemDamage());
			//noinspection ConstantConditions
			if(danmakuVariant == null) {
				LogHelper.warn("Found null form. Setting to default");
				stack.setItemDamage(DanmakuRegistry.FORM.getId(LibForms.SPHERE));
				return false;
			}

			if(!danmakuVariant.onShootDanmaku(player, alternateMode, pos, angle)) return false;
		}

		int amount = getAmount(stack);
		double shotSpeed = getSpeed(stack);
		int danmakuPattern = getPattern(stack);
		ShotData shot = ShotData.fromNBTItemStack(stack);
		Vector3 gravity = getGravity(stack);

		float wide;
		DanmakuTemplate.Builder danmaku = DanmakuTemplate.builder();
		danmaku.setUser(player).setShot(shot).setWorld(world).setMovementData(shotSpeed, gravity).setPos(pos).setAngle(angle);
		DanmakuTemplate built = danmaku.build();

		switch(danmakuPattern) {
			case 0:
				danmaku.setPos(pos.offset(angle, offset));

				for(int i = 1; i <= amount; i++) {
					danmaku.setMovementData(shotSpeed / amount * i);
					built.world.spawnEntityInWorld(danmaku.build().asEntity());
				}
				break;
			case 1:
				wide = 120F;
				if(alternateMode) {
					wide *= 0.5F;
				}
				DanmakuCreationHelper.createRandomRingShot(built, amount, wide, offset);
				break;
			case 2:
				wide = amount * 8F;
				if(alternateMode) {
					wide = wide * 0.5F;
				}
				DanmakuCreationHelper.createWideShot(built, amount, wide, 0F, offset);
				break;
			case 3:
				DanmakuCreationHelper.createCircleShot(built, amount, 0F, offset);
				break;
			case 4:
				danmaku.setMovementData(Vector3.GravityZero());
				DanmakuCreationHelper.createStarShot(danmaku.build(), amount, 0F, 0F, offset);
				break;
			case 5:
				wide = 15F;
				if(alternateMode) {
					wide *= 0.5F;
				}
				DanmakuCreationHelper.createRingShot(built, amount, wide, world.rand.nextFloat() * 360F, offset);
				break;
			default:
				break;
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool) {
		super.addInformation(stack, player, list, bool);
		ShotData shot = ShotData.fromNBTItemStack(stack);
		int amount = getAmount(stack);
		double shotSpeed = getSpeed(stack);
		int danmakuPattern = getPattern(stack);
		Vector3 gravity = getGravity(stack);
		boolean isInfinity = getInfinity(stack);
		boolean custom = getCustom(stack);

		float powerDamage = new BigDecimal(DanmakuHelper.adjustDamageCoreData(player, shot.damage()) - shot.damage())
				.setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
		String item = "item.danmaku";

		list.add(I18n.format(item + ".damage") + " : " + shot.damage() + " + " + powerDamage);
		list.add(I18n.format(item + ".size") + " : " + shot.sizeX() + ", " + shot.sizeY() + " " + shot.sizeZ());
		list.add(I18n.format(item + ".amount") + " : " + amount);
		if(custom) {
			list.add(I18n.format(item + ".form") + " : " + I18n.format(shot.form().getUnlocalizedName()));
		}
		list.add(I18n.format(item + ".pattern") + " : " + I18n.format(item + ".pattern." + danmakuPattern));
		list.add(I18n.format(item + ".speed") + " : " + shotSpeed);
		if(gravity.x() != 0D || gravity.y() != 0D || gravity.z() != 0D) {
			list.add(I18n.format(item + ".gravity") + " : " + gravity.x() + " " + gravity.y() + " " + gravity.z());
		}
		else {
			list.add(I18n.format(item + ".gravity") + " : " + I18n.format(item + ".noGravity"));
		}
		if(DanmakuHelper.isNormalColor(shot.color())) {
			list.add(I18n.format(item + ".color") + " : " + I18n.format(item + ".color." + shot.color()));
		}
		else {
			list.add(I18n.format(item + ".color") + " : " + I18n.format(item + ".color.custom"));
		}
		if(shot.subEntity() != LibSubEntities.DEFAULT_TYPE) {
			list.add(I18n.format(item + ".subentity") + " : " + I18n.format(shot.subEntity().getUnlocalizedName()));
		}
		if(isInfinity) {
			list.add(I18n.format(item + ".infinity"));
		}
		if(custom) {
			list.add(I18n.format(item + ".custom"));
		}
	}

	@SuppressWarnings("WeakerAccess")
	public static Vector3 getGravity(ItemStack stack) {
		double gravityX = ItemNBTHelper.getDouble(stack, NBT_GRAVITYX, 0D);
		double gravityY = ItemNBTHelper.getDouble(stack, NBT_GRAVITYY, 0D);
		double gravityZ = ItemNBTHelper.getDouble(stack, NBT_GRAVITYZ, 0D);
		return new Vector3(gravityX, gravityY, gravityZ);
	}

	public static void setGravity(ItemStack stack, Vector3 gravity) {
		ItemNBTHelper.setDouble(stack, NBT_GRAVITYX, gravity.x());
		ItemNBTHelper.setDouble(stack, NBT_GRAVITYY, gravity.y());
		ItemNBTHelper.setDouble(stack, NBT_GRAVITYZ, gravity.z());
	}

	@SuppressWarnings("WeakerAccess")
	public static int getPattern(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, NBT_PATTERN, 0);
	}

	public static void setPattern(ItemStack stack, int pattern) {
		ItemNBTHelper.setInt(stack, NBT_PATTERN, pattern);
	}

	@SuppressWarnings("WeakerAccess")
	public static int getAmount(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, NBT_AMOUNT, 1);
	}

	public static void setAmount(ItemStack stack, int amount) {
		ItemNBTHelper.setInt(stack, NBT_AMOUNT, amount);
	}

	@SuppressWarnings("WeakerAccess")
	public static double getSpeed(ItemStack stack) {
		return ItemNBTHelper.getDouble(stack, NBT_SPEED, 0.4D);
	}

	public static void setSpeed(ItemStack stack, double speed) {
		ItemNBTHelper.setDouble(stack, NBT_SPEED, speed);
	}

	public static boolean getCustom(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, ItemDanmaku.NBT_CUSTOM, false);
	}

	public static void setCustom(ItemStack stack, boolean custom) {
		ItemNBTHelper.setBoolean(stack, NBT_CUSTOM, custom);
	}

	@SuppressWarnings("WeakerAccess")
	public static boolean getInfinity(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, ItemDanmaku.NBT_INFINITY, false);
	}

	@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
	public static void setInfinity(ItemStack stack, boolean infinity) {
		ItemNBTHelper.setBoolean(stack, NBT_INFINITY, infinity);
	}
}
