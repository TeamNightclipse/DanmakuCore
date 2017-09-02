/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.registry.RegistryValueShootable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * A {@link DanmakuVariant} can be thought of as a named {@link ShotData}
 * with {@link MovementData} and {@link RotationData} packed into it.
 * It's purpose is to define what danmaku to fire, given only this and a source,
 * or a world + position + direction.
 *
 * Remember to not load the ShotData (and thus Form and SubEntity) before everything
 * there has finished to register. One approach is to make it lazy.
 */
public abstract class DanmakuVariant extends RegistryValueShootable<DanmakuVariant> {

	public DanmakuVariant(String name) {
		setRegistryName(name);
		DanmakuCore.proxy.bakeDanmakuVariant(this);
	}

	public DanmakuVariant() {}

	public abstract ShotData getShotData();

	public abstract MovementData getMovementData();

	public abstract RotationData getRotationData();

	public DanmakuTemplate.Builder toBuilder() {
		DanmakuTemplate.Builder builder = DanmakuTemplate.builder();
		builder.setShot(getShotData());
		builder.setMovementData(getMovementData());
		builder.setRotationData(getRotationData());
		return builder;
	}

	@Override
	public boolean onRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		return true;
	}

	@Override
	public boolean onShootDanmaku(@Nullable EntityLivingBase user, boolean alternateMode, Vector3 pos, Vector3 direction) {
		return true;
	}

	@Override
	public String getUnlocalizedName() {
		return "danmakuvariant." + getModId() + "." + getName();
	}

	@Override
	public ModelResourceLocation getItemModel() {
		return new ModelResourceLocation(new ResourceLocation(getModId(), "danmaku/variant/" + getName()), "inventory");
	}
}
