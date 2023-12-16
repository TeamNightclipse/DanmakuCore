package net.katsstuff.danmakucore.math

import java.text.NumberFormat
import java.util.Random

import scala.beans.BeanProperty

import net.minecraft.core.{BlockPos, Direction, Vec3i}
import net.minecraft.util.Mth
import net.minecraft.world.entity.{Entity, LivingEntity}
import net.minecraft.world.phys.Vec3

sealed trait AbstractVector3 extends Any { self =>
  //TODO: Check that types look correct from the JVM side, and are usable from Java
  type SelfA
  type Self = SelfA with AbstractVector3 { type SelfA = self.SelfA }

  /** The x component */
  def x: Double

  /** The y component */
  def y: Double

  /** The z component */
  def z: Double

  /** Creates a new vector of this type. For easy of use */
  def create(x: Double, y: Double, z: Double): Self

  /**
    * Create a new vector with this vector and the other vector added together.
    */
  def +(other: AbstractVector3): Self   = this + (other.x, other.y, other.z)
  def add(other: AbstractVector3): Self = this + other

  /** Create a new vector with the passed in value added to this vector */
  def +(other: Double): Self   = this + (other, other, other)
  def add(other: Double): Self = this + other

  /** Create a new vector with the passed in values added to this vector */
  def +(x: Double, y: Double, z: Double): Self   = create(this.x + x, this.y + y, this.z + z)
  def add(x: Double, y: Double, z: Double): Self = this + (x, y, z)

  /**
    * Create a new vector with the passed in vector subtracted from this vector
    */
  def -(other: AbstractVector3): Self        = this - (other.x, other.y, other.z)
  def subtract(other: AbstractVector3): Self = this - other

  /**
    * Create a new vector with the passed in value subtracted from this vector
    */
  def -(other: Double): Self        = this - (other, other, other)
  def subtract(other: Double): Self = this - other

  /**
    * Create a new vector with the passed in values subtracted from this vector
    */
  def -(x: Double, y: Double, z: Double): Self        = create(this.x - x, this.y - y, this.z - z)
  def subtract(x: Double, y: Double, z: Double): Self = this - (x, y, z)

  /**
    * Create a new vector with this vector and the other vector multiplied
    * together.
    */
  def *(other: AbstractVector3): Self        = this * (other.x, other.y, other.z)
  def multiply(other: AbstractVector3): Self = this * other

  /**
    * Create a new vector with the passed in value multiplied with this vector
    */
  def *(other: Double): Self        = this * (other, other, other)
  def multiply(other: Double): Self = this * other

  /**
    * Create a new vector with the passed in values multiplied with this vector
    */
  def *(x: Double, y: Double, z: Double): Self        = create(this.x * x, this.y * y, this.z * z)
  def multiply(x: Double, y: Double, z: Double): Self = this * (x, y, z)

  /** Create a new vector with the passed in vector divided by this vector */
  def /(other: AbstractVector3): Self      = this / (other.x, other.y, other.z)
  def divide(other: AbstractVector3): Self = this / other

  /** Create a new vector with the passed in value divided by this vector */
  def /(other: Double): Self      = /(other, other, other)
  def divide(other: Double): Self = this./(other)

  /** Create a new vector with the passed in values divided by this vector */
  def /(x: Double, y: Double, z: Double): Self      = create(this.x / x, this.y / y, this.z / z)
  def divide(x: Double, y: Double, z: Double): Self = this / (x, y, z)

  def unary_- : Self = create(-x, -y, -z)
  def negate: Self   = unary_-

  /** Create a new vector that is normalized. */
  def normalize: Self = {
    val len = lengthSquared
    if (len == 0D || len == 1D) create(x, y, z) else *(1D / Math.sqrt(len))
  }

  /** Gets the dot product of this vector and the other vector. */
  def dot(other: AbstractVector3): Double = dot(other.x, other.y, other.z)

  /** Gets the dot product of this vector and the passed in values. */
  def dot(x: Double, y: Double, z: Double): Double = this.x * x + this.y * y + this.z * z

  /**
    * Creates a new vector that is the cross product of this vector and the
    * passed in vector.
    */
  def cross(other: AbstractVector3): Self = cross(other.x, other.y, other.z)

  /**
    * Creates a new vector that is the cross product of this vector and the
    * passed in values.
    */
  def cross(x: Double, y: Double, z: Double): Self = {
    val newX = this.y * z - this.z * y
    val newY = this.z * x - this.x * z
    val newZ = this.x * y - this.y * x
    create(newX, newY, newZ)
  }

  /** Gets the length of this vector. */
  def length: Double = Math.sqrt(x * x + y * y + z * z)

  /** Gets the length of this vector squared. */
  def lengthSquared: Double = x * x + y * y + z * z

  /** Gets the distance between this vector and the passed in vector. */
  def distance(other: AbstractVector3): Double = distance(other.x, other.y, other.z)

  /** Gets the distance between this vector and the passed in values. */
  def distance(x: Double, y: Double, z: Double): Double = {
    val xDist = x - this.x
    val yDist = y - this.y
    val zDist = z - this.z
    Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist)
  }

  /** Gets the distance between this vector and the passed in vector squared. */
  def distanceSquared(other: AbstractVector3): Double = distanceSquared(other.x, other.y, other.z)

  /** Gets the distance between this vector and the passed in values squared. */
  def distanceSquared(x: Double, y: Double, z: Double): Double = {
    val d0 = x - this.x
    val d1 = y - this.y
    val d2 = z - this.z
    d0 * d0 + d1 * d1 + d2 * d2
  }

  /** Offsets this vec in an angle th given distance. */
  def offset(direction: AbstractVector3, distance: Double): Self = this + (direction * distance)

  /** Gets the angle between this vector and the passed in vector. */
  def angle(other: AbstractVector3): Double = Math.acos(normalize.dot(other.normalize))

  /** Rotates this vector using the given quat. */
  def rotate(quat: Quat): Self = quat.rotate(this)

  /** Rotate this vector around the given point. */
  def rotate(angle: Double, axis: AbstractVector3): Self = rotate(Quat.fromAxisAngle(axis, angle))

  /** Rotate this vector around the given axis. */
  def rotate(angle: Double, axis: Direction.Axis): Self = rotate(Quat.fromAxisAngle(axis, angle))

  /**
    * Rotate this vector around the given point. The angle must be in radians.
    */
  def rotateRad(angle: Double, axis: AbstractVector3): Self = rotate(Quat.fromAxisAngleRad(axis, angle))

  /** Calculates the yaw of this vector in radians. */
  def yawRad: Double = -Math.atan2(x, z)

  /** Calculates the yaw from this vector. */
  def yaw: Double = Math.toDegrees(yawRad)

  /** Calculates the pitch of this vector in radians. */
  def pitchRad: Double = {
    val f = Math.sqrt(x * x + z * z)
    -Mth.atan2(y, f)
  }

  /** Calculates the pitch from this vector. */
  def pitch: Double = Math.toDegrees(pitchRad)

  def lerp(target: AbstractVector3, alpha: Double): Self =
    create(x + alpha * (target.x - x), y + alpha * (target.y - y), z + alpha * (target.z - z))

  // From libgdx
  def slerp(target: AbstractVector3, alpha: Double): Self = {
    val dotProd = this.dot(target)

    if (Math.abs(dotProd) > 0.9995) lerp(target, alpha).normalize
    else {
      val theta0 = Math.acos(dotProd)
      val theta  = (theta0 * alpha).toFloat

      val st = Mth.sin(theta)
      val tx = target.x - x * dotProd
      val ty = target.y - y * dotProd
      val tz = target.z - z * dotProd
      val l2 = tx * tx + ty * ty + tz * tz
      val dl = st * (if (l2 < 0.0001F) 1F else 1F / Math.sqrt(l2).toFloat)

      val thetaCos = Mth.cos(theta)
      create(this.x * thetaCos + tx * dl, this.y * thetaCos + ty * dl, this.z * thetaCos + tz * dl).normalize
    }
  }

  def transformDirection(mat: AbstractMat4): Self = mat.transformDirection(this)

  def asMutable: MutableVector3

  def asImmutable: Vector3

  def toVec3d: Vec3 = new Vec3(x, y, z)

  def toBlockPos: BlockPos = new BlockPos(x, y, z)

  override def toString: String = {
    val format = NumberFormat.getNumberInstance
    format.setMaximumFractionDigits(6)
    toString(format)
  }

  def toString(format: NumberFormat): String =
    s"Vector3(${format.format(x)}, ${format.format(y)}, ${format.format(z)})"
}

final case class MutableVector3(@BeanProperty var x: Double, @BeanProperty var y: Double, @BeanProperty var z: Double)
    extends AbstractVector3 {

  override type SelfA = MutableVector3

  def this(vec: Vec3) =
    this(vec.x, vec.y, vec.z)

  def this(entity: Entity) =
    this(entity.position)

  def this(living: LivingEntity) =
    this(living.getX, living.getY + living.getEyeHeight, living.getZ)

  override def create(x: Double, y: Double, z: Double): MutableVector3 = MutableVector3(x, y, z)

  /** Sets the components of this vector. */
  def set(x: Double, y: Double, z: Double): this.type = {
    this.x = x
    this.y = y
    this.z = z
    this
  }

  /** Adds the passed in vector to this vector. */
  def +=(other: AbstractVector3): this.type         = +=(other.x, other.y, other.z)
  def addMutable(other: AbstractVector3): this.type = this.+=(other)

  /** Adds the passed in value to this vector. */
  def +=(other: Double): this.type         = +=(other, other, other)
  def addMutable(other: Double): this.type = this.+=(other)

  /** Adds the passed in values to this vector. */
  def +=(x: Double, y: Double, z: Double): this.type = {
    this.x += x
    this.y += y
    this.z += z
    this
  }
  def addMutable(x: Double, y: Double, z: Double): this.type = this.+=(x, y, z)

  /** Subtracts the passed in vector from this vector. */
  def -=(other: AbstractVector3): this.type              = -=(other.x, other.y, other.z)
  def subtractMutable(other: AbstractVector3): this.type = this.-=(other)

  /** Subtracts the passed in value from this vector. */
  def -=(other: Double): this.type              = -=(other, other, other)
  def subtractMutable(other: Double): this.type = this.-=(other)

  /** Subtracts the passed in values from this vector. */
  def -=(x: Double, y: Double, z: Double): this.type = {
    this.x -= x
    this.y -= y
    this.z -= z
    this
  }
  def subtractMutable(x: Double, y: Double, z: Double): this.type = this.-=(x, y, z)

  /** Multiplies this vector with the passed in vector. */
  def *=(other: AbstractVector3): this.type              = *=(other.x, other.y, other.z)
  def multiplyMutable(other: AbstractVector3): this.type = this.*=(other)

  /** Multiplies this vector with the passed in value. */
  def *=(other: Double): this.type              = *=(other, other, other)
  def multiplyMutable(other: Double): this.type = this.*=(other)

  /** Multiplies this vector with the passed in values. */
  def *=(x: Double, y: Double, z: Double): this.type = {
    this.x *= x
    this.y *= y
    this.z *= z
    this
  }
  def multiplyMutable(x: Double, y: Double, z: Double): this.type = this.*=(x, y, z)

  /** Divides this vector with the passed in vector. */
  def /=(other: AbstractVector3): this.type            = /=(other.x, other.y, other.z)
  def divideMutable(other: AbstractVector3): this.type = this./=(other)

  /** Divides this vector with the passed in value. */
  def /=(other: Double): this.type            = /=(other, other, other)
  def divideMutable(other: Double): this.type = this./=(other)

  /** Divides this vector with the passed in values. */
  def /=(x: Double, y: Double, z: Double): this.type = {
    this.x /= x
    this.y /= y
    this.z /= z
    this
  }
  def divideMutable(x: Double, y: Double, z: Double): this.type = this./=(x, y, z)

  /** Normalizes this vector */
  def normalizeMutable: this.type = {
    val len = lengthSquared
    if (len == 0D || len == 1D) this else *=(1D / Math.sqrt(len))
  }

  /**
    * Sets this vector to the cross product of this vector and the passed in
    * vector.
    */
  def crossMutable(other: AbstractVector3): this.type = crossMutable(other.x, other.y, other.z)

  /**
    * Sets this vector to the cross product of this vector and the passed in
    * values.
    */
  def crossMutable(x: Double, y: Double, z: Double): this.type = {
    this.x = this.y * z - this.z * y
    this.y = this.z * x - this.x * z
    this.z = this.x * y - this.y * x
    this
  }

  def rotateMutable(quat: MutableQuat, destroyQuat: Boolean = false): MutableVector3 =
    quat.rotateMutable(this, destroyQuat)
  def rotateMutable(angle: Double, axis: Direction.Axis): MutableVector3 =
    rotateMutable(MutableQuat.fromAxisAngle(axis, angle), destroyQuat = true)
  def rotateMutable(angle: Double, axis: AbstractVector3): MutableVector3 =
    rotateMutable(MutableQuat.fromAxisAngle(axis, angle), destroyQuat = true)

  override def asMutable: MutableVector3 = this

  override def asImmutable: Vector3 = Vector3(x, y, z)

  def copyObj: MutableVector3 = copy()

  // These methods beyond this only call super, but don't have dependent type so they are java friendly

  override def add(other: AbstractVector3): MutableVector3          = super.add(other)
  override def add(other: Double): MutableVector3                   = super.add(other)
  override def add(x: Double, y: Double, z: Double): MutableVector3 = super.add(x, y, z)

  override def subtract(other: AbstractVector3): MutableVector3          = super.subtract(other)
  override def subtract(other: Double): MutableVector3                   = super.subtract(other)
  override def subtract(x: Double, y: Double, z: Double): MutableVector3 = super.subtract(x, y, z)

  override def multiply(other: AbstractVector3): MutableVector3          = super.multiply(other)
  override def multiply(other: Double): MutableVector3                   = super.multiply(other)
  override def multiply(x: Double, y: Double, z: Double): MutableVector3 = super.multiply(x, y, z)

  override def divide(other: AbstractVector3): MutableVector3          = super.divide(other)
  override def divide(other: Double): MutableVector3                   = super.divide(other)
  override def divide(x: Double, y: Double, z: Double): MutableVector3 = super.divide(x, y, z)

  override def negate: MutableVector3    = unary_-
  override def normalize: MutableVector3 = super.normalize

  override def cross(other: AbstractVector3): MutableVector3          = super.cross(other)
  override def cross(x: Double, y: Double, z: Double): MutableVector3 = super.cross(x, y, z)

  override def offset(direction: AbstractVector3, distance: Double): MutableVector3 = super.offset(direction, distance)

  override def rotate(quat: Quat): MutableVector3                              = super.rotate(quat)
  override def rotate(angle: Double, axis: Direction.Axis): MutableVector3     = super.rotate(angle, axis)
  override def rotate(angle: Double, axis: AbstractVector3): MutableVector3    = super.rotate(angle, axis)
  override def rotateRad(angle: Double, axis: AbstractVector3): MutableVector3 = super.rotateRad(angle, axis)
  override def lerp(target: AbstractVector3, alpha: Double): MutableVector3    = super.lerp(target, alpha)
  override def slerp(target: AbstractVector3, alpha: Double): MutableVector3   = super.slerp(target, alpha)

  override def transformDirection(mat: AbstractMat4): MutableVector3 = super.transformDirection(mat)
}

final case class Vector3(@BeanProperty x: Double, @BeanProperty y: Double, @BeanProperty z: Double)
    extends AbstractVector3 {

  override type SelfA = Vector3

  /**
    * In most cases use [[Vector3.WrappedVec3d]] instead. Only use this if you
    * actually need a [[Vector3]]
    */
  def this(vec: Vec3) =
    this(vec.x, vec.y, vec.z)

  def this(entity: Entity) =
    this(entity.position)

  def this(living: LivingEntity) =
    this(living.getX, living.getY + living.getEyeHeight, living.getZ)

  def create(x: Double, y: Double, z: Double): Vector3 = Vector3(x, y, z)

  override lazy val length: Double     = super.length
  override lazy val normalize: Vector3 = super.normalize
  override lazy val yawRad: Double     = super.yawRad
  override lazy val pitchRad: Double   = super.pitchRad

  override def asMutable: MutableVector3 = MutableVector3(x, y, z)

  override def asImmutable: Vector3 = this

  // These methods beyond this only call super, but don't have dependent type so they are java friendly

  override def add(other: AbstractVector3): Vector3          = super.add(other)
  override def add(other: Double): Vector3                   = super.add(other)
  override def add(x: Double, y: Double, z: Double): Vector3 = super.add(x, y, z)

  override def subtract(other: AbstractVector3): Vector3          = super.subtract(other)
  override def subtract(other: Double): Vector3                   = super.subtract(other)
  override def subtract(x: Double, y: Double, z: Double): Vector3 = super.subtract(x, y, z)

  override def multiply(other: AbstractVector3): Vector3          = super.multiply(other)
  override def multiply(other: Double): Vector3                   = super.multiply(other)
  override def multiply(x: Double, y: Double, z: Double): Vector3 = super.multiply(x, y, z)

  override def divide(other: AbstractVector3): Vector3          = super.divide(other)
  override def divide(other: Double): Vector3                   = super.divide(other)
  override def divide(x: Double, y: Double, z: Double): Vector3 = super.divide(x, y, z)

  override def negate: Vector3 = unary_-

  override def cross(other: AbstractVector3): Vector3          = super.cross(other)
  override def cross(x: Double, y: Double, z: Double): Vector3 = super.cross(x, y, z)

  override def offset(direction: AbstractVector3, distance: Double): Vector3 = super.offset(direction, distance)

  override def rotate(quat: Quat): Vector3                              = super.rotate(quat)
  override def rotate(angle: Double, axis: Direction.Axis): Vector3     = super.rotate(angle, axis)
  override def rotate(angle: Double, axis: AbstractVector3): Vector3    = super.rotate(angle, axis)
  override def rotateRad(angle: Double, axis: AbstractVector3): Vector3 = super.rotateRad(angle, axis)

  override def lerp(target: AbstractVector3, alpha: Double): Vector3  = super.lerp(target, alpha)
  override def slerp(target: AbstractVector3, alpha: Double): Vector3 = super.slerp(target, alpha)

  override def transformDirection(mat: AbstractMat4): Vector3 = super.transformDirection(mat)
}

object Vector3 {

  private val rand = new Random

  /* ============================== Constants ============================== */

  final val Zero   = Vector3(0D, 0D, 0D)
  final val Center = Vector3(0.5D, 0.5D, 0.5D)
  final val One    = Vector3(1D, 1D, 1D)

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

  //TODO
  //def fromEntityCenter(entity: Entity): Vector3 =
  //  Vector3(entity.getX, (entity.getY - entity.getYOffset) + entity.getType.getDimensions.height / 2, entity.getZ)

  def posRandom(pos: Vector3): Vector3 = pos * randomVector

  /* ============================== Angle ============================== */

  /** Creates an immutable vector from the passed in yaw and pitch. */
  def fromSpherical(yaw: Double, pitch: Double): Vector3 = {
    val clampedPitch = if (pitch > 90F || pitch < -90F) Math.IEEEremainder(pitch, 180F) else pitch
    val clampedYaw   = if (yaw > 180F || yaw < -180F) Math.IEEEremainder(yaw, 360F) else yaw

    fromSphericalRad(Math.toRadians(clampedYaw).toFloat, Math.toRadians(clampedPitch).toFloat)
  }

  /**
    * Creates an immutable vector from the passed in yaw and pitch. Yaw and
    * pitch must be in radians.
    */
  def fromSphericalRad(yaw: Float, pitch: Float): Vector3 = {
    val sinYaw   = Mth.sin(yaw)
    val sinPitch = Mth.sin(pitch)

    val cosYaw   = Mth.cos(yaw)
    val cosPitch = Mth.cos(pitch)

    Vector3(-sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch)
  }

  def directionEntity(entity: Entity): Vector3 = {
    val rot = entity.getRotationVector
    fromSpherical(rot.y, rot.x)
  }

  private def directionToPosNotNormalized(posA: AbstractVector3, posB: AbstractVector3): posB.Self = posB - posA

  def directionToPos(posA: AbstractVector3, posB: AbstractVector3): AbstractVector3 =
    directionToPosNotNormalized(posA, posB).normalize

  def directionToEntity(from: AbstractVector3, to: Entity): Vector3 =
    directionToPosNotNormalized(from, new Vector3(to)).normalize

  def directionToEntity(from: Entity, to: Entity): Vector3 =
    directionToPosNotNormalized(new Vector3(from), new Vector3(to)).normalize

  def directionToLiving(from: AbstractVector3, to: LivingEntity): Vector3 =
    directionToPosNotNormalized(from, new Vector3(to)).normalize

  def directionToLiving(from: LivingEntity, to: LivingEntity): Vector3 =
    directionToPosNotNormalized(new Vector3(from), new Vector3(to)).normalize

  def randomDirection: Vector3 = randomVector

  def limitRandomDirection(direction: Vector3, limitAngle: Float): Vector3 = {
    val rotate = rotateRandom
    direction.rotate(rand.nextFloat * limitAngle - limitAngle / 2.0F, rotate)
  }

  /* ============================== Misc ============================== */
  def rotateRandom: Vector3 = randomVector

  def gravity(gravityY: Double): Vector3 = Vector3(0.0D, gravityY, 0.0D)

  def getVecWithoutY(vec: AbstractVector3): AbstractVector3 = vec.create(vec.x, 0.0D, vec.z).normalize

  def randomVector: Vector3 =
    fromSpherical(rand.nextFloat * 360F, rand.nextFloat * 180F - 90F)

  // Should I keep this as a AnyVal? Almost all uses requires instantiation
  implicit class WrappedVec3d(override val toVec3d: Vec3) extends AnyVal with AbstractVector3 {

    override type SelfA = WrappedVec3d
    override def x: Double = toVec3d.x
    override def y: Double = toVec3d.y
    override def z: Double = toVec3d.z

    override def create(x: Double, y: Double, z: Double): WrappedVec3d = new Vec3(x, y, z)
    override def asMutable: MutableVector3                             = MutableVector3(x, y, z)
    override def asImmutable: Vector3                                  = Vector3(x, y, z)

    // These methods beyond this only call super, but don't have dependent type so they are java friendly

    override def add(other: AbstractVector3): WrappedVec3d          = super.add(other)
    override def add(other: Double): WrappedVec3d                   = super.add(other)
    override def add(x: Double, y: Double, z: Double): WrappedVec3d = super.add(x, y, z)

    override def subtract(other: AbstractVector3): WrappedVec3d          = super.subtract(other)
    override def subtract(other: Double): WrappedVec3d                   = super.subtract(other)
    override def subtract(x: Double, y: Double, z: Double): WrappedVec3d = super.subtract(x, y, z)

    override def multiply(other: AbstractVector3): WrappedVec3d          = super.multiply(other)
    override def multiply(other: Double): WrappedVec3d                   = super.multiply(other)
    override def multiply(x: Double, y: Double, z: Double): WrappedVec3d = super.multiply(x, y, z)

    override def divide(other: AbstractVector3): WrappedVec3d          = super.divide(other)
    override def divide(other: Double): WrappedVec3d                   = super.divide(other)
    override def divide(x: Double, y: Double, z: Double): WrappedVec3d = super.divide(x, y, z)

    override def negate: WrappedVec3d    = unary_-
    override def normalize: WrappedVec3d = super.normalize

    override def cross(other: AbstractVector3): WrappedVec3d          = super.cross(other)
    override def cross(x: Double, y: Double, z: Double): WrappedVec3d = super.cross(x, y, z)

    override def offset(direction: AbstractVector3, distance: Double): WrappedVec3d = super.offset(direction, distance)

    override def rotate(quat: Quat): WrappedVec3d                              = super.rotate(quat)
    override def rotate(angle: Double, axis: Direction.Axis): WrappedVec3d     = super.rotate(angle, axis)
    override def rotate(angle: Double, axis: AbstractVector3): WrappedVec3d    = super.rotate(angle, axis)
    override def rotateRad(angle: Double, axis: AbstractVector3): WrappedVec3d = super.rotateRad(angle, axis)
  }

  // Should I keep this as a AnyVal? Almost all uses requires instantiation
  implicit class WrappedVec3i(val toVec3i: Vec3i) extends AnyVal with AbstractVector3 {

    override type SelfA = WrappedVec3i
    override def x: Double = toVec3i.getX
    override def y: Double = toVec3i.getY
    override def z: Double = toVec3i.getZ

    override def create(x: Double, y: Double, z: Double): WrappedVec3i = new Vec3i(x, y, z)
    override def asMutable: MutableVector3                             = MutableVector3(x, y, z)
    override def asImmutable: Vector3                                  = Vector3(x, y, z)

    // These methods beyond this only call super, but don't have dependent type so they are java friendly

    override def add(other: AbstractVector3): WrappedVec3i          = super.add(other)
    override def add(other: Double): WrappedVec3i                   = super.add(other)
    override def add(x: Double, y: Double, z: Double): WrappedVec3i = super.add(x, y, z)

    override def subtract(other: AbstractVector3): WrappedVec3i          = super.subtract(other)
    override def subtract(other: Double): WrappedVec3i                   = super.subtract(other)
    override def subtract(x: Double, y: Double, z: Double): WrappedVec3i = super.subtract(x, y, z)

    override def multiply(other: AbstractVector3): WrappedVec3i          = super.multiply(other)
    override def multiply(other: Double): WrappedVec3i                   = super.multiply(other)
    override def multiply(x: Double, y: Double, z: Double): WrappedVec3i = super.multiply(x, y, z)

    override def divide(other: AbstractVector3): WrappedVec3i          = super.divide(other)
    override def divide(other: Double): WrappedVec3i                   = super.divide(other)
    override def divide(x: Double, y: Double, z: Double): WrappedVec3i = super.divide(x, y, z)

    override def negate: WrappedVec3i    = unary_-
    override def normalize: WrappedVec3i = super.normalize

    override def cross(other: AbstractVector3): WrappedVec3i          = super.cross(other)
    override def cross(x: Double, y: Double, z: Double): WrappedVec3i = super.cross(x, y, z)

    override def offset(direction: AbstractVector3, distance: Double): WrappedVec3i = super.offset(direction, distance)

    override def rotate(quat: Quat): WrappedVec3i                              = super.rotate(quat)
    override def rotate(angle: Double, axis: Direction.Axis): WrappedVec3i     = super.rotate(angle, axis)
    override def rotate(angle: Double, axis: AbstractVector3): WrappedVec3i    = super.rotate(angle, axis)
    override def rotateRad(angle: Double, axis: AbstractVector3): WrappedVec3i = super.rotateRad(angle, axis)
  }

  implicit class DoubleOps(private val double: Double) extends AnyVal {
    def +(vec: AbstractVector3): vec.Self = vec + double
    def *(vec: AbstractVector3): vec.Self = vec * double
  }

  import scala.language.implicitConversions

  implicit def toVec3d(vector3: AbstractVector3): Vec3 = vector3.toVec3d
}
