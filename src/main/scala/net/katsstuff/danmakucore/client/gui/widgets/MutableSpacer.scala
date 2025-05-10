package net.katsstuff.danmakucore.client.gui.widgets

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.LayoutElement

import java.util.function.Consumer
import scala.beans.BeanProperty

class MutableSpacer(@BeanProperty var x: Int, @BeanProperty var y: Int, var width: Int, var height: Int)
  extends LayoutElement {
  override def getWidth: Int = width

  override def getHeight: Int = height

  override def visitWidgets(pConsumer: Consumer[AbstractWidget]): Unit = ()
}
