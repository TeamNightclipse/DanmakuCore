package net.katsstuff.danmakucore.client.gui

import java.util
import java.util.function.Consumer

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.events.{
  AbstractContainerEventHandler,
  ContainerEventHandler,
  GuiEventListener
}
import net.minecraft.client.gui.components.{AbstractButton, AbstractWidget}
import net.minecraft.client.gui.layouts.{GridLayout, LayoutElement, LayoutSettings, SpacerElement}
import net.minecraft.client.gui.narration.{
  NarratableEntry,
  NarratedElementType,
  NarrationElementOutput,
  NarrationSupplier
}
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import org.joml.{Vector2d, Vector2i}

class NodeWidget(
    x: Int,
    y: Int,
    var width: Int,
    var height: Int,
    topColor: Int,
    color: Int,
    title: Component,
    extraWidgets: () => Seq[NodeWidget.NodeContent],
    addScreenWidget: AbstractWidget => Unit,
    removeWidget: AbstractWidget => Unit,
    parent: ContainerEventHandler
) extends AbstractContainerEventHandler,
      NarratableEntry,
      LayoutElement {

  private val _children: mutable.Buffer[GuiEventListener] = mutable.Buffer.empty
  private val grid: GridLayout                            = new GridLayout(0, 0).columnSpacing(10)
  private val rows: GridLayout#RowHelper                  = grid.createRowHelper(1)
  private val background: NodeWidget.NodeBackgroundWidget = rows.addChild(
    new NodeWidget.NodeBackgroundWidget(
      grid.getX,
      grid.getY,
      width,
      height,
      topColor,
      color,
      title
    ) {

      var dragOffsetX: Double = 0
      var dragOffsetY: Double = 0

      override def onClick(pMouseX: Double, pMouseY: Double): Unit = {
        dragOffsetX = pMouseX - grid.getX
        dragOffsetY = pMouseY - grid.getY
      }

      override def onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double): Unit = {
        grid.setX((pMouseX - dragOffsetX).toInt)
        grid.setY((pMouseY - dragOffsetY).toInt)
      }
    }
  )
  rows.addChild(new SpacerElement(width, 3))
  locally {
    val content = extraWidgets()
    val (miscs, io) =
      content.partitionMap {
        case NodeWidget.NodeContent.Misc(content) => Left(content(addScreenWidget, removeWidget, parent))
        case NodeWidget.NodeContent.Input(i)      => Right(Left(i(addScreenWidget, removeWidget, parent)))
        case NodeWidget.NodeContent.Output(o)     => Right(Right(o(addScreenWidget, removeWidget, parent)))
      }
    val (inputs, outputs) = io.partitionMap(identity)

    def addWidgets(widgets: Seq[AbstractWidget], settings: LayoutSettings, addSpacer: Boolean): Unit =
      if widgets.nonEmpty then
        widgets.foreach { widget =>
          rows.addChild(widget, settings)
          _children += widget
        }
        if addSpacer then rows.addChild(new SpacerElement(width, 5))
    end addWidgets

    addWidgets(miscs, rows.defaultCellSetting(), inputs.nonEmpty || outputs.nonEmpty)
    addWidgets(inputs, rows.newCellSettings().alignHorizontallyLeft(), outputs.nonEmpty)
    addWidgets(outputs, rows.newCellSettings().alignHorizontallyRight(), false)
  }
  grid.setX(x)
  grid.setX(y)
  grid.arrangeElements()
  background.setRenderedHeight(grid.getHeight + 10)

  override def narrationPriority(): NarratableEntry.NarrationPriority =
    if (this.isFocused) NarratableEntry.NarrationPriority.FOCUSED
    else if (background.isHovered) NarratableEntry.NarrationPriority.HOVERED
    else NarratableEntry.NarrationPriority.NONE

  override def children(): util.List[_ <: GuiEventListener] = _children.asJava

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

  override def getRectangle: ScreenRectangle = new ScreenRectangle(grid.getX, grid.getY, width, height)

  override def setX(pX: Int): Unit = grid.setX(pX)

  override def setY(pY: Int): Unit = grid.setY(pY)

  override def getX: Int = grid.getX

  override def getY: Int = grid.getY

  override def getWidth: Int = width

  override def getHeight: Int = height

  def setWidth(pWidth: Int): Unit = {
    width = pWidth
    background.setWidth(pWidth)
    grid.arrangeElements()
  }

  def setHeight(pHeight: Int): Unit = {
    height = pHeight
    background.setRenderedHeight(pHeight)
  }

  override def visitWidgets(pConsumer: Consumer[AbstractWidget]): Unit =
    grid.visitWidgets(pConsumer)
}
object NodeWidget {
  enum NodeContent {
    case Misc(content: (AbstractWidget => Unit, AbstractWidget => Unit, ContainerEventHandler) => AbstractWidget)
    case Input(content: (AbstractWidget => Unit, AbstractWidget => Unit, ContainerEventHandler) => AbstractWidget)
    case Output(content: (AbstractWidget => Unit, AbstractWidget => Unit, ContainerEventHandler) => AbstractWidget)
  }

  private class NodeBackgroundWidget(
      _x: Int,
      _y: Int,
      sizeX: Int,
      var sizeY: Int,
      topColor: Int,
      color: Int,
      title: Component
  ) extends AbstractWidget(_x, _y, sizeX, 10, title) {

    def x: Int = getX

    def y: Int = getY

    def setRenderedHeight(pHeight: Int): Unit = sizeY = pHeight

    override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
      pGuiGraphics.fill(x, y, x + width, y + 10, topColor)
      pGuiGraphics.drawString(Minecraft.getInstance().font, this.getMessage, x + 2, y + 2, 0xFFFFFFFF, false)
      pGuiGraphics.fill(x, y + 10, x + width, y + sizeY - 10, color)
    }

    override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
      pNarrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage())
  }

  enum IOWidgetVariant {
    case Input
    case Output
  }

  def simpleInput(title: Component, color: Int): NodeContent =
    NodeContent.Input(
      new NodeIOWidget(
        _x = 0,
        _y = 0,
        _width = Math.min(Minecraft.getInstance().font.width(title) + 10, 70),
        _height = 10,
        title = title,
        color = color,
        variant = IOWidgetVariant.Input,
        connectorSize = 5
      )(_, _, _)
    )

  def simpleOutput(title: Component, color: Int): NodeContent =
    NodeContent.Output(
      new NodeIOWidget(
        _x = 0,
        _y = 0,
        _width = Math.min(Minecraft.getInstance().font.width(title) + 10, 70),
        _height = 10,
        title = title,
        color = color,
        variant = IOWidgetVariant.Output,
        connectorSize = 5
      )(_, _, _)
    )

  class NodeIOWidget(
      _x: Int,
      _y: Int,
      _width: Int,
      _height: Int,
      title: Component,
      color: Int,
      variant: IOWidgetVariant,
      connectorSize: Int
  )(addWidget: AbstractWidget => Unit, removeWidget: AbstractWidget => Unit, parent: ContainerEventHandler)
      extends AbstractButton(_x, _y, _width, _height, title) {
    def x: Int = getX

    def y: Int = getY

    private var connection: BezierCurveWidget | Null = _
    private var setConnectionDragging                = false
    private var newConnectionDragging                = false

    private def connectorRectangle = {
      val paddingX = 0
      variant match
        case IOWidgetVariant.Input =>
          new ScreenRectangle(
            x - Mth.floor(connectorSize / 2D) - 1 - paddingX,
            y + Mth.floor(connectorSize / 2D),
            connectorSize,
            connectorSize
          )
        case IOWidgetVariant.Output =>
          new ScreenRectangle(
            x + width - Mth.floor(connectorSize / 2D) + paddingX,
            y + Mth.floor(connectorSize / 2D),
            connectorSize,
            connectorSize
          )
    }

    override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean = {
      val c = connectorRectangle
      c.left <= pMouseX && c.right >= pMouseX && c.top <= pMouseY && c.bottom >= pMouseY
    }

    override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
      isHovered = isMouseOver(pMouseX, pMouseY)

      val minecraft = Minecraft.getInstance
      RenderSystem.enableBlend()
      RenderSystem.enableDepthTest()
      pGuiGraphics.setColor(1F, 1F, 1F, 1F)

      val textPaddingX = 4
      AbstractWidget.renderScrollingString(
        pGuiGraphics,
        minecraft.font,
        getMessage,
        getX + textPaddingX,
        getY,
        getX + width - textPaddingX,
        getY + height,
        getFGColor | Mth.ceil(this.alpha * 255.0F) << 24
      )

      val c = connectorRectangle
      pGuiGraphics.fill(RenderType.gui(), c.left, c.top, c.right, c.bottom, 100, color)

      pGuiGraphics.flush()

      if setConnectionDragging then
        parent.setFocused(connection)
        variant match
          case IOWidgetVariant.Input =>
            if newConnectionDragging then connection.toDragging = true
            else connection.fromDragging = true

          case IOWidgetVariant.Output =>
            if newConnectionDragging then connection.fromDragging = true
            else connection.toDragging = true

        setConnectionDragging = false
        newConnectionDragging = false
    }

    override def clicked(pMouseX: Double, pMouseY: Double): Boolean =
      active && visible && isMouseOver(pMouseX, pMouseY)

    override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
      defaultButtonNarrationText(pNarrationElementOutput)

    override def onPress(): Unit = ()

    def removeConnection(): Unit = connection = null

    override def onClick(pMouseX: Double, pMouseY: Double): Unit = {
      super.onClick(pMouseX, pMouseY)
      if connection == null then
        val c = connectorRectangle
        val x = c.left + Mth.floor(c.width / 2D)
        val y = c.top + Mth.floor(c.height / 2D)
        connection = new BezierCurveWidget(
          _from = new Vector2i(x, y),
          _to = new Vector2i(x, y),
          width = 1,
          color = 0xFFFFFFFF,
          removeWidget = removeWidget
        )
        variant match
          case IOWidgetVariant.Input  => connection.fromWidget = this
          case IOWidgetVariant.Output => connection.toWidget = this

        addWidget(connection)
        newConnectionDragging = true
      end if

      setConnectionDragging = true
    }

    override def onRelease(pMouseX: Double, pMouseY: Double): Unit = {
      if connection == null then
        parent.children().asScala.foreach {
          case w: BezierCurveWidget if w.isMouseOver(pMouseX, pMouseY) && variant == IOWidgetVariant.Input =>
            connection = w
            w.fromWidget = this

          case w: BezierCurveWidget if w.isMouseOver(pMouseX, pMouseY) && variant == IOWidgetVariant.Output =>
            connection = w
            w.toWidget = this

          case _ =>
        }
    }

    override def setX(pX: Int): Unit = {
      super.setX(pX)
      if connection != null then
        val c = connectorRectangle
        variant match
          case IOWidgetVariant.Input =>
            connection.from = new Vector2d(c.left + (connectorSize / 2D), connection.from.y)
          case IOWidgetVariant.Output =>
            connection.to = new Vector2d(c.left + (connectorSize / 2D), connection.to.y)
    }

    override def setY(pY: Int): Unit = {
      super.setY(pY)
      if connection != null then
        val c = connectorRectangle
        variant match
          case IOWidgetVariant.Input =>
            connection.from = new Vector2d(connection.from.x, c.top + Mth.floor(connectorSize / 2D))
          case IOWidgetVariant.Output =>
            connection.to = new Vector2d(connection.to.x, c.top + Mth.floor(connectorSize / 2D))
    }
  }
}
