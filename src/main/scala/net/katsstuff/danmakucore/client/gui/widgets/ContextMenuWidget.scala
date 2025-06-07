package net.katsstuff.danmakucore.client.gui.widgets

import net.minecraft.client.gui.GuiGraphics

import java.util.function.Consumer
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import net.minecraft.client.gui.components.{AbstractWidget, Button}
import net.minecraft.client.gui.components.events.{AbstractContainerEventHandler, GuiEventListener}
import net.minecraft.client.gui.layouts.{GridLayout, LayoutElement}
import net.minecraft.client.gui.narration.{NarratableEntry, NarratedElementType, NarrationElementOutput, NarrationSupplier}
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component

class ContextMenuWidget(pX: Int, pY: Int, pWidth: Int, entries: Seq[(Component, () => Unit)], close: () => Unit)
    extends AbstractContainerEventHandler,
      NarratableEntry,
      LayoutElement { self =>

  private val _children: mutable.Buffer[LayoutElement] = mutable.Buffer.empty
  private val layout: GridLayout                       = new GridLayout(pX, pY)

  entries.zipWithIndex.foreach:
    case ((text, action), idx) =>
      val button = Button.builder(text, _ => action()).size(pWidth, 14).build()
      _children += button
      layout.addChild(button, idx, 0)
  layout.arrangeElements()

  val background: AbstractWidget = new AbstractWidget(pX, pY, layout.getWidth, layout.getHeight, Component.empty()) {
    override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
      val stack = pGuiGraphics.pose()
      stack.pushPose()
      stack.translate(0, 0, -1000)
      pGuiGraphics.fill(this.getX, this.getY, this.getX + this.width, this.getY + this.height, 0xFF808080)
      stack.popPose()
    }

    override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit = ()
  }

  override def narrationPriority(): NarratableEntry.NarrationPriority =
    if (this.isFocused) NarratableEntry.NarrationPriority.FOCUSED
    else if (background.isHovered) NarratableEntry.NarrationPriority.HOVERED
    else NarratableEntry.NarrationPriority.NONE

  override def children(): java.util.List[_ <: GuiEventListener] = _children.collect { case w: GuiEventListener =>
    w
  }.asJava

  override def updateNarration(pNarrationElementOutput: NarrationElementOutput): Unit = {
    _children.view
      .collectFirst {
        case w: AbstractWidget if w.isHovered => w
      }
      .orElse(
        Option(this.getFocused).collect { case w: NarrationSupplier =>
          w
        }
      )
      .foreach(_.updateNarration(pNarrationElementOutput.nest()))

    pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"))
  }

  override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = {
    val inArea = background.isMouseOver(pMouseX, pMouseY)

    if !inArea then
      close()
      true
    else
      val handled = super.mouseClicked(pMouseX, pMouseY, pButton)
      if handled then close()
      handled
  }

  override def getRectangle: ScreenRectangle =
    new ScreenRectangle(layout.getX, layout.getY, layout.getWidth, layout.getHeight)

  override def setX(pX: Int): Unit = layout.setX(pX)

  override def setY(pY: Int): Unit = layout.setY(pY)

  override def getX: Int = layout.getX

  override def getY: Int = layout.getY

  override def getWidth: Int = layout.getWidth

  override def getHeight: Int = layout.getHeight

  override def visitWidgets(pConsumer: Consumer[AbstractWidget]): Unit =
    layout.visitWidgets(pConsumer)
    pConsumer.accept(background)
}
