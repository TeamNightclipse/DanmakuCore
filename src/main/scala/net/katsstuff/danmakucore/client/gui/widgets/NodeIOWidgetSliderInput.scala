package net.katsstuff.danmakucore.client.gui.widgets

import net.katsstuff.danmakucore.client.gui.NodeFactory
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraftforge.client.gui.widget.ForgeSlider

class NodeIOWidgetSliderInput(style: NodeFactory.IONodeContentStyle, val internals: SliderInputWidget, maxWidth: Int = 70)
    extends NodeIOWidget(style, maxWidth) {

  def value: Double = internals.getValue

  override protected def renderSideContent(
      pGuiGraphics: GuiGraphics,
      pMouseX: Int,
      pMouseY: Int,
      pPartialTick: Float
  ): Unit = {
    if connections.nonEmpty then super.renderSideContent(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    else {
      val textPaddingX = 4

      internals.setX(x + textPaddingX)
      internals.setY(y)
      internals.setWidth(width - textPaddingX)
      internals.setHeight(height)

      internals.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }
  }

  override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
    internals.updateWidgetNarration(pNarrationElementOutput)

  override def setFocused(pFocused: Boolean): Unit = {
    super.setFocused(pFocused)
    internals.setFocused(pFocused)
  }

  override def onClick(pMouseX: Double, pMouseY: Double): Unit = {
    super.onClick(pMouseX, pMouseY)
    if internals.isMouseOver(pMouseX, pMouseY) then internals.onClick(pMouseX, pMouseY)
  }

  override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean = super.isMouseOver(pMouseX, pMouseY) || internals.isMouseOver(pMouseX, pMouseY)

  override def mouseDragged(
      pMouseX: Double,
      pMouseY: Double,
      pButton: Int,
      pDragX: Double,
      pDragY: Double
  ): Boolean = {
    super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
    internals.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
  }

  override def keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean =
    super.keyPressed(pKeyCode, pScanCode, pModifiers)
    internals.keyPressed(pKeyCode, pScanCode, pModifiers)

  override def onRelease(pMouseX: Double, pMouseY: Double): Unit =
    super.onRelease(pMouseX, pMouseY)
    internals.onRelease(pMouseX, pMouseY)

  override def charTyped(pCodePoint: Char, pModifiers: Int): Boolean =
    super.charTyped(pCodePoint, pModifiers)
    internals.charTyped(pCodePoint, pModifiers)
}
