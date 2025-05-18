package net.katsstuff.danmakucore.client.gui.widgets

import net.katsstuff.danmakucore.client.gui.widgets.SearchableSelectWidget.SelectSelectionList
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.components.{EditBox, ObjectSelectionList}
import net.minecraft.network.chat.Component

class SearchableSelectWidget[A](
    pX: Int,
    pY: Int,
    pWidth: Int,
    pHeight: Int,
    pMessage: Component,
    options: Seq[A],
    nameOfOption: A => Component
) extends ObjectSelectionListDelegatingWidget[SearchableSelectWidget.SelectOption[A]](
      pX,
      pY,
      pWidth,
      pHeight,
      pMessage
    ):
  private val minecraft: Minecraft = Minecraft.getInstance()

  protected var selectionList: SearchableSelectWidget.SelectSelectionList[A] =
    new SearchableSelectWidget.SelectSelectionList[A](
      minecraft,
      width,
      height,
      pY,
      pY + height,
      minecraft.font.lineHeight + 2,
      options,
      nameOfOption
    )

  def value: Option[A] = Option(selectionList.getSelected).map(_.option)

object SearchableSelectWidget:
  class SelectSelectionList[A](
      pMinecraft: Minecraft,
      pWidth: Int,
      pHeight: Int,
      pY0: Int,
      pY1: Int,
      pItemHeight: Int,
      options: Seq[A],
      nameOfOption: A => Component
  ) extends ObjectSelectionList[SelectOption[A]](pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight):
    setRenderTopAndBottom(false)
    setRenderBackground(false)
    setRenderHeader(true, minecraft.font.lineHeight + 6)
    private val allOptions = options.map(o => new SelectOption(this, o, nameOfOption))
    allOptions.foreach(addEntry)

    private val searchBox =
      new EditBox(minecraft.font, 0, 0, width - 15, minecraft.font.lineHeight + 2, Component.translatable("gui.recipebook.search_hint"))
    searchBox.setHint(Component.translatable("gui.recipebook.search_hint"))
    searchBox.setResponder { s =>
      val selected         = getSelected
      val searchboxFocused = searchBox.isFocused

      clearEntries()
      val newEntries = allOptions
        .filter { p =>
          searchBox.getValue.isEmpty || p.nameOfOption(p.option).toString.toLowerCase.contains(s.toLowerCase)
        }
      newEntries.foreach(addEntry)

      if newEntries.contains(selected) then setSelected(selected)
      if searchboxFocused then searchBox.setFocused(true)
    }
    private var renderMouseX: Int        = -1
    private var renderMouseY: Int        = -1
    private var renderPartialTick: Float = 0

    override def render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit = {
      renderMouseX = pMouseX
      renderMouseY = pMouseY
      renderPartialTick = pPartialTick
      searchBox.setWidth(width - 15)

      super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    override def renderHeader(pGuiGraphics: GuiGraphics, pX: Int, pY: Int): Unit =
      super.renderHeader(pGuiGraphics, pX, pY)
      searchBox.setX(pX + 1)
      searchBox.setY(pY)
      searchBox.render(pGuiGraphics, renderMouseX, renderMouseY, renderPartialTick)

    override def renderBackground(pGuiGraphics: GuiGraphics): Unit =
      pGuiGraphics.fill(x0 + 3, y0, x1, y1, 0xFFA0A0A0)
      pGuiGraphics.fill(x0 + 4, y0 + 1, x1 - 1, y1 - 1, 0xFF000000)
      super.renderBackground(pGuiGraphics)

    override def getRowLeft: Int = x0 + 5

    override def getRowWidth: Int = width - 13

    override def renderSelection(
        pGuiGraphics: GuiGraphics,
        pTop: Int,
        pWidth: Int,
        pHeight: Int,
        pOuterColor: Int,
        pInnerColor: Int
    ): Unit = {
      val xMin = getRowLeft
      val xMax = getRowRight
      pGuiGraphics.fill(xMin, pTop - 2, xMax, pTop + pHeight + 2, pOuterColor)
      pGuiGraphics.fill(xMin + 1, pTop - 1, xMax - 1, pTop + pHeight + 1, 0xFF202020)
    }

    override def getScrollbarPosition: Int = width - 5

    override def keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean = {
      if searchBox.isFocused then searchBox.keyPressed(pKeyCode, pScanCode, pModifiers)
      else super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override def charTyped(pCodePoint: Char, pModifiers: Int): Boolean =
      if searchBox.isFocused then searchBox.charTyped(pCodePoint, pModifiers)
      super.charTyped(pCodePoint, pModifiers)

    override def clickedHeader(pMouseX: Int, pMouseY: Int): Unit =
      this.setFocused(null)
      searchBox.setFocused(true)
      searchBox.onClick(pMouseX, pMouseY)

    override def setFocused(pListener: GuiEventListener): Unit =
      if pListener != searchBox then searchBox.setFocused(false)
      super.setFocused(pListener)

    override def setSelected(pSelected: SelectOption[A]): Unit =
      searchBox.setFocused(false)
      super.setSelected(pSelected)
  end SelectSelectionList

  class SelectOption[A](all: SelectSelectionList[A], val option: A, val nameOfOption: A => Component)
      extends ObjectSelectionList.Entry[SelectOption[A]]:
    override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = {
      all.setSelected(this)
      false
    }

    override def getNarration: Component = nameOfOption(option)

    override def render(
        pGuiGraphics: GuiGraphics,
        pIndex: Int,
        pTop: Int,
        pLeft: Int,
        pWidth: Int,
        pHeight: Int,
        pMouseX: Int,
        pMouseY: Int,
        pHovering: Boolean,
        pPartialTick: Float
    ): Unit = pGuiGraphics.drawString(Minecraft.getInstance().font, nameOfOption(option), pLeft + 1, pTop, 0xFFFFFFFF)
