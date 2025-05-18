package net.katsstuff.danmakucore.client.gui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraftforge.client.gui.widget.ScrollPanel

abstract class ScrollPanelDelegatingWidget(pX: Int, pY: Int, pWidth: Int, pHeight: Int, pMessage: Component)
    extends AbstractWidget(pX, pY, pWidth, pHeight, pMessage):

  protected def scrollPanel: ScrollPanel

  protected def resetPanel(): Unit
  
  resetPanel()

  override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit =
    scrollPanel.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

  override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
    scrollPanel.updateNarration(pNarrationElementOutput)

  override def narrationPriority(): NarrationPriority = scrollPanel.narrationPriority()

  override def mouseScrolled(pMouseX: Double, pMouseY: Double, pDelta: Double): Boolean =
    scrollPanel.mouseScrolled(pMouseX, pMouseY, pDelta)

  override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean = scrollPanel.isMouseOver(pMouseX, pMouseY)

  override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
    scrollPanel.mouseClicked(pMouseX, pMouseY, pButton)

  override def mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
    scrollPanel.mouseReleased(pMouseX, pMouseY, pButton)

  override def mouseDragged(
      pMouseX: Double,
      pMouseY: Double,
      pButton: Int,
      pDragX: Double,
      pDragY: Double
  ): Boolean = scrollPanel.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)

  override def setX(pX: Int): Unit =
    super.setX(pX)
    resetPanel()

  override def setY(pY: Int): Unit = 
    super.setY(pY)
    resetPanel()

  override def setWidth(value: Int): Unit = 
    super.setWidth(value)
    resetPanel()

  override def setHeight(value: Int): Unit = 
    super.setHeight(value)
    resetPanel()
