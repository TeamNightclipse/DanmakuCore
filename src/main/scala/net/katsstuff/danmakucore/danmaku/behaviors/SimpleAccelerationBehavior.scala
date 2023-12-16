package net.katsstuff.danmakucore.danmaku.behaviors

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.resources.ResourceLocation

class SimpleAccelerationBehavior extends Behavior[SimpleAccelerationBehavior.Data] {
  override def extraColumns: Seq[ResourceLocation] = Seq(
    DanmakuCore.resource("speed-accel"),
    DanmakuCore.resource("motion-z")
  )

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Nil

  override def noOpData: SimpleAccelerationBehavior.Data = SimpleAccelerationBehavior.Data(0)

  override def transferData(
      objData: SimpleAccelerationBehavior.Data,
      extraData: Array[Array[Float]],
      index: Int
  ): Unit =
    extraData(0)(index) = objData.speedAccel

  override def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit = {
    val speedAccel = extraData(0)
    val motion     = extraData(1)

    var i: Int = 0
    while (i < size) {
      motion(i) = motion(i) + speedAccel(i)
      i += 1
    }
  }
}
object SimpleAccelerationBehavior {
  case class Data(speedAccel: Float)
}
