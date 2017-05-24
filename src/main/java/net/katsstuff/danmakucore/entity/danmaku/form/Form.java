/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.form;

import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.katsstuff.danmakucore.registry.RegistryValueItemStack;
import net.katsstuff.danmakucore.registry.RegistryValueShootable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Something that dictates the appearance and the logic that comes from that of a {@link EntityDanmaku}.
 *
 * Please note that even though both this and {@link SubEntity} have callbacks
 * for tick and changes, the callbacks here should only be used if the logic is
 * specific to this form. For example size restrictions.
 */
@SuppressWarnings("UnusedParameters")
public abstract class Form extends RegistryValueShootable<Form> {

	public Form(String name) {
		setRegistryName(name);
		DanmakuCore.proxy.bakeDanmakuForm(this);
	}

	public Form() {}

	/**
	 * @return The ResourceLocation assigned to this registration.
	 */
	public abstract ResourceLocation getTexture(EntityDanmaku danmaku);

	/**
	 * @return The IRenderForm assigned to this registration.
	 */
	@SideOnly(Side.CLIENT)
	public abstract IRenderForm getRenderer(EntityDanmaku danmaku);

	@Override
	public boolean onRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		return true;
	}

	/**
	 * Called before a danmaku is shot using this variant.
	 *
	 * @return If the danmaku should be allowed to fire.
	 */
	@Override
	public boolean onShootDanmaku(@Nullable EntityLivingBase user, boolean alternateMode, Vector3 pos, Vector3 angle) {
		return true;
	}

	/**
	 * Called each tick for danmaku that uses this form. While you can emulate the most of a
	 * {@link SubEntity} with this, please only use it
	 * for something that would always be true for the given form. For example, a wrecking ball
	 * danmaku would probably always be dragged down a slight bit by gravity, even if it doesn't have normal
	 * gravity.
	 */
	public void onTick(EntityDanmaku danmaku) {}

	/**
	 * Callback that is executed whenever {@link ShotData} is set on the underlying entity
	 * @param oldShot The old shot
	 * @param newShot The new shot
	 * @return The shot that will be switched to
	 */
	public ShotData onShotDataChange(ShotData oldShot, ShotData newShot) {
		return newShot;
	}

	/**
	 * Callback that is executed when {@link MovementData} is set on the underlying entity.
	 * @param oldMovement The old movement
	 * @param newMovement the new movement
	 * @return The movement that will be switched to
	 */
	public MovementData onMovementDataChange(MovementData oldMovement, MovementData newMovement) {
		return newMovement;
	}

	/**
	 * Callback that is executed when {@link RotationData} is set on the underlying entity.
	 * @param oldRotation The old rotation
	 * @param newRotation The new rotation
	 * @return The rotation that will be switched to
	 */
	public RotationData onRotationDataChange(RotationData oldRotation, RotationData newRotation) {
		return newRotation;
	}

	@Override
	public String getUnlocalizedName() {
		return "form." + getModId() + "." + getName();
	}

	@Override
	public ModelResourceLocation getItemModel() {
		ResourceLocation name = getRegistryName();
		return new ModelResourceLocation(new ResourceLocation(name.getResourceDomain(), "danmaku/form/" + name.getResourcePath()), "inventory");
	}
}
