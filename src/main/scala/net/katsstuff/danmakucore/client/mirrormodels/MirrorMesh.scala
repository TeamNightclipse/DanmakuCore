package net.katsstuff.danmakucore.client.mirrormodels

import java.io.IOException
import java.nio.FloatBuffer
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.Using
import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.{Vector3f, Vector4f}
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec2
import net.minecraftforge.client.model.obj.{MaterialLibrary, OBJLoader, OBJModel}
import org.lwjgl.opengl.{GL11, GL15, GL20, GL30, GL31}
import org.lwjgl.system.{MemoryStack, MemoryUtil}

class MirrorMesh(val vbo: Int, val vao: Int, val ebo: Int, val indicesCount: Int, val vertexCount: Int) {

  def draw(): Unit = {
    bind()
    RenderSystem.drawElements(GlConst.GL_TRIANGLES, indicesCount, GlConst.GL_UNSIGNED_INT)
    unbind()
  }

  def drawInstanced(count: Int): Unit = {
    bind()
    GL31.glDrawElementsInstanced(GlConst.GL_TRIANGLES, indicesCount, GlConst.GL_UNSIGNED_INT, 0, count)
    unbind()
  }

  def bind(): Unit =
    RenderSystem.glBindVertexArray(() => vao)

  def unbind(): Unit =
    RenderSystem.glBindVertexArray(() => 0)

  def delete(): Unit = {
    RenderSystem.glDeleteVertexArrays(vao)
    RenderSystem.glDeleteBuffers(vbo)
    RenderSystem.glDeleteBuffers(ebo)
  }
}
object MirrorMesh {

  private def accessibleFieldGetter[A, B](clazz: Class[_ <: A], fieldName: String): A => B = {
    val field = clazz.getDeclaredField(fieldName)
    field.setAccessible(true)

    obj => field.get(obj).asInstanceOf[B]
  }

  private val objModelClass = classOf[OBJModel]
  private val positionsFieldGetter =
    accessibleFieldGetter[OBJModel, java.util.List[Vector3f]](objModelClass, "positions")
  private val texCoordsFieldGetter = accessibleFieldGetter[OBJModel, java.util.List[Vec2]](objModelClass, "texCoords")
  private val normalsFieldGetter   = accessibleFieldGetter[OBJModel, java.util.List[Vector3f]](objModelClass, "normals")
  private val colorsFieldGetter    = accessibleFieldGetter[OBJModel, java.util.List[Vector4f]](objModelClass, "colors")
  private val objModelPartsGetter =
    accessibleFieldGetter[OBJModel, java.util.Map[String, AnyRef]](objModelClass, "parts")

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
    accessibleFieldGetter[AnyRef, MaterialLibrary.Material](
      modelMeshClass.asInstanceOf[Class[_ <: AnyRef]],
      "mat"
    )

  private val modelMeshFacesGetter =
    accessibleFieldGetter[AnyRef, java.util.List[Array[Array[Int]]]](
      modelMeshClass.asInstanceOf[Class[_ <: AnyRef]],
      "faces"
    )

  //TODO: Make it work with reloading
  //noinspection DuplicatedCode
  def make(
      modelLoc: ResourceLocation,
      posIdx: Int,
      texCoordsIdx: Option[Int] = None,
      normalsIdx: Option[Int] = None,
      colorsIdx: Option[Int] = None,
      initExtra: () => Unit = () => ()
  ): MirrorMesh = {
    val objModel = OBJLoader.INSTANCE.loadModel(
      new OBJModel.ModelSettings(modelLoc, false, false, false, false, null)
    )

    val vao: Int = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val vbo: Int = GL15.glGenBuffers
    val ebo: Int = GL15.glGenBuffers

    def requireOnlyOne[A](iterable: Iterable[A]): A = {
      if (iterable.isEmpty) throw new IOException("Requires at least one Object in OBJ file")

      val first  = iterable.head
      val second = iterable.tail.headOption

      if (second.isDefined) throw new IOException("Can't load complex OBJ files")
      else first
    }

    val (indicesCount, vertexCount) = Using(MemoryStack.stackPush()) { stack =>
      val positions = positionsFieldGetter(objModel).asScala
      val texCoords = texCoordsFieldGetter(objModel).asScala
      val normals   = normalsFieldGetter(objModel).asScala
      val colors    = colorsFieldGetter(objModel).asScala

      val modelGroup = requireOnlyOne(objModelPartsGetter(objModel).asScala.values)
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

      val indiciesCapacity = faces.length * 3
      val indices =
        if (indiciesCapacity <= 256) stack.callocInt(indiciesCapacity) else MemoryUtil.memCallocInt(indiciesCapacity)

      val verticies = mutable.Map[Vector3f, (Vec2, Vector3f, Vector4f)]()

      faces.foreach { face =>
        if (face.length != 3) {
          throw new IOException("MirrorMesh can only use models with triangles")
        }

        val defaultNormal = {
          val a = positions(face(0)(0))
          val ab = positions(face(1)(0))
          val ac = positions(face(2)(0))
          val abs = ab.copy
          abs.sub(a)
          val acs = ac.copy
          acs.sub(a)
          abs.cross(acs)
          abs.normalize
          abs
        }

        face.foreach { faceArr =>
          val posIdx = faceArr(0)

          indices.put(posIdx)

          verticies.put(
            positions(posIdx),
            (
              faceArr.lift(1).flatMap(texCoords.lift).getOrElse(Vec2.ZERO),
              faceArr.lift(2).flatMap(normals.lift).getOrElse(defaultNormal),
              faceArr.lift(3).flatMap(colors.lift).getOrElse(new Vector4f(1, 1, 1, 1))
            )
          )
        }
      }

      val sizePerVertex =
        (3 +
          (if (texCoordsIdx.isEmpty) 0 else 2) +
          (if (normalsIdx.isEmpty) 0 else 3) +
          (if (colorsIdx.isEmpty) 0 else 4)) * 4

      val verticiesCapacity = positions.length * sizePerVertex / 4
      val verticesBuf: FloatBuffer =
        if (verticiesCapacity <= 256) stack.callocFloat(verticiesCapacity)
        else MemoryUtil.memCallocFloat(verticiesCapacity)

      positions.foreach { pos =>
        verticesBuf.put(pos.x())
        verticesBuf.put(pos.y())
        verticesBuf.put(pos.z())

        val (vTexCoords, vNormals, vColors) = verticies(pos)
        if (texCoordsIdx.isDefined) {
          verticesBuf.put(vTexCoords.x)
          verticesBuf.put(vTexCoords.y)
        }

        if (normalsIdx.isDefined) {
          verticesBuf.put(vNormals.x())
          verticesBuf.put(vNormals.y())
          verticesBuf.put(vNormals.z())
        }

        if (colorsIdx.isDefined) {
          verticesBuf.put(vColors.x())
          verticesBuf.put(vColors.y())
          verticesBuf.put(vColors.z())
          verticesBuf.put(vColors.w())
        }
      }


      verticesBuf.flip()
      GL15.glBindBuffer(GlConst.GL_ARRAY_BUFFER, vbo)
      GL15.glBufferData(GlConst.GL_ARRAY_BUFFER, verticesBuf, GlConst.GL_STATIC_DRAW)

      indices.flip()
      GL15.glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, ebo)
      GL15.glBufferData(GlConst.GL_ELEMENT_ARRAY_BUFFER, indices, GlConst.GL_STATIC_DRAW)

      GL20.glVertexAttribPointer(posIdx, 3, GlConst.GL_FLOAT, false, sizePerVertex, 0)
      GL20.glEnableVertexAttribArray(posIdx)

      texCoordsIdx.foreach { texCoordsIdx =>
        GL20.glVertexAttribPointer(texCoordsIdx, 2, GlConst.GL_FLOAT, false, sizePerVertex, 3 * 4)
        GL20.glEnableVertexAttribArray(texCoordsIdx)
      }

      normalsIdx.foreach { normalsIdx =>
        val offset = if (texCoordsIdx.isEmpty) 3 else 5

        GL20.glVertexAttribPointer(normalsIdx, 3, GlConst.GL_FLOAT, false, sizePerVertex, offset * 4)
        GL20.glEnableVertexAttribArray(normalsIdx)
      }

      colorsIdx.foreach { colorsIdx =>
        val normalsOffset = if (texCoordsIdx.isEmpty) 3 else 5
        val offset = if (normalsIdx.isEmpty) normalsOffset else normalsOffset + 3

        GL20.glVertexAttribPointer(colorsIdx, 4, GlConst.GL_FLOAT, false, sizePerVertex, offset * 4)
        GL20.glEnableVertexAttribArray(colorsIdx)
      }

      initExtra()

      GL30.glBindVertexArray(0)
      GL15.glBindBuffer(GlConst.GL_ARRAY_BUFFER, 0)
      GL15.glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, 0)

      if (verticiesCapacity > 256) {
        MemoryUtil.memFree(verticesBuf)
      }
      if (indiciesCapacity > 256) {
        MemoryUtil.memFree(indices)
      }

      (faces.length * 3, verticies.size)
    }.get

    new MirrorMesh(vbo, vao, ebo, indicesCount, vertexCount)
  }

}
