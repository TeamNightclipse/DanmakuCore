package net.katsstuff.danmakucore.danmaku.behaviors

import net.minecraft.resources.ResourceLocation

trait Behavior[A] {
  def extraColumns: Seq[ResourceLocation]

  def requiredMainColumns: Seq[MainColumns.RequiredColumns]

  def noOpData: A

  def transferData(objData: A, extraData: Array[Array[Float]], index: Int): Unit

  def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit
}
