package net.katsstuff.danmakucore.client.gui.widgets

import net.minecraft.client.gui.components.{AbstractWidget, ObjectSelectionList}
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.{FocusNavigationEvent, ScreenRectangle}
import net.minecraft.client.gui.{ComponentPath, GuiGraphics}
import net.minecraft.network.chat.Component

abstract class ObjectSelectionListDelegatingWidget[A <: ObjectSelectionList.Entry[A]](
    _x: Int,
    _y: Int,
    _width: Int,
    _height: Int,
    _message: Component
) extends AbstractWidget(_x, _y, _width, _height, _message) {
  protected def selectionList: ObjectSelectionList[A]

  override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit =
    selectionList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

  override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
    selectionList.updateNarration(pNarrationElementOutput)

  override def nextFocusPath(pEvent: FocusNavigationEvent): ComponentPath = selectionList.nextFocusPath(pEvent)

  override def mouseMoved(pMouseX: Double, pMouseY: Double): Unit =
    selectionList.mouseMoved(pMouseX, pMouseY)

  override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
    selectionList.mouseClicked(pMouseX, pMouseY, pButton)

  override def mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
    selectionList.mouseReleased(pMouseX, pMouseY, pButton)

  override def mouseDragged(
      pMouseX: Double,
      pMouseY: Double,
      pButton: Int,
      pDragX: Double,
      pDragY: Double
  ): Boolean = selectionList.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)

  override def mouseScrolled(pMouseX: Double, pMouseY: Double, pDelta: Double): Boolean =
    selectionList.mouseScrolled(pMouseX, pMouseY, pDelta)

  override def keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean =
    selectionList.keyPressed(pKeyCode, pScanCode, pModifiers)

  override def keyReleased(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean =
    selectionList.keyReleased(pKeyCode, pScanCode, pModifiers)

  override def charTyped(pCodePoint: Char, pModifiers: Int): Boolean = selectionList.charTyped(pCodePoint, pModifiers)

  override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean = selectionList.isMouseOver(pMouseX, pMouseY)

  override def narrationPriority(): NarrationPriority = selectionList.narrationPriority()

  override def getRectangle: ScreenRectangle = selectionList.getRectangle

  override def getWidth: Int = selectionList.getWidth

  override def getHeight: Int = selectionList.getHeight

  override def getX: Int = selectionList.getLeft

  override def getY: Int = selectionList.getTop

  override def setWidth(pWidth: Int): Unit = {
    val x0 = selectionList.getLeft
    selectionList.updateSize(pWidth, selectionList.getHeight, selectionList.getTop, selectionList.getBottom)
    selectionList.setLeftPos(x0)
  }

  override def setHeight(value: Int): Unit = {
    val diff = value - selectionList.getHeight
    val x0   = selectionList.getLeft
    selectionList.updateSize(selectionList.getWidth, value, selectionList.getTop, selectionList.getBottom + diff)
    selectionList.setLeftPos(x0)
  }

  override def setX(pX: Int): Unit = {
    selectionList.setLeftPos(pX)
  }

  override def setY(pY: Int): Unit = {
    val diff = pY - selectionList.getTop
    val x0 = selectionList.getLeft
    selectionList.updateSize(selectionList.getWidth, selectionList.getHeight, pY, selectionList.getBottom + diff)
    selectionList.setLeftPos(x0)
  }
}
