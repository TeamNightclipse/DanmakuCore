/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.registry.IRegistryValueItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

/**
 * A {@link DanmakuVariant} can be though of as a named {@link ShotData}
 * with {@link MovementData} and {@link RotationData} packed into it.
 * It's purpose is to define what danmaku to fire, given only this and a source,
 * or a world + position + angle.
 */
public abstract class DanmakuVariant extends IForgeRegistryEntry.Impl<DanmakuVariant> implements IRegistryValueItemStack<DanmakuVariant> {

	public abstract ShotData getShotData();

	public abstract MovementData getMovementData();

	public abstract RotationData getRotationData();

	public DanmakuBuilder.Builder toBuilder() {
		DanmakuBuilder.Builder builder = DanmakuBuilder.builder();
		builder.setShot(getShotData());
		builder.setMovementData(getMovementData());
		builder.setRotationData(getRotationData());
		return builder;
	}

	@Override
	public FMLControlledNamespacedRegistry<DanmakuVariant> getRegistry() {
		return DanmakuRegistry.INSTANCE.danmakuVariant.getRegistry();
	}

	@Override
	public DanmakuVariant getObject() {
		return this;
	}

	@Override
	public boolean onRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		return true;
	}

	/**
	 * Called before a danmaku is shot using this variant if
	 * the variant is used directly by some entity.
	 *
	 * @return If the danmaku should be allowed to fire.
	 */
	@SuppressWarnings("UnusedParameters")
	public boolean onShootDanmaku(EntityLivingBase user, boolean alternateMode, Vector3 pos, Vector3 angle) {
		return true;
	}

	@Override
	public String getUnlocalizedName() {
		return "danmakuvariant";
	}
}
