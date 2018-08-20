/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package danmaku

import java.text.NumberFormat

sealed abstract class AbstractQuat { self =>

  /**
    * The type of this quat
    */
  type Self <: AbstractQuat { type Self = self.Self }

  /**
    * The x component
    */
  def x: Double

  /**
    * The y component
    */
  def y: Double

  /**
    * The z component
    */
  def z: Double

  /**
    * The w component
    */
  def w: Double

  /**
    * Creates a new quat of this type. Used for default implementation.
    */
  def create(x: Double, y: Double, z: Double, w: Double): Self

  /**
    * Create a new quat with this quat and the other quat added together.
    */
  def +(other: AbstractQuat): Self   = this + (other.x, other.y, other.z, other.w)
  def add(other: AbstractQuat): Self = this + other

  /**
    * Create a new quat with the passed in values added to this quat
    */
  def +(x: Double, y: Double, z: Double, w: Double): Self   = create(this.x + x, this.y + y, this.z + z, this.w + w)
  def add(x: Double, y: Double, z: Double, w: Double): Self = this + (x, y, z, w)

  /**
    * Create a new quat with this quat and the other quat multiplied together.
    */
  def *(other: AbstractQuat): Self        = this * (other.x, other.y, other.z, other.w)
  def multiply(other: AbstractQuat): Self = this * other

  /**
    * Create a new quat with the passed in value multiplied with this quat
    */
  def *(other: Double): Self        = create(x * other, y * other, z * other, w * other)
  def multiply(other: Double): Self = this * other

  /**
    * Create a new quat with the passed in values multiplied with this quat
    */
  def *(x: Double, y: Double, z: Double, w: Double): Self = {
    val tx = this.x
    val ty = this.y
    val tz = this.z
    val tw = this.w

    val newW = tw * w - tx * x - ty * y - tz * z
    val newX = tw * x + tx * w + ty * z - tz * y
    val newY = tw * y + ty * w + tz * x - tx * z
    val newZ = tw * z + tz * w + tx * y - ty * x
    create(newX, newY, newZ, newW)
  }
  def multiply(x: Double, y: Double, z: Double, w: Double): Self = this * (x, y, z, w)

  def mulLeft(other: AbstractQuat): Self = mulLeft(other.x, other.y, other.z, other.w)

  def mulLeft(x: Double, y: Double, z: Double, w: Double): Self = {
    val tx = this.x
    val ty = this.y
    val tz = this.z
    val tw = this.w

    val newX = w * tx + x * tw + y * tz - z * ty
    val newY = w * ty + y * tw + z * tx - x * tz
    val newZ = w * tz + z * tw + x * ty - y * tx
    val newW = w * tw - x * tx - y * ty - z * tz
    create(newX, newY, newZ, newW)
  }

  /**
    * Gets the length of this quat.
    */
  def length: Double = Math.sqrt(x * x + y * y + z * z + w * w)

  /**
    * Gets the length of this quat squared.
    */
  def lengthSquared: Double = x * x + y * y + z * z + w * w

  /**
    * Gets the pole of the gimbal lock, if any.
    *
    * @return Positive (+1) for north pole, negative (-1) for south pole, zero (0) when no gimbal lock
    */
  def gimbalPole: Int = {
    val t = y * x + z * w
    if (t > 0.499F) 1 else if (t < -0.499F) -1 else 0
  }

  /**
    * Calculates the roll of this quat in radians.
    * Requires that the quat is normalized.
    */
  def rollRad: Double = {
    val pole = gimbalPole
    if (pole == 0) Math.atan2(2f * (w * z + y * x), 1f - 2f * (x * x + z * z)) else pole * 2f * Math.atan2(y, w)
  }

  /**
    * Calculates the roll of this quat in degrees.
    * Requires that the quat is normalized.
    */
  def roll: Double = Math.toDegrees(rollRad)

  /**
    * Calculates the pitch of this quat in radians.
    * Required that the quat is normalized.
    */
  def pitchRad: Double = {
    val pole = gimbalPole

    def clamp(num: Double, min: Double, max: Double) = if (num < min) min else if (num > max) max else num

    if (pole == 0) -Math.asin(clamp(2F * (w * x - z * y), -1F, 1F)) else pole * Math.PI * 0.5F
  }

  /**
    * Calculates the pitch of this quat in degrees.
    * Required that the quat is normalized.
    */
  def pitch: Double = Math.toDegrees(pitchRad)

  /**
    * Calculates the yaw of this quat in radians.
    * Required that the quat is normalized.
    */
  def yawRad: Double = if (gimbalPole == 0) Math.atan2(2f * (y * w + x * z), 1f - 2f * (y * y + x * x)) else 0f

  /**
    * Calculates the yaw of this quat in degrees.
    * Required that the quat is normalized.
    */
  def yaw: Double = Math.toDegrees(yawRad)

  /**
    * Creates a rotated vec from the passed in vec.
    */
  //TODO: Find a more efficient solution
  def rotate(vec3: AbstractVector3): vec3.Self = {
    if(this eq Quat.Identity) {
      vec3.asInstanceOf[vec3.Self]
    }
    else {
      val pure       = Quat(vec3.x, vec3.y, vec3.z, 0)
      val multiplied = this * pure * this.conjugate
      vec3.create(multiplied.x, multiplied.y, multiplied.z)
    }
  }

  def *(vec3: AbstractVector3): vec3.Self = rotate(vec3)

  lazy val conjugate: Self = create(-x, -y, -z, w)

  //https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java#L210
  def normalize: Self = {
    val len2 = lengthSquared
    if (len2 != 0D && len2 != 1D) {
      val len  = Math.sqrt(len2)
      val newX = x / len
      val newY = y / len
      val newZ = z / len
      val newW = w / len
      create(newX, newY, newZ, newW)
    } else create(x, y, z, w)
  }

  /**
    * Computes tha rotation angle of this quat.
    */
  def computeAngle: Double = {
    val normalized = if (w > 1) normalize else this
    Math.toDegrees(2.0 * Math.acos(normalized.w))
  }

  /**
    * Computes the rotationVec of this quat.
    */
  def computeVector: Vector3 = {
    val normalized = if (w > 1) normalize else this
    val scalar     = Math.sqrt(1 - normalized.w * normalized.w)
    if (scalar < 0.000001f) Vector3(normalized.x, normalized.y, normalized.z)
    else Vector3(normalized.x / scalar, normalized.y / scalar, normalized.z / scalar)
  }

  def dot(other: AbstractQuat): Double =
    this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w

  //Taken from libgdx
  def slerp(end: AbstractQuat, alpha: Float): Self = {
    val dotProd = this.dot(end)
    val absDot  = Math.abs(dotProd)

    // Set the first and second scale for the interpolation
    var scale0 = 1F - alpha
    var scale1 = alpha

    // Check if the angle between the 2 quaternions was big enough to
    // warrant such calculations
    if ((1 - absDot) > 0.1) {
      // Get the angle between the 2 quaternions,
      // and then store the sin() of that angle
      val angle       = Math.acos(absDot).toFloat
      val invSinTheta = 1F / Math.sin(angle)
      // Calculate the scale for q1 and q2, according to the angle and
      // it's sine value
      scale0 = (Math.sin((1F - alpha) * angle) * invSinTheta).toFloat
      scale1 = (Math.sin(alpha * angle) * invSinTheta).toFloat
    }

    if (dotProd < 0F) scale1 = -scale1

    // Calculate the x, y, z and w values for the quaternion by using a
    // special form of linear interpolation for quaternions.
    create(
      x = (scale0 * x) + (scale1 * end.x),
      y = (scale0 * y) + (scale1 * end.y),
      z = (scale0 * z) + (scale1 * end.z),
      w = (scale0 * w) + (scale1 * end.w)
    )
  }

  def asImmutable: Quat

  def asMutable: MutableQuat

  override def toString: String = {
    val format = NumberFormat.getNumberInstance
    format.setMaximumFractionDigits(6)
    toString(format)
  }

  def toString(format: NumberFormat): String =
    s"Quat(${format.format(x)}, ${format.format(y)}, ${format.format(z)}, ${format.format(w)})"
}

final case class MutableQuat(
    var x: Double,
    var y: Double,
    var z: Double,
    var w: Double
) extends AbstractQuat {

  override type Self = MutableQuat

  override def create(x: Double, y: Double, z: Double, w: Double): MutableQuat = MutableQuat(x, y, z, w)

  def set(x: Double, y: Double, z: Double, w: Double): this.type = {
    this.x = x
    this.y = y
    this.z = z
    this.w = w
    this
  }

  /**
    * Create a new quat with this quat and the other quat added together.
    */
  def +=(other: AbstractQuat): this.type         = +=(other.x, other.y, other.z, other.w)
  def addMutable(other: AbstractQuat): this.type = this.+=(other)

  /**
    * Create a new quat with the passed in values added to this quat
    */
  def +=(x: Double, y: Double, z: Double, w: Double): this.type = {
    this.x += x
    this.x += y
    this.x += z
    this.x += w
    this
  }
  def addMutable(x: Double, y: Double, z: Double, w: Double): this.type = this.+=(x, y, z, w)

  /**
    * Create a new quat with this quat and the other quat multiplied together.
    */
  def *=(other: AbstractQuat): this.type              = *=(other.x, other.y, other.z, other.w)
  def multiplyMutable(other: AbstractQuat): this.type = this.*=(other)

  /**
    * Create a new quat with the passed in value multiplied with this quat
    */
  def *=(other: Double): this.type = {
    x *= other
    y *= other
    z *= other
    w *= other
    this
  }
  def multiplyMutable(other: Double): this.type = this.*=(other)

  /**
    * Create a new quat with the passed in values multiplied with this quat
    */
  def *=(x: Double, y: Double, z: Double, w: Double): this.type = {
    val newX = this.w * x + this.x * w + this.y * z - this.z * y
    val newY = this.w * y + this.y * w + this.z * x - this.x * z
    val newZ = this.w * z + this.z * w + this.x * y - this.y * x
    val newW = this.w * w - this.x * x - this.y * y - this.z * z
    set(newX, newY, newZ, newW)
  }
  def multiplyMutable(x: Double, y: Double, z: Double, w: Double): this.type = this.*=(x, y, z, w)

  def mulLeftMutable(other: AbstractQuat): MutableQuat = mulLeftMutable(other.x, other.y, other.z, other.w)

  def mulLeftMutable(x: Double, y: Double, z: Double, w: Double): MutableQuat = {
    val newX = w * this.x + x * this.w + y * this.z - z * y
    val newY = w * this.y + y * this.w + z * this.x - x * z
    val newZ = w * this.z + z * this.w + x * this.y - y * x
    val newW = w * this.w - x * this.x - y * this.y - z * z
    set(newX, newY, newZ, newW)
  }

  def rotateMutable(vec3: MutableVector3, destroyThis: Boolean = false): MutableVector3 = {
    val pure = Quat(vec3.x, vec3.y, vec3.z, 0)
    val multiplied = if (destroyThis) {
      this *= pure *= conjugate
    } else {
      this * pure * this.conjugate
    }
    vec3.set(multiplied.x, multiplied.y, multiplied.z)
  }

  def *=(vec3: MutableVector3): MutableVector3 = rotate(vec3)

  def conjugateMutable(): this.type = {
    x = -x
    y = -y
    z = -z
    this
  }

  def normalizeMutable: this.type = {
    val len2 = lengthSquared
    if (len2 != 0D && len2 != 1D) {
      val len  = Math.sqrt(len2)
      val newX = x / len
      val newY = y / len
      val newZ = z / len
      val newW = w / len
      set(newX, newY, newZ, newW)
    } else this
  }

  override def asImmutable: Quat = Quat(x, y, z, w)

  override def asMutable: MutableQuat = this

  def copyObj: MutableQuat = copy()

  //Beyond this is just methods that call super, but with defined Type so that Java likes them

  override def add(other: AbstractQuat): MutableQuat                        = super.add(other)
  override def add(x: Double, y: Double, z: Double, w: Double): MutableQuat = super.add(x, y, z, w)

  override def multiply(other: AbstractQuat): MutableQuat                        = super.multiply(other)
  override def multiply(other: Double): MutableQuat                              = super.multiply(other)
  override def multiply(x: Double, y: Double, z: Double, w: Double): MutableQuat = super.multiply(x, y, z, w)

  override def mulLeft(other: AbstractQuat): MutableQuat                        = super.mulLeft(other)
  override def mulLeft(x: Double, y: Double, z: Double, w: Double): MutableQuat = super.mulLeft(x, y, z, w)
}
object MutableQuat {

  /**
    * Creates a quat from a rotation vec and an angle.
    *
    * @param vec3 The rotation vec.
    * @param angle The rotation angle. This needs to be in radians.
    */
  def fromAxisAngleRad(vec3: AbstractVector3, angle: Double): MutableQuat = {
    val halfAngle    = (angle * 0.5).toFloat
    val sinHalfAngle = Math.sin(halfAngle)
    MutableQuat(vec3.x * sinHalfAngle, vec3.y * sinHalfAngle, vec3.z * sinHalfAngle, Math.cos(halfAngle))
  }

  def fromAxisAngle(vec3: AbstractVector3, angle: Double): MutableQuat = fromAxisAngleRad(vec3, Math.toRadians(angle))

  def fromEuler(yaw: Float, pitch: Float, roll: Float): MutableQuat = {
    val clampedPitch = if (pitch > 90F || pitch < -90F) Math.IEEEremainder(pitch, 180F) else pitch
    val clampedYaw   = if (yaw > 180F || yaw < -180F) Math.IEEEremainder(yaw, 360F) else yaw
    val clampedRoll  = if (roll > 180F || roll < -180F) Math.IEEEremainder(roll, 360F) else roll

    fromEulerRad(
      Math.toRadians(clampedYaw).toFloat,
      Math.toRadians(clampedPitch).toFloat,
      Math.toRadians(clampedRoll).toFloat
    )
  }

  def fromEulerRad(yaw: Float, pitch: Float, roll: Float): MutableQuat = {
    val cy = Math.cos(yaw / 2)
    val cp = Math.cos(pitch / 2)
    val cr = Math.cos(roll / 2)

    val sy = -Math.sin(yaw / 2)
    val sp = Math.sin(pitch / 2)
    val sr = Math.sin(roll / 2)

    val w = cy * cr * cp - sy * sr * sp
    val x = sy * sr * cp + cy * cr * sp
    val y = sy * cr * cp + cy * sr * sp
    val z = cy * sr * cp - sy * cr * sp

    MutableQuat(x, y, z, w)
  }

  def lookRotation(forward: AbstractVector3, up: AbstractVector3): MutableQuat = {
    val vect3 = forward.normalize
    val vect1 = up.cross(forward).normalize
    val vect2 = forward.cross(vect1)
    fromAxes(vect1, vect2, vect3)
  }

  def fromAxes(xAxis: AbstractVector3, yAxis: AbstractVector3, zAxis: AbstractVector3): MutableQuat =
    fromAxes(xAxis.x, yAxis.x, zAxis.x, xAxis.y, yAxis.y, zAxis.y, xAxis.z, yAxis.z, zAxis.z)

  //https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java#L496
  def fromAxes(
      xx: Double,
      xy: Double,
      xz: Double,
      yx: Double,
      yy: Double,
      yz: Double,
      zx: Double,
      zy: Double,
      zz: Double
  ): MutableQuat = {
    val t = xx + yy + zz

    if (t >= 0) {
      val squared = Math.sqrt(t + 1)
      val w       = 0.5f * squared
      val s       = 0.5f / squared

      val x = (zy - yz) * s
      val y = (xz - zx) * s
      val z = (yx - xy) * s
      MutableQuat(x, y, z, w)
    } else if ((xx > yy) && (xx > zz)) {
      val squared = Math.sqrt(1.0 + xx - yy - zz)
      val x       = squared * 0.5f

      val s = 0.5f / squared
      val y = (yx + xy) * s
      val z = (xz + zx) * s
      val w = (zy - yz) * s
      MutableQuat(x, y, z, w)
    } else if (yy > zz) {
      val squared = Math.sqrt(1.0 + yy - xx - zz)
      val y       = squared * 0.5f

      val s = 0.5f / squared
      val x = (yx + xy) * s
      val z = (zy + yz) * s
      val w = (xz - zx) * s
      MutableQuat(x, y, z, w)
    } else {
      val squared = Math.sqrt(1.0 + zz - xx - yy)
      val z       = squared * 0.5f

      val s = 0.5f / squared
      val x = (xz + zx) * s
      val y = (zy + yz) * s
      val w = (yx - xy) * s
      MutableQuat(x, y, z, w)
    }
  }
}

final case class Quat(
    x: Double,
    y: Double,
    z: Double,
    w: Double
) extends AbstractQuat {

  override type Self = Quat

  override def create(x: Double, y: Double, z: Double, w: Double): Quat = Quat(x, y, z, w)

  //We make conjugate a lazy val to prevent recomputing it
  override def asImmutable: Quat    = this

  override def asMutable: MutableQuat = MutableQuat(x, y, z, w)

  override def add(other: AbstractQuat): Quat                        = super.add(other)
  override def add(x: Double, y: Double, z: Double, w: Double): Quat = super.add(x, y, z, w)

  override def multiply(other: AbstractQuat): Quat                        = super.multiply(other)
  override def multiply(other: Double): Quat                              = super.multiply(other)
  override def multiply(x: Double, y: Double, z: Double, w: Double): Quat = super.multiply(x, y, z, w)

  override def mulLeft(other: AbstractQuat): Quat                        = super.mulLeft(other)
  override def mulLeft(x: Double, y: Double, z: Double, w: Double): Quat = super.mulLeft(x, y, z, w)

  /**
    * Creates a rotated vec from the passed in vec.
    */
  //TODO: Find a more efficient solution
  def rotate(vec3: Vector3): Vector3 = {
    if(this eq Quat.Identity) {
      vec3
    }
    else {
      val pure       = Quat(vec3.x, vec3.y, vec3.z, 0)
      val multiplied = this * pure * this.conjugate
      vec3.create(multiplied.x, multiplied.y, multiplied.z)
    }
  }
}

object Quat {

  final val Identity = Quat(0, 0, 0, 1)

  /**
    * Creates a quat from a rotation vec and an angle.
    *
    * @param vec3 The rotation vec.
    * @param angle The rotation angle. This needs to be in radians.
    */
  def fromAxisAngleRad(vec3: AbstractVector3, angle: Double): Quat = {
    val halfAngle    = (angle * 0.5).toFloat
    val sinHalfAngle = Math.sin(halfAngle)
    Quat(vec3.x * sinHalfAngle, vec3.y * sinHalfAngle, vec3.z * sinHalfAngle, Math.cos(halfAngle))
  }

  def fromAxisAngle(vec3: AbstractVector3, angle: Double): Quat = fromAxisAngleRad(vec3, Math.toRadians(angle))

  def fromEuler(yaw: Float, pitch: Float, roll: Float): Quat = {
    val clampedPitch = if (pitch > 90F || pitch < -90F) Math.IEEEremainder(pitch, 180F) else pitch
    val clampedYaw   = if (yaw > 180F || yaw < -180F) Math.IEEEremainder(yaw, 360F) else yaw
    val clampedRoll  = if (roll > 180F || roll < -180F) Math.IEEEremainder(roll, 360F) else roll

    fromEulerRad(
      Math.toRadians(clampedYaw).toFloat,
      Math.toRadians(clampedPitch).toFloat,
      Math.toRadians(clampedRoll).toFloat
    )
  }

  def fromEulerRad(yaw: Float, pitch: Float, roll: Float): Quat = {
    val cy = Math.cos(yaw / 2)
    val cp = Math.cos(pitch / 2)
    val cr = Math.cos(roll / 2)

    val sy = -Math.sin(yaw / 2)
    val sp = Math.sin(pitch / 2)
    val sr = Math.sin(roll / 2)

    val w = cy * cr * cp - sy * sr * sp
    val x = sy * sr * cp + cy * cr * sp
    val y = sy * cr * cp + cy * sr * sp
    val z = cy * sr * cp - sy * cr * sp

    Quat(x, y, z, w)
  }

  def lookRotation(forward: AbstractVector3, up: AbstractVector3): Quat = {
    val vect3 = forward.normalize
    val vect1 = up.cross(forward).normalize
    val vect2 = forward.cross(vect1)
    fromAxes(vect1, vect2, vect3)
  }

  def fromAxes(xAxis: AbstractVector3, yAxis: AbstractVector3, zAxis: AbstractVector3): Quat =
    fromAxes(xAxis.x, yAxis.x, zAxis.x, xAxis.y, yAxis.y, zAxis.y, xAxis.z, yAxis.z, zAxis.z)

  //https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java#L496
  // format: OFF
  def fromAxes(
      xx: Double, xy: Double, xz: Double,
      yx: Double, yy: Double, yz: Double,
      zx: Double, zy: Double, zz: Double
  ): Quat = {
    // format: ON
    val t = xx + yy + zz

    if (t >= 0) {
      val squared = Math.sqrt(t + 1)
      val w       = 0.5f * squared
      val s       = 0.5f / squared

      val x = (zy - yz) * s
      val y = (xz - zx) * s
      val z = (yx - xy) * s
      Quat(x, y, z, w)
    } else if ((xx > yy) && (xx > zz)) {
      val squared = Math.sqrt(1.0 + xx - yy - zz)
      val x       = squared * 0.5f

      val s = 0.5f / squared
      val y = (yx + xy) * s
      val z = (xz + zx) * s
      val w = (zy - yz) * s
      Quat(x, y, z, w)
    } else if (yy > zz) {
      val squared = Math.sqrt(1.0 + yy - xx - zz)
      val y       = squared * 0.5f

      val s = 0.5f / squared
      val x = (yx + xy) * s
      val z = (zy + yz) * s
      val w = (xz - zx) * s
      Quat(x, y, z, w)
    } else {
      val squared = Math.sqrt(1.0 + zz - xx - yy)
      val z       = squared * 0.5f

      val s = 0.5f / squared
      val x = (xz + zx) * s
      val y = (zy + yz) * s
      val w = (yx - xy) * s
      Quat(x, y, z, w)
    }
  }

  implicit class DoubleOps(private val double: Double) extends AnyVal {
    def *(quat: AbstractQuat): quat.Self = quat * double
  }
}

