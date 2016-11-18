/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form;

import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.minecraft.client.renderer.entity.RenderManager;

public class FormControl extends FormGeneric {

	public FormControl() {
		super(LibFormName.CONTROL);
	}

	@Override
	public void renderForm(EntityDanmaku danmaku, double x, double y, double z, float entityYaw, float partialTicks, RenderManager rendermanager) {
		//NO-OP
	}

	@Override
	public ShotData onShotDataChange(ShotData oldShot, ShotData newShot) {
		return newShot.setSize(oldShot.sizeX(), oldShot.sizeY(), oldShot.sizeZ());
	}
}
