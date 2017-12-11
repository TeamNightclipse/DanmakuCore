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
import org.lwjgl.util.vector.Matrix4f

import net.minecraft.client.renderer.OpenGlHelper

//Copied from ShaderUniform
case class DanCoreUniform(
    location: Int,
    tpe: UniformType,
    bufferSize: Int,
    intBuffer: IntBuffer,
    floatBuffer: FloatBuffer
) {
  var dirty = false

  def set(f: Float): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(0, f)
    dirty = true
  }

  def set(f1: Float, f2: Float): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(0, f1)
    floatBuffer.put(1, f2)
    dirty = true
  }

  def set(f1: Float, f2: Float, f3: Float): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(0, f1)
    floatBuffer.put(1, f2)
    floatBuffer.put(2, f3)
    dirty = true
  }

  def set(f1: Float, f2: Float, f3: Float, f4: Float): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(f1)
    floatBuffer.put(f2)
    floatBuffer.put(f3)
    floatBuffer.put(f4)
    floatBuffer.flip
    dirty = true
  }

  def set(i: Int): Unit = {
    intBuffer.position(0)
    intBuffer.put(0, i)
    dirty = true
  }

  def set(i1: Int, i2: Int): Unit = {
    intBuffer.position(0)
    intBuffer.put(0, i1)
    intBuffer.put(1, i2)
    dirty = true
  }

  def set(i1: Int, i2: Int, i3: Int): Unit = {
    intBuffer.position(0)
    intBuffer.put(0, i1)
    intBuffer.put(1, i2)
    intBuffer.put(2, i3)
    dirty = true
  }

  def set(i1: Int, i2: Int, i3: Int, i4: Int): Unit = {
    intBuffer.position(0)
    intBuffer.put(i1)
    intBuffer.put(i2)
    intBuffer.put(i3)
    intBuffer.put(i4)
    intBuffer.flip
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
  ): Unit = {
    floatBuffer.position(0)
    floatBuffer.put(0, m00)
    floatBuffer.put(1, m01)
    floatBuffer.put(2, m02)
    floatBuffer.put(3, m03)
    floatBuffer.put(4, m10)
    floatBuffer.put(5, m11)
    floatBuffer.put(6, m12)
    floatBuffer.put(7, m13)
    floatBuffer.put(8, m20)
    floatBuffer.put(9, m21)
    floatBuffer.put(10, m22)
    floatBuffer.put(11, m23)
    floatBuffer.put(12, m30)
    floatBuffer.put(13, m31)
    floatBuffer.put(14, m32)
    floatBuffer.put(15, m33)
    dirty = true
  }

  def set(matrix: Matrix4f): Unit =
    this.set(
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
      matrix.m33
    )

  def upload(): Unit = {
    if (dirty) {
      dirty = false
      tpe match {
        //Upload int
        case UniformType.Int   => OpenGlHelper.glUniform1(location, intBuffer)
        case UniformType.IVec2 => OpenGlHelper.glUniform2(location, intBuffer)
        case UniformType.IVec3 => OpenGlHelper.glUniform3(location, intBuffer)
        case UniformType.IVec4 => OpenGlHelper.glUniform4(location, intBuffer)
        //Upload float
        case UniformType.Float => OpenGlHelper.glUniform1(location, floatBuffer)
        case UniformType.Vec2  => OpenGlHelper.glUniform2(location, floatBuffer)
        case UniformType.Vec3  => OpenGlHelper.glUniform3(location, floatBuffer)
        case UniformType.Vec4  => OpenGlHelper.glUniform4(location, floatBuffer)
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
      DanCoreUniform(location, tpe, count, null, BufferUtils.createFloatBuffer(count * tpe.bufferSize))
    else DanCoreUniform(location, tpe, count, BufferUtils.createIntBuffer(count * tpe.bufferSize), null)
}

sealed abstract class UniformType(val floatBuffer: Boolean, val bufferSize: Int)
object UniformType {
  case object Int   extends UniformType(false, 1)
  case object IVec2 extends UniformType(false, 2)
  case object IVec3 extends UniformType(false, 3)
  case object IVec4 extends UniformType(false, 4)
  case object Float extends UniformType(true, 1)
  case object Vec2  extends UniformType(true, 2)
  case object Vec3  extends UniformType(true, 3)
  case object Vec4  extends UniformType(true, 4)
  case object Mat2  extends UniformType(true, 4)
  case object Mat3  extends UniformType(true, 9)
  case object Mat4  extends UniformType(true, 16)
}

case class UniformBase(name: String, tpe: UniformType, count: Int)
