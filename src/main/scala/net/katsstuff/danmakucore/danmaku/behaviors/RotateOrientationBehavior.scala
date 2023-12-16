package net.katsstuff.danmakucore.danmaku.behaviors

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.math.Quat
import net.minecraft.resources.ResourceLocation

class RotateOrientationBehavior extends Behavior[RotateOrientationBehavior.Data] {
  override def extraColumns: Seq[ResourceLocation] = Seq(
    DanmakuCore.resource("rotation-x"),
    DanmakuCore.resource("rotation-y"),
    DanmakuCore.resource("rotation-z"),
    DanmakuCore.resource("rotation-w")
  )

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Seq(
    MainColumns.RequiredColumns.Orientation
  )

  override def noOpData: RotateOrientationBehavior.Data = RotateOrientationBehavior.Data(Quat.Identity)

  override def transferData(
      objData: RotateOrientationBehavior.Data,
      extraData: Array[Array[Float]],
      index: Int
  ): Unit = {
    extraData(0)(index) = objData.rotationQuat.x.toFloat
    extraData(1)(index) = objData.rotationQuat.y.toFloat
    extraData(2)(index) = objData.rotationQuat.z.toFloat
    extraData(3)(index) = objData.rotationQuat.w.toFloat
  }

  override def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit = {
    val rotX = extraData(0)
    val rotY = extraData(1)
    val rotZ = extraData(2)
    val rotW = extraData(3)

    // Too much data here. We'll just do normal ops
    var i: Int = 0
    while (i < size) {
      val orientation = mainColumns.orientation(i)
      val x = orientation.x
      val y = orientation.y
      val z = orientation.z
      val w = orientation.w

      orientation *= Quat(rotX(i), rotY(i), rotZ(i), rotW(i))
      mainColumns.oldOrientation(i).set(x, y, z, w)
      i += 1
    }
  }
}
object RotateOrientationBehavior {
  case class Data(rotationQuat: Quat)
}
