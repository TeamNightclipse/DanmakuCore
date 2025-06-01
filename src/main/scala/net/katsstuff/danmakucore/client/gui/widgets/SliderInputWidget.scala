package net.katsstuff.danmakucore.client.gui.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.{EditBox, Tooltip}
import net.minecraft.network.chat.Component
import net.minecraftforge.client.gui.widget.ForgeSlider
import org.lwjgl.glfw.GLFW

import java.text.NumberFormat
import java.util.Locale

class SliderInputWidget(
    _x: Int,
    _y: Int,
    _width: Int,
    _height: Int,
    _prefix: Component,
    _suffix: Component,
    _min: Double,
    _max: Double,
    _value: Double,
    stepSize: Double,
    precision: Int,
    _drawString: Boolean
) extends ForgeSlider(
      _x,
      _y,
      _width,
      _height,
      _prefix,
      _suffix,
      Math.min(_min, _value),
      Math.max(_max, _value),
      _value,
      stepSize,
      precision,
      _drawString
    ) {
  private val editBox       = new EditBox(Minecraft.getInstance.font, 0, 0, width, height, Component.empty)
  private var dragTime      = 0
  var responder: () => Unit = () => ()

  override def setValue(value: Double): Unit =
    if value < minValue then minValue = value
    if value > maxValue then maxValue = value
    super.setValue(value)
    responder()

  override def applyValue(): Unit = responder()

  def setMinValue(newMinValue: Double): Unit =
    val value = getValue
    this.minValue = newMinValue
    setValue(value)

  def setMaxValue(newMaxValue: Double): Unit =
    val value = getValue
    this.maxValue = newMaxValue
    setValue(value)

  override def onDrag(mouseX: Double, mouseY: Double, dragX: Double, dragY: Double): Unit =
    dragTime += 1
    if editBox.isFocused then ()
    else super.onDrag(mouseX, mouseY, dragX, dragY)

  override def mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean =
    if editBox.isFocused then editBox.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
    else super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)

  override def onRelease(pMouseX: Double, pMouseY: Double): Unit =
    if editBox.isFocused then editBox.onRelease(pMouseX, pMouseY)
    else if dragTime == 0 then
      editBox.setFocused(true)
      editBox.setValue("%.4f".formatLocal(Locale.ROOT, getValue))
    else super.onClick(pMouseX, pMouseY)

  private def finishEdit(): Unit =
    editBox.getValue.toDoubleOption match
      case Some(value) =>
        setValue(value)
        setTooltip(null)
      case None =>
        setTooltip(
          Tooltip.create(Component.translatable("danmakucore.gui.nodeEditor.notValidNumber", editBox.getValue))
        )
    editBox.setFocused(false)

  override def renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float): Unit =
    if !isFocused && editBox.isFocused then finishEdit() //We do this here instead of in setFocused as sometimes we get set and unset focused back to back

    if editBox.isFocused then
      editBox.setX(this.getX)
      editBox.setY(this.getY)
      editBox.setWidth(this.width)
      editBox.setHeight(this.height)
      editBox.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
    else super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)

  override def keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    if editBox.isFocused then
      if keyCode == GLFW.GLFW_KEY_ENTER then
        finishEdit()
        true
      else editBox.keyPressed(keyCode, scanCode, modifiers)
    else super.keyPressed(keyCode, scanCode, modifiers)

  override def charTyped(pCodePoint: Char, pModifiers: Int): Boolean =
    if editBox.isFocused then editBox.charTyped(pCodePoint, pModifiers) else super.charTyped(pCodePoint, pModifiers)

  override def onClick(mouseX: Double, mouseY: Double): Unit =
    dragTime = 0
    if editBox.isFocused then editBox.onClick(mouseX, mouseY)

  override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean =
    if editBox.isFocused then editBox.isMouseOver(pMouseX, pMouseY) else super.isMouseOver(pMouseX, pMouseY)

}
