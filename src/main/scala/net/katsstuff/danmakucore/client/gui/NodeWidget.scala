package net.katsstuff.danmakucore.client.gui

import java.util
import java.util.function.Consumer

import scala.beans.BeanProperty
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

import com.google.common.graph.Graph
import com.mojang.blaze3d.systems.RenderSystem
import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.gui.NodeWidget.MutableSpacer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.events.{AbstractContainerEventHandler, GuiEventListener}
import net.minecraft.client.gui.components.{AbstractButton, AbstractWidget}
import net.minecraft.client.gui.layouts.{GridLayout, LayoutElement, LayoutSettings, SpacerElement}
import net.minecraft.client.gui.narration.{
  NarratableEntry,
  NarratedElementType,
  NarrationElementOutput,
  NarrationSupplier
}
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.{FastColor, Mth}
import org.joml.Vector2d

class NodeWidget(
    x: Int,
    y: Int,
    var width: Int,
    style: NodeFactory.NodeStyle
) extends AbstractContainerEventHandler,
      NarratableEntry,
      LayoutElement {

  private val _children: mutable.Buffer[LayoutElement] = mutable.Buffer.empty
  private var grid: GridLayout                            = _
  private var styleContentWidgets: Seq[LayoutElement]     = style.contents.map(_.widget)

  private val background: NodeWidget.NodeBackgroundWidget = new NodeWidget.NodeBackgroundWidget(0, 0, width, 0, style) {

    var dragOffsetX: Double = 0
    var dragOffsetY: Double = 0

    override def onClick(pMouseX: Double, pMouseY: Double): Unit = {
      dragOffsetX = pMouseX - grid.getX
      dragOffsetY = pMouseY - grid.getY
    }

    override def onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double): Unit = {
      super.onDrag(pMouseX, pMouseY, pDragX, pDragY)
      grid.setX((pMouseX - dragOffsetX).toInt)
      grid.setY((pMouseY - dragOffsetY).toInt)
    }
  }

  def init(): Unit = {
    grid = new GridLayout(0, 0).columnSpacing(10)
    val rows: GridLayout#RowHelper = grid.createRowHelper(1)
    rows.addChild(background)
    rows.addChild(new MutableSpacer(0, 0, width, 3))
    style.contents.foreach { c =>
      rows.addChild(c.widget, c.layoutSettings(rows.defaultCellSetting()))
      _children += c.widget
    }
    styleContentWidgets = style.contents.map(_.widget)
    grid.setX(x)
    grid.setY(y)
    grid.arrangeElements()
    background.setRenderedHeight(grid.getHeight + 10)
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

  override def getRectangle: ScreenRectangle = new ScreenRectangle(grid.getX, grid.getY, width, grid.getHeight + 10)

  override def setX(pX: Int): Unit = grid.setX(pX)

  override def setY(pY: Int): Unit = grid.setY(pY)

  override def getX: Int = grid.getX

  override def getY: Int = grid.getY

  override def getWidth: Int = width

  override def getHeight: Int = grid.getHeight + 10

  def setWidth(pWidth: Int): Unit = {
    width = pWidth
    background.setWidth(pWidth)
    _children.foreach { case w: MutableSpacer =>
      w.width = pWidth
    }
    grid.arrangeElements()
  }

  override def visitWidgets(pConsumer: Consumer[AbstractWidget]): Unit =
    grid.visitWidgets(pConsumer)
}
object NodeWidget {
  enum NodeContent {
    case Misc(content: AbstractWidget)
    case Input(content: AbstractWidget)
    case Output(content: AbstractWidget)
  }

  class MutableSpacer(@BeanProperty var x: Int, @BeanProperty var y: Int, var width: Int, height: Int)
      extends LayoutElement {
    override def getWidth: Int = width

    override def getHeight: Int = height

    override def visitWidgets(pConsumer: Consumer[AbstractWidget]): Unit = ()
  }

  private class NodeBackgroundWidget(
      _x: Int,
      _y: Int,
      sizeX: Int,
      private var sizeY: Int,
      style: NodeFactory.NodeStyle
  ) extends AbstractWidget(_x, _y, sizeX, 13, style.title) {

    private val nodeResource = DanmakuCore.resource("textures/gui/node.png")

    def x: Int = getX

    def y: Int = getY

    def setRenderedHeight(pHeight: Int): Unit = sizeY = pHeight

    override def setMessage(pMessage: Component): Unit = {
      super.setMessage(pMessage)
      style.title = pMessage
    }

    private var dragging = false

    override def onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double): Unit =
      dragging = true

    override def onRelease(pMouseX: Double, pMouseY: Double): Unit =
      dragging = false

    override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
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
      pGuiGraphics.blitNineSliced(nodeResource, x, y, width, sizeY, slice, slice * 3, slice * 3, offsetU, 0)
      setColorFromInt(style.topColor)
      pGuiGraphics.blitNineSliced(nodeResource, x, y, width, 13, slice, slice * 3, slice * 3, offsetU, 0)
      pGuiGraphics.setColor(1F, 1F, 1F, 1F)

      // pGuiGraphics.fill(x, y, x + width, y + 10, style.topColor)
      pGuiGraphics.drawString(Minecraft.getInstance().font, this.getMessage, x + 3, y + 3, 0xFFFFFFFF, false)

      // pGuiGraphics.fill(x, y + 10, x + width, y + sizeY - 10, style.color)

    }

    override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
      pNarrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage())
  }

  class NodeIOWidget(val style: NodeFactory.IONodeContentStyle)
      extends AbstractButton(
        0,
        0,
        Math.min(Minecraft.getInstance().font.width(style.title) + 10, 70),
        10,
        style.title
      ) {
    def x: Int = getX

    def y: Int = getY

    private val connectorSize = 7

    override def setMessage(pMessage: Component): Unit = {
      super.setMessage(pMessage)
      style.title = pMessage
      setWidth(Math.min(Minecraft.getInstance().font.width(style.title) + 10, 70))
    }

    var connection: Option[BezierCurveWidget] = None

    var _connectorRectangle: ScreenRectangle = computeConnectorRectangle
    def connectorRectangle: ScreenRectangle  = _connectorRectangle

    def computeConnectorRectangle: ScreenRectangle = {
      val paddingX = -1
      val paddingY = -1
      style.variant match
        case NodeFactory.IOContentVariant.Input =>
          new ScreenRectangle(
            x - Mth.floor(connectorSize / 2D) - 1 - paddingX,
            y + Mth.floor(connectorSize / 2D) + paddingY,
            connectorSize,
            connectorSize
          )
        case NodeFactory.IOContentVariant.Output =>
          new ScreenRectangle(
            x + width - Mth.floor(connectorSize / 2D) + paddingX,
            y + Mth.floor(connectorSize / 2D) + paddingY,
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
      //pGuiGraphics.fill(RenderType.gui(), c.left, c.top, c.right, c.bottom, 100, style.color)
      val color = style.color
      pGuiGraphics.setColor(
        FastColor.ARGB32.red(color) / 255F,
        FastColor.ARGB32.green(color) / 255F,
        FastColor.ARGB32.blue(color) / 255F,
        FastColor.ARGB32.alpha(color) / 255F
      )
      pGuiGraphics.pose().pushPose()
      pGuiGraphics.pose().translate(0, 0, 10)
      pGuiGraphics.blitNineSliced(DanmakuCore.resource("textures/gui/node.png"), c.left, c.top, c.width, c.height, 3, 9, 9, 0, 0)
      pGuiGraphics.pose().popPose()
      pGuiGraphics.setColor(1F, 1F, 1F, 1F)

      pGuiGraphics.flush()
    }

    override def clicked(pMouseX: Double, pMouseY: Double): Boolean =
      active && visible && isMouseOver(pMouseX, pMouseY)

    override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
      defaultButtonNarrationText(pNarrationElementOutput)

    override def onPress(): Unit = ()

    override def setX(pX: Int): Unit = {
      super.setX(pX)
      _connectorRectangle = computeConnectorRectangle
      connection.foreach { conn =>
        val c = connectorRectangle
        style.variant match
          case NodeFactory.IOContentVariant.Input =>
            conn.from = new Vector2d(c.left + (connectorSize / 2D), conn.from.y)
          case NodeFactory.IOContentVariant.Output =>
            conn.to = new Vector2d(c.left + (connectorSize / 2D), conn.to.y)
      }
    }

    override def setY(pY: Int): Unit = {
      super.setY(pY)
      _connectorRectangle = computeConnectorRectangle
      connection.foreach { conn =>
        val c = connectorRectangle
        style.variant match
          case NodeFactory.IOContentVariant.Input =>
            conn.from = new Vector2d(conn.from.x, c.top + Mth.floor(connectorSize / 2D))
          case NodeFactory.IOContentVariant.Output =>
            conn.to = new Vector2d(conn.to.x, c.top + Mth.floor(connectorSize / 2D))
      }
    }
  }
}
