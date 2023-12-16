package net.katsstuff.danmakucore.danmaku.behaviors

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.math.Vector3
import net.minecraft.resources.ResourceLocation

class ComplexAccelerationBehavior extends Behavior[ComplexAccelerationBehavior.Data] {
  override def extraColumns: Seq[ResourceLocation] = Seq(
    DanmakuCore.resource("speed-accel"),
    DanmakuCore.resource("motion-x"),
    DanmakuCore.resource("motion-y"),
    DanmakuCore.resource("motion-z"),
    DanmakuCore.resource("forward-x"),
    DanmakuCore.resource("forward-y"),
    DanmakuCore.resource("forward-z")
  )

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Nil

  override def noOpData: ComplexAccelerationBehavior.Data = ComplexAccelerationBehavior.Data(0, Vector3.Forward)

  override def transferData(
      objData: ComplexAccelerationBehavior.Data,
      extraData: Array[Array[Float]],
      index: Int
  ): Unit = {
    extraData(0)(index) = objData.speedAccel

    extraData(4)(index) = objData.forward.x.toFloat
    extraData(5)(index) = objData.forward.y.toFloat
    extraData(6)(index) = objData.forward.z.toFloat
  }

  override def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit = {
    val speedAccel = extraData(0)

    var axis: Int = 0
    while (axis < 3) {
      val mot = extraData(1 + axis)
      val dir = extraData(4 + axis)

      var i: Int = 0
      while (i < size) {
        mot(i) = mot(i) + dir(i) * speedAccel(i)
        i += 1
      }

      axis += 1
    }
  }
}
object ComplexAccelerationBehavior {
  case class Data(speedAccel: Float, forward: Vector3)
}
