/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.data

import org.junit.runner.RunWith
import org.scalacheck.Gen
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class QuatSuite extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  final val Epsilon = 1E5

  implicit val doubleEquality: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1E-5)
  implicit val floatEquality: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1E-2.toFloat)

  val saneDouble: Gen[Double] = Gen.choose(-Epsilon, Epsilon)
  val angleFloat: Gen[Float]  = Gen.choose(0F, 360F)

  val randPos: Gen[Vector3] = for {
    x <- saneDouble
    y <- saneDouble
    z <- saneDouble
  } yield Vector3(x, y, z)

  val randDirection: Gen[Vector3] = for {
    yaw <- angleFloat
    pitch <- angleFloat
  } yield Vector3.fromSpherical(yaw, pitch)

  val randQuat: Gen[Quat] = for {
    axis <- randDirection
    angle <- angleFloat
  } yield Quat.fromAxisAngle(axis, angle)

  val i = Quat(1, 0, 0, 0)
  val j = Quat(0, 1, 0, 0)
  val k = Quat(0, 0, 1, 0)

  val ex = Vector3(1, 0, 0)
  val ey = Vector3(0, 1, 0)
  val ez = Vector3(0, 0, 1)

  test("q* = -1/2 * (q + i * q * i + j * q * j + k * q * k)") {
    forAll (randQuat) { q: Quat =>
      q.conjugate shouldEqual (-1D/2D) * (q + i * q * i + j * q * j + k * q * k)
    }
  }

  test("|a * q| = |a| * |q|") {
    forAll (saneDouble, randQuat) { (a: Double, q: Quat) =>
      (a * q).length shouldEqual math.abs(a) * q.length
    }
  }

  test("|p * q| = |p| * |q|") {
    forAll (randQuat, randQuat) { (p: Quat, q: Quat) =>
      (p * q).length shouldEqual p.length * q.length
    }
  }

  test("q1 + q2") {
    forAll (randQuat, randQuat) { (q1: Quat, q2: Quat) =>
      val sum = q1 + q2

      (q1.x + q2.x) shouldBe sum.x
      (q1.y + q2.y) shouldBe sum.y
      (q1.z + q2.z) shouldBe sum.z
      (q1.w + q2.w) shouldBe sum.w
    }
  }

  test("v rotated = qvq*") {
    forAll (randPos, randQuat) { (v: Vector3, q: Quat) =>
      val pure = q * Quat(v.x, v.y, v.z, 0) * q.conjugate
      q * v shouldEqual Vector3(pure.x, pure.y, pure.z)
    }
  }

  test("A Quat constructed from a yaw and a pitch should rotate by that") {
    forAll (angleFloat, angleFloat) { (yaw: Float, pitch: Float) =>
      val quat = Quat.fromEuler(yaw, pitch, 0F)
      val rotated = quat * Vector3.Forward
      val reference = Vector3.fromSpherical(yaw, pitch)
      rotated.x shouldEqual reference.x +- 0.1
      rotated.y shouldEqual reference.y +- 0.1
      rotated.z shouldEqual reference.z +- 0.1
    }
  }

  test("Look at") {
    forAll (randPos, randPos) { (from: Vector3, to: Vector3) =>
      val direction = Vector3.directionToPos(from, to)
      val rotate = Quat.lookRotation(direction, Vector3.Up)
      val rotated = rotate * Vector3.Forward
      rotated.x shouldEqual direction.x
      rotated.y shouldEqual direction.y
      rotated.z shouldEqual direction.z
    }
  }
}
