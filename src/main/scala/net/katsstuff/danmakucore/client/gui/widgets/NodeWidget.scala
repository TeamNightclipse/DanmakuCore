package net.katsstuff.danmakucore.client.gui.widgets

import java.util
import java.util.function.Consumer

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

import net.katsstuff.danmakucore.client.gui.NodeFactory
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.events.{AbstractContainerEventHandler, GuiEventListener}
import net.minecraft.client.gui.layouts.{GridLayout, LayoutElement, LinearLayout}
import net.minecraft.client.gui.narration.{
  NarratableEntry,
  NarratedElementType,
  NarrationElementOutput,
  NarrationSupplier
}
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component

class NodeWidget(
    x: Int,
    y: Int,
    var width: Int,
    style: NodeFactory.NodeStyle,
    onContentsChange: (self: NodeWidget) => Unit
) extends AbstractContainerEventHandler,
      NarratableEntry,
      LayoutElement { self =>

  private val _children: mutable.Buffer[LayoutElement] = mutable.Buffer.empty
  private var layout: GridLayout                       = _

  private val background: NodeBackgroundWidget = new NodeBackgroundWidget(0, 0, width, 0, style) {
    var dragOffsetX: Double = 0
    var dragOffsetY: Double = 0

    override def onClick(pMouseX: Double, pMouseY: Double): Unit = {
      super.onClick(pMouseX, pMouseY)
      dragOffsetX = pMouseX - layout.getX
      dragOffsetY = pMouseY - layout.getY
    }

    override def onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double): Unit = {
      super.onDrag(pMouseX, pMouseY, pDragX, pDragY)
      if dragging then
        layout.setX((pMouseX - dragOffsetX).toInt)
        layout.setY((pMouseY - dragOffsetY).toInt)
      else if resizing then
        val ioMin = _children.collect {
          case io: NodeIOWidget  => io.style.width
          case w: AbstractWidget => self.width - w.getWidth
        }
        val min = Math.max(30, ioMin.maxOption.getOrElse(0))

        self.setWidth(Math.max(min, (-this.x + pMouseX).toInt))
    }
  }

  def init(): Unit = {
    _children.clear()

    layout = new GridLayout(0, 0).columnSpacing(10)
    val rows: GridLayout#RowHelper = layout.createRowHelper(1)
    rows.addChild(background)
    _children += background
    val topSpacer = new MutableSpacer(0, 0, width, 3)
    rows.addChild(topSpacer)
    _children += topSpacer
    style.contents.foreach { c =>
      rows.addChild(c.widget, c.layoutSettings(rows.defaultCellSetting()))
      _children += c.widget
    }
    layout.setX(x)
    layout.setY(y)
    layout.arrangeElements()
    background.setRenderedHeight(layout.getHeight + 10)
    onContentsChange(this)
  }
  init()

  style.onContentsChange(() => init())

  override def narrationPriority(): NarratableEntry.NarrationPriority =
    if (this.isFocused) NarratableEntry.NarrationPriority.FOCUSED
    else if (background.isHovered) NarratableEntry.NarrationPriority.HOVERED
    else NarratableEntry.NarrationPriority.NONE

  override def children(): util.List[_ <: GuiEventListener] = _children.collect { case w: GuiEventListener =>
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

  override def getRectangle: ScreenRectangle = new ScreenRectangle(layout.getX, layout.getY, width, layout.getHeight + 10)

  override def setX(pX: Int): Unit = layout.setX(pX)

  override def setY(pY: Int): Unit = layout.setY(pY)

  override def getX: Int = layout.getX

  override def getY: Int = layout.getY

  override def getWidth: Int = width

  override def getHeight: Int = layout.getHeight + 10

  def setWidth(pWidth: Int): Unit = {
    val diff = pWidth - width
    width = pWidth
    background.setWidth(pWidth)
    _children.foreach {
      case w: MutableSpacer  => w.width = pWidth
      case io: NodeIOWidget  => io.setMaxWidth(width)
      case w: AbstractWidget => w.setWidth(w.getWidth + diff)
      case _                 => ()
    }
    _children.foreach(_.setX(0))
    layout.arrangeElements()
  }

  override def visitWidgets(pConsumer: Consumer[AbstractWidget]): Unit =
    layout.visitWidgets(pConsumer)
}
object NodeWidget {
  enum NodeContent {
    case Misc(content: AbstractWidget)
    case Input(content: AbstractWidget)
    case Output(content: AbstractWidget)
  }
}
