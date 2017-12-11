/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.data

import java.text.NumberFormat

import scala.beans.BeanProperty

import org.lwjgl.util.vector.{Matrix4f, Quaternion}

import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

abstract sealed class AbstractQuat {

  /**
		* The type of this quat
		*/
  type Self <: AbstractQuat

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
  def +(other: AbstractQuat):   Self = this + (other.x, other.y, other.z, other.w)
  def add(other: AbstractQuat): Self = this + other

  /**
		* Create a new quat with the passed in values added to this quat
		*/
  def +(x: Double, y: Double, z: Double, w: Double):   Self = create(this.x + x, this.y + y, this.z + z, this.w + w)
  def add(x: Double, y: Double, z: Double, w: Double): Self = this + (x, y, z, w)

  /**
		* Create a new quat with this quat and the other quat multiplied together.
		*/
  def *(other: AbstractQuat):        Self = this * (other.x, other.y, other.z, other.w)
  def multiply(other: AbstractQuat): Self = this * other

  /**
		* Create a new quat with the passed in value multiplied with this quat
		*/
  def *(other: Double):        Self = create(x * other, y * other, z * other, w * other)
  def multiply(other: Double): Self = this * other

  /**
		* Create a new quat with the passed in values multiplied with this quat
		*/
  def *(x: Double, y: Double, z: Double, w: Double): Self = {
    val newX = this.w * x + this.x * w + this.y * z - this.z * y
    val newY = this.w * y + this.y * w + this.z * x - this.x * z
    val newZ = this.w * z + this.z * w + this.x * y - this.y * x
    val newW = this.w * w - this.x * x - this.y * y - this.z * z
    create(newX, newY, newZ, newW)
  }
  def multiply(x: Double, y: Double, z: Double, w: Double): Self = this * (x, y, z, w)

  def mulLeft(other: AbstractQuat): Self = mulLeft(other.x, other.y, other.z, other.w)

  def mulLeft(x: Double, y: Double, z: Double, w: Double): Self = {
    val newX = w * this.x + x * this.w + y * this.z - z * this.y
    val newY = w * this.y + y * this.w + z * this.x - x * this.z
    val newZ = w * this.z + z * this.w + x * this.y - y * this.x
    val newW = w * this.w - x * this.x - y * this.y - z * this.z
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
    if (pole == 0) -Math.asin(MathHelper.clamp(2F * (w * x - z * y), -1F, 1F)) else pole * Math.PI * 0.5F
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
    val pure       = Quat(vec3.x, vec3.y, vec3.z, 0)
    val multiplied = this * pure * this.conjugate
    vec3.create(multiplied.x, multiplied.y, multiplied.z)
  }
  def *(vec3: AbstractVector3): vec3.Self = rotate(vec3)

  def conjugate: Self = create(-x, -y, -z, w)

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
      val invSinTheta = 1F / MathHelper.sin(angle)
      // Calculate the scale for q1 and q2, according to the angle and
      // it's sine value
      scale0 = MathHelper.sin((1F - alpha) * angle) * invSinTheta
      scale1 = MathHelper.sin(alpha * angle) * invSinTheta
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

  @SideOnly(Side.CLIENT)
  def toQuaternion: Quaternion = new Quaternion(x.toFloat, y.toFloat, z.toFloat, w.toFloat)

  @SideOnly(Side.CLIENT)
  def toMatrix4f: Matrix4f = {
    val mat = new Matrix4f()
    val f =  (x * x).toFloat
    val f1 = (x * y).toFloat
    val f2 = (x * z).toFloat
    val f3 = (x * w).toFloat
    val f4 = (y * y).toFloat
    val f5 = (y * z).toFloat
    val f6 = (y * w).toFloat
    val f7 = (z * z).toFloat
    val f8 = (z * w).toFloat
    mat.m00 = 1.0F - 2.0F * (f4 + f7)
    mat.m01 = 2.0F * (f1 + f8)
    mat.m02 = 2.0F * (f2 - f6)
    mat.m03 = 0.0F
    mat.m10 = 2.0F * (f1 - f8)
    mat.m11 = 1.0F - 2.0F * (f + f7)
    mat.m12 = 2.0F * (f5 + f3)
    mat.m13 = 0.0F
    mat.m20 = 2.0F * (f2 + f6)
    mat.m21 = 2.0F * (f5 - f3)
    mat.m22 = 1.0F - 2.0F * (f + f4)
    mat.m23 = 0.0F
    mat.m30 = 0.0F
    mat.m31 = 0.0F
    mat.m32 = 0.0F
    mat.m33 = 1.0F

    mat
  }

  override def toString: String = {
    val format = NumberFormat.getNumberInstance
    format.setMaximumFractionDigits(6)
    toString(format)
  }

  def toString(format: NumberFormat): String =
    s"Quat(${format.format(x)}, ${format.format(y)}, ${format.format(z)}, ${format.format(w)})"
}

final case class MutableQuat(
    @BeanProperty var x: Double,
    @BeanProperty var y: Double,
    @BeanProperty var z: Double,
    @BeanProperty var w: Double
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
  def +=(other: AbstractQuat):         this.type = +=(other.x, other.y, other.z, other.w)
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
  def *=(other: AbstractQuat):              this.type = *=(other.x, other.y, other.z, other.w)
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

  override def add(other: AbstractQuat):                        MutableQuat = super.add(other)
  override def add(x: Double, y: Double, z: Double, w: Double): MutableQuat = super.add(x, y, z, w)

  override def multiply(other: AbstractQuat):                        MutableQuat = super.multiply(other)
  override def multiply(other: Double):                              MutableQuat = super.multiply(other)
  override def multiply(x: Double, y: Double, z: Double, w: Double): MutableQuat = super.multiply(x, y, z, w)

  override def mulLeft(other: AbstractQuat):                        MutableQuat = super.mulLeft(other)
  override def mulLeft(x: Double, y: Double, z: Double, w: Double): MutableQuat = super.mulLeft(x, y, z, w)
}

final case class Quat(
    @BeanProperty x: Double,
    @BeanProperty y: Double,
    @BeanProperty z: Double,
    @BeanProperty w: Double
) extends AbstractQuat {

  override type Self = Quat

  override def create(x: Double, y: Double, z: Double, w: Double): Quat = Quat(x, y, z, w)

  //We make conjugate a lazy val to prevent recomputing it
  override lazy val conjugate: Quat = super.conjugate
  override def asImmutable:    Quat = this

  override def asMutable: MutableQuat = MutableQuat(x, y, z, w)

  override def add(other: AbstractQuat):                        Quat = super.add(other)
  override def add(x: Double, y: Double, z: Double, w: Double): Quat = super.add(x, y, z, w)

  override def multiply(other: AbstractQuat):                        Quat = super.multiply(other)
  override def multiply(other: Double):                              Quat = super.multiply(other)
  override def multiply(x: Double, y: Double, z: Double, w: Double): Quat = super.multiply(x, y, z, w)

  override def mulLeft(other: AbstractQuat):                        Quat = super.mulLeft(other)
  override def mulLeft(x: Double, y: Double, z: Double, w: Double): Quat = super.mulLeft(x, y, z, w)
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
    val sinHalfAngle = MathHelper.sin(halfAngle)
    Quat(vec3.x * sinHalfAngle, vec3.y * sinHalfAngle, vec3.z * sinHalfAngle, MathHelper.cos(halfAngle))
  }

  def fromAxisAngle(vec3: AbstractVector3, angle: Double): Quat = fromAxisAngleRad(vec3, Math.toRadians(angle))

  def fromEuler(yaw: Float, pitch: Float, roll: Float): Quat = {
    val clampedPitch = if (pitch > 90F || pitch < -90F) Math.IEEEremainder(pitch, 180F) else pitch
    val clampedYaw   = if (yaw > 180F || yaw < -180F) Math.IEEEremainder(yaw, 360F) else yaw

    fromEulerRad(Math.toRadians(clampedYaw).toFloat, Math.toRadians(clampedPitch).toFloat, Math.toRadians(roll).toFloat)
  }

  def fromEulerRad(yaw: Float, pitch: Float, roll: Float): Quat = {
    val cy = MathHelper.cos(yaw / 2)
    val cp = MathHelper.cos(pitch / 2)
    val cr = MathHelper.cos(roll / 2)

    val sy = -MathHelper.sin(yaw / 2)
    val sp = MathHelper.sin(pitch / 2)
    val sr = MathHelper.sin(roll / 2)

    val w = cy * cr * cp - sy * sr * sp
    val x = sy * sr * cp + cy * cr * sp
    val y = sy * cr * cp + cy * sr * sp
    val z = cy * sr * cp - sy * cr * sp

    Quat(x, y, z, w)
  }

  def orientationOf(entity: Entity): Quat = fromEuler(entity.rotationYaw, entity.rotationPitch, 0F)

  def lookRotation(forward: AbstractVector3, up: AbstractVector3): Quat = {
    val vect3 = forward.normalize
    val vect1 = up.cross(forward).normalize
    val vect2 = forward.cross(vect1)
    fromAxes(vect1, vect2, vect3)
  }

  def fromAxes(xAxis: AbstractVector3, yAxis: AbstractVector3, zAxis: AbstractVector3): Quat =
    fromAxes(xAxis.x, yAxis.x, zAxis.x, xAxis.y, yAxis.y, zAxis.y, xAxis.z, yAxis.z, zAxis.z)

  //https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java#L496
  def fromAxes(
      xx: Double, xy: Double, xz: Double,
      yx: Double, yy: Double, yz: Double,
      zx: Double, zy: Double, zz: Double
  ): Quat = {
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
