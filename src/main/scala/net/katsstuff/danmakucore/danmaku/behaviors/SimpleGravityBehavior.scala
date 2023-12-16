package net.katsstuff.danmakucore.danmaku.behaviors

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.resources.ResourceLocation

class SimpleGravityBehavior extends Behavior[SimpleGravityBehavior.Data] {
  override def extraColumns: Seq[ResourceLocation] = Seq(
    DanmakuCore.resource("motion-y"),
    DanmakuCore.resource("gravity-y")
  )

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Nil

  override def noOpData: SimpleGravityBehavior.Data = SimpleGravityBehavior.Data(0)

  override def transferData(objData: SimpleGravityBehavior.Data, extraData: Array[Array[Float]], index: Int): Unit =
    extraData(1)(index) = objData.gravity

  override def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit = {
    val ticksExisted = mainColumns.ticksExisted
    val mot          = extraData(0)
    val gravity      = extraData(1)

    {
      var i: Int = 0
      while (i < size) {
        val t = ticksExisted(i)

        mot(i) = mot(i) + gravity(i) * ((t * t) / 2F)
        i += 1
      }
    }
  }
}
object SimpleGravityBehavior {
  case class Data(gravity: Float)
}
