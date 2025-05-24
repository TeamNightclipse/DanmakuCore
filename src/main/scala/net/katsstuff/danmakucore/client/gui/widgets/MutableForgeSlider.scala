package net.katsstuff.danmakucore.client.gui.widgets

import net.minecraft.network.chat.Component
import net.minecraftforge.client.gui.widget.ForgeSlider

class MutableForgeSlider(
    _x: Int,
    _y: Int,
    _width: Int,
    _height: Int,
    _prefix: Component,
    _suffix: Component,
    _minValue: Double,
    _maxValue: Double,
    _currentValue: Double,
    _stepSize: Double,
    _precision: Int,
    _drawString: Boolean
) extends ForgeSlider(
      _x,
      _y,
      _width,
      _height,
      _prefix,
      _suffix,
      _minValue,
      _maxValue,
      _currentValue,
      _stepSize,
      _precision,
      _drawString
    ) {

  def setMinValue(newMinValue: Double): Unit =
    val value = getValue
    this.minValue = newMinValue
    setValue(value)

  def setMaxValue(newMaxValue: Double): Unit =
    val value = getValue
    this.maxValue = newMaxValue
    setValue(value)
}
