/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.shader

import java.nio.{FloatBuffer, IntBuffer}

import org.lwjgl.BufferUtils
import org.lwjgl.util.vector.{Matrix2f, Matrix3f, Matrix4f}

import net.minecraft.client.renderer.OpenGlHelper

//Copied from ShaderUniform
class DanCoreUniform(
    location: Int,
    val tpe: UniformType,
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

  def setIdx(matrix: Matrix2f, idx: Int): Unit = setIdx(matrix.m00, matrix.m01, matrix.m10, matrix.m11, idx)

  def set(matrix: Matrix2f): Unit = setIdx(matrix, 0)

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

  def setIdx(matrix: Matrix3f, idx: Int): Unit =
    setIdx(
      matrix.m00,
      matrix.m01,
      matrix.m02,
      matrix.m10,
      matrix.m11,
      matrix.m12,
      matrix.m20,
      matrix.m21,
      matrix.m22,
      idx
    )

  def set(matrix: Matrix3f): Unit = setIdx(matrix, 0)

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

  def setIdx(matrix: Matrix4f, idx: Int): Unit =
    setIdx(
      matrix.m00,
      matrix.m01,
      matrix.m02,
      matrix.m03,
      matrix.m10,
      matrix.m11,
      matrix.m12,
      matrix.m13,
      matrix.m20,
      matrix.m21,
      matrix.m22,
      matrix.m23,
      matrix.m30,
      matrix.m31,
      matrix.m32,
      matrix.m33,
      idx
    )

  def set(matrix: Matrix4f): Unit = setIdx(matrix, 0)

  def upload(): Unit = {
    if (dirty) {
      dirty = false
      tpe match {
        //Upload int
        case UniformType.UnInt => OpenGlHelper.glUniform1(location, intBuffer)
        case UniformType.IVec2 => OpenGlHelper.glUniform2(location, intBuffer)
        case UniformType.IVec3 => OpenGlHelper.glUniform3(location, intBuffer)
        case UniformType.IVec4 => OpenGlHelper.glUniform4(location, intBuffer)
        //Upload float
        case UniformType.UnFloat => OpenGlHelper.glUniform1(location, floatBuffer)
        case UniformType.Vec2    => OpenGlHelper.glUniform2(location, floatBuffer)
        case UniformType.Vec3    => OpenGlHelper.glUniform3(location, floatBuffer)
        case UniformType.Vec4    => OpenGlHelper.glUniform4(location, floatBuffer)
        //Upload float matrix
        case UniformType.Mat2 => OpenGlHelper.glUniformMatrix2(location, true, floatBuffer)
        case UniformType.Mat3 => OpenGlHelper.glUniformMatrix3(location, true, floatBuffer)
        case UniformType.Mat4 => OpenGlHelper.glUniformMatrix4(location, true, floatBuffer)
      }
    }
  }
}
object DanCoreUniform {

  def create(location: Int, tpe: UniformType, count: Int): DanCoreUniform =
    if (tpe.floatBuffer)
      new DanCoreUniform(location, tpe, count, null, BufferUtils.createFloatBuffer(count * tpe.bufferSize))
    else new DanCoreUniform(location, tpe, count, BufferUtils.createIntBuffer(count * tpe.bufferSize), null)
}

sealed abstract class UniformType(val floatBuffer: Boolean, val bufferSize: Int) {
  def instance: UniformType = this
}
object UniformType {
  sealed trait UnInt         extends UniformType
  implicit case object UnInt extends UniformType(false, 1) with UnInt

  sealed trait IVec2         extends UniformType
  implicit case object IVec2 extends UniformType(false, 2) with IVec2

  sealed trait IVec3         extends UniformType
  implicit case object IVec3 extends UniformType(false, 3) with IVec3

  sealed trait IVec4         extends UniformType
  implicit case object IVec4 extends UniformType(false, 4) with IVec4

  sealed trait UnFloat         extends UniformType
  implicit case object UnFloat extends UniformType(true, 1) with UnFloat

  sealed trait Vec2         extends UniformType
  implicit case object Vec2 extends UniformType(true, 2) with Vec2

  sealed trait Vec3         extends UniformType
  implicit case object Vec3 extends UniformType(true, 3) with Vec3

  sealed trait Vec4         extends UniformType
  implicit case object Vec4 extends UniformType(true, 4) with Vec4

  sealed trait Mat2         extends UniformType
  implicit case object Mat2 extends UniformType(true, 4) with Mat2

  sealed trait Mat3         extends UniformType
  implicit case object Mat3 extends UniformType(true, 9) with Mat3

  sealed trait Mat4         extends UniformType
  implicit case object Mat4 extends UniformType(true, 16) with Mat4
}

case class UniformBase(name: String, tpe: UniformType, count: Int)

class NOOPUniform(tpe: UniformType, count: Int) extends DanCoreUniform(0, tpe, count, null, null) {
  override def setIdx(f: Float, idx: Int):                                   Unit = ()
  override def setIdx(f1: Float, f2: Float, idx: Int):                       Unit = ()
  override def setIdx(f1: Float, f2: Float, f3: Float, idx: Int):            Unit = ()
  override def setIdx(f1: Float, f2: Float, f3: Float, f4: Float, idx: Int): Unit = ()

  override def setIdx(i: Int, idx: Int):                             Unit = ()
  override def setIdx(i1: Int, i2: Int, idx: Int):                   Unit = ()
  override def setIdx(i1: Int, i2: Int, i3: Int, idx: Int):          Unit = ()
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
