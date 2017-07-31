/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form;

import org.lwjgl.opengl.GL11;

import net.katsstuff.danmakucore.client.helper.RenderHelper;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A One sided crystal. Pointy on one side.
 */
public class FormCrystal2 extends AbstractFormCrystal {

	public FormCrystal2() {
		super(LibFormName.CRYSTAL2);
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void createCrystal(int color, float alpha) {
		GL11.glPushMatrix();

		GL11.glTranslatef(0F, 0F, 1F);
		RenderHelper.drawCone(color, alpha);
		GL11.glTranslatef(0F, 0F, -1F);
		RenderHelper.drawCylinder(color, alpha);
		GL11.glTranslatef(0F, 0F, -0.5F);
		RenderHelper.drawDisk(color, alpha);

		GL11.glPopMatrix();
	}
}
