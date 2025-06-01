package net.katsstuff.danmakucore.client.gui.screen

import scala.collection.mutable

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.gui.widgets.{
  MutableSpacer,
  NodeBackgroundWidget,
  NodeContainer,
  SearchableSelectWidget
}
import net.katsstuff.danmakucore.client.gui.{DanmakuInstantiationNodeFactory, NodeFactory}
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.layouts.{GridLayout, LayoutElement, LayoutSettings}
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class NodeEditorTab(
    screen: DanmakuEditorScreen,
    addWidget: AbstractWidget => Unit,
    removeWidget: AbstractWidget => Unit
) extends RepositionTab(Component.translatable("danmakucore.gui.nodeEditor")):
  private val leftSide  = new GridLayout()
  private val rightSide = new MutableGridLayout()

  leftSide.defaultCellSetting().padding(3).paddingHorizontal(5)
  rightSide.defaultCellSetting().padding(3).paddingHorizontal(5)

  private val container: NodeContainer[DanmakuInstantiationNodeFactory.type] =
    new NodeContainer(
      DanmakuInstantiationNodeFactory,
      0,
      0,
      screen.width - (screen.width / 6) * 2,
      screen.height - 26
    )
  container.newNodeAt(50, 50, container.nodeFactory.NodeType.Input)
  container.newNodeAt(50, 100, container.nodeFactory.NodeType.Input)
  container.newNodeAt(150, 150, container.nodeFactory.NodeType.Output)
  container.newNodeAt(150, 200, container.nodeFactory.NodeType.Math)

  layout.addChild(leftSide, 0, 0)
  layout.addChild(container, 0, 1, layout.newCellSettings().paddingTop(2))
  layout.addChild(rightSide, 0, 2)

  private def addField[A <: AbstractWidget](side: GridLayout, maxWidth: () => Int)(
      name: Component,
      content: A,
      startRow: Int,
      layout: LayoutSettings = side.newCellSettings().paddingBottom(5),
      setMessage: Boolean = true
  ) = {
    val stringLayout = side.newCellSettings().paddingBottom(0)
    if startRow == 0 then stringLayout.paddingTop(5)

    val string = side.addChild(
      new StringWidget(screen.fontInstance.width(name.getVisualOrderText), 9, name, screen.fontInstance),
      startRow,
      0,
      stringLayout
    )

    if setMessage then content.setMessage(name)
    if content.getWidth > maxWidth() then content.setWidth(maxWidth())

    side.addChild(content, startRow + 1, 0, layout)

    NodeEditorTab.Field(content, Some(string), layout.getExposed, maxWidth)
  }
  private def addLeftField[A <: AbstractWidget](
      name: Component,
      content: A,
      startRow: Int,
      layout: LayoutSettings = leftSide.newCellSettings().paddingBottom(5),
      setMessage: Boolean = true
  ): NodeEditorTab.Field[A] = addField(leftSide, () => screen.width / 6)(name, content, startRow, layout, setMessage)

  private def addRightField[A <: AbstractWidget](
      name: Component,
      content: A,
      startRow: Int,
      layout: LayoutSettings = rightSide.newCellSettings().paddingBottom(5),
      setMessage: Boolean = true
  ): NodeEditorTab.Field[A] = addField(rightSide, () => screen.width / 6)(name, content, startRow, layout, setMessage)

  private val nameBox = addLeftField(
    Component.translatable("danmakucore.gui.danmakuEditor.entry.name", ""),
    new EditBox(screen.fontInstance, 0, 0, screen.width / 6, 10, Component.empty),
    0
  )
  private val authorBox = addLeftField(
    Component.translatable("danmakucore.gui.danmakuEditor.entry.author", ""),
    new EditBox(screen.fontInstance, 0, 0, screen.width / 6 - 6, 10, Component.empty),
    2
  )
  private val descriptionBox = addLeftField(
    Component.translatable("danmakucore.gui.danmakuEditor.entry.description", ""),
    new MultiLineEditBox(
      screen.fontInstance,
      0,
      0,
      screen.width / 6 - 12,
      screen.fontInstance.lineHeight * 6 + 10,
      Component.empty,
      Component.empty
    ),
    4,
    leftSide.newCellSettings().paddingRight(12)
  )
  private val formSelect = addLeftField(
    Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.form.name").append(":"),
    new SearchableSelectWidget[String](
      0,
      0,
      screen.width / 6,
      screen.fontInstance.lineHeight * 6 + 10,
      Component.empty,
      Seq(
        "foo",
        "bar",
        "baz",
        "qux",
        "quux",
        "corge",
        "grault",
        "garply",
        "waldo",
        "fred",
        "plugh"
      ),
      s => Component.literal(s)
    ),
    6,
    leftSide.newCellSettings().paddingLeft(2)
  )

  private def leftSpacerHeight =
    Math.max(
      0,
      screen.height - 26 - nameBox.totalHeight - authorBox.totalHeight - descriptionBox.totalHeight - formSelect.totalHeight - 20 + 6
    )

  private val leftSpacer = leftSide.addChild(
    new NodeEditorTab.BackgroundSpacer(screen, screen.width / 6, leftSpacerHeight),
    8,
    0,
    leftSide.newCellSettings().padding(0)
  )

  private val saveButton = NodeEditorTab.Field(
    leftSide.addChild(
      Button
        .builder(Component.translatable("danmakucore.gui.danmakuEditor.save"), _ => {})
        .size(screen.width / 6 - 3, 20)
        .build(),
      9,
      0
    ),
    None,
    leftSide.defaultCellSetting().getExposed,
    () => screen.width / 6
  )

  private val nodeTitle = NodeEditorTab.Field(
    rightSide.addChild(
      new StringWidget(0, 0, Component.empty, screen.fontInstance).alignLeft(),
      0,
      0,
      rightSide.newCellSettings().paddingTop(10)
    ),
    None,
    rightSide.newCellSettings().paddingTop(10).getExposed,
    () => screen.width / 6
  )

  private val currentRightSidebarWidgets: mutable.Buffer[LayoutElement | NodeEditorTab.Field[AbstractWidget]] =
    mutable.Buffer.empty

  private def rightSpacerHeight =
    screen.height - 26 - nodeTitle.totalHeight - currentRightSidebarWidgets.map {
      case e: LayoutElement          => e.getHeight
      case e: NodeEditorTab.Field[_] => e.totalHeight
    }.sum

  private val rightSpacer = rightSide.addChild(
    new NodeEditorTab.BackgroundSpacer(screen, screen.width / 6, screen.height - 26),
    1,
    0,
    rightSide.newCellSettings().padding(0)
  )

  def setRightSidebarFromStyle(style: NodeFactory.NodeStyle): Unit = {
    currentRightSidebarWidgets.foreach {
      case w: AbstractWidget         => removeWidget(w)
      case w: NodeEditorTab.Field[_] => removeWidget(w.contents)
      case _                         =>
    }
    currentRightSidebarWidgets.clear()

    nodeTitle.contents.setMessage(style.title)
    rightSide.clear()
    rightSide.addChild(
      nodeTitle.contents,
      0,
      0,
      rightSide.newCellSettings().paddingTop(10)
    )

    style.sidebarContents.zipWithIndex.foreach { case (c, i) =>
      val layoutSettings = c.layoutSettings(rightSide.defaultCellSetting())
      c.widget match {
        case w: MutableSpacer =>
          w.width = screen.width / 6
          currentRightSidebarWidgets += w

        case w: AbstractWidget =>
          w.setWidth(screen.width / 6)
          addWidget(w)
          val field = NodeEditorTab.Field(
            w,
            None,
            layoutSettings.getExposed,
            () => screen.width / 6
          )
          field.setWidth(screen.width / 6)
          currentRightSidebarWidgets += field
        case w => currentRightSidebarWidgets += w
      }
      rightSide.addChild(c.widget, i + 1, 0, layoutSettings)
    }
    rightSide.addChild(
      rightSpacer,
      1 + style.sidebarContents.length,
      0,
      rightSide.newCellSettings().padding(0)
    )

    rightSpacer.setHeight(rightSpacerHeight)
    rightSide.arrangeElements()
  }

  container.onFocusedChanges = (oldFocused, newFocus) =>
    if oldFocused != newFocus then
      newFocus match
        case Some(value: NodeBackgroundWidget) => setRightSidebarFromStyle(value.style)
        case _                                 =>

  override def reposition(): Unit = {
    container.setHeight(screen.height - 26)
    leftSpacer.setHeight(leftSpacerHeight)
    rightSpacer.setHeight(rightSpacerHeight)

    leftSpacer.setWidth(screen.width / 6)
    rightSpacer.setWidth(screen.width / 6)
    container.setWidth(screen.width - leftSpacer.getWidth - rightSpacer.getWidth)

    nameBox.setWidth(screen.width / 6)
    authorBox.setWidth(screen.width / 6)
    descriptionBox.setWidth(screen.width / 6)
    formSelect.setWidth(screen.width / 6)
    saveButton.setWidth(screen.width / 6 - 10)
    currentRightSidebarWidgets.foreach {
      case w: MutableSpacer          => w.width = screen.width / 6
      case w: AbstractWidget         => w.setWidth(screen.width / 6)
      case w: NodeEditorTab.Field[_] => w.setWidth(screen.width / 6)
      case _                         =>
    }
  }

object NodeEditorTab:
  case class Field[A <: AbstractWidget](
      contents: A,
      label: Option[StringWidget],
      layout: LayoutSettings.LayoutSettingsImpl,
      maxWidth: () => Int
  ):
    lazy val layoutHeight: Int = layout.paddingTop + layout.paddingBottom
    lazy val layoutWidth: Int  = layout.paddingLeft + layout.paddingRight

    def totalWidth: Int  = contents.getWidth + layoutWidth
    def totalHeight: Int = label.fold(0)(_.getHeight + layoutHeight) + contents.getHeight + layoutHeight

    def setWidth(width: Int): Unit =
      val usedWidth = if width + layoutWidth > maxWidth() then maxWidth() - layoutWidth else width
      contents.setWidth(usedWidth)

  private class BackgroundSpacer(screen: DanmakuEditorScreen, _width: Int, _height: Int)
      extends AbstractWidget(0, 0, _width, _height, Component.empty) {
    override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
      val slice   = 3
      val offsetU = 9

      val pose = pGuiGraphics.pose()
      pose.pushPose()
      pose.translate(0, 0, -1000)

      pGuiGraphics.fill(getX, 24, getX + width, screen.height - 2, 0xFF000000)
      pGuiGraphics.setColor(0.5F, 0.5F, 0.5F, 1F)
      pGuiGraphics.blitNineSliced(
        DanmakuCore.resource("textures/gui/node.png"),
        getX,
        24,
        width,
        screen.height - 24 - 2,
        slice,
        slice * 3,
        slice * 3,
        offsetU,
        0
      )
      pGuiGraphics.setColor(1F, 1F, 1F, 1F)
      pose.popPose()
    }

    override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit = ()

    override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = false

    override def mouseScrolled(pMouseX: Double, pMouseY: Double, pDelta: Double): Boolean = false

    override def mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = false

    override def mouseDragged(
        pMouseX: Double,
        pMouseY: Double,
        pButton: Int,
        pDragX: Double,
        pDragY: Double
    ): Boolean = false
  }
