package net.katsstuff.danmakucore.client.gui.screen

import scala.collection.mutable
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.components.tabs.{TabManager, TabNavigationBar}
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
import net.minecraft.client.gui.{Font, GuiGraphics}
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth

class DanmakuEditorScreen extends Screen(Component.translatable("danmakucore.gui.danmakuEditor")) { screen =>

  protected[screen] val tabManager = new TabManager(addRenderableWidget(_), removeWidget(_))

  private var tabNavigationBar: TabNavigationBar = uninitialized

  protected[screen] val tabs: mutable.Buffer[RepositionTab] = mutable.Buffer()

  def fontInstance: Font = font

  protected[screen] def onTabsChange(reposition: Boolean): Unit = {
    if tabNavigationBar != null then removeWidget(tabNavigationBar)

    val newCurrentTab = Option(tabManager.getCurrentTab).fold(0)(tabs.indexOf)

    tabNavigationBar = TabNavigationBar
      .builder(tabManager, this.width)
      .addTabs(tabs.toSeq*)
      .build()
    addRenderableWidget(tabNavigationBar)
    tabNavigationBar.selectTab(newCurrentTab, false)
    if reposition then repositionElements()
  }

  override def init(): Unit = {
    tabs.clear()
    tabs += new MainOptionsTab(screen)

    onTabsChange(reposition = false)
    repositionElements()
  }

  override def render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit =
    pGuiGraphics.blit(
      CreateWorldScreen.FOOTER_SEPERATOR,
      0,
      this.height - 2,
      0.0F,
      0.0F,
      this.width,
      2,
      32,
      2
    )
    super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

  override def repositionElements(): Unit = {
    if (tabNavigationBar != null) {
      tabs.foreach(_.reposition())
      tabNavigationBar.setWidth(width)
      tabNavigationBar.arrangeElements()
      val i               = tabNavigationBar.getRectangle.bottom
      val screenrectangle = new ScreenRectangle(0, i, width, height)
      tabManager.setTabArea(screenrectangle)
    }
  }

  override def isPauseScreen: Boolean = false

  override def tick(): Unit =
    tabManager.tickCurrent()
}
