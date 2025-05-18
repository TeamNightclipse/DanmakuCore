package net.katsstuff.danmakucore.client.gui.screen

import net.minecraft.client.gui.components.tabs.GridLayoutTab
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component

abstract class RepositionTab(component: Component) extends GridLayoutTab(component):
  def reposition(): Unit

  override def doLayout(pRectangle: ScreenRectangle): Unit = {
    super.doLayout(pRectangle)
    layout.setY(layout.getY - 5)
  }
