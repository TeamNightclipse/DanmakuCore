package net.katsstuff.danmakucore.client.gui.widgets

import java.util
import java.util.function.{BooleanSupplier, Consumer, Function}
import java.util.{Optional, UUID}

import scala.beans.BeanProperty
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*

import com.google.common.graph.{GraphBuilder, MutableGraph}
import net.katsstuff.danmakucore.client.gui.{GraphNodeIdentifier, NodeFactory}
import net.minecraft.Util
import net.minecraft.client.gui.components.events.{AbstractContainerEventHandler, GuiEventListener}
import net.minecraft.client.gui.components.{
  AbstractWidget,
  CycleButton as TopCycleButton,
  Renderable,
  StringWidget as TopStringWidget,
  Tooltip
}
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.narration.{NarratableEntry, NarrationElementOutput, NarrationSupplier}
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.{Font, GuiGraphics}
import net.minecraft.client.{Minecraft, OptionInstance}
import net.minecraft.network.chat.{CommonComponents, Component, MutableComponent}
import net.minecraft.util.Mth
import net.minecraftforge.client.event.ScreenEvent
import org.joml.Vector2d

//Used as reference: https://github.com/MattiDragon/nodeflow/blob/1.21.5/src/client/java/io/github/mattidragon/nodeflow/client/ui/widget/ZoomableAreaWidget.java#L79
//noinspection UnstableApiUsage
class NodeContainer[NF <: NodeFactory](
    val nodeFactory: NF,
    @BeanProperty var x: Int,
    @BeanProperty var y: Int,
    @BeanProperty val width: Int,
    @BeanProperty val height: Int
) extends AbstractContainerEventHandler,
      Renderable,
      LayoutElement,
      NarratableEntry { container =>
  private var zoom: Double                              = 1
  private var offsetX: Double                           = 0
  private var offsetY: Double                           = 0
  private val _children: mutable.Buffer[AbstractWidget] = mutable.Buffer.empty

  override def children(): util.List[_ <: GuiEventListener] = _children.asJava

  private val graph: MutableGraph[GraphNodeIdentifier] = GraphBuilder.directed().build()
  private val widgetToNodeIdentifierMap: mutable.Map[NodeWidget | NodeIOWidget, GraphNodeIdentifier] = mutable.Map.empty

  private val globalInfo = nodeFactory.makeGlobalInfo

  override def getRectangle: ScreenRectangle = super.getRectangle

  override def render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
    pGuiGraphics.enableScissor(x, y, x + width, y + height)

    pGuiGraphics.fillGradient(x, y, x + this.width, y + this.height, -1072689136, -804253680)
    net.minecraftforge.common.MinecraftForge.EVENT_BUS
      .post(new ScreenEvent.BackgroundRendered(Minecraft.getInstance().screen, pGuiGraphics))

    val p = pGuiGraphics.pose()
    p.pushPose()
    p.translate(x.toFloat, y.toFloat, 0)
    p.translate(offsetX, offsetY, 0)
    p.scale(zoom.toFloat, zoom.toFloat, 1)

    _children.foreach(_.render(pGuiGraphics, modMouseX(pMouseX).toInt, modMouseY(pMouseY).toInt, pPartialTick))

    p.popPose()

    pGuiGraphics.disableScissor()
  }

  def modMouseX(x: Double): Double = (x - offsetX - this.x) / zoom
  def modMouseY(y: Double): Double = (y - offsetY - this.y) / zoom

  def reverseModMouseX(x: Double): Double = x * zoom + offsetX + this.x
  def reverseModMouseY(y: Double): Double = y * zoom + offsetY + this.y

  def newNodeAt(x: Int, y: Int, nodeType: nodeFactory.NodeType): Unit = {
    val coreIdentifier: GraphNodeIdentifier.Core = GraphNodeIdentifier.Core(nodeType.identifier, UUID.randomUUID())
    graph.addNode(coreIdentifier)
    val style = nodeType.make(this.asInstanceOf[NodeContainer[nodeFactory.type]], globalInfo)

    val oldWidgets: mutable.Buffer[AbstractWidget] = mutable.Buffer.empty

    val node: NodeWidget = new NodeWidget(
      x = x,
      y = y,
      width = style.defaultWidth, // TODO: Needs to go. Should be derived automatically
      style = style,
      onContentsChange = self =>
        oldWidgets.foreach { w =>
          _children -= w
          w match
            case w: (NodeWidget | NodeIOWidget) =>
              graph.removeNode(widgetToNodeIdentifierMap(w))
              widgetToNodeIdentifierMap.remove(w)
        }

        oldWidgets.clear()
        self.visitWidgets { w =>
          oldWidgets += w
          w match
            case io: NodeIOWidget =>
              println("Node visit C")
              widgetToNodeIdentifierMap.put(
                io,
                GraphNodeIdentifier.IO(
                  coreIdentifier,
                  io.style.asInstanceOf[nodeFactory.IONodeContentInfoBase].identifier,
                  isInput = io.style.variant == NodeFactory.IOContentVariant.Input
                )
              )

            case _ => ()

          _children += w
        }
    )
    widgetToNodeIdentifierMap.put(node, coreIdentifier)
  }

  def removeNode(node: NodeWidget): Unit = {
    val coreIdentifier = widgetToNodeIdentifierMap(node)

    graph.removeNode(coreIdentifier)
    graph.nodes.asScala.foreach {
      case i: GraphNodeIdentifier.IO if i.core == coreIdentifier => graph.removeEdge(i, coreIdentifier)
      case _                                                     =>
    }

    node.visitWidgets(_children -= _)
  }

  private def radiusInRectangle(rect: ScreenRectangle, x: Double, y: Double, size: Double) = {
    val xCond =
      if size > rect.width
      then x - size < rect.left && x + size > rect.right
      else x - size >= rect.left && x + size <= rect.right

    val yCond =
      if size > rect.height
      then y - size < rect.top && y + size > rect.bottom
      else y - size >= rect.top && y + size <= rect.bottom

    xCond && yCond
  }

  override def getChildAt(pMouseX: Double, pMouseY: Double): Optional[GuiEventListener] =
    super.getChildAt(modMouseX(pMouseX), modMouseY(pMouseY))

  override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean =
    pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height

  def snapLocation(x: Double, y: Double): (Double, Double) = {
    val snapRadius = 8

    _children
      .collectFirst {
        case w: NodeIOWidget if radiusInRectangle(w.connectorRectangle, x, y, snapRadius) =>
          val c = w.connectorRectangle
          (c.left + (c.width / 2D), c.top + (c.height / 2D))
      }
      .getOrElse((x, y))
  }

  override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = {
    val moddedMouseX = modMouseX(pMouseX)
    val moddedMouseY = modMouseY(pMouseY)
    _children
      .collectFirst {
        case w: NodeIOWidget if w.mouseClicked(moddedMouseX, moddedMouseY, pButton) =>
          val newConnection = w.style.variant == NodeFactory.IOContentVariant.Output || w.connections.isEmpty
          var connection: BezierCurveWidget | Null = null
          if newConnection then
            val c = w.connectorRectangle
            val x = c.left + (c.width / 2D)
            val y = c.top + (c.height / 2D)
            connection = new BezierCurveWidget(
              _from = new Vector2d(x, y),
              _to = new Vector2d(x, y),
              width = 1,
              color = 0xFFFFFFFF,
              snapLocation = snapLocation
            )
            w.style.variant match
              case NodeFactory.IOContentVariant.Input  => connection.toWidget = Some(w)
              case NodeFactory.IOContentVariant.Output => connection.fromWidget = Some(w)

            _children += connection
          else
            connection = w.connections.head
          end if

          w.style.variant match
            case NodeFactory.IOContentVariant.Input =>
              if newConnection then connection.fromDragging = true
              else connection.toDragging = true

            case NodeFactory.IOContentVariant.Output =>
              connection.toDragging = true
          end match

          setFocused(connection)
          if (pButton == 0) this.setDragging(true)

          true
      }
      .getOrElse(super.mouseClicked(moddedMouseX, moddedMouseY, pButton))

    true
  }

  override def mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = {
    this.setDragging(false)
    val moddedMouseX = modMouseX(pMouseX)
    val moddedMouseY = modMouseY(pMouseY)

    val toRemove = _children.collect {
      case w: BezierCurveWidget if w.fromDragging || w.toDragging =>
        val fromDragging = w.fromDragging
        w.fromDragging = false
        w.toDragging = false

        val point = if fromDragging then w.from else w.to
        val size  = w.sizeD2

        val io = _children.collectFirst {
          case w2: NodeIOWidget if radiusInRectangle(w2.connectorRectangle, point.x, point.y, size) => w2
        }

        def identifierPair =
          w.fromWidget.map(widgetToNodeIdentifierMap).zip(w.toWidget.map(widgetToNodeIdentifierMap)).filter(_ != _)

        io match
          case Some(value) =>
            identifierPair.foreach(graph.removeEdge(_, _))

            val connectionsToRemove = if fromDragging then
              w.fromWidget = Some(value)
              Nil
            else
              val toRemove = value.connections.toSeq
              w.toWidget = Some(value)
              toRemove

            identifierPair.foreach(graph.putEdge(_, _))

            connectionsToRemove

          case None =>
            identifierPair.foreach(graph.removeEdge(_, _))

            w.fromWidget = None
            w.toWidget = None

            Seq(w)
    }.flatten

    toRemove.foreach(_children -= _)

    _children
      .collect {
        case w if w.isMouseOver(moddedMouseX, moddedMouseY) => w.mouseReleased(moddedMouseX, moddedMouseY, pButton)
      }
      .exists(b => b)
  }

  override def mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean = {
    val moddedMouseX = modMouseX(pMouseX)
    val moddedMouseY = modMouseY(pMouseY)

    super.mouseDragged(moddedMouseX, moddedMouseY, pButton, pDragX, pDragY) || locally:
      offsetX += pDragX
      offsetY += pDragY
      true
  }

  override def mouseScrolled(pMouseX: Double, pMouseY: Double, pDelta: Double): Boolean = {
    val moddedMouseX = modMouseX(pMouseX)
    val moddedMouseY = modMouseY(pMouseY)

    getChildAt(pMouseX, pMouseY).toScala.exists(_.mouseScrolled(moddedMouseX, moddedMouseY, pDelta)) || locally:
      val change     = pDelta * 0.05
      val lowerLimit = 0.25
      val upperLimit = 2
      val clampedChange =
        if zoom + change < lowerLimit
        then lowerLimit - zoom
        else if zoom + change > upperLimit
        then upperLimit - zoom
        else change

      zoom += clampedChange
      offsetX -= moddedMouseX * clampedChange
      offsetY -= moddedMouseY * clampedChange
      true
  }

  override def visitWidgets(pConsumer: Consumer[AbstractWidget]): Unit =
    _children.foreach(_.visitWidgets(pConsumer))

  override def narrationPriority(): NarratableEntry.NarrationPriority = NarratableEntry.NarrationPriority.FOCUSED

  override def updateNarration(pNarrationElementOutput: NarrationElementOutput): Unit = getFocused match {
    case w: NarrationSupplier => w.updateNarration(pNarrationElementOutput.nest())
    case _                    => ()
  }

  def renderScrollingString(
      pGuiGraphics: GuiGraphics,
      pFont: Font,
      pText: Component,
      pMinX: Int,
      pMinY: Int,
      pMaxX: Int,
      pMaxY: Int,
      pColor: Int
  ): Unit =
    val i = pFont.width(pText)
    val j = (pMinY + pMaxY - 9) / 2 + 1
    val k = pMaxX - pMinX
    if i > k then
      val l  = i - k
      val d0 = Util.getMillis.toDouble / 1000.0D
      val d1 = Math.max(l.toDouble * 0.5D, 3.0D)
      val d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / 2.0D + 0.5D
      val d3 = Mth.lerp(d2, 0.0D, l.toDouble)

      pGuiGraphics.enableScissor(
        reverseModMouseX(pMinX).toInt,
        reverseModMouseY(pMinY).toInt,
        reverseModMouseX(pMaxX).toInt,
        reverseModMouseY(pMaxY).toInt
      )
      pGuiGraphics.drawString(pFont, pText, pMinX - d3.toInt, j, pColor)
      pGuiGraphics.disableScissor()
    else pGuiGraphics.drawCenteredString(pFont, pText, (pMinX + pMaxX) / 2, j, pColor)
  end renderScrollingString

  trait ContainerRenderScrollingString extends AbstractWidget:
    override def renderScrollingString(pGuiGraphics: GuiGraphics, pFont: Font, pWidth: Int, pColor: Int): Unit =
      val i = this.getX + pWidth
      val j = this.getX + this.getWidth - pWidth
      container.renderScrollingString(
        pGuiGraphics,
        pFont,
        this.getMessage,
        i,
        this.getY,
        j,
        this.getY + this.getHeight,
        pColor
      )
  end ContainerRenderScrollingString

  class StringWidget(pX: Int, pY: Int, pWidth: Int, pHeight: Int, pMessage: Component, pFont: Font)
      extends TopStringWidget(pX, pY, pWidth, pHeight, pMessage, pFont),
        ContainerRenderScrollingString:

    def this(pMessage: Component, pFont: Font) =
      this(0, 0, pFont.width(pMessage.getVisualOrderText), 9, pMessage, pFont)

    def this(pWidth: Int, pHeight: Int, pMessage: Component, pFont: Font) =
      this(0, 0, pWidth, pHeight, pMessage, pFont)
  end StringWidget

  class CycleButton[T](
      pX: Int,
      pY: Int,
      pWidth: Int,
      pHeight: Int,
      pMessage: Component,
      pName: Component,
      pIndex: Int,
      pValue: T,
      pValues: TopCycleButton.ValueListSupplier[T],
      pValueStringifier: Function[T, Component],
      pNarrationProvider: Function[TopCycleButton[T], MutableComponent],
      pOnValueChange: TopCycleButton.OnValueChange[T],
      pTooltipSupplier: OptionInstance.TooltipSupplier[T],
      pDisplayOnlyValue: Boolean
  ) extends TopCycleButton(
        pX,
        pY,
        pWidth,
        pHeight,
        pMessage,
        pName,
        pIndex,
        pValue,
        pValues,
        pValueStringifier,
        pNarrationProvider,
        pOnValueChange,
        pTooltipSupplier,
        pDisplayOnlyValue
      ),
        ContainerRenderScrollingString

  object CycleButton:
    def builder[T](pValueStringifier: T => Component): Builder[T] =
      new Builder[T](pValueStringifier)

    def booleanBuilder(pComponentOn: Component, pComponentOff: Component): Builder[Boolean] =
      new Builder[Boolean](value => if value then pComponentOn else pComponentOff).withValues(true, false)

    def onOffBuilder(): Builder[Boolean] =
      new Builder[Boolean](value => if value then CommonComponents.OPTION_ON else CommonComponents.OPTION_OFF)
        .withValues(true, false)

    def onOffBuilder(pInitialValue: Boolean): Builder[Boolean] =
      onOffBuilder().withInitialValue(pInitialValue)

    // noinspection ConvertExpressionToSAM
    case class Builder[T](
        valueStringifier: T => Component,
        var initialIndex: Int = 0,
        var initialValue: Option[T] = None,
        var tooltipSupplier: OptionInstance.TooltipSupplier[T] = new OptionInstance.TooltipSupplier[T] {
          override def apply(p: T): Tooltip = null
        },
        var narrationProvider: TopCycleButton[T] => MutableComponent =
          (_: TopCycleButton[T]).createDefaultNarrationMessage,
        var values: TopCycleButton.ValueListSupplier[T] =
          TopCycleButton.ValueListSupplier.create((Nil: Seq[T]).asJavaCollection),
        var _displayOnlyValue: Boolean = false
    ):
      def withValues(pValues: Iterable[T]): Builder[T] =
        withValues(TopCycleButton.ValueListSupplier.create(pValues.asJavaCollection))

      def withValues(pValues: T*): Builder[T] =
        withValues(pValues)

      def withValues(pDefaultList: Seq[T], pSelectedList: Seq[T]): Builder[T] =
        withValues(
          TopCycleButton.ValueListSupplier
            .create(TopCycleButton.DEFAULT_ALT_LIST_SELECTOR, pDefaultList.asJava, pSelectedList.asJava)
        )

      def withValues(pAltListSelector: BooleanSupplier, pDefaultList: Seq[T], pSelectedList: Seq[T]): Builder[T] =
        withValues(TopCycleButton.ValueListSupplier.create(pAltListSelector, pDefaultList.asJava, pSelectedList.asJava))

      def withValues(pValues: TopCycleButton.ValueListSupplier[T]): Builder[T] =
        values = pValues
        this

      def withTooltip(pTooltipSupplier: OptionInstance.TooltipSupplier[T]): Builder[T] =
        tooltipSupplier = pTooltipSupplier
        this

      def withInitialValue(pInitialValue: T): Builder[T] =
        initialValue = Some(pInitialValue)
        val i = values.getDefaultList.indexOf(pInitialValue)
        if i != -1 then initialIndex = i
        this

      def withCustomNarration(pNarrationProvider: TopCycleButton[T] => MutableComponent): Builder[T] =
        narrationProvider = pNarrationProvider
        this

      def displayOnlyValue(): Builder[T] =
        _displayOnlyValue = true
        this

      def create(pX: Int, pY: Int, pWidth: Int, pHeight: Int, pName: Component): CycleButton[T] =
        create(pX, pY, pWidth, pHeight, pName, (_: TopCycleButton[T], _: T) => ())

      def create(
          pX: Int,
          pY: Int,
          pWidth: Int,
          pHeight: Int,
          pName: Component,
          pOnValueChange: TopCycleButton.OnValueChange[T]
      ): CycleButton[T] =
        val list = values.getDefaultList
        if list.isEmpty then throw IllegalStateException("No values for cycle button")
        val t          = initialValue.getOrElse(list.get(initialIndex))
        val component  = valueStringifier(t)
        val component1 = if _displayOnlyValue then component else CommonComponents.optionNameValue(pName, component)
        CycleButton(
          pX,
          pY,
          pWidth,
          pHeight,
          component1,
          pName,
          initialIndex,
          t,
          values,
          (t: T) => valueStringifier(t),
          (b: TopCycleButton[T]) => narrationProvider(b),
          pOnValueChange,
          tooltipSupplier,
          _displayOnlyValue
        )
  end CycleButton
}
