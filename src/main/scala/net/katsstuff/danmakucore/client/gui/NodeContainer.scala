package net.katsstuff.danmakucore.client.gui

import scala.jdk.CollectionConverters.*

import net.katsstuff.danmakucore.client.gui.NodeWidget.{IOWidgetVariant, NodeIOWidget}
import net.minecraft.client.gui.components.events.{ContainerEventHandler, GuiEventListener}
import net.minecraft.client.gui.navigation.ScreenRectangle
import org.joml.Vector2d

trait NodeContainer extends ContainerEventHandler {

  protected def addBezierWidget(widget: BezierCurveWidget): BezierCurveWidget

  protected def removeBezierWidget(widget: BezierCurveWidget): Unit

  private def radiusInRectangle(rect: ScreenRectangle, x: Double, y: Double, size: Double): Boolean = {
    val xCond = if size > rect.width
      then x - size < rect.left && x + size > rect.right
    else x - size >= rect.left && x + size <= rect.right

    val yCond = if size > rect.height
      then y - size < rect.top && y + size > rect.bottom
    else y - size >= rect.top && y + size <= rect.bottom

    xCond && yCond
  }

  def snapLocation(x: Double, y: Double): (Double, Double) = {
    val snapRadius = 7

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
          val newConnection = w.connection == null
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
            w.variant match
              case IOWidgetVariant.Input  => connection.fromWidget = w
              case IOWidgetVariant.Output => connection.toWidget = w

            addBezierWidget(connection)
          end if

          w.variant match
            case IOWidgetVariant.Input =>
              if newConnection then w.connection.toDragging = true
              else w.connection.fromDragging = true

            case IOWidgetVariant.Output =>
              if newConnection then w.connection.fromDragging = true
              else w.connection.toDragging = true
          end match
          setFocused(w.connection)
          if (pButton == 0) this.setDragging(true)

          true
      }
      .getOrElse(super.mouseClicked(pMouseX, pMouseY, pButton))

  override def mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = {
    this.setDragging(false)

    val toRemove = children.asScala.collect {
      case w: BezierCurveWidget if w.fromDragging || w.toDragging =>
        val fromDragging = w.fromDragging

        val point = if fromDragging then w.from else w.to
        val size  = w.sizeD2

        val io = children.asScala.collectFirst {
          case w2: NodeIOWidget if radiusInRectangle(w2.connectorRectangle, point.x, point.y, size) => w2
        }

        io match
          case Some(value) =>
            if fromDragging then w.fromWidget = value
            else w.toWidget = value
            None

          case None =>
            w.fromWidget = null
            w.toWidget = null

            Some(w)
    }.flatten

    toRemove.foreach(removeBezierWidget)

    children.asScala
      .collect {
        case w if w.isMouseOver(pMouseX, pMouseY) => w.mouseReleased(pMouseX, pMouseY, pButton)
      }
      .exists(b => b)
  }
}
