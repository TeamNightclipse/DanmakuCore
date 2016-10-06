/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.form;

import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.registry.IRegistryValueItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("UnusedParameters")
public abstract class Form extends IForgeRegistryEntry.Impl<Form> implements IRegistryValueItemStack<Form> {

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
	public boolean onShootDanmaku(EntityLivingBase user, boolean alternateMode, Vector3 pos, Vector3 angle) {
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

	@Override
	public FMLControlledNamespacedRegistry<Form> getRegistry() {
		return DanmakuRegistry.INSTANCE.form.getRegistry();
	}

	@Override
	public Form getObject() {
		return this;
	}

	@Override
	public String getUnlocalizedName() {
		return "form";
	}
}
