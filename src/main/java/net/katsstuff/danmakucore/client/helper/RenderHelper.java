/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.helper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHelper {

	private static int sphereId = 0;
	private static int cylinderId = 0;
	private static int coneId = 0;
	private static int diskId = 0;

	public static void bakeModels() {
		Sphere sphere = new Sphere();
		sphere.setDrawStyle(GLU.GLU_FILL);
		sphere.setNormals(GLU.GLU_FLAT);

		sphereId = GL11.glGenLists(1);
		GL11.glNewList(sphereId, GL11.GL_COMPILE);

		sphere.draw(1F, 8, 16);

		GL11.glEndList();

		Cylinder cylinder = new Cylinder();
		cylinder.setDrawStyle(GLU.GLU_FILL);
		cylinder.setNormals(GLU.GLU_FLAT);

		cylinderId = GL11.glGenLists(1);
		GL11.glNewList(cylinderId, GL11.GL_COMPILE);

		GL11.glTranslatef(0F, 0F, -0.5F);
		cylinder.draw(1F, 1F, 1F, 8, 1);
		GL11.glTranslatef(0F, 0F, 0.5F);

		GL11.glEndList();

		Cylinder cone = new Cylinder();
		cone.setDrawStyle(GLU.GLU_FILL);
		cone.setNormals(GLU.GLU_FLAT);

		coneId = GL11.glGenLists(1);
		GL11.glNewList(coneId, GL11.GL_COMPILE);

		GL11.glTranslatef(0F, 0F, -0.5F);
		cone.draw(1F, 0F, 1F, 8, 1);
		GL11.glTranslatef(0F, 0F, 0.5F);

		GL11.glEndList();

		diskId = GL11.glGenLists(1);
		GL11.glNewList(diskId, GL11.GL_COMPILE);

		Disk disk = new Disk();
		disk.setDrawStyle(GLU.GLU_FILL);
		disk.setNormals(GLU.GLU_FLAT);
		disk.draw(1F, 0F, 8, 1);

		GL11.glEndList();
	}

	public static void drawSphere(int color, float alpha) {
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;

		GlStateManager.color(r, g, b, alpha);
		GL11.glCallList(sphereId);
	}

	public static void drawCylinder(int color, float alpha) {
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;

		GlStateManager.color(r, g, b, alpha);
		GL11.glCallList(cylinderId);
	}

	public static void drawCone(int color, float alpha) {
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;

		GlStateManager.color(r, g, b, alpha);
		GL11.glCallList(coneId);
	}

	public static void drawDisk(int color, float alpha) {
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;

		GlStateManager.color(r, g, b, alpha);
		GL11.glCallList(diskId);
	}

	public static void transformEntity(EntityDanmaku danmaku) {
		ShotData shotData = danmaku.getShotData();
		rotateDanmaku(danmaku);
		GL11.glScalef(shotData.getSizeX(), shotData.getSizeY(), shotData.getSizeZ());
	}

	public static void rotateDanmaku(EntityDanmaku danmaku) {
		rotate(-danmaku.rotationYaw, danmaku.rotationPitch, danmaku.getRoll());
	}

	public static void rotate(float yaw, float pitch, float roll) {
		GL11.glRotatef(yaw, 0F, 1F, 0F);
		GL11.glRotatef(pitch, 1F, 0F, 0F);
		GL11.glRotatef(roll, 0F, 0F, 1F);
	}

}
