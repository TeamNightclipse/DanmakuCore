/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.helper;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHelper {

	//TODO: Bake this
	public static void drawSphere(int rings, int segments, int color, float alpha, float radius) {
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		GlStateManager.color(r, g, b, alpha);
		Sphere sphere = new Sphere();
		sphere.setDrawStyle(GLU.GLU_FILL);
		sphere.setNormals(GLU.GLU_FLAT);
		sphere.draw(radius, rings, segments);
	}
}
