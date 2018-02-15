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
import org.lwjgl.util.glu.{Cylinder, Disk, Sphere}

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.shader.{DanCoreShaderProgram, DanCoreUniform, ShaderManager, ShaderType, UniformBase, UniformType}
import net.katsstuff.danmakucore.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.data.{Quat, ShotData}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GLAllocation, GlStateManager, OpenGlHelper, Tessellator}
import net.minecraft.client.resources.{IResourceManagerReloadListener, SimpleReloadableResourceManager}
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

//Many render methods adopted from GLU classes
@SideOnly(Side.CLIENT)
object DanCoreRenderHelper {

  val OverwriteColorEdge = 0xFF0000
  private val ocer       = (OverwriteColorEdge >> 16 & 255) / 255F
  private val oceg       = (OverwriteColorEdge >> 8 & 255) / 255F
  private val oceb       = (OverwriteColorEdge & 255) / 255F

  val OverwriteColorCore = 0x00FF00
  private val occr       = (OverwriteColorCore >> 16 & 255) / 255F
  private val occg       = (OverwriteColorCore >> 8 & 255) / 255F
  private val occb       = (OverwriteColorCore & 255) / 255F

  private val swizzleRegex = """^.+\.[rgbaxyzwstpq]$"""

  val baseDanmakuShaderLoc:  ResourceLocation = DanmakuCore.resource("shaders/danmaku")
  val fancyDanmakuShaderLoc: ResourceLocation = DanmakuCore.resource("shaders/danmaku_fancy")

  private var sphereHighId   = 0
  private var sphereMidId    = 0
  private var sphereLowId    = 0
  private var cylinderHighId = 0
  private var cylinderMidId  = 0
  private var cylinderLowId  = 0
  private var coneHighId     = 0
  private var coneMidId      = 0
  private var coneLowId      = 0
  private var diskHighId     = 0
  private var diskMidId      = 0
  private var diskLowId      = 0

  private val useVBO = OpenGlHelper.useVbo()

  def bakeModels(): Unit = {
    val tes = Tessellator.getInstance()
    val vb  = tes.getBuffer

    val sphere   = new Sphere
    val cylinder = new Cylinder
    val cone     = new Cylinder
    val disk     = new Disk

    sphereHighId = createList {
      GlStateManager.rotate(90F, 1F, 0F, 0F)
      sphere.draw(1F, 32, 16)
      GlStateManager.rotate(-90F, 1F, 0F, 0F)
    }
    sphereMidId = createList {
      GlStateManager.rotate(90F, 1F, 0F, 0F)
      sphere.draw(1F, 16, 8)
      GlStateManager.rotate(-90F, 1F, 0F, 0F)
    }
    sphereLowId = createList {
      GlStateManager.rotate(90F, 1F, 0F, 0F)
      sphere.draw(1F, 8, 4)
      GlStateManager.rotate(-90F, 1F, 0F, 0F)
    }

    cylinderHighId = createList {
      GlStateManager.translate(0F, 0F, -0.5F)
      cylinder.draw(1F, 1F, 1F, 32, 1)
      GlStateManager.translate(0F, 0F, 0.5F)
    }
    cylinderMidId = createList {
      GlStateManager.translate(0F, 0F, -0.5F)
      cylinder.draw(1F, 1F, 1F, 16, 1)
      GlStateManager.translate(0F, 0F, 0.5F)
    }
    cylinderLowId = createList {
      GlStateManager.translate(0F, 0F, -0.5F)
      cylinder.draw(1F, 1F, 1F, 8, 1)
      GlStateManager.translate(0F, 0F, 0.5F)
    }

    coneHighId = createList {
      GlStateManager.translate(0F, 0F, -0.5F)
      cone.draw(1F, 0F, 1F, 32, 1)
      GlStateManager.translate(0F, 0F, 0.5F)
    }
    coneMidId = createList {
      GlStateManager.translate(0F, 0F, -0.5F)
      cone.draw(1F, 0F, 1F, 16, 1)
      GlStateManager.translate(0F, 0F, 0.5F)
    }
    coneLowId = createList {
      GlStateManager.translate(0F, 0F, -0.5F)
      cone.draw(1F, 0F, 1F, 8, 1)
      GlStateManager.translate(0F, 0F, 0.5F)
    }

    diskHighId = createList(disk.draw(1F, 0F, 32, 1))
    diskMidId = createList(disk.draw(1F, 0F, 16, 1))
    diskLowId = createList(disk.draw(1F, 0F, 8, 1))

    if (OpenGlHelper.shadersSupported) {
      ShaderManager.initShader(
        baseDanmakuShaderLoc,
        Seq(ShaderType.Vertex),
        Seq(
          UniformBase("overwriteColorEdge", UniformType.Vec3, 1),
          UniformBase("overwriteColorCore", UniformType.Vec3, 1),
          UniformBase("coreColor", UniformType.Vec3, 1),
          UniformBase("edgeColor", UniformType.Vec3, 1)
        ),
        shader => {
          shader.begin()
          shader.getUniform("overwriteColorEdge").foreach { uniform =>
            uniform.set(ocer, oceg, oceb)
            uniform.upload()
          }

          shader.getUniform("overwriteColorCore").foreach { uniform =>
            uniform.set(occr, occg, occb)
            uniform.upload()
          }
          shader.end()
        }
      )

      ShaderManager.initShader(
        fancyDanmakuShaderLoc,
        Seq(ShaderType.Vertex, ShaderType.Fragment),
        Seq(
          UniformBase("coreColor", UniformType.Vec3, 1),
          UniformBase("edgeColor", UniformType.Vec3, 1),
          UniformBase("coreSize", UniformType.UnFloat, 1),
          UniformBase("coreHardness", UniformType.UnFloat, 1),
          UniformBase("edgeHardness", UniformType.UnFloat, 1),
          UniformBase("edgeGlow", UniformType.UnFloat, 1)
        ),
        shader => {
          shader.begin()
          shader.getUniform("coreColor").foreach { uniform =>
            uniform.set(1F, 1F, 1F)
            uniform.upload()
          }
          shader.getUniform("edgeColor").foreach { uniform =>
            uniform.set(1F, 0F, 0F)
            uniform.upload()
          }
          shader.end()
        }
      )
    }
  }

  def createList(create: => Unit): Int = {
    val res = GLAllocation.generateDisplayLists(1)
    GlStateManager.glNewList(res, GL11.GL_COMPILE)

    create

    GlStateManager.glEndList()
    res
  }

  private def drawObj(color: Int, alpha: Float, dist: Double, highId: Int, midId: Int, lowId: Int): Unit = {
    val r = (color >> 16 & 255) / 255F
    val g = (color >> 8 & 255) / 255F
    val b = (color & 255) / 255F
    GlStateManager.color(r, g, b, alpha)

    val id =
      if (dist < 8 * 8) highId
      else if (dist < 32 * 32) midId
      else lowId
    GlStateManager.callList(id)
  }

  def drawSphere(color: Int, alpha: Float, dist: Double): Unit =
    drawObj(color, alpha, dist, sphereHighId, sphereMidId, sphereLowId)

  def drawCylinder(color: Int, alpha: Float, dist: Double): Unit =
    drawObj(color, alpha, dist, cylinderHighId, cylinderMidId, cylinderLowId)

  def drawCone(color: Int, alpha: Float, dist: Double): Unit =
    drawObj(color, alpha, dist, coneHighId, coneMidId, coneLowId)

  def drawDisk(color: Int, alpha: Float, dist: Double): Unit =
    drawObj(color, alpha, dist, diskHighId, diskMidId, diskLowId)

  def transformDanmaku(shot: ShotData, orientation: Quat): Unit = {
    GlStateManager.rotate(orientation.toQuaternion)
    GlStateManager.scale(shot.getSizeX, shot.getSizeY, shot.getSizeZ)
  }

  def renderDropOffSphere(
      radius: Float,
      slices: Int,
      stacks: Int,
      dropOffRate: Float,
      color: Int,
      alpha: Float
  ): Unit = {
    val r = (color >> 16 & 255) / 255F
    val g = (color >> 8 & 255) / 255F
    val b = (color & 255) / 255F

    val tes = Tessellator.getInstance
    val bb  = tes.getBuffer

    val drho   = (Math.PI / stacks).toFloat
    val dtheta = (2F * Math.PI / slices).toFloat

    GlStateManager.disableCull()
    GlStateManager.depthMask(false)

    bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
    bb.pos(0F, 0F, radius).color(r, g, b, alpha).endVertex()
    for (j <- 0 to slices) {
      val theta =
        if (j == slices) 0.0f
        else j * dtheta
      val x = -MathHelper.sin(theta) * MathHelper.sin(drho)
      val y = MathHelper.cos(theta) * MathHelper.sin(drho)
      val z = MathHelper.cos(drho)
      bb.pos(x * radius, y * radius, z * radius).color(r, g, b, alpha).endVertex()
    }
    tes.draw()

    val imin = 1
    val imax = stacks - 1

    for (i <- imin until imax) {
      val newAlpha = Math.max(alpha - i * dropOffRate, 0F)
      val rho      = i * drho
      bb.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR)
      for (j <- 0 to slices) {
        val theta = if (j == slices) 0.0f else j * dtheta
        var x     = -MathHelper.sin(theta) * MathHelper.sin(rho)
        var y     = MathHelper.cos(theta) * MathHelper.sin(rho)
        var z     = MathHelper.cos(rho)
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

  def registerResourceReloadListener(listener: IResourceManagerReloadListener): Unit = {
    Minecraft.getMinecraft.getResourceManager match {
      case resourceManager: SimpleReloadableResourceManager => resourceManager.registerReloadListener(listener)
      case _                                                => listener.onResourceManagerReload(Minecraft.getMinecraft.getResourceManager)
    }
  }

  def danmakuShaderProgram: Option[DanCoreShaderProgram] = ShaderManager.getShaderProgram(baseDanmakuShaderLoc)

  def updateDanmakuShaderAttributes(shaderProgram: DanCoreShaderProgram, form: IRenderForm, shot: ShotData): Unit = {
    val edgeColor = shot.edgeColor
    val er     = (edgeColor >> 16 & 255) / 255F
    val eg     = (edgeColor >> 8 & 255) / 255F
    val eb     = (edgeColor & 255) / 255F

    val coreColor = shot.coreColor
    val cr     = (coreColor >> 16 & 255) / 255F
    val cg     = (coreColor >> 8 & 255) / 255F
    val cb     = (coreColor & 255) / 255F

    val attributeMap = form.defaultAttributeValues.keys.map { k =>
        val isSwizzle   = k.matches(swizzleRegex)
        val newKey      = if (isSwizzle) k.dropRight(2) else k
        val swizzleChar = if (isSwizzle) Option(k.last) else None
        newKey -> (swizzleChar, shot.renderProperties.getOrElse(k, form.defaultAttributeValues(k).default))
      }.toMap

    attributeMap.flatMap { t =>
      val res: Option[((Option[Char], Float), DanCoreUniform)] = shaderProgram.getUniform(t._1).map(t._2 -> _)
      res
    }.foreach {
      case ((swizzleChar, value), uniform) =>
        swizzleChar match {
          case Some('r' | 'x' | 's') => uniform.setIdx(value, 0)
          case Some('g' | 'y' | 's') => uniform.setIdx(value, 1)
          case Some('b' | 'z' | 'p') => uniform.setIdx(value, 2)
          case Some('a' | 'w' | 'q') => uniform.setIdx(value, 3)
          case _                     => uniform.set(value)
        }

        uniform.upload()
    }

    shaderProgram.getUniform("coreColor").foreach { uniform =>
      uniform.set(cr, cg, cb)
      uniform.upload()
    }

    shaderProgram.getUniform("edgeColor").foreach { uniform =>
      uniform.set(er, eg, eb)
      uniform.upload()
    }
  }
}
