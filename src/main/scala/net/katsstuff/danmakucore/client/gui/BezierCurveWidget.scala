package net.katsstuff.danmakucore.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.katsstuff.danmakucore.client.gui.NodeWidget.NodeIOWidget
import net.katsstuff.danmakucore.util.Bezier
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.util.{FastColor, Mth}
import org.joml.Vector2d

// Info to continue this:
// https://pomax.github.io/bezierinfo/
// https://ciechanow.ski/drawing-bezier-curves/
// https://www.youtube.com/watch?v=aVwxzDHniEw
class BezierCurveWidget(
    _from: Vector2d,
    _to: Vector2d,
    color: Int,
    width: Float,
    snapLocation: (Double, Double) => (Double, Double)
) extends AbstractWidget((_from.x - 1).toInt, (_from.y - 1).toInt, 3, 3, Component.empty) {

  var fromDragging = false
  var toDragging   = false

  var _toWidget: NodeIOWidget | Null   = _
  var _fromWidget: NodeIOWidget | Null = _

  val sizeD2 = 1

  private val fromD: Vector2d = new Vector2d(_from)
  private val toD: Vector2d   = new Vector2d(_to)

  private val control1: Vector2d = new Vector2d()
  private val control2: Vector2d = new Vector2d()

  def toWidget: NodeIOWidget | Null   = _toWidget
  def fromWidget: NodeIOWidget | Null = _fromWidget

  def toWidget_=(widget: NodeIOWidget): Unit = {
    if _toWidget != null then _toWidget.connection = null
    if widget != null then widget.connection = this

    _toWidget = widget
  }
  def fromWidget_=(widget: NodeIOWidget): Unit = {
    if _fromWidget != null then _fromWidget.connection = null
    if widget != null then widget.connection = this

    _fromWidget = widget
  }

  setControls()

  private def setControls(): Unit = {
    control1.y = fromD.y
    control2.y = toD.y

    val distance = 0.4

    val diff = fromD.x - toD.x

    control1.x = if diff < 0 then fromD.x + diff / 2 else Mth.lerp(distance, fromD.x, toD.x)
    control2.x = if diff < 0 then toD.x - diff / 2 else Mth.lerp(1 - distance, fromD.x, toD.x)
  }

  private var rectangles: Seq[(Vector2d, Vector2d, Vector2d, Vector2d)] = computeRectangles

  def from: Vector2d = new Vector2d(fromD)
  def to: Vector2d   = new Vector2d(toD)

  override def setX(pX: Int): Unit = {
    super.setX(pX)
    fromD.x = pX + sizeD2
    setControls()
    rectangles = computeRectangles
  }
  override def setY(pY: Int): Unit = {
    super.setY(pY)
    fromD.y = pY + sizeD2
    setControls()
    rectangles = computeRectangles
  }

  def from_=(from: Vector2d): Unit = {
    super.setX((from.x - 2).toInt)
    super.setY((from.y - 2).toInt)
    this.fromD.x = from.x
    this.fromD.y = from.y

    setControls()
    rectangles = computeRectangles
  }

  def to_=(to: Vector2d): Unit = {
    this.toD.x = to.x
    this.toD.y = to.y
    setControls()
    rectangles = computeRectangles
  }

  private def computeRectangles: Seq[(Vector2d, Vector2d, Vector2d, Vector2d)] = Seq
    .tabulate(31)(i => i / 30.0)
    .map(t => Bezier.cubicBezier2(fromD, control1, control2, toD, t))
    .map { p =>
      val dir = new Vector2d(p.tangent).mul(width / 2)
      val p1  = new Vector2d(p.point).add(dir)
      val p2  = new Vector2d(p.point).sub(dir)
      (p1, p2)
    }
    .sliding(2)
    .map { case Seq((p1, p2), (p3, p4)) =>
      (p1, p2, p3, p4)
    }
    .toSeq

  def mouseOver(point: Vector2d, pMouseX: Double, pMouseY: Double): Boolean =
    pMouseX >= point.x - sizeD2 && pMouseX <= point.x + sizeD2 && pMouseY >= point.y - sizeD2 && pMouseY <= point.y + sizeD2

  override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean =
    mouseOver(fromD, pMouseX, pMouseY) || mouseOver(toD, pMouseX, pMouseY)

  override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
    isHovered = isMouseOver(pMouseX, pMouseY)
    val z = if fromDragging || toDragging then 200F else 0F

    RenderSystem.enableDepthTest()

    val a        = FastColor.ARGB32.alpha(color)
    val r        = FastColor.ARGB32.red(color)
    val g        = FastColor.ARGB32.green(color)
    val b        = FastColor.ARGB32.blue(color)
    val matrix4f = pGuiGraphics.pose.last.pose

    val vertexConsumer = pGuiGraphics.bufferSource.getBuffer(RenderType.gui())

    def vertex(x: Double, y: Double): Unit =
      vertexConsumer.vertex(matrix4f, x.toFloat, y.toFloat, z).color(r, g, b, a).endVertex()

    def vertexPoint(p: Vector2d): Unit = vertex(p.x, p.y)

    rectangles.foreach { case (p1, p2, p3, p4) =>
      vertexPoint(p1)
      vertexPoint(p2)
      vertexPoint(p4)
      vertexPoint(p3)
    }

    def sizedSquare(p: Vector2d): Unit = {
      vertex(p.x - sizeD2, p.y - sizeD2)
      vertex(p.x - sizeD2, p.y + sizeD2)
      vertex(p.x + sizeD2, p.y + sizeD2)
      vertex(p.x + sizeD2, p.y - sizeD2)
    }

    sizedSquare(fromD)
    sizedSquare(toD)

    // pGuiGraphics.bufferSource.endBatch()

    pGuiGraphics.flush()
  }

  override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit = ()

  override def clicked(pMouseX: Double, pMouseY: Double): Boolean =
    isMouseOver(pMouseX, pMouseY)

  override def onClick(pMouseX: Double, pMouseY: Double): Unit = {
    fromDragging = mouseOver(fromD, pMouseX, pMouseY)
    toDragging = mouseOver(toD, pMouseX, pMouseY)
  }

  override def onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double): Unit = {
    val (snappedX, snappedY) = snapLocation(pMouseX, pMouseY)
    
    if fromDragging then fromD.set(snappedX, snappedY)
    if toDragging then toD.set(snappedX, snappedY)

    setControls()
    rectangles = computeRectangles
  }

  override def onRelease(pMouseX: Double, pMouseY: Double): Unit = {
    fromDragging = false
    toDragging = false
  }
}
