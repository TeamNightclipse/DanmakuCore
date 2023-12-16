package net.katsstuff.danmakucore.client.mirrorshaders

import java.nio.{FloatBuffer, IntBuffer}
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.math.{Matrix3f, Matrix4f}
import net.katsstuff.danmakucore.math.{AbstractMat4, Mat4}
import org.lwjgl.BufferUtils

/**
  * Represents a uniform within a shader.
  * @param location
  *   The location of the uniform in memory.
  * @param tpe
  *   The uniform type.
  * @param count
  *   The amount of uniforms. Used for arrays.
  * @param intBuffer
  *   The int buffer for this uniform. Might not be valid given the type.
  * @param floatBuffer
  *   The float buffer for this uniform. Might not be valid given the type.
  * @tparam Type
  *   The type for this uniform.
  */
//Copied from ShaderUniform
class MirrorUniform[+Type <: UniformType](
    location: Int,
    val tpe: Type,
    val count: Int,
    intBuffer: IntBuffer,
    floatBuffer: FloatBuffer
) {
  var dirty = false

  def setIdx(f: Float, idx: Int): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(idx, f)
    dirty = true
  }

  def set(f: Float): Unit = setIdx(f, 0)

  def setIdx(f1: Float, f2: Float, idx: Int): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(idx + 0, f1)
    floatBuffer.put(idx + 1, f2)
    dirty = true
  }

  def set(f1: Float, f2: Float): Unit = set(f1, f2, 0)

  def setIdx(f1: Float, f2: Float, f3: Float, idx: Int): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(idx + 0, f1)
    floatBuffer.put(idx + 1, f2)
    floatBuffer.put(idx + 2, f3)
    dirty = true
  }

  def set(f1: Float, f2: Float, f3: Float): Unit = setIdx(f1, f2, f3, 0)

  def setIdx(f1: Float, f2: Float, f3: Float, f4: Float, idx: Int): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(idx + 0, f1)
    floatBuffer.put(idx + 1, f2)
    floatBuffer.put(idx + 2, f3)
    floatBuffer.put(idx + 3, f4)
    dirty = true
  }

  def set(f1: Float, f2: Float, f3: Float, f4: Float): Unit = setIdx(f1, f2, f3, f4, 0)

  def setIdx(i: Int, idx: Int): Unit = {
    intBuffer.position(0)
    intBuffer.put(idx, i)
    dirty = true
  }

  def set(i: Int): Unit = setIdx(i, 0)

  def setIdx(i1: Int, i2: Int, idx: Int): Unit = {
    intBuffer.position(0)
    intBuffer.put(idx + 0, i1)
    intBuffer.put(idx + 1, i2)
    dirty = true
  }

  def set(i1: Int, i2: Int): Unit = setIdx(i1, i2, 0)

  def setIdx(i1: Int, i2: Int, i3: Int, idx: Int): Unit = {
    intBuffer.position(0)
    intBuffer.put(idx + 0, i1)
    intBuffer.put(idx + 1, i2)
    intBuffer.put(idx + 2, i3)
    dirty = true
  }

  def set(i1: Int, i2: Int, i3: Int): Unit = setIdx(i1, i2, i3, 0)

  def setIdx(i1: Int, i2: Int, i3: Int, i4: Int, idx: Int): Unit = {
    intBuffer.position(0)
    intBuffer.put(idx + 0, i1)
    intBuffer.put(idx + 1, i2)
    intBuffer.put(idx + 2, i3)
    intBuffer.put(idx + 3, i4)
    dirty = true
  }

  def set(i1: Int, i2: Int, i3: Int, i4: Int): Unit = setIdx(i1, i2, i3, i4, 0)

  def setIdx(
      m00: Float,
      m01: Float,
      m02: Float,
      m10: Float,
      m11: Float,
      m12: Float,
      m20: Float,
      m21: Float,
      m22: Float,
      idx: Int
  ): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(idx + 0, m00)
    floatBuffer.put(idx + 1, m01)
    floatBuffer.put(idx + 2, m02)
    floatBuffer.put(idx + 3, m10)
    floatBuffer.put(idx + 4, m11)
    floatBuffer.put(idx + 5, m12)
    floatBuffer.put(idx + 6, m20)
    floatBuffer.put(idx + 7, m21)
    floatBuffer.put(idx + 8, m22)
    dirty = true
  }

  def set(
      m00: Float,
      m01: Float,
      m02: Float,
      m10: Float,
      m11: Float,
      m12: Float,
      m20: Float,
      m21: Float,
      m22: Float
  ): Unit = setIdx(m00, m01, m02, m10, m11, m12, m20, m21, m22, 0)

  def set(matrix: Matrix3f): Unit = {
    floatBuffer.position(0)
    matrix.store(floatBuffer)
  }

  def setIdx(
      m00: Float,
      m01: Float,
      m02: Float,
      m03: Float,
      m10: Float,
      m11: Float,
      m12: Float,
      m13: Float,
      m20: Float,
      m21: Float,
      m22: Float,
      m23: Float,
      m30: Float,
      m31: Float,
      m32: Float,
      m33: Float,
      idx: Int
  ): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(idx + 0, m00)
    floatBuffer.put(idx + 1, m01)
    floatBuffer.put(idx + 2, m02)
    floatBuffer.put(idx + 3, m03)
    floatBuffer.put(idx + 4, m10)
    floatBuffer.put(idx + 5, m11)
    floatBuffer.put(idx + 6, m12)
    floatBuffer.put(idx + 7, m13)
    floatBuffer.put(idx + 8, m20)
    floatBuffer.put(idx + 9, m21)
    floatBuffer.put(idx + 10, m22)
    floatBuffer.put(idx + 11, m23)
    floatBuffer.put(idx + 12, m30)
    floatBuffer.put(idx + 13, m31)
    floatBuffer.put(idx + 14, m32)
    floatBuffer.put(idx + 15, m33)
    dirty = true
  }

  def set(
      m00: Float,
      m01: Float,
      m02: Float,
      m03: Float,
      m10: Float,
      m11: Float,
      m12: Float,
      m13: Float,
      m20: Float,
      m21: Float,
      m22: Float,
      m23: Float,
      m30: Float,
      m31: Float,
      m32: Float,
      m33: Float
  ): Unit = setIdx(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33, 0)

  def set(matrix: Matrix4f): Unit = {
    floatBuffer.position(0)
    matrix.storeTransposed(floatBuffer)
    dirty = true
  }

  def set(mat: AbstractMat4): Unit = {
    set(
      mat.m00.toFloat,
      mat.m01.toFloat,
      mat.m02.toFloat,
      mat.m03.toFloat,
      mat.m10.toFloat,
      mat.m11.toFloat,
      mat.m12.toFloat,
      mat.m13.toFloat,
      mat.m20.toFloat,
      mat.m21.toFloat,
      mat.m22.toFloat,
      mat.m23.toFloat,
      mat.m30.toFloat,
      mat.m31.toFloat,
      mat.m32.toFloat,
      mat.m33.toFloat
    )
  }

  def upload(): Unit =
    if (dirty) {
      dirty = false
      tpe.upload(location, intBuffer, floatBuffer)
    }
}
object MirrorUniform {

  private[mirrorshaders] def create[Type <: UniformType](location: Int, tpe: Type, count: Int): MirrorUniform[Type] =
    if (tpe.useFloatBuffer)
      new MirrorUniform(location, tpe, count, null, BufferUtils.createFloatBuffer(count * tpe.bufferSize))
    else new MirrorUniform(location, tpe, count, BufferUtils.createIntBuffer(count * tpe.bufferSize), null)
}

sealed abstract class UniformType(val useFloatBuffer: Boolean, val bufferSize: Int) {
  def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit
}
object UniformType {
  sealed abstract class UnInt extends UniformType(false, 1) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniform1(location, intBuffer)
  }
  implicit case object UnInt extends UnInt
  def unInt: UnInt.type = UnInt

  sealed abstract class IVec2 extends UniformType(false, 2) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniform2(location, intBuffer)
  }
  implicit case object IVec2 extends IVec2
  def iVec2: IVec2.type = IVec2

  sealed abstract class IVec3 extends UniformType(false, 3) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniform3(location, intBuffer)
  }
  implicit case object IVec3 extends IVec3
  def iVec3: IVec3.type = IVec3

  sealed abstract class IVec4 extends UniformType(false, 4) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniform4(location, intBuffer)
  }
  implicit case object IVec4 extends IVec4
  def iVec4: IVec4.type = IVec4

  sealed abstract class UnFloat extends UniformType(true, 1) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniform1(location, floatBuffer)
  }
  implicit case object UnFloat extends UnFloat
  def unFloat: UnFloat.type = UnFloat

  sealed abstract class Vec2 extends UniformType(true, 2) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniform2(location, floatBuffer)
  }
  implicit case object Vec2 extends Vec2
  def vec2: Vec2.type = Vec2

  sealed abstract class Vec3 extends UniformType(true, 3) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniform3(location, floatBuffer)
  }
  implicit case object Vec3 extends Vec3
  def vec3: Vec3.type = Vec3

  sealed abstract class Vec4 extends UniformType(true, 4) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniform4(location, floatBuffer)
  }
  implicit case object Vec4 extends Vec4
  def vec4: Vec4.type = Vec4

  sealed abstract class Mat2 extends UniformType(true, 4) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniformMatrix2(location, true, floatBuffer)
  }
  implicit case object Mat2 extends Mat2
  def mat2: Mat2.type = Mat2

  sealed abstract class Mat3 extends UniformType(true, 9) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniformMatrix3(location, true, floatBuffer)
  }
  implicit case object Mat3 extends Mat3
  def mat3: Mat3.type = Mat3

  sealed abstract class Mat4 extends UniformType(true, 16) {
    override def upload(location: Int, intBuffer: IntBuffer, floatBuffer: FloatBuffer): Unit =
      GlStateManager._glUniformMatrix4(location, true, floatBuffer)
  }
  implicit case object Mat4 extends Mat4
  def mat4: Mat4.type = Mat4
}

case class UniformBase[+Type <: UniformType](tpe: Type, count: Int)

class NOOPUniform(tpe: UniformType, count: Int) extends MirrorUniform(0, tpe, count, null, null) {
  override def setIdx(f: Float, idx: Int): Unit                                   = ()
  override def setIdx(f1: Float, f2: Float, idx: Int): Unit                       = ()
  override def setIdx(f1: Float, f2: Float, f3: Float, idx: Int): Unit            = ()
  override def setIdx(f1: Float, f2: Float, f3: Float, f4: Float, idx: Int): Unit = ()

  override def setIdx(i: Int, idx: Int): Unit                             = ()
  override def setIdx(i1: Int, i2: Int, idx: Int): Unit                   = ()
  override def setIdx(i1: Int, i2: Int, i3: Int, idx: Int): Unit          = ()
  override def setIdx(i1: Int, i2: Int, i3: Int, i4: Int, idx: Int): Unit = ()

  override def setIdx(
      m00: Float,
      m01: Float,
      m02: Float,
      m10: Float,
      m11: Float,
      m12: Float,
      m20: Float,
      m21: Float,
      m22: Float,
      idx: Int
  ): Unit = ()

  override def setIdx(
      m00: Float,
      m01: Float,
      m02: Float,
      m03: Float,
      m10: Float,
      m11: Float,
      m12: Float,
      m13: Float,
      m20: Float,
      m21: Float,
      m22: Float,
      m23: Float,
      m30: Float,
      m31: Float,
      m32: Float,
      m33: Float,
      idx: Int
  ): Unit = ()

  override def upload(): Unit = ()
}
