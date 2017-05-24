/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.data.Vector3;
import net.minecraft.entity.EntityLivingBase;

public abstract class RegistryValueShootable<T extends RegistryValueShootable<T>> extends RegistryValueItemStack<T> {

	public abstract boolean onShootDanmaku(@Nullable EntityLivingBase user, boolean alternateMode, Vector3 pos, Vector3 angle);
}
