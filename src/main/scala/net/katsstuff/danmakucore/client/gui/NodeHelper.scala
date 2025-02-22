package net.katsstuff.danmakucore.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.{AbstractWidget, StringWidget}
import net.minecraft.client.gui.layouts.{LayoutElement, LinearLayout}
import net.minecraft.client.gui.narration.{NarratedElementType, NarrationElementOutput}
import net.minecraft.network.chat.Component

object NodeHelper {

  class NodeIOWidget(_x: Int, _y: Int, _width: Int, _height: Int, title: Component, color: Int)
      extends AbstractWidget(_x, _y, _width, _height, title) {
    def x: Int = getX
    def y: Int = getY

    override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit =
      pGuiGraphics.fill(x, y, x + width, y + height, color)

    override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit = {
      // TODO
    }
  }

  def inputNode(content: Component, color: Int): LinearLayout =
    inputNode(
      new StringWidget(content, Minecraft.getInstance().font),
      new NodeIOWidget(0, 0, 5, 5, Component.empty(), color)
    )

  def inputNode(content: LayoutElement, input: LayoutElement): LinearLayout = {
    val layout = new LinearLayout(
      content.getWidth + input.getWidth,
      Math.max(content.getHeight, input.getHeight),
      LinearLayout.Orientation.HORIZONTAL
    )
    layout.addChild(input, layout.newChildLayoutSettings().paddingTop(2).paddingLeft(-3))
    layout.addChild(content)
    layout.arrangeElements()
    layout
  }

  def outputNode(content: Component, color: Int): LinearLayout =
    outputNode(
      new StringWidget(content, Minecraft.getInstance().font),
      new NodeIOWidget(0, 0, 5, 5, Component.empty(), color)
    )

  def outputNode(content: LayoutElement, output: LayoutElement): LinearLayout = {
    val layout = new LinearLayout(
      content.getWidth + output.getWidth,
      Math.max(content.getHeight, output.getHeight),
      LinearLayout.Orientation.HORIZONTAL
    )
    layout.addChild(content)
    layout.addChild(output, layout.newChildLayoutSettings().paddingTop(2).paddingRight(-3))

    layout.arrangeElements()
    layout
  }

  class NodeBackgroundWidget(
      _x: Int,
      _y: Int,
      sizeX: Int,
      sizeY: Int,
      topColor: Int,
      color: Int,
      title: Component,
  ) extends AbstractWidget(_x, _y, sizeX, 10, title) {

    def x: Int = getX
    def y: Int = getY

    override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
      pGuiGraphics.fill(x, y, x + sizeX, y + 10, topColor)
      pGuiGraphics.drawString(Minecraft.getInstance().font, this.getMessage, x + 2, y + 2, 0xFFFFFFFF, false)
      pGuiGraphics.fill(x, y + 10, x + sizeX, y + sizeY - 10, color)
    }

    override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
      pNarrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage())
  }
}
