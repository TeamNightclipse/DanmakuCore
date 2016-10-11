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
import net.katsstuff.danmakucore.data.MutableShotData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuBuilder;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.capability.IDanmakuCoreData;
import net.katsstuff.danmakucore.helper.DanmakuCreationHelper;
import net.katsstuff.danmakucore.helper.DanmakuHelper;
import net.katsstuff.danmakucore.helper.ItemNBTHelper;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.LibItemName;
import net.katsstuff.danmakucore.lib.data.LibSubEntities;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.registry.Registry;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
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
	public static final String NBT_CUSTOM = "custom";
	private static final String NBT_SPEED = "speed";

	ItemDanmaku() {
		super(LibItemName.DANMAKU);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(DanmakuCore.DANMAKU_CREATIVE_TAB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
		Registry<DanmakuVariant> variantRegistry = DanmakuRegistry.INSTANCE.danmakuVariant;
		list.addAll(variantRegistry.getValues().stream()
				.sorted()
				.map(variant -> {
					ShotData shot = variant.getShotData().setColor(DanmakuHelper.randomSaturatedColor());
					ItemStack stack = new ItemStack(DanmakuCoreItem.danmaku, 1, variantRegistry.getId(variant));
					NBTTagCompound compound = new NBTTagCompound();
					compound.setTag(ShotData.NbtShotData(), shot.serializeNBT());
					stack.setTagCompound(compound);
					return stack;})
				.collect(Collectors.toList()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, NBT_INFINITY, false);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String name = ItemNBTHelper.getBoolean(stack, NBT_CUSTOM, false) ?
				ShotData.fromNBTItemStack(stack).form().getUnlocalizedName() :
				DanmakuRegistry.INSTANCE.danmakuVariant.get(stack.getItemDamage()).getUnlocalizedName();
		return this.getUnlocalizedName() + "." + name;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(ItemNBTHelper.getBoolean(stack, NBT_CUSTOM, false)) {
			if(!DanmakuRegistry.INSTANCE.form.get(stack.getItemDamage()).onRightClick(stack, world, player, hand)) {
				return super.onItemRightClick(stack, world, player, hand);
			}
		}
		else {
			if(!DanmakuRegistry.INSTANCE.danmakuVariant.get(stack.getItemDamage()).onRightClick(stack, world, player, hand)) {
				return super.onItemRightClick(stack, world, player, hand);
			}
		}

		if(!world.isRemote) {
			if(player.capabilities.isCreativeMode) {
				ItemNBTHelper.setBoolean(stack, NBT_INFINITY, true);
			}

			boolean isInfinity = ItemNBTHelper.getBoolean(stack, NBT_INFINITY, false);

			if(!isInfinity) {
				stack.stackSize--;
			}

			shootDanmaku(stack, player, player.isSneaking(), new Vector3(player), new Vector3(player.getLookVec()),
					ShotData.fromNBTItemStack(stack).sizeZ() / 4);
		}
		DanmakuHelper.playShotSound(player);

		if(!world.isRemote) {
			if(player.isSneaking()) {
				TouhouHelper.setPowerPlayerSync(player, 0F);
			}
			else {
				TouhouHelper.addPowerPlayerSync(player, 0.1F);
			}
		}

		/*
		//ShapeWideShot shape = new ShapeWideShot(DanmakuBuilder.builder().setUser(player).setMovementData(0.4D).setShot(LibShotData.SHOT_SMALLSTAR).build(), 8, 45F, 0F, 0D);
		//ShapeCircle shape = new ShapeCircle(DanmakuBuilder.builder().setUser(player).setMovementData(0.4D).setShot(LibShotData.SHOT_SMALLSTAR).build(), 16, 0F, 0D);
		//ShapeArrow shape = new ShapeArrow(DanmakuBuilder.builder().setUser(player).setMovementData(0.4D).setShot(LibShotData.SHOT_SMALLSTAR).build(), 4, 1D, 0.5F);
		//ShapeRandomRing shape = new ShapeRandomRing(DanmakuBuilder.builder().setUser(player).setMovementData(0.4D).setShot(LibShotData.SHOT_SMALLSTAR).build(), 8, 40F, 0D);
		//ShapeRing shape = new ShapeRing(DanmakuBuilder.builder().setUser(player).setMovementData(0.4D).setShot(LibShotData.SHOT_SMALLSTAR).build(), 8, 10F, 0F, 0D);
		//ShapeStar shape = new ShapeStar(DanmakuBuilder.builder().setUser(player).setMovementData(0.4D).setShot(LibShotData.SHOT_SMALLSTAR).build(), 32, 0F, 0F, 0D);
		ShapeWideShot shape = new ShapeWideShot(DanmakuBuilder.builder().setUser(player).setMovementData(0.4D).setShot(LibShotData.SHOT_SMALLSTAR).build(), 8, 45F, 0F, 0D);
		//ShapeSphere shape = new ShapeSphere(DanmakuBuilder.builder().setUser(player).setMovementData(0.4D).setShot(LibShotData.SHOT_SMALLSTAR).build(), 8, 8, 0F, 0D);
		ShapeHandler.createShape(shape, player);
		DanmakuHelper.playShotSound(player);
		*/

		/*
		MutableShotData shot = new MutableShotData(LibForms.SPHERE_DARK, LibShotData.COLOR_SATURATED_RED, 0.5F, 0.5F, 0.5F, 0.5F, 0, 100, LibSubEntities.DEFAULT_TYPE);
		//ShotData shot = new ShotData(LibForms.INSTANCE.circle, LibShotData.COLOR_SATURATED_RED, 0.5F, 0.5F, 0, 100);

		Color color = new Color(shot.color());
		float[] colorHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		colorHSB[0] = world.getWorldTime() % 255 / 25F;
		color = Color.getHSBColor(colorHSB[0], colorHSB[1], colorHSB[2]);
		shot.setColor(color.getRGB());
		DanmakuBuilder danmaku = DanmakuBuilder.builder().setUser(player).setShot(shot.asImmutable()).setMovementData(0.2D).build();

		DanmakuHelper.playShotSound(player);
		DanmakuCreationHelper.createWideShot(danmaku, 8, 45F, 0F, 0.1D);
		*/

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	private void shootDanmaku(ItemStack stack, EntityPlayer player, boolean alternateMode, Vector3 pos, Vector3 angle, double distance) {
		if(ItemNBTHelper.getBoolean(stack, NBT_CUSTOM, false)) {
			if(!DanmakuRegistry.INSTANCE.form.get(stack.getItemDamage()).onShootDanmaku(player, alternateMode, pos, angle)) return;
		}
		else {
			if(!DanmakuRegistry.INSTANCE.danmakuVariant.get(stack.getItemDamage()).onShootDanmaku(player, alternateMode, pos, angle)) return;
		}

		int amount = ItemNBTHelper.getShort(stack, NBT_AMOUNT, (short)1);
		double shotSpeed = ItemNBTHelper.getDouble(stack, NBT_SPEED, 0.5D);
		int danmakuPattern = ItemNBTHelper.getByte(stack, NBT_PATTERN, (byte)0);
		MutableShotData shot = ShotData.fromNBTItemStack(stack).asMutable();
		Vector3 gravity = getGravity(stack);
		int maxNumber = ConfigHandler.getDanmakuMaxNumber();

		if(amount > maxNumber) {
			amount = maxNumber;
		}

		float wide;
		shot.setDamage(shot.getDamage() + TouhouHelper.getDanmakuCoreData(player).map(IDanmakuCoreData::getPower).orElse(0F));
		DanmakuBuilder.Builder danmaku = DanmakuBuilder.builder();
		danmaku.setUser(player).setShot(shot.asImmutable()).setMovementData(shotSpeed, gravity).setAngle(new Vector3(player.getLookVec()));
		DanmakuBuilder built = danmaku.build();

		switch(danmakuPattern) {
			case 0:
				danmaku.setPos(pos.offset(angle, distance));

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
				DanmakuCreationHelper.createRandomRingShot(built, amount, wide, distance);
				break;
			case 2:
				wide = amount * 3F;
				if(alternateMode) {
					wide = wide * 0.5F;
				}
				DanmakuCreationHelper.createWideShot(built, amount, wide, 0F, distance);
				break;
			case 3:
				DanmakuCreationHelper.createCircleShot(built, amount, 0F, distance);
				break;
			case 4:
				danmaku.setMovementData(Vector3.GravityZero());
				DanmakuCreationHelper.createSphereShot(danmaku.build(), amount, 0F, 0F, distance);
				break;
			case 5:
				wide = 15F;
				if(alternateMode) {
					wide *= 0.5F;
				}
				DanmakuCreationHelper.createRingShot(built, amount, wide, player.getRNG().nextFloat() * 360F, distance);
				break;
			default:
				break;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool) {
		super.addInformation(stack, player, list, bool);
		ShotData shot = ShotData.fromNBTItemStack(stack);
		int amount = ItemNBTHelper.getShort(stack, NBT_AMOUNT, (short)1);
		double shotSpeed = ItemNBTHelper.getDouble(stack, NBT_SPEED, 0.5D);
		int danmakuPattern = ItemNBTHelper.getByte(stack, NBT_PATTERN, (byte)0);
		Vector3 gravity = getGravity(stack);
		boolean isInfinity = ItemNBTHelper.getBoolean(stack, NBT_INFINITY, false);
		boolean custom = ItemNBTHelper.getBoolean(stack, NBT_CUSTOM, false);

		float powerDamage = TouhouHelper.getDanmakuCoreData(player).map(IDanmakuCoreData::getPower).orElse(0F);
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
			list.add(I18n.format(item + ".gravity") + " : " + I18n.format(item + ".gFree"));
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

	public static int getPattern(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, NBT_PATTERN, 0);
	}

	public static void setPattern(ItemStack stack, int pattern) {
		ItemNBTHelper.setInt(stack, NBT_PATTERN, pattern);
	}

	public static int getAmount(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, NBT_AMOUNT, 1);
	}

	public static void setAmount(ItemStack stack, int amount) {
		ItemNBTHelper.setInt(stack, NBT_AMOUNT, amount);
	}

	public static double getSpeed(ItemStack stack) {
		return ItemNBTHelper.getDouble(stack, NBT_SPEED, 0.4D);
	}

	public static void setSpeed(ItemStack stack, double speed) {
		ItemNBTHelper.setDouble(stack, NBT_SPEED, speed);
	}
}
