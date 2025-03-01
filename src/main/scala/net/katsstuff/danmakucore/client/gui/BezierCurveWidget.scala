package net.katsstuff.danmakucore.client.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import org.joml.Vector2i

// Info to continue this:
// https://pomax.github.io/bezierinfo/
// https://ciechanow.ski/drawing-bezier-curves/
// https://www.youtube.com/watch?v=aVwxzDHniEw
class BezierCurveWidget(
    var from: Vector2i,
    var to: Vector2i
) extends AbstractWidget(from.x - 2, from.y - 2, 5, 5, Component.empty) {

  override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean = {
    def mouseOver(point: Vector2i): Boolean =
      val size = 3
      pMouseX >= point.x - size && pMouseX <= point.x + size && pMouseY >= point.y - size && pMouseY <= point.y + size

    mouseOver(from) || mouseOver(to)
  }

  override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
    isHovered = isMouseOver(pMouseX, pMouseY)
    ???
  }

  override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit = ()

  override def clicked(pMouseX: Double, pMouseY: Double): Boolean =
    isMouseOver(pMouseX, pMouseY)
}
