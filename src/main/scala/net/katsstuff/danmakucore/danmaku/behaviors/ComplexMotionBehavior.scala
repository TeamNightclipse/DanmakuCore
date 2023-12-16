package net.katsstuff.danmakucore.danmaku.behaviors

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.math.Vector3
import net.minecraft.resources.ResourceLocation

class ComplexMotionBehavior extends Behavior[ComplexMotionBehavior.Data] {
  override def extraColumns: Seq[ResourceLocation] = Seq(
    DanmakuCore.resource("motion-x"),
    DanmakuCore.resource("motion-y"),
    DanmakuCore.resource("motion-z")
  )

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Seq(
    MainColumns.RequiredColumns.PosX,
    MainColumns.RequiredColumns.PosY,
    MainColumns.RequiredColumns.PosZ
  )

  override def noOpData: ComplexMotionBehavior.Data = ComplexMotionBehavior.Data(Vector3.Zero)

  override def transferData(objData: ComplexMotionBehavior.Data, extraData: Array[Array[Float]], index: Int): Unit = {
    extraData(0)(index) = objData.motion.x.toFloat
    extraData(1)(index) = objData.motion.y.toFloat
    extraData(2)(index) = objData.motion.z.toFloat
  }

  override def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit = {
    var dir: Int = 0
    while (dir < 3) {
      val pos    = mainColumns.pos(dir)
      val oldPos = mainColumns.oldPos(dir)
      val mot    = extraData(dir)

      {
        var i: Int = 0
        while (i < size) {
          oldPos(i) = pos(i)
          i += 1
        }
      }

      {
        var i: Int = 0
        while (i < size) {
          pos(i) = pos(i) + mot(i)
          i += 1
        }
      }
      dir += 1
    }
  }
}
object ComplexMotionBehavior {
  case class Data(motion: Vector3)
}
