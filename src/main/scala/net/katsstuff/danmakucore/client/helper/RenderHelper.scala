/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.helper

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.{GL_QUAD_STRIP, GL_TRIANGLE_FAN}
import org.lwjgl.util.glu.{Cylinder, Disk, GLU, Sphere}

import net.katsstuff.danmakucore.data.{Quat, ShotData}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
object RenderHelper {

  private var sphereId   = 0
  private var cylinderId = 0
  private var coneId     = 0
  private var diskId     = 0

  def bakeModels(): Unit = {
    val sphere = new Sphere
    sphere.setDrawStyle(GLU.GLU_FILL)
    sphere.setNormals(GLU.GLU_FLAT)

    sphereId = GL11.glGenLists(1)
    GL11.glNewList(sphereId, GL11.GL_COMPILE)

    sphere.draw(1F, 8, 16)

    GL11.glEndList()

    val cylinder = new Cylinder
    cylinder.setDrawStyle(GLU.GLU_FILL)
    cylinder.setNormals(GLU.GLU_FLAT)

    cylinderId = GL11.glGenLists(1)
    GL11.glNewList(cylinderId, GL11.GL_COMPILE)

    GL11.glTranslatef(0F, 0F, -0.5F)
    cylinder.draw(1F, 1F, 1F, 8, 1)
    GL11.glTranslatef(0F, 0F, 0.5F)

    GL11.glEndList()

    val cone = new Cylinder
    cone.setDrawStyle(GLU.GLU_FILL)
    cone.setNormals(GLU.GLU_FLAT)

    coneId = GL11.glGenLists(1)
    GL11.glNewList(coneId, GL11.GL_COMPILE)

    GL11.glTranslatef(0F, 0F, -0.5F)
    cone.draw(1F, 0F, 1F, 8, 1)
    GL11.glTranslatef(0F, 0F, 0.5F)

    GL11.glEndList()

    diskId = GL11.glGenLists(1)
    GL11.glNewList(diskId, GL11.GL_COMPILE)

    val disk = new Disk
    disk.setDrawStyle(GLU.GLU_FILL)
    disk.setNormals(GLU.GLU_FLAT)
    disk.draw(1F, 0F, 8, 1)

    GL11.glEndList()
  }
  private def drawObj(color: Int, alpha: Float, callListId: Int): Unit = {
    val r = (color >> 16 & 255) / 255.0F
    val g = (color >> 8 & 255) / 255.0F
    val b = (color & 255) / 255.0F
    GlStateManager.color(r, g, b, alpha)
    GL11.glCallList(callListId)
  }

  def drawSphere(color: Int, alpha: Float): Unit = drawObj(color, alpha, sphereId)

  def drawCylinder(color: Int, alpha: Float): Unit = drawObj(color, alpha, cylinderId)

  def drawCone(color: Int, alpha: Float): Unit = drawObj(color, alpha, coneId)

  def drawDisk(color: Int, alpha: Float): Unit = drawObj(color, alpha, diskId)

  def transformDanmaku(shot: ShotData, orientation: Quat): Unit = {
    GlStateManager.rotate(orientation.toQuaternion)
    GL11.glScalef(shot.getSizeX, shot.getSizeY, shot.getSizeZ)
  }

  //Adapted from Glu Sphere
  def drawDropOffSphere(radius: Float, slices: Int, stacks: Int, dropOffRate: Float, color: Int, alpha: Float): Unit = {
    val r   = (color >> 16 & 255) / 255.0F
    val g   = (color >> 8 & 255) / 255.0F
    val b   = (color & 255) / 255.0F
    val tes = Tessellator.getInstance
    val bb  = tes.getBuffer

    val drho   = (Math.PI / stacks).toFloat
    val dtheta = (2.0f * Math.PI / slices).toFloat

    GlStateManager.disableCull()
    GlStateManager.depthMask(false)

    bb.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
    bb.pos(0F, 0F, radius).color(r, g, b, alpha).endVertex()
    for(j <- 0 to slices) {
      val theta = if (j == slices) 0.0f
      else j * dtheta
      val x = -MathHelper.sin(theta) * MathHelper.sin(drho)
      val y = MathHelper.cos(theta) * MathHelper.sin(drho)
      val z = MathHelper.cos(drho)
      bb.pos(x * radius, y * radius, z * radius).color(r, g, b, alpha).endVertex()
    }
    tes.draw()

    val imin = 1
    val imax = stacks - 1

    for(i <- imin until imax) {
      val newAlpha = Math.max(alpha - i * dropOffRate, 0F)
      val rho = i * drho
      bb.begin(GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR)
      for(j <- 0 to slices) {
        val theta = if (j == slices) 0.0f else j * dtheta
        var x = -MathHelper.sin(theta) * MathHelper.sin(rho)
        var y = MathHelper.cos(theta) * MathHelper.sin(rho)
        var z = MathHelper.cos(rho)
        bb.pos(x * radius, y * radius, z * radius).color(r, g, b, newAlpha).endVertex()
        x = -MathHelper.sin(theta) * MathHelper.sin(rho + drho)
        y = MathHelper.cos(theta) * MathHelper.sin(rho + drho)
        z = MathHelper.cos(rho + drho)
        bb.pos(x * radius, y * radius, z * radius).color(r, g, b, newAlpha).endVertex()
      }
      tes.draw()
    }

    GlStateManager.enableCull()
    GlStateManager.depthMask(true)
  }
}
