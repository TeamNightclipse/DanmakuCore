package net.katsstuff.danmakucore.client.gui.widgets

import com.mojang.blaze3d.systems.RenderSystem
import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.gui.NodeFactory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.{AbstractButton, AbstractWidget}
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.util.{FastColor, Mth}
import org.joml.Vector2d

import scala.annotation.unused

class NodeIOWidget(val style: NodeFactory.IONodeContentStyle, var maxWidth: Int = 70)
  extends AbstractButton(0, 0, Math.min(style.width, maxWidth), style.height, style.title) {
  def x: Int = getX

  def y: Int = getY

  private val connectorSize = 7

  def setMaxWidth(pWidth: Int): Unit = {
    maxWidth = pWidth
    setWidth(Math.min(style.width, maxWidth))
  }

  override def setMessage(pMessage: Component): Unit = {
    super.setMessage(pMessage)
    style.title = pMessage
    setWidth(Math.min(style.width, maxWidth))
  }

  var connections: Set[BezierCurveWidget] = Set.empty

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

  // noinspection ScalaWeakerAccess
  protected def renderConnector(
    pGuiGraphics: GuiGraphics,
    @unused pMouseX: Int,
    @unused pMouseY: Int,
    @unused pPartialTick: Float
  ): Unit = {
    val c = connectorRectangle
    // pGuiGraphics.fill(RenderType.gui(), c.left, c.top, c.right, c.bottom, 100, style.color)
    val color = style.color
    pGuiGraphics.setColor(
      FastColor.ARGB32.red(color) / 255F,
      FastColor.ARGB32.green(color) / 255F,
      FastColor.ARGB32.blue(color) / 255F,
      FastColor.ARGB32.alpha(color) / 255F
    )
    pGuiGraphics.pose().pushPose()
    pGuiGraphics.pose().translate(0, 0, 10)
    pGuiGraphics.blitNineSliced(
      DanmakuCore.resource("textures/gui/node.png"),
      c.left,
      c.top,
      c.width,
      c.height,
      3,
      9,
      9,
      0,
      0
    )
    pGuiGraphics.pose().popPose()
    pGuiGraphics.setColor(1F, 1F, 1F, 1F)
  }

  // noinspection ScalaWeakerAccess
  protected def renderSideContent(
    pGuiGraphics: GuiGraphics,
    @unused pMouseX: Int,
    @unused pMouseY: Int,
    @unused pPartialTick: Float
  ): Unit = {
    val minecraft = Minecraft.getInstance

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
  }

  override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
    isHovered = isMouseOver(pMouseX, pMouseY)

    RenderSystem.enableBlend()
    RenderSystem.enableDepthTest()
    pGuiGraphics.setColor(1F, 1F, 1F, 1F)

    renderSideContent(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    renderConnector(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

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
    connections.foreach { conn =>
      val c = connectorRectangle
      style.variant match
        case NodeFactory.IOContentVariant.Input =>
          conn.to = new Vector2d(c.left + (connectorSize / 2D), conn.to.y)
        case NodeFactory.IOContentVariant.Output =>
          conn.from = new Vector2d(c.left + (connectorSize / 2D), conn.from.y)
    }
  }

  override def setY(pY: Int): Unit = {
    super.setY(pY)
    _connectorRectangle = computeConnectorRectangle
    connections.foreach { conn =>
      val c = connectorRectangle
      style.variant match
        case NodeFactory.IOContentVariant.Input =>
          conn.to = new Vector2d(conn.to.x, c.top + Mth.floor(connectorSize / 2D))
        case NodeFactory.IOContentVariant.Output =>
          conn.from = new Vector2d(conn.from.x, c.top + Mth.floor(connectorSize / 2D))
    }
  }
}
