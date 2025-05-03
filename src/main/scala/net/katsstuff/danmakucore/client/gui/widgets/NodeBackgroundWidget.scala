package net.katsstuff.danmakucore.client.gui.widgets

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.gui.{CursorShapes, NodeFactory}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.{NarratedElementType, NarrationElementOutput}
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class NodeBackgroundWidget(
    _x: Int,
    _y: Int,
    sizeX: Int,
    private var _sizeY: Int,
    style: NodeFactory.NodeStyle
) extends AbstractWidget(_x, _y, sizeX, 13, style.title) {

  private val nodeResource = DanmakuCore.resource("textures/gui/node.png")
  private val mc           = Minecraft.getInstance()

  def x: Int = getX

  def y: Int = getY
  
  def renderedHeight: Int = _sizeY

  def setRenderedHeight(pHeight: Int): Unit = _sizeY = pHeight

  override def setMessage(pMessage: Component): Unit = {
    super.setMessage(pMessage)
    style.title = pMessage
  }

  private var _dragging = false
  private var _resizing = false
  def dragging: Boolean = _dragging
  def resizing: Boolean = _resizing

  private var mouseOverDrag    = false
  private var mouserOverResize = false

  private def mouseHoverResize(pMouseX: Double, pMouseY: Double): Boolean = {
    val resizePadding = 3
    pMouseX > x + width - resizePadding && pMouseX < x + width + resizePadding &&
    pMouseY > y && pMouseY < y + _sizeY
  }

  override def onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double): Unit =
    if mouseOverDrag then _dragging = true
    else if mouserOverResize then _resizing = true

  override def onRelease(pMouseX: Double, pMouseY: Double): Unit =
    _dragging = false
    _resizing = false

  override def onClick(pMouseX: Double, pMouseY: Double): Unit = {
    if mouseHoverResize(pMouseX, pMouseY)
    then
      mouserOverResize = true
      mouseOverDrag = false
    else
      mouseOverDrag = true
      mouserOverResize = false
  }

  override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean =
    this.active && this.visible && pMouseX >= x && pMouseY >= y && pMouseX < (x + this.width) && pMouseY < (y + _sizeY)

  override def clicked(pMouseX: Double, pMouseY: Double): Boolean =
    mouseHoverResize(pMouseX, pMouseY) || super.clicked(pMouseX, pMouseY)

  override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
    super.mouseClicked(pMouseX, pMouseY, pButton)

  override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
    if mouseHoverResize(pMouseX, pMouseY)
    then CursorShapes.setResizeEw()

    val slice   = 3
    val offsetU = if isFocused then 9 else 0

    def setColorFromInt(int: Int): Unit = {
      pGuiGraphics.setColor(
        FastColor.ARGB32.red(int) / 255F,
        FastColor.ARGB32.green(int) / 255F,
        FastColor.ARGB32.blue(int) / 255F,
        FastColor.ARGB32.alpha(int) / 255F
      )
    }

    setColorFromInt(style.color)
    pGuiGraphics.blitNineSliced(nodeResource, x, y, width, _sizeY, slice, slice * 3, slice * 3, offsetU, 0)
    setColorFromInt(style.topColor)
    pGuiGraphics.blitNineSliced(nodeResource, x, y, width, 13, slice, slice * 3, slice * 3, offsetU, 0)
    pGuiGraphics.setColor(1F, 1F, 1F, 1F)

    pGuiGraphics.drawString(mc.font, this.getMessage, x + 3, y + 3, 0xFFFFFFFF, false)
  }

  override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
    pNarrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage())
}
