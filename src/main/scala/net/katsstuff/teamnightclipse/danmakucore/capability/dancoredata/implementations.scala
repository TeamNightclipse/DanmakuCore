/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata

case class BoundlessDanmakuCoreData(var power: Float, var score: Int, var lives: Int, var bombs: Int)
    extends IDanmakuCoreData {

  def this() {
    this(0F, 0, 0, 0)
  }
}

case class BoundedDanmakuCoreData(
    var power: Float,
    var score: Int,
    var lives: Int,
    var bombs: Int,
    powerBound: Float,
    lifeBombBound: Int
) extends IDanmakuCoreData {

  def this(powerBound: Float, lifeBombBound: Int) {
    this(0F, 0, 0, 0, powerBound, lifeBombBound)
  }

  def this() {
    this(4F, 9)
  }
}
