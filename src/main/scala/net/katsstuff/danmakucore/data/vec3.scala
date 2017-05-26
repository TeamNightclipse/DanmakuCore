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
import java.util.function.Predicate
import java.util.{Optional, Random}

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

import com.google.common.base.{Predicate => GPredicate}

import net.katsstuff.danmakucore.helper.DanmakuHelper
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.math.{BlockPos, MathHelper, Vec3d}

sealed trait AbstractVector3 extends Any {

  /**
		* The type of this vector
		*/
  type Self <: AbstractVector3

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
		* Creates a new vector of this type. For easy of use
		*/
  def create(x: Double, y: Double, z: Double): Self

  /**
		* Create a new vector with this vector and the other vector added together.
		*/
  def +(other: AbstractVector3):   Self = this.+(other.x, other.y, other.z)
  def add(other: AbstractVector3): Self = this.+(other)

  /**
		* Create a new vector with the passed in value added to this vector
		*/
  def +(other: Double):   Self = this.+(other, other, other)
  def add(other: Double): Self = this.+(other)

  /**
		* Create a new vector with the passed in values added to this vector
		*/
  def +(x: Double, y: Double, z: Double):   Self = create(this.x + x, this.y + y, this.z + z)
  def add(x: Double, y: Double, z: Double): Self = this.+(x, y, z)

  /**
		* Create a new vector with the passed in vector subtracted from this vector
		*/
  def -(other: AbstractVector3):        Self = this.-(other.x, other.y, other.z)
  def subtract(other: AbstractVector3): Self = this.-(other)

  /**
		* Create a new vector with the passed in value subtracted from this vector
		*/
  def -(other: Double):        Self = this.-(other, other, other)
  def subtract(other: Double): Self = this.-(other)

  /**
		* Create a new vector with the passed in values subtracted from this vector
		*/
  def -(x: Double, y: Double, z: Double):        Self = create(this.x - x, this.y - y, this.z - z)
  def subtract(x: Double, y: Double, z: Double): Self = this.-(x, y, z)

  /**
		* Create a new vector with this vector and the other vector multiplied together.
		*/
  def *(other: AbstractVector3):        Self = *(other.x, other.y, other.z)
  def multiply(other: AbstractVector3): Self = this.*(other)

  /**
		* Create a new vector with the passed in value multiplied with this vector
		*/
  def *(other: Double):        Self = *(other, other, other)
  def multiply(other: Double): Self = this.*(other)

  /**
		* Create a new vector with the passed in values multiplied with this vector
		*/
  def *(x: Double, y: Double, z: Double):        Self = create(this.x * x, this.y * y, this.z * z)
  def multiply(x: Double, y: Double, z: Double): Self = this.*(x, y, z)

  /**
		* Create a new vector with the passed in vector divided by this vector
		*/
  def /(other: AbstractVector3):      Self = /(other.x, other.y, other.z)
  def divide(other: AbstractVector3): Self = this./(other)

  /**
		* Create a new vector with the passed in value divided by this vector
		*/
  def /(other: Double):      Self = /(other, other, other)
  def divide(other: Double): Self = this./(other)

  /**
		* Create a new vector with the passed in values divided by this vector
		*/
  def /(x: Double, y: Double, z: Double):      Self = create(this.x / x, this.y / y, this.z / z)
  def divide(x: Double, y: Double, z: Double): Self = this./(x, y, z)

  def unary_- : Self = create(-x, -y, -z)
  def negate:   Self = unary_-

  /**
		* Create a new vector that is normalized.
		*/
  def normalize: Self = {
    val len = lengthSquared
    if (len == 0D || len == 1D) create(x, y, z) else *(1D / Math.sqrt(len))
  }

  /**
		* Gets the dot product of this vector and the other vector.
		*/
  def dot(other: AbstractVector3): Double = dot(other.x, other.y, other.z)

  /**
		* Gets the dot product of this vector and the passed in values.
		*/
  def dot(x: Double, y: Double, z: Double): Double = this.x * x + this.y * y + this.z * z

  /**
		* Creates a new vector that is the cross product of this vector and the passed in vector.
		*/
  def cross(other: AbstractVector3): Self = cross(other.x, other.y, other.z)

  /**
		* Creates a new vector that is the cross product of this vector and the passed in values.
		*/
  def cross(x: Double, y: Double, z: Double): Self = {
    val newX = this.y * z - this.z * y
    val newY = this.z * x - this.x * z
    val newZ = this.x * y - this.y * x
    create(newX, newY, newZ)
  }

  /**
		* Gets the length of this vector.
		*/
  def length: Double = Math.sqrt(x * x + y * y + z * z)

  /**
		* Gets the length of this vector squared.
		*/
  def lengthSquared: Double = x * x + y * y + z * z

  /**
		* Gets the distance between this vector and the passed in vector.
		*/
  def distance(other: AbstractVector3): Double = distance(other.x, other.y, other.z)

  /**
		* Gets the distance between this vector and the passed in values.
		*/
  def distance(x: Double, y: Double, z: Double): Double = {
    val xDist = x - this.x
    val yDist = y - this.y
    val zDist = z - this.z
    Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist)
  }

  /**
		* Gets the distance between this vector and the passed in vector squared.
		*/
  def distanceSquared(other: AbstractVector3): Double = distanceSquared(other.x, other.y, other.z)

  /**
		* Gets the distance between this vector and the passed in values squared.
		*/
  def distanceSquared(x: Double, y: Double, z: Double): Double = {
    val d0 = x - this.x
    val d1 = y - this.y
    val d2 = z - this.z
    d0 * d0 + d1 * d1 + d2 * d2
  }

  /**
		* Offsets this vec in an angle th given distance.
		*/
  def offset(angle: AbstractVector3, distance: Double): Self = this + (angle * distance)

  /**
		* Gets the angle between this vector and the passed in vector.
		*/
  def angle(other: AbstractVector3): Double = Math.acos(normalize.dot(other.normalize))

  /**
		* Rotates this vector using the given quat.
		*/
  def rotate(quat: Quat): Self = quat.rotate(this)

  /**
		* Rotate this vector around the given point.
		*/
  def rotate(angle: Double, point: AbstractVector3): Self = rotate(Quat.fromVector(point, angle))

  /**
		* Rotate this vector around the given point.
		* The angle must be in radiens.
		*/
  def rotateRad(angle: Double, point: AbstractVector3): Self = rotate(Quat.fromVectorRad(point, angle))

  /**
		* Calculates the yaw of this vector in radians.
		*/
  def yawRad: Double = -Math.atan2(x, z)

  /**
		* Calculates the yaw from this vector.
		*/
  def yaw: Double = Math.toDegrees(yawRad)

  /**
		* Calculates the pitch of this vector in radians.
		*/
  def pitchRad: Double = {
    val f = Math.sqrt(x * x + z * z)
    -MathHelper.atan2(y, f)
  }

  /**
		* Calculates the pitch from this vector.
		*/
  def pitch: Double = Math.toDegrees(pitchRad)

  def lerp(target: AbstractVector3, alpha: Double): Self = {
    create(x + alpha * (target.x - x), y + alpha * (target.y - y), z + alpha * (target.z - z))
  }

  //From libgdx
  def slerp(target: AbstractVector3, alpha: Double): Self#Self = {
    val dotProd = this.dot(target)

    if (Math.abs(dotProd) > 0.9995) lerp(target, alpha).normalize
    else {
      val theta0 = Math.acos(dotProd)
      val theta  = (theta0 * alpha).toFloat

      val st = MathHelper.sin(theta)
      val tx = target.x - x * dotProd
      val ty = target.y - y * dotProd
      val tz = target.z - z * dotProd
      val l2 = tx * tx + ty * ty + tz * tz
      val dl = st * (if (l2 < 0.0001F) 1F else 1F / Math.sqrt(l2).toFloat)

      val thetaCos = MathHelper.cos(theta)
      create(this.x * thetaCos + tx * dl, this.y * thetaCos + ty * dl, this.z * thetaCos + tz * dl).normalize
    }
  }

  def transformDirection(mat: AbstractMat4): Self = mat.transformDirection(this)

  def asMutable: MutableVector3

  def asImmutable: Vector3

  def toVec3d: Vec3d = new Vec3d(x, y, z)

  def toBlockPos: BlockPos = new BlockPos(x, y, z)

  override def toString: String = {
    val format = NumberFormat.getNumberInstance
    format.setMaximumFractionDigits(6)
    toString(format)
  }

  def toString(format: NumberFormat): String =
    s"Vector3(${format.format(x)}, ${format.format(y)}, ${format.format(z)})"
}

final case class MutableVector3(@BeanProperty var x: Double, @BeanProperty var y: Double, @BeanProperty var z: Double) extends AbstractVector3 {

  override type Self = MutableVector3

  def this(entity: Entity) {
    this(entity.posX, entity.posY, entity.posZ)
  }

  def this(living: EntityLivingBase) {
    this(living.posX, living.posY + living.getEyeHeight, living.posZ)
  }

  def this(vec: Vec3d) {
    this(vec.xCoord, vec.yCoord, vec.zCoord)
  }

  override def create(x: Double, y: Double, z: Double): MutableVector3 = MutableVector3(x, y, z)

  /**
		* Sets the components of this vector.
		*/
  def set(x: Double, y: Double, z: Double): this.type = {
    this.x = x
    this.y = y
    this.z = z
    this
  }

  /**
		* Adds the passed in vector to this vector.
		*/
  def +=(other: AbstractVector3):         this.type = +=(other.x, other.y, other.z)
  def addMutable(other: AbstractVector3): this.type = this.+=(other)

  /**
		* Adds the passed in value to this vector.
		*/
  def +=(other: Double):         this.type = +=(other, other, other)
  def addMutable(other: Double): this.type = this.+=(other)

  /**
		* Adds the passed in values to this vector.
		*/
  def +=(x: Double, y: Double, z: Double): this.type = {
    this.x += x
    this.y += y
    this.z += z
    this
  }
  def addMutable(x: Double, y: Double, z: Double): this.type = this.+=(x, y, z)

  /**
		* Subtracts the passed in vector from this vector.
		*/
  def -=(other: AbstractVector3):              this.type = -=(other.x, other.y, other.z)
  def subtractMutable(other: AbstractVector3): this.type = this.-=(other)

  /**
		* Subtracts the passed in value from this vector.
		*/
  def -=(other: Double):              this.type = -=(other, other, other)
  def subtractMutable(other: Double): this.type = this.-=(other)

  /**
		* Subtracts the passed in values from this vector.
		*/
  def -=(x: Double, y: Double, z: Double): this.type = {
    this.x -= x
    this.y -= y
    this.z -= z
    this
  }
  def subtractMutable(x: Double, y: Double, z: Double): this.type = this.-=(x, y, z)

  /**
		* Multiplies this vector with the passed in vector.
		*/
  def *=(other: AbstractVector3):              this.type = *=(other.x, other.y, other.z)
  def multiplyMutable(other: AbstractVector3): this.type = this.*=(other)

  /**
		* Multiplies this vector with the passed in value.
		*/
  def *=(other: Double):              this.type = *=(other, other, other)
  def multiplyMutable(other: Double): this.type = this.*=(other)

  /**
		* Multiplies this vector with the passed in values.
		*/
  def *=(x: Double, y: Double, z: Double): this.type = {
    this.x *= x
    this.y *= y
    this.z *= z
    this
  }
  def multiplyMutable(x: Double, y: Double, z: Double): this.type = this.*=(x, y, z)

  /**
		* Divides this vector with the passed in vector.
		*/
  def /=(other: AbstractVector3):            this.type = /=(other.x, other.y, other.z)
  def divideMutable(other: AbstractVector3): this.type = this./=(other)

  /**
		* Divides this vector with the passed in value.
		*/
  def /=(other: Double):            this.type = /=(other, other, other)
  def divideMutable(other: Double): this.type = this./=(other)

  /**
		* Divides this vector with the passed in values.
		*/
  def /=(x: Double, y: Double, z: Double): this.type = {
    this.x /= x
    this.y /= y
    this.z /= z
    this
  }
  def divideMutable(x: Double, y: Double, z: Double): this.type = this./=(x, y, z)

  /**
		* Normalizes this vector
		*/
  def normalizeMutable: this.type = {
    val len = lengthSquared
    if (len == 0D || len == 1D) this else *=(1D / Math.sqrt(len))
  }

  /**
		* Sets this vector to the cross product of this vector and the passed in vector.
		*/
  def crossMutable(other: AbstractVector3): this.type = crossMutable(other.x, other.y, other.z)

  /**
		* Sets this vector to the cross product of this vector and the passed in values.
		*/
  def crossMutable(x: Double, y: Double, z: Double): this.type = {
    this.x = this.y * z - this.z * y
    this.y = this.z * x - this.x * z
    this.z = this.x * y - this.y * x
    this
  }

  override def asMutable: MutableVector3 = this

  override def asImmutable: Vector3 = Vector3(x, y, z)

  def copyObj: MutableVector3 = copy()

  //These methods beyond this only call super, but don't have dependent type so they are java friendly

  override def add(other: AbstractVector3):          MutableVector3 = super.add(other)
  override def add(other: Double):                   MutableVector3 = super.add(other)
  override def add(x: Double, y: Double, z: Double): MutableVector3 = super.add(x, y, z)

  override def subtract(other: AbstractVector3):          MutableVector3 = super.subtract(other)
  override def subtract(other: Double):                   MutableVector3 = super.subtract(other)
  override def subtract(x: Double, y: Double, z: Double): MutableVector3 = super.subtract(x, y, z)

  override def multiply(other: AbstractVector3):          MutableVector3 = super.multiply(other)
  override def multiply(other: Double):                   MutableVector3 = super.multiply(other)
  override def multiply(x: Double, y: Double, z: Double): MutableVector3 = super.multiply(x, y, z)

  override def divide(other: AbstractVector3):          MutableVector3 = super.divide(other)
  override def divide(other: Double):                   MutableVector3 = super.divide(other)
  override def divide(x: Double, y: Double, z: Double): MutableVector3 = super.divide(x, y, z)

  override def negate:    MutableVector3 = unary_-
  override def normalize: MutableVector3 = super.normalize

  override def cross(other: AbstractVector3):          MutableVector3 = super.cross(other)
  override def cross(x: Double, y: Double, z: Double): MutableVector3 = super.cross(x, y, z)

  override def offset(angle: AbstractVector3, distance: Double): MutableVector3 = super.offset(angle, distance)

  override def rotate(quat: Quat):                               MutableVector3 = super.rotate(quat)
  override def rotate(angle: Double, point: AbstractVector3):    MutableVector3 = super.rotate(angle, point)
  override def rotateRad(angle: Double, point: AbstractVector3): MutableVector3 = super.rotateRad(angle, point)
  override def lerp(target: AbstractVector3, alpha: Double):     MutableVector3 = super.lerp(target, alpha)
  override def slerp(target: AbstractVector3, alpha: Double):    MutableVector3 = super.slerp(target, alpha)

  override def transformDirection(mat: AbstractMat4): MutableVector3 = super.transformDirection(mat)
}

final case class Vector3(@BeanProperty x: Double, @BeanProperty y: Double, @BeanProperty z: Double) extends AbstractVector3 {

  override type Self = Vector3

  def this(entity: Entity) {
    this(entity.posX, entity.posY, entity.posZ)
  }

  def this(living: EntityLivingBase) {
    this(living.posX, living.posY + living.getEyeHeight, living.posZ)
  }

  /**
		* In most cases use [[Vector3.WrappedVec3d]] instead. Only use this if you actually need a [[Vector3]]
		*/
  def this(vec: Vec3d) {
    this(vec.xCoord, vec.yCoord, vec.zCoord)
  }

  def create(x: Double, y: Double, z: Double): Vector3 = Vector3(x, y, z)

  override lazy val length:    Double  = super.length
  override lazy val normalize: Vector3 = super.normalize
  override lazy val yawRad:    Double  = super.yawRad
  override lazy val pitchRad:  Double  = super.pitchRad

  override def asMutable: MutableVector3 = MutableVector3(x, y, z)

  override def asImmutable: Vector3 = this

  //These methods beyond this only call super, but don't have dependent type so they are java friendly

  override def add(other: AbstractVector3):          Vector3 = super.add(other)
  override def add(other: Double):                   Vector3 = super.add(other)
  override def add(x: Double, y: Double, z: Double): Vector3 = super.add(x, y, z)

  override def subtract(other: AbstractVector3):          Vector3 = super.subtract(other)
  override def subtract(other: Double):                   Vector3 = super.subtract(other)
  override def subtract(x: Double, y: Double, z: Double): Vector3 = super.subtract(x, y, z)

  override def multiply(other: AbstractVector3):          Vector3 = super.multiply(other)
  override def multiply(other: Double):                   Vector3 = super.multiply(other)
  override def multiply(x: Double, y: Double, z: Double): Vector3 = super.multiply(x, y, z)

  override def divide(other: AbstractVector3):          Vector3 = super.divide(other)
  override def divide(other: Double):                   Vector3 = super.divide(other)
  override def divide(x: Double, y: Double, z: Double): Vector3 = super.divide(x, y, z)

  override def negate: Vector3 = unary_-

  override def cross(other: AbstractVector3):          Vector3 = super.cross(other)
  override def cross(x: Double, y: Double, z: Double): Vector3 = super.cross(x, y, z)

  override def offset(angle: AbstractVector3, distance: Double): Vector3 = super.offset(angle, distance)

  override def rotate(quat: Quat):                               Vector3 = super.rotate(quat)
  override def rotate(angle: Double, point: AbstractVector3):    Vector3 = super.rotate(angle, point)
  override def rotateRad(angle: Double, point: AbstractVector3): Vector3 = super.rotateRad(angle, point)

  override def lerp(target: AbstractVector3, alpha: Double):  Vector3 = super.lerp(target, alpha)
  override def slerp(target: AbstractVector3, alpha: Double): Vector3 = super.slerp(target, alpha)

  override def transformDirection(mat: AbstractMat4): Vector3 = super.transformDirection(mat)
}

object Vector3 {

  private val rand = new Random

  /* ============================== Constants ============================== */

  final val Zero   = Vector3(0D, 0D, 0D)
  final val Center = Vector3(0.5D, 0.5D, 0.5D)
  final val One    = Vector3(1D, 1D, 1D)

  final val GravityZero    = Zero
  final val GravityDefault = Vector3(0D, DanmakuHelper.GRAVITY_DEFAULT, 0D)
  final val RotateDefault  = Vector3(0D, 1D, 0D)

  final val Up    = Vector3(0, 1, 0)
  final val Down  = Vector3(0, -1, 0)
  final val North = Vector3(0, 0, -1)
  final val South = Vector3(0, 0, 1)
  final val West  = Vector3(-1, 0, 0)
  final val East  = Vector3(1, 0, 0)

  final val Forward  = Vector3(0, 0, 1)
  final val Left     = Vector3(-1, 0, 0)
  final val Right    = Vector3(1, 0, 0)
  final val Backward = Vector3(0, 0, -1)

  /* ============================== Position ============================== */

  def fromEntityCenter(entity: Entity): Vector3 = Vector3(entity.posX, (entity.posY - entity.getYOffset) + entity.height / 2, entity.posZ)

  def posRandom(pos: Vector3): Vector3 = pos * randomVector

  /* ============================== Angle ============================== */

  /**
		* Creates an immutable vector from the passed in yaw and pitch.
		*/
  def fromSpherical(yaw: Double, pitch: Double): Vector3 = {
    val clampedPitch = if (pitch > 90F || pitch < -90F) Math.IEEEremainder(pitch, 180F) else pitch
    val clampedYaw   = if (yaw > 180F || yaw < -180F) Math.IEEEremainder(yaw, 360F) else yaw

    fromSphericalRad(Math.toRadians(clampedYaw).toFloat, Math.toRadians(clampedPitch).toFloat)
  }

  /**
		* Creates an immutable vector from the passed in yaw and pitch.
		* Yaw and pitch must be in radians.
		*/
  def fromSphericalRad(yaw: Float, pitch: Float): Vector3 = {
    val sinYaw   = MathHelper.sin(yaw)
    val sinPitch = MathHelper.sin(pitch)

    val cosYaw   = MathHelper.cos(yaw)
    val cosPitch = MathHelper.cos(pitch)

    Vector3(-sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch)
  }

  def angleEntity(entity: Entity): Vector3 = fromSpherical(entity.rotationYaw, entity.rotationPitch)

  private def angleToPosNotNormalized(posA: AbstractVector3, posB: AbstractVector3): posB.Self = posB - posA

  def angleToPos(posA: AbstractVector3, posB: AbstractVector3): AbstractVector3 = angleToPosNotNormalized(posA, posB).normalize

  def angleToEntity(from: AbstractVector3, to: Entity): Vector3 = angleToPosNotNormalized(from, new Vector3(to)).normalize

  def angleToEntity(from: Entity, to: Entity): Vector3 = angleToPosNotNormalized(new Vector3(from), new Vector3(to)).normalize

  def angleToLiving(from: AbstractVector3, to: EntityLivingBase): Vector3 = angleToPosNotNormalized(from, new Vector3(to)).normalize

  def angleToLiving(from: EntityLivingBase, to: EntityLivingBase): Vector3 = angleToPosNotNormalized(new Vector3(from), new Vector3(to)).normalize

  def angleRandom: Vector3 = randomVector

  def angleLimitRandom(angle: Vector3, limitAngle: Float): Vector3 = {
    val rotate = rotateRandom
    angle.rotate(rand.nextFloat * limitAngle - limitAngle / 2.0F, rotate)
  }

  /* ============================== Misc ============================== */
  def rotateRandom: Vector3 = randomVector

  def gravity(gravityY: Double): Vector3 = Vector3(0.0D, gravityY, 0.0D)

  def getVecWithoutY(vec: AbstractVector3): AbstractVector3 = vec.create(vec.x, 0.0D, vec.z).normalize

  def randomVector: Vector3 =
    fromSpherical(rand.nextFloat * 360F, rand.nextFloat * 180F - 90F)

  //Should I keep this as a AnyVal? Almost all uses requires instantiation
  implicit class WrappedVec3d(override val toVec3d: Vec3d) extends AnyVal with AbstractVector3 {

    override type Self = WrappedVec3d
    override def x: Double = toVec3d.xCoord
    override def y: Double = toVec3d.yCoord
    override def z: Double = toVec3d.zCoord

    override def create(x: Double, y: Double, z: Double): WrappedVec3d   = new Vec3d(x, y, z)
    override def asMutable:                               MutableVector3 = MutableVector3(x, y, z)
    override def asImmutable:                             Vector3        = Vector3(x, y, z)

    //These methods beyond this only call super, but don't have dependent type so they are java friendly

    override def add(other: AbstractVector3):          WrappedVec3d = super.add(other)
    override def add(other: Double):                   WrappedVec3d = super.add(other)
    override def add(x: Double, y: Double, z: Double): WrappedVec3d = super.add(x, y, z)

    override def subtract(other: AbstractVector3):          WrappedVec3d = super.subtract(other)
    override def subtract(other: Double):                   WrappedVec3d = super.subtract(other)
    override def subtract(x: Double, y: Double, z: Double): WrappedVec3d = super.subtract(x, y, z)

    override def multiply(other: AbstractVector3):          WrappedVec3d = super.multiply(other)
    override def multiply(other: Double):                   WrappedVec3d = super.multiply(other)
    override def multiply(x: Double, y: Double, z: Double): WrappedVec3d = super.multiply(x, y, z)

    override def divide(other: AbstractVector3):          WrappedVec3d = super.divide(other)
    override def divide(other: Double):                   WrappedVec3d = super.divide(other)
    override def divide(x: Double, y: Double, z: Double): WrappedVec3d = super.divide(x, y, z)

    override def negate:    WrappedVec3d = unary_-
    override def normalize: WrappedVec3d = super.normalize

    override def cross(other: AbstractVector3):          WrappedVec3d = super.cross(other)
    override def cross(x: Double, y: Double, z: Double): WrappedVec3d = super.cross(x, y, z)

    override def offset(angle: AbstractVector3, distance: Double): WrappedVec3d = super.offset(angle, distance)

    override def rotate(quat: Quat):                               WrappedVec3d = super.rotate(quat)
    override def rotate(angle: Double, point: AbstractVector3):    WrappedVec3d = super.rotate(angle, point)
    override def rotateRad(angle: Double, point: AbstractVector3): WrappedVec3d = super.rotateRad(angle, point)
  }

  import scala.language.implicitConversions

  implicit def toVec3d(vector3: AbstractVector3): Vec3d = vector3.toVec3d

  //We only use java classes for return and entry to make it easier to call from java
  //From Psi
  def getEntityLookedAt(sourceEntity: Entity, filter: Predicate[Entity] = _ => true, distanceReach: Double = 32D): Optional[Entity] = {
    val posSource = sourceEntity match {
      case living: EntityLivingBase => new Vector3(living)
      case _                        => new Vector3(sourceEntity)
    }
    val posSourceVec3d = posSource.toVec3d

    val angle    = sourceEntity.getLookVec
    val posReach = posSource.offset(angle, distanceReach).toVec3d
    val rayTrace = sourceEntity.world.rayTraceBlocks(posSourceVec3d, posReach, false, false, true)

    val distance = if (rayTrace != null) rayTrace.hitVec.distanceTo(posSourceVec3d) else distanceReach

    val foundEntities: Seq[Entity] = sourceEntity.world
      .getEntitiesInAABBexcluding(
        sourceEntity,
        sourceEntity.getEntityBoundingBox.addCoord(angle.x * distanceReach, angle.y * distanceReach, angle.z * distanceReach).expandXyz(1F),
        (entity => filter.test(entity)): GPredicate[Entity]
      )
      .asScala

    val (foundEntity, distanceTo) = foundEntities.foldRight((None: Option[Entity], 0D)) {
      case (potentialEntity, prev @ (_, minDistance)) if potentialEntity.canBeCollidedWith && !potentialEntity.noClip =>
        val hitbox            = potentialEntity.getEntityBoundingBox.expandXyz(potentialEntity.getCollisionBorderSize)
        val interceptPosition = hitbox.calculateIntercept(posSourceVec3d, posReach)

        if (hitbox.isVecInside(posSourceVec3d)) {
          if (minDistance >= 0.0D) (Some(potentialEntity), 0D) else prev
        } else if (interceptPosition != null) {
          val distanceToEntity = posSourceVec3d.distanceTo(interceptPosition.hitVec)
          if (distanceToEntity < minDistance || minDistance == 0.0D) (Some(potentialEntity), distanceToEntity) else prev
        } else prev
      case (_, prev) => prev
    }

    if (distanceTo < distance || rayTrace == null) Optional.ofNullable(foundEntity.orNull) else Optional.empty()
  }
}
