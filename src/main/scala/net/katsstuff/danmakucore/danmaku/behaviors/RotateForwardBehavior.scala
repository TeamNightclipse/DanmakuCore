package net.katsstuff.danmakucore.danmaku.behaviors

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.math.{Quat, Vector3}
import net.minecraft.resources.ResourceLocation

class RotateForwardBehavior extends Behavior[RotateOrientationBehavior.Data] {
  override def extraColumns: Seq[ResourceLocation] = Seq(
    DanmakuCore.resource("rotation-x"),
    DanmakuCore.resource("rotation-y"),
    DanmakuCore.resource("rotation-z"),
    DanmakuCore.resource("rotation-w"),
    DanmakuCore.resource("forward-x"),
    DanmakuCore.resource("forward-y"),
    DanmakuCore.resource("forward-z")
  )

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Nil

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

    val forwardX = extraData(4)
    val forwardY = extraData(5)
    val forwardZ = extraData(6)

    // Too much data here. We'll just do normal ops
    var i: Int = 0
    while (i < size) {
      val forward = Vector3(forwardX(i), forwardY(i), forwardZ(i))
      val rot     = Quat(rotX(i), rotY(i), rotZ(i), rotW(i))

      val res = forward.rotate(rot)

      forwardX(i) = res.x.toFloat
      forwardY(i) = res.y.toFloat
      forwardZ(i) = res.z.toFloat

      i += 1
    }
  }
}
