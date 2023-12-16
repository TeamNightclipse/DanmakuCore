package net.katsstuff.danmakucore.danmaku.data

import net.katsstuff.danmakucore.danmaku.form.Form

case class ShotData(
    form: Form,
    renderProperties: Map[String, Float] = Map.empty,
    mainColor: Int = 0xFF0000,
    secondaryColor: Int = 0xFFFFFF,
    damage: Float = 2,
    sizeX: Float = 0.5F,
    sizeY: Float = 0.5F,
    sizeZ: Float = 0.5F,
    endTime: Short = 100,
) {

  def setForm(form: Form): ShotData                                 = copy(form = form)
  def setRenderProperties(properties: Map[String, Float]): ShotData = copy(renderProperties = properties)
  def setMainColor(color: Int): ShotData                            = copy(mainColor = color)
  def setSecondaryColor(color: Int): ShotData                       = copy(secondaryColor = color)
  def setDamage(damage: Float): ShotData                            = copy(damage = damage)

  def setSize(sizeX: Float, sizeY: Float, sizeZ: Float): ShotData = copy(sizeX = sizeX, sizeY = sizeY, sizeZ = sizeZ)
  def setSize(size: Float): ShotData                              = setSize(size, size, size)
  def setSizeX(sizeX: Float): ShotData                            = copy(sizeX = sizeX)
  def setSizeY(sizeY: Float): ShotData                            = copy(sizeY = sizeY)
  def setSizeZ(sizeZ: Float): ShotData                            = copy(sizeZ = sizeZ)

  def scaleSize(scale: Float): ShotData = scaleSize(scale, scale, scale)
  def scaleSize(scaleX: Float, scaleY: Float, scaleZ: Float): ShotData =
    copy(sizeX = sizeX * scaleX, sizeY = sizeY * scaleY, sizeZ = sizeZ * scaleZ)

  def setEndTime(endTime: Short): ShotData = copy(endTime = endTime)
}
