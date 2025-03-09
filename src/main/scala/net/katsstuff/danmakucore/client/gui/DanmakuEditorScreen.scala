package net.katsstuff.danmakucore.client.gui

import scala.compiletime.uninitialized

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.tabs.{TabManager, TabNavigationBar}
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class DanmakuEditorScreen extends Screen(Component.literal("Danmaku editor")), NodeContainer {

  // TODO: Grid layout
  // TODO: Toasts
  // TODO: Make node editor as a widget, and have the tabs be seperate node editor instances
  // TODO: Investigate other layouts

  private val tabManager = new TabManager(addRenderableWidget(_), removeWidget(_))

  private var tabNavigationBar: TabNavigationBar = uninitialized
  private var bottomButtons: GridLayout          = uninitialized

  override protected def addBezierWidget(widget: BezierCurveWidget): BezierCurveWidget = addRenderableWidget(widget)

  override protected def removeBezierWidget(widget: BezierCurveWidget): Unit = removeWidget(widget)

  override def init(): Unit = {
    super.init()
    val node1 = new NodeWidget(
      x = 50,
      y = 50,
      width = 70,
      height = 64,
      topColor = 0xFFFF0000,
      color = 0xFFAAAAAA,
      title = Component.literal("Node 1"),
      extraWidgets = () =>
        Seq(
          NodeWidget.simpleInput(Component.literal("Input 1"), 0xFF00FF00),
          NodeWidget.simpleInput(Component.literal("Input 2"), 0xFF00FF00),
          NodeWidget.simpleOutput(Component.literal("Output 1"), 0xFF0000FF),
          NodeWidget.simpleOutput(Component.literal("Output 2"), 0xFF0000FF)
        )
    )
    val node2 = new NodeWidget(
      x = 150,
      y = 150,
      width = 80,
      height = 64,
      topColor = 0xFFFF00FF,
      color = 0xFFAAAAAA,
      title = Component.literal("Node 2"),
      extraWidgets = () =>
        Seq(
          NodeWidget.simpleInput(Component.literal("Input 1"), 0xFF00FF00),
          NodeWidget.simpleInput(Component.literal("Input 2"), 0xFF00FF00),
          NodeWidget.simpleOutput(Component.literal("Output 1"), 0xFF0000FF),
          NodeWidget.simpleOutput(Component.literal("Output 2"), 0xFF0000FF)
        )
    )

    node1.visitWidgets(v => addRenderableWidget(v))
    node2.visitWidgets(v => addRenderableWidget(v))

    /*
    addRenderableWidget(???)
    tabNavigationBar = TabNavigationBar
      .builder(tabManager, this.width)
      .addTabs(
        ???,
        ???
      )
      .build()
    addRenderableWidget(tabNavigationBar)

    this.bottomButtons = new GridLayout().columnSpacing(10)
    val rows = this.bottomButtons.createRowHelper(2)
    rows.addChild(
      Button
        .builder(
          Component.translatable("danmakucore.gui.nodeEditor.create"),
          _ => ???
        )
        .build
    )
    rows.addChild(
      Button
        .builder(
          CommonComponents.GUI_CANCEL,
          _ => this.onClose()
        )
        .build
    )
    this.bottomButtons.visitWidgets { (widget: AbstractWidget) =>
      widget.setTabOrderGroup(1)
      this.addRenderableWidget(widget)
    }
     */
  }

  override def render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
    renderBackground(pGuiGraphics)
    // println(getFocused)
    super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
  }

  override def repositionElements(): Unit = {
    /*
    if (this.tabNavigationBar != null && this.bottomButtons != null) {
      this.tabNavigationBar.setWidth(this.width)
      this.tabNavigationBar.arrangeElements()
      this.bottomButtons.arrangeElements()
      FrameLayout.centerInRectangle(this.bottomButtons, 0, this.height - 36, this.width, 36)
      val i = this.tabNavigationBar.getRectangle.bottom
      val screenrectangle = new ScreenRectangle(0, i, this.width, this.bottomButtons.getY - i)
      this.tabManager.setTabArea(screenrectangle)
    }
     */
  }

  override def isPauseScreen: Boolean = false

  override def tick(): Unit = {
    // this.tabManager.tickCurrent()
  }
}
