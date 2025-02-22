package net.katsstuff.danmakucore.client.gui

import scala.compiletime.uninitialized

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.tabs.{TabManager, TabNavigationBar}
import net.minecraft.client.gui.components.{AbstractWidget, Button}
import net.minecraft.client.gui.layouts.{FrameLayout, GridLayout, SpacerElement}
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.{CommonComponents, Component}

class DanmakuEditorScreen extends Screen(Component.literal("Danmaku editor")) {

  // TODO: Grid layout
  // TODO: Toasts
  // TODO: Make node editor as a widget, and have the tabs be seperate node editor instances
  // TODO: Investigate other layouts

  private val tabManager = new TabManager(addRenderableWidget(_), removeWidget(_))

  private var tabNavigationBar: TabNavigationBar = uninitialized
  private var bottomButtons: GridLayout          = uninitialized

  private var node: GridLayout = uninitialized

  override def init(): Unit = {
    super.init()
    node = new GridLayout().columnSpacing(10)

    val nodeWidth = 70

    val rows = node.createRowHelper(1)
    rows.addChild(
      new NodeHelper.NodeBackgroundWidget(
        0,
        0,
        nodeWidth,
        64,
        0xFFFF0000,
        0xFFAAAAAA,
        Component.literal("Node")
      ) {
        var dragOffsetX: Double = 0
        var dragOffsetY: Double = 0

        override def onClick(pMouseX: Double, pMouseY: Double): Unit = {
          dragOffsetX = pMouseX - node.getX
          dragOffsetY = pMouseY - node.getY
        }

        override def onDrag(pMouseX: Double, pMouseY: Double, pDragX: Double, pDragY: Double): Unit = {
          node.setX((pMouseX - dragOffsetX).toInt)
          node.setY((pMouseY - dragOffsetY).toInt)
        }
      }
    )
    rows.addChild(new SpacerElement(nodeWidth, 3))

    rows.addChild(
      NodeHelper.inputNode(Component.literal("Input 1"), 0xFF00FF00),
      node.newCellSettings().alignHorizontallyLeft()
    )
    rows.addChild(
      NodeHelper.inputNode(Component.literal("Input 2"), 0xFF00FF00),
      node.newCellSettings().alignHorizontallyLeft()
    )
    rows.addChild(new SpacerElement(nodeWidth, 5))
    rows.addChild(
      NodeHelper.outputNode(Component.literal("Output 1"), 0xFF0000FF),
      node.newCellSettings().alignHorizontallyRight()
    )
    rows.addChild(
      NodeHelper.outputNode(Component.literal("Output 2"), 0xFF0000FF),
      node.newCellSettings().alignHorizontallyRight()
    )

    node.setX(50)
    node.setY(50)

    node.arrangeElements()
    node.visitWidgets(v => addRenderableWidget(v))

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

  override def tick(): Unit = ()
  // this.tabManager.tickCurrent()

}
