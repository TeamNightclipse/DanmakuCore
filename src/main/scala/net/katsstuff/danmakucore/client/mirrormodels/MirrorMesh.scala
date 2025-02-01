package net.katsstuff.danmakucore.client.mirrormodels

import java.io.IOException

import scala.jdk.CollectionConverters.*

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec2
import net.minecraftforge.client.model.obj.{ObjLoader, ObjMaterialLibrary, ObjModel}
import org.joml.{Matrix4f, Vector3f, Vector4f}
import org.lwjgl.opengl.{GL11, GL15, GL31}

class MirrorMesh(val vertexBuffer: VertexBuffer, val drawState: BufferBuilder.DrawState) {

  private def setupShader(modelViewMatrix: Matrix4f, projectionMatrix: Matrix4f, shader: ShaderInstance): Unit = {
    for (i <- 0 until 12) do {
      val j = RenderSystem.getShaderTexture(i)
      shader.setSampler("Sampler" + i, j)
    }

    if (shader.MODEL_VIEW_MATRIX != null) shader.MODEL_VIEW_MATRIX.set(modelViewMatrix)

    if (shader.PROJECTION_MATRIX != null) shader.PROJECTION_MATRIX.set(projectionMatrix)

    if (shader.INVERSE_VIEW_ROTATION_MATRIX != null)
      shader.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix)

    if (shader.COLOR_MODULATOR != null) shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor)

    if (shader.GLINT_ALPHA != null) shader.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha)

    if (shader.FOG_START != null) shader.FOG_START.set(RenderSystem.getShaderFogStart)

    if (shader.FOG_END != null) shader.FOG_END.set(RenderSystem.getShaderFogEnd)

    if (shader.FOG_COLOR != null) shader.FOG_COLOR.set(RenderSystem.getShaderFogColor)

    if (shader.FOG_SHAPE != null) shader.FOG_SHAPE.set(RenderSystem.getShaderFogShape.getIndex)

    if (shader.TEXTURE_MATRIX != null) shader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix)

    if (shader.GAME_TIME != null) shader.GAME_TIME.set(RenderSystem.getShaderGameTime)

    if (shader.SCREEN_SIZE != null) {
      val window = Minecraft.getInstance.getWindow
      shader.SCREEN_SIZE.set(window.getWidth.toFloat, window.getHeight.toFloat)
    }

    if (
      shader.LINE_WIDTH != null && ((drawState.mode == VertexFormat.Mode.LINES) || (drawState.mode == VertexFormat.Mode.LINE_STRIP))
    ) shader.LINE_WIDTH.set(RenderSystem.getShaderLineWidth)

    RenderSystem.setupShaderLights(shader)
  }

  def drawWithShader(
      modelViewMatrix: Matrix4f,
      projectionMatrix: Matrix4f,
      shader: ShaderInstance
  ): Unit = {
    setupShader(modelViewMatrix, projectionMatrix, shader)
    shader.apply()
    bind()
    GL11.glDrawElements(
      VertexFormat.Mode.QUADS.asGLMode,
      drawState.indexCount,
      VertexFormat.IndexType.least(drawState.indexCount).asGLType,
      0
    )
    unbind()
    shader.clear()
  }

  def drawInstancedWithShader(
      modelViewMatrix: Matrix4f,
      projectionMatrix: Matrix4f,
      shader: ShaderInstance,
      count: Int
  ): Unit = {
    setupShader(modelViewMatrix, projectionMatrix, shader)
    shader.apply()
    bind()
    GL31.glDrawElementsInstanced(
      VertexFormat.Mode.QUADS.asGLMode,
      drawState.indexCount,
      VertexFormat.IndexType.least(drawState.indexCount).asGLType,
      0,
      count
    )
    unbind()
    shader.clear()
  }

  def bind(): Unit = vertexBuffer.bind()

  def unbind(): Unit = VertexBuffer.unbind()

  def delete(): Unit = vertexBuffer.close()
}
object MirrorMesh {

  private def accessibleFieldGetter[A, B](clazz: Class[_ <: A], fieldName: String): A => B = {
    val field = clazz.getDeclaredField(fieldName)
    field.setAccessible(true)

    obj => field.get(obj).asInstanceOf[B]
  }

  private val objModelClass = classOf[ObjModel]
  private val positionsFieldGetter =
    accessibleFieldGetter[ObjModel, java.util.List[Vector3f]](objModelClass, "positions")
  private val texCoordsFieldGetter = accessibleFieldGetter[ObjModel, java.util.List[Vec2]](objModelClass, "texCoords")
  private val normalsFieldGetter   = accessibleFieldGetter[ObjModel, java.util.List[Vector3f]](objModelClass, "normals")
  private val colorsFieldGetter    = accessibleFieldGetter[ObjModel, java.util.List[Vector4f]](objModelClass, "colors")
  private val objModelPartsGetter =
    accessibleFieldGetter[ObjModel, java.util.Map[String, AnyRef]](objModelClass, "parts")

  private val modelObjectClass = objModelClass.getDeclaredClasses.find(_.getSimpleName == "ModelObject").get
  private val modelGroupClass  = objModelClass.getDeclaredClasses.find(_.getSimpleName == "ModelGroup").get
  private val modelMeshClass   = objModelClass.getDeclaredClasses.find(_.getSimpleName == "ModelMesh").get

  private val modelGroupPartsGetter =
    accessibleFieldGetter[AnyRef, java.util.Map[String, AnyRef]](
      modelGroupClass.asInstanceOf[Class[_ <: AnyRef]],
      "parts"
    )

  private val modelObjectMeshesGetter =
    accessibleFieldGetter[AnyRef, java.util.List[AnyRef]](
      modelObjectClass.asInstanceOf[Class[_ <: AnyRef]],
      "meshes"
    )

  private val modelMeshMatGetter =
    accessibleFieldGetter[AnyRef, ObjMaterialLibrary.Material](
      modelMeshClass.asInstanceOf[Class[_ <: AnyRef]],
      "mat"
    )

  private val modelMeshFacesGetter =
    accessibleFieldGetter[AnyRef, java.util.List[Array[Array[Int]]]](
      modelMeshClass.asInstanceOf[Class[_ <: AnyRef]],
      "faces"
    )

  // TODO: Make it work with reloading
  // noinspection DuplicatedCode
  def make(
      modelLoc: ResourceLocation,
      vertexFormat: VertexFormat,
      initExtra: () => Unit = () => ()
  ): MirrorMesh = {
    val objModel = ObjLoader.INSTANCE.loadModel(
      new ObjModel.ModelSettings(modelLoc, false, false, false, false, null)
    )

    def requireOnlyOne[A](iterable: Iterable[A]): A = {
      if (iterable.isEmpty) throw new IOException("Requires at least one Object in OBJ file")

      val first  = iterable.head
      val second = iterable.tail.headOption

      if (second.isDefined) throw new IOException("Can't load complex OBJ files")
      else first
    }

    val positions = positionsFieldGetter(objModel).asScala
    val texCoords = texCoordsFieldGetter(objModel).asScala
    val normals   = normalsFieldGetter(objModel).asScala
    val colors    = colorsFieldGetter(objModel).asScala

    val modelGroup  = requireOnlyOne(objModelPartsGetter(objModel).asScala.values)
    val groupMeshes = modelObjectMeshesGetter(modelGroup).asScala
    val mesh = if (groupMeshes.isEmpty) {
      val modelObj = requireOnlyOne(modelGroupPartsGetter(modelGroup).asScala.values)
      requireOnlyOne(modelObjectMeshesGetter(modelObj).asScala)
    } else {
      requireOnlyOne(groupMeshes)
    }

    // Ignored for now
    // val mat = modelMeshMatGetter(mesh)
    val faces = modelMeshFacesGetter(mesh).asScala

    // TODO: IBO is not generated optimally by this
    val bb = Tesselator.getInstance().getBuilder
    bb.begin(VertexFormat.Mode.TRIANGLES, vertexFormat)

    faces.foreach { face =>
      if (face.length != 3) {
        throw new IOException("MirrorMesh can only use models with triangles")
      }

      val defaultNormal = {
        val a  = positions(face(0)(0))
        val ab = positions(face(1)(0))
        val ac = positions(face(2)(0))

        val abs = new Vector3f(ab)
        abs.sub(a)
        val acs = new Vector3f(ac)
        acs.sub(a)
        abs.cross(acs)
        abs.normalize
        abs
      }

      face.foreach { vertexArr =>
        val pos      = positions(vertexArr(0))
        val texCoord = vertexArr.lift(1).flatMap(texCoords.lift).getOrElse(Vec2.ZERO)
        val normal   = vertexArr.lift(2).flatMap(normals.lift).getOrElse(defaultNormal)
        val color    = vertexArr.lift(3).flatMap(colors.lift).getOrElse(new Vector4f(1, 1, 1, 1))

        vertexFormat.getElements.asScala.foreach { element =>
          element.getUsage match {
            case VertexFormatElement.Usage.POSITION =>
              bb.vertex(pos.x, pos.y, pos.z)

            case VertexFormatElement.Usage.UV =>
              bb.uv(texCoord.x, texCoord.y)

            case VertexFormatElement.Usage.NORMAL =>
              bb.normal(normal.x, normal.y, normal.z)
            case VertexFormatElement.Usage.COLOR =>
              bb.color(color.x, color.y, color.z, color.w)
            case _ =>
          }
        }
        bb.endVertex()
      }
    }

    /*
    extension (bb: BufferBuilder)
      private def float(f: Float): Unit = {
        bb.putFloat(0, f)
        bb.nextElement()
      }

    val bb = Tesselator.getInstance().getBuilder
    bb.begin(VertexFormat.Mode.QUADS, DanCoreShaders.entireDanmakuVertexFormat)
    bb.vertex(0, 0, 0) /*.normal(0, 1, 0)*/ .color(1F, 0, 0, 1F) // .color(0, 0, 0, 1F)
    // bb.float(1.1)
    // bb.float(2.5)
    // bb.float(3)
    // bb.float(3)
    bb.endVertex()

    bb.vertex(0, 1, 0) /*.normal(0, 1, 0)*/ .color(0, 1F, 0, 1F) // .color(0, 0, 0, 1F)
    // bb.float(1.1)
    // bb.float(2.5)
    // bb.float(3)
    // bb.float(3)
    bb.endVertex()

    bb.vertex(1, 1, 0) /*.normal(0, 1, 0)*/ .color(0, 0, 1F, 1F) // .color(0, 0, 0, 1F)
    // bb.float(1.1)
    // bb.float(2.5)
    // bb.float(3)
    // bb.float(3)
    bb.endVertex()

    bb.vertex(1, 0, 0) /*.normal(0, 1, 0)*/ .color(0, 0, 0, 1F) // .color(0, 0, 0, 1F)
    // bb.float(1.1)
    // bb.float(2.5)
    // bb.float(3)
    // bb.float(3)
    bb.endVertex()
     */

    val vb             = new VertexBuffer(VertexBuffer.Usage.STATIC)
    val renderedBuffer = bb.end()
    vb.bind()
    vb.upload(renderedBuffer)
    initExtra()

    VertexBuffer.unbind()
    GL15.glBindBuffer(GlConst.GL_ARRAY_BUFFER, 0)
    GL15.glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, 0)

    new MirrorMesh(vb, renderedBuffer.drawState())
  }

}
