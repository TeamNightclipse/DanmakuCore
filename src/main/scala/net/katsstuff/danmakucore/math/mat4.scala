package net.katsstuff.danmakucore.math

import com.mojang.math.Matrix4f

import java.nio.FloatBuffer

sealed trait AbstractMat4 { self =>
  type Self <: AbstractMat4 { type Self = self.Self }

  def m00: Double
  def m01: Double
  def m02: Double
  def m03: Double

  def m10: Double
  def m11: Double
  def m12: Double
  def m13: Double

  def m20: Double
  def m21: Double
  def m22: Double
  def m23: Double

  def m30: Double
  def m31: Double
  def m32: Double
  def m33: Double

  // format: OFF
  def create(
    m00: Double, m01: Double, m02: Double, m03: Double,
    m10: Double, m11: Double, m12: Double, m13: Double,
    m20: Double, m21: Double, m22: Double, m23: Double,
    m30: Double, m31: Double, m32: Double, m33: Double
  ): Self
  // format: ON

  // format: OFF
  def transpose: Self = create(
    m00 = m00, m01 = m10, m02 = m20, m03 = m30,
    m10 = m01, m11 = m11, m12 = m21, m13 = m31,
    m20 = m02, m21 = m12, m22 = m22, m23 = m32,
    m30 = m03, m31 = m13, m32 = m23, m33 = m33
  )

  def *(scalar: Double): Self = create(
    m00 = m00 * scalar, m01 = m01 * scalar, m02 = m02 * scalar, m03 = m03 * scalar,
    m10 = m10 * scalar, m11 = m11 * scalar, m12 = m12 * scalar, m13 = m13 * scalar,
    m20 = m20 * scalar, m21 = m21 * scalar, m22 = m22 * scalar, m23 = m23 * scalar,
    m30 = m30 * scalar, m31 = m31 * scalar, m32 = m32 * scalar, m33 = m33 * scalar
  )

  //noinspection DuplicatedCode
  def *(that: AbstractMat4): Self = {
    val lm00 = this.m00
    val lm01 = this.m01
    val lm02 = this.m02
    val lm03 = this.m03
    val lm10 = this.m10
    val lm11 = this.m11
    val lm12 = this.m12
    val lm13 = this.m13
    val lm20 = this.m20
    val lm21 = this.m21
    val lm22 = this.m22
    val lm23 = this.m23
    val lm30 = this.m30
    val lm31 = this.m31
    val lm32 = this.m32
    val lm33 = this.m33

    val rm00 = that.m00
    val rm01 = that.m01
    val rm02 = that.m02
    val rm03 = that.m03
    val rm10 = that.m10
    val rm11 = that.m11
    val rm12 = that.m12
    val rm13 = that.m13
    val rm20 = that.m20
    val rm21 = that.m21
    val rm22 = that.m22
    val rm23 = that.m23
    val rm30 = that.m30
    val rm31 = that.m31
    val rm32 = that.m32
    val rm33 = that.m33

    val m00 = lm00 * rm00 + lm01 * rm10 + lm02 * rm20 + lm03 * rm30
    val m01 = lm00 * rm01 + lm01 * rm11 + lm02 * rm21 + lm03 * rm31
    val m02 = lm00 * rm02 + lm01 * rm12 + lm02 * rm22 + lm03 * rm32
    val m03 = lm00 * rm03 + lm01 * rm13 + lm02 * rm23 + lm03 * rm33
    val m10 = lm10 * rm00 + lm11 * rm10 + lm12 * rm20 + lm13 * rm30
    val m11 = lm10 * rm01 + lm11 * rm11 + lm12 * rm21 + lm13 * rm31
    val m12 = lm10 * rm02 + lm11 * rm12 + lm12 * rm22 + lm13 * rm32
    val m13 = lm10 * rm03 + lm11 * rm13 + lm12 * rm23 + lm13 * rm33
    val m20 = lm20 * rm00 + lm21 * rm10 + lm22 * rm20 + lm23 * rm30
    val m21 = lm20 * rm01 + lm21 * rm11 + lm22 * rm21 + lm23 * rm31
    val m22 = lm20 * rm02 + lm21 * rm12 + lm22 * rm22 + lm23 * rm32
    val m23 = lm20 * rm03 + lm21 * rm13 + lm22 * rm23 + lm23 * rm33
    val m30 = lm30 * rm00 + lm31 * rm10 + lm32 * rm20 + lm33 * rm30
    val m31 = lm30 * rm01 + lm31 * rm11 + lm32 * rm21 + lm33 * rm31
    val m32 = lm30 * rm02 + lm31 * rm12 + lm32 * rm22 + lm33 * rm32
    val m33 = lm30 * rm03 + lm31 * rm13 + lm32 * rm23 + lm33 * rm33
    
    create(
      m00 = m00, m01 = m01, m02 = m02, m03 = m03,
      m10 = m10, m11 = m11, m12 = m12, m13 = m13,
      m20 = m20, m21 = m21, m22 = m22, m23 = m23,
      m30 = m30, m31 = m31, m32 = m32, m33 = m33
    )
  }
  // format: ON

  def multiplyMutableDest(that: AbstractMat4, dest: MutableMat4): Unit = {
    val lm00 = this.m00
    val lm01 = this.m01
    val lm02 = this.m02
    val lm03 = this.m03
    val lm10 = this.m10
    val lm11 = this.m11
    val lm12 = this.m12
    val lm13 = this.m13
    val lm20 = this.m20
    val lm21 = this.m21
    val lm22 = this.m22
    val lm23 = this.m23
    val lm30 = this.m30
    val lm31 = this.m31
    val lm32 = this.m32
    val lm33 = this.m33

    val rm00 = that.m00
    val rm01 = that.m01
    val rm02 = that.m02
    val rm03 = that.m03
    val rm10 = that.m10
    val rm11 = that.m11
    val rm12 = that.m12
    val rm13 = that.m13
    val rm20 = that.m20
    val rm21 = that.m21
    val rm22 = that.m22
    val rm23 = that.m23
    val rm30 = that.m30
    val rm31 = that.m31
    val rm32 = that.m32
    val rm33 = that.m33

    dest.m00 = lm00 * rm00 + lm01 * rm10 + lm02 * rm20 + lm03 * rm30
    dest.m01 = lm00 * rm01 + lm01 * rm11 + lm02 * rm21 + lm03 * rm31
    dest.m02 = lm00 * rm02 + lm01 * rm12 + lm02 * rm22 + lm03 * rm32
    dest.m03 = lm00 * rm03 + lm01 * rm13 + lm02 * rm23 + lm03 * rm33
    dest.m10 = lm10 * rm00 + lm11 * rm10 + lm12 * rm20 + lm13 * rm30
    dest.m11 = lm10 * rm01 + lm11 * rm11 + lm12 * rm21 + lm13 * rm31
    dest.m12 = lm10 * rm02 + lm11 * rm12 + lm12 * rm22 + lm13 * rm32
    dest.m13 = lm10 * rm03 + lm11 * rm13 + lm12 * rm23 + lm13 * rm33
    dest.m20 = lm20 * rm00 + lm21 * rm10 + lm22 * rm20 + lm23 * rm30
    dest.m21 = lm20 * rm01 + lm21 * rm11 + lm22 * rm21 + lm23 * rm31
    dest.m22 = lm20 * rm02 + lm21 * rm12 + lm22 * rm22 + lm23 * rm32
    dest.m23 = lm20 * rm03 + lm21 * rm13 + lm22 * rm23 + lm23 * rm33
    dest.m30 = lm30 * rm00 + lm31 * rm10 + lm32 * rm20 + lm33 * rm30
    dest.m31 = lm30 * rm01 + lm31 * rm11 + lm32 * rm21 + lm33 * rm31
    dest.m32 = lm30 * rm02 + lm31 * rm12 + lm32 * rm22 + lm33 * rm32
    dest.m33 = lm30 * rm03 + lm31 * rm13 + lm32 * rm23 + lm33 * rm33
  }

  def =*(that: MutableMat4): Unit = multiplyMutableDest(that, that)

  def transformMutablePositionVector(that: MutableVector3): Unit = {
    that.x = m00 * that.x + m01 * that.y + m02 * that.z + m03
    that.y = m10 * that.x + m11 * that.y + m12 * that.z + m13
    that.z = m20 * that.x + m21 * that.y + m22 * that.z + m23
  }

  def multiplyScalar(scalar: Double): Self = this * scalar

  def transformDirection(vec: AbstractVector3): vec.Self = {
    val x = vec.x
    val y = vec.y
    val z = vec.z

    vec.create(
      m00 * x + m10 * y + m20 * z,
      m01 * x + m11 * y + m21 * z,
      m02 * x + m12 * y + m22 * z
    )
  }

  // noinspection DuplicatedCode
  def addToBuffer(buffer: FloatBuffer): Unit = {
    buffer.put(m00.toFloat)
    buffer.put(m01.toFloat)
    buffer.put(m02.toFloat)
    buffer.put(m03.toFloat)
    buffer.put(m10.toFloat)
    buffer.put(m11.toFloat)
    buffer.put(m12.toFloat)
    buffer.put(m13.toFloat)
    buffer.put(m20.toFloat)
    buffer.put(m21.toFloat)
    buffer.put(m22.toFloat)
    buffer.put(m23.toFloat)
    buffer.put(m30.toFloat)
    buffer.put(m31.toFloat)
    buffer.put(m32.toFloat)
    buffer.put(m33.toFloat)
  }

  def asMutable: MutableMat4

  def asImmutable: Mat4

  protected def dataToString: String =
    s"""|$m00 $m01 $m02 $m03
        |$m10 $m11 $m12 $m13
        |$m20 $m21 $m22 $m23
        |$m30 $m31 $m32 $m33
        |""".stripMargin
}
final case class MutableMat4(
    var m00: Double,
    var m01: Double,
    var m02: Double,
    var m03: Double,
    var m10: Double,
    var m11: Double,
    var m12: Double,
    var m13: Double,
    var m20: Double,
    var m21: Double,
    var m22: Double,
    var m23: Double,
    var m30: Double,
    var m31: Double,
    var m32: Double,
    var m33: Double
) extends AbstractMat4 {

  override type Self = MutableMat4

  // format: OFF
  override def create(
    m00: Double, m01: Double, m02: Double, m03: Double,
    m10: Double, m11: Double, m12: Double, m13: Double,
    m20: Double, m21: Double, m22: Double, m23: Double,
    m30: Double, m31: Double, m32: Double, m33: Double
  ): Self = MutableMat4(
    m00, m01, m02, m03,
    m10, m11, m12, m13,
    m20, m21, m22, m23,
    m30, m31, m32, m33
  )
  
  def set(
    m00: Double, m01: Double, m02: Double, m03: Double,
    m10: Double, m11: Double, m12: Double, m13: Double,
    m20: Double, m21: Double, m22: Double, m23: Double,
    m30: Double, m31: Double, m32: Double, m33: Double
  ): this.type = {
    this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03
    this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13
    this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23
    this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33
    
    this
  }

  //noinspection DuplicatedCode
  def setIdentity(): this.type = set(
    1, 0, 0, 0,
    0, 1, 0, 0,
    0, 0, 1, 0,
    0, 0, 0, 1
  )
  // format: ON

  def *=(scalar: Double): Unit = {
    m00 *= scalar
    m01 *= scalar
    m02 *= scalar
    m03 *= scalar
    m10 *= scalar
    m11 *= scalar
    m12 *= scalar
    m13 *= scalar
    m20 *= scalar
    m21 *= scalar
    m22 *= scalar
    m23 *= scalar
    m30 *= scalar
    m31 *= scalar
    m32 *= scalar
    m33 *= scalar
  }

  def *=(that: AbstractMat4): Unit = multiplyMutableDest(that, this)

  def multiplyScalarMutable(scalar: Double): Unit = this *= scalar

  override def transpose: MutableMat4                      = super.transpose
  override def multiplyScalar(scalar: Double): MutableMat4 = super.multiplyScalar(scalar)
  def copyObj: MutableMat4                                 = copy()

  override def asMutable: MutableMat4 = this

  override def asImmutable: Mat4 = Mat4(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33)

  override def toString: String = s"MutableMat4:\n$dataToString"
}

// format: OFF
final case class Mat4(
  m00: Double, m01: Double, m02: Double, m03: Double,
  m10: Double, m11: Double, m12: Double, m13: Double,
  m20: Double, m21: Double, m22: Double, m23: Double,
  m30: Double, m31: Double, m32: Double, m33: Double
) extends AbstractMat4 {
  // format: ON
  override type Self = Mat4

  override def create(
      m00: Double,
      m01: Double,
      m02: Double,
      m03: Double,
      m10: Double,
      m11: Double,
      m12: Double,
      m13: Double,
      m20: Double,
      m21: Double,
      m22: Double,
      m23: Double,
      m30: Double,
      m31: Double,
      m32: Double,
      m33: Double
  ): Self = Mat4(
    m00,
    m01,
    m02,
    m03,
    m10,
    m11,
    m12,
    m13,
    m20,
    m21,
    m22,
    m23,
    m30,
    m31,
    m32,
    m33
  )

  override def transpose: Mat4                      = super.transpose
  override def multiplyScalar(scalar: Double): Mat4 = super.multiplyScalar(scalar)

  override def asMutable: MutableMat4 =
    MutableMat4(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33)

  override def asImmutable: Mat4 = this

  override def toString: String = s"Mat4:\n$dataToString"
}
object Mat4 {

  // format: OFF
  val Identity: Mat4 = Mat4(
    1, 0, 0, 0,
    0, 1, 0, 0,
    0, 0, 1, 0,
    0, 0, 0, 1
  )
  // format: ON

  // From libgdx https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Matrix4.java#L876
  def fromWorld(pos: AbstractVector3, forward: AbstractVector3, up: AbstractVector3): Mat4 = {
    val forwardNormalized = forward.normalize
    val right             = forwardNormalized.cross(up).normalize
    val newUp             = right.cross(forwardNormalized).normalize

    fromAxes(right, newUp, forwardNormalized * -1, pos)
  }

  def fromAxes(xAxis: AbstractVector3, yAxis: AbstractVector3, zAxis: AbstractVector3, pos: AbstractVector3): Mat4 =
  // format: OFF
    Mat4(
      xAxis.x, xAxis.y, xAxis.z, pos.x,
      yAxis.x, yAxis.y, yAxis.z, pos.y,
      zAxis.x, zAxis.y, zAxis.z, pos.z,
      0,       0,       0,       1
    )
  // format: ON
}
