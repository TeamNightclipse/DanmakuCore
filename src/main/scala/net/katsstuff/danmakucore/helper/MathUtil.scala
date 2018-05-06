/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper

import java.lang.{Double => JDouble, Float => JFloat}

//We use Util instead of helper to avoid collision with minecraft's MathHelper
object MathUtil {
  val Epsilon = 1E-5

  def fuzzyEqual(a: Float, b: Float): Boolean = Math.abs(a - b) <= Epsilon

  def fuzzyEqual(a: Double, b: Double): Boolean = Math.abs(a - b) <= Epsilon

  def fuzzyCompare(a: Float, b: Float): Int   = if (Math.abs(a - b) <= Epsilon) 0 else JFloat.compare(a, b)
  def fuzzyCompare(a: Double, b: Double): Int = if (Math.abs(a - b) <= Epsilon) 0 else JDouble.compare(a, b)

  implicit class RichFloat(val self: Float) extends AnyVal {
    def ==~(that: Float): Boolean = fuzzyEqual(self, that)
    def !=~(that: Float): Boolean = !fuzzyEqual(self, that)
    def >~(that: Float): Boolean  = fuzzyCompare(self, that) > 0
    def <~(that: Float): Boolean  = fuzzyCompare(self, that) < 0
    def >=~(that: Float): Boolean = fuzzyCompare(self, that) >= 0
    def <=~(that: Float): Boolean = fuzzyCompare(self, that) <= 0
  }

  implicit class RichDouble(val self: Double) extends AnyVal {
    def =~(that: Double): Boolean  = fuzzyEqual(self, that)
    def !=~(that: Double): Boolean = !fuzzyEqual(self, that)
    def >~(that: Double): Boolean  = fuzzyCompare(self, that) > 0
    def <~(that: Double): Boolean  = fuzzyCompare(self, that) < 0
    def >=~(that: Double): Boolean = fuzzyCompare(self, that) >= 0
    def <=~(that: Double): Boolean = fuzzyCompare(self, that) <= 0
  }
}
