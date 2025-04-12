package net.katsstuff.danmakucore.client.gui.widgets

import java.util.UUID

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

import com.google.common.graph.{GraphBuilder, MutableGraph}
import net.katsstuff.danmakucore.client.gui.{GraphNodeIdentifier, NodeFactory}
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.events.{ContainerEventHandler, GuiEventListener}
import net.minecraft.client.gui.navigation.ScreenRectangle
import org.joml.Vector2d

//noinspection UnstableApiUsage
trait NodeContainer[NF <: NodeFactory](val nodeFactory: NF) extends ContainerEventHandler {

  private val graph: MutableGraph[GraphNodeIdentifier] = GraphBuilder.directed().build()
  private val widgetToNodeIdentifierMap: mutable.Map[NodeWidget | NodeIOWidget, GraphNodeIdentifier] = mutable.Map.empty

  private val globalInfo = nodeFactory.makeGlobalInfo

  protected def addWidgetToNodeContainer(widget: AbstractWidget): AbstractWidget
  protected def removeWidgetFromNodeContainer(widget: AbstractWidget): Unit

  def newNodeAt(x: Int, y: Int, nodeType: nodeFactory.NodeType): Unit = {
    val coreIdentifier: GraphNodeIdentifier.Core = GraphNodeIdentifier.Core(nodeType.identifier, UUID.randomUUID())
    graph.addNode(coreIdentifier)
    val style = nodeType.make(globalInfo)

    val oldWidgets: mutable.Buffer[AbstractWidget] = mutable.Buffer.empty

    val node: NodeWidget = new NodeWidget(
      x = x,
      y = y,
      width = style.defaultWidth, // TODO: Needs to go. Should be derived automatically
      style = style,
      onContentsChange = self =>
        oldWidgets.foreach { w =>
          removeWidgetFromNodeContainer(w)
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

          addWidgetToNodeContainer(w)
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

    node.visitWidgets(removeWidgetFromNodeContainer)
  }

  private def radiusInRectangle(rect: ScreenRectangle, x: Double, y: Double, size: Double): Boolean = {
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

  def snapLocation(x: Double, y: Double): (Double, Double) = {
    val snapRadius = 8

    children.asScala
      .collectFirst {
        case w: NodeIOWidget if radiusInRectangle(w.connectorRectangle, x, y, snapRadius) =>
          val c = w.connectorRectangle
          (c.left + (c.width / 2D), c.top + (c.height / 2D))
      }
      .getOrElse((x, y))
  }

  override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
    children.asScala
      .collectFirst {
        case w: NodeIOWidget if w.mouseClicked(pMouseX, pMouseY, pButton) =>
          val newConnection = w.connection.isEmpty
          if newConnection then
            val c = w.connectorRectangle
            val x = c.left + (c.width / 2D)
            val y = c.top + (c.height / 2D)
            val connection = new BezierCurveWidget(
              _from = new Vector2d(x, y),
              _to = new Vector2d(x, y),
              width = 1,
              color = 0xFFFFFFFF,
              snapLocation = snapLocation
            )
            w.style.variant match
              case NodeFactory.IOContentVariant.Input  => connection.fromWidget = Some(w)
              case NodeFactory.IOContentVariant.Output => connection.toWidget = Some(w)

            addWidgetToNodeContainer(connection)
          end if

          // Should always be set at this point
          val connection = w.connection.get

          w.style.variant match
            case NodeFactory.IOContentVariant.Input =>
              if newConnection then connection.toDragging = true
              else connection.fromDragging = true

            case NodeFactory.IOContentVariant.Output =>
              if newConnection then connection.fromDragging = true
              else connection.toDragging = true
          end match

          setFocused(connection)
          if (pButton == 0) this.setDragging(true)

          true
      }
      .getOrElse(super.mouseClicked(pMouseX, pMouseY, pButton))

  override def mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = {
    this.setDragging(false)

    val toRemove = children.asScala.collect {
      case w: BezierCurveWidget if w.fromDragging || w.toDragging =>
        val fromDragging = w.fromDragging
        w.fromDragging = false
        w.toDragging = false

        val point = if fromDragging then w.from else w.to
        val size  = w.sizeD2

        val io = children.asScala.collectFirst {
          case w2: NodeIOWidget if radiusInRectangle(w2.connectorRectangle, point.x, point.y, size) => w2
        }

        def identifierPair = w.fromWidget.map(widgetToNodeIdentifierMap).zip(w.toWidget.map(widgetToNodeIdentifierMap)).filter(_ != _)

        io match
          case Some(value) =>
            identifierPair.foreach((from, to) => graph.removeEdge(from, to))

            if fromDragging then w.fromWidget = Some(value)
            else w.toWidget = Some(value)

            identifierPair.foreach((from, to) => graph.putEdge(from, to))

            None

          case None =>
            identifierPair.foreach((from, to) => graph.removeEdge(from, to))

            w.fromWidget = None
            w.toWidget = None

            Some(w)
    }.flatten

    toRemove.foreach(removeWidgetFromNodeContainer)

    children.asScala
      .collect {
        case w if w.isMouseOver(pMouseX, pMouseY) => w.mouseReleased(pMouseX, pMouseY, pButton)
      }
      .exists(b => b)
  }
}
