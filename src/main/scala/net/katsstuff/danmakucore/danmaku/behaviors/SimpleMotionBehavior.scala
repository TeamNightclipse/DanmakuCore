package net.katsstuff.danmakucore.danmaku.behaviors

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.resources.ResourceLocation

class SimpleMotionBehavior extends Behavior[SimpleMotionBehavior.Data] {
  override def extraColumns: Seq[ResourceLocation] = Seq(DanmakuCore.resource("motion-z"))

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Seq(
    MainColumns.RequiredColumns.PosZ
  )

  override def noOpData: SimpleMotionBehavior.Data = SimpleMotionBehavior.Data(0)

  override def transferData(objData: SimpleMotionBehavior.Data, extraData: Array[Array[Float]], index: Int): Unit =
    extraData(0)(index) = objData.forwardSpeed

  override def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit = {
    val motionZ = extraData(0)
    val posZ    = mainColumns.posZ
    val oldPosZ = mainColumns.oldPosZ

    {
      var i: Int = 0
      while (i < size) {
        oldPosZ(i) = posZ(i)
        i += 1
      }
    }

    {
      var i: Int = 0
      while (i < size) {
        posZ(i) = posZ(i) + motionZ(i)
        i += 1
      }
    }
  }
}
object SimpleMotionBehavior {
  case class Data(forwardSpeed: Float)
}
