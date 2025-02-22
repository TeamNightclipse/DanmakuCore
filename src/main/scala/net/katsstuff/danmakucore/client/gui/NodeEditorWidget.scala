package net.katsstuff.danmakucore.client.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class NodeEditorWidget(x: Int, y: Int, width: Int, height: Int, message: Component)
    extends AbstractWidget(x, y, width, height, message) {

  override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = ???

  override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit = ???
}
