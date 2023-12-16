package net.katsstuff.danmakucore.danmaku.behaviors

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.math.Vector3
import net.minecraft.resources.ResourceLocation

class ComplexGravityBehavior extends Behavior[ComplexGravityBehavior.Data] {
  override def extraColumns: Seq[ResourceLocation] = Seq(
    DanmakuCore.resource("motion-x"),
    DanmakuCore.resource("motion-y"),
    DanmakuCore.resource("motion-z"),
    DanmakuCore.resource("gravity-x"),
    DanmakuCore.resource("gravity-y"),
    DanmakuCore.resource("gravity-z")
  )

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Nil

  override def noOpData: ComplexGravityBehavior.Data = ComplexGravityBehavior.Data(Vector3.Zero)

  override def transferData(objData: ComplexGravityBehavior.Data, extraData: Array[Array[Float]], index: Int): Unit = {
    extraData(3)(index) = objData.gravity.x.toFloat
    extraData(4)(index) = objData.gravity.y.toFloat
    extraData(5)(index) = objData.gravity.z.toFloat
  }

  override def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit = {
    val ticksExisted = mainColumns.ticksExisted

    var dir: Int = 0
    while (dir < 3) {
      val mot     = extraData(dir)
      val gravity = extraData(3 + dir)

      {
        var i: Int = 0
        while (i < size) {
          val t = ticksExisted(i)

          mot(i) = mot(i) + gravity(i) * ((t * t) / 2F)
          i += 1
        }
      }
      dir += 1
    }
  }
}
object ComplexGravityBehavior {
  case class Data(gravity: Vector3)
}
