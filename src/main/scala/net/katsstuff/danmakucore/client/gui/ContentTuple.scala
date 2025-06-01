package net.katsstuff.danmakucore.client.gui

import scala.util.NotGiven

import net.katsstuff.danmakucore.client.gui.widgets.SliderInputWidget
import net.katsstuff.danmakucore.danmaku.DanmakuInstantiation.VariableType
import net.minecraft.client.gui.components.{AbstractWidget, EditBox}

class ContentTuple[Widget <: AbstractWidget, Info, CT <: ContentType[Widget]](val contentType: CT)(
    val mainContents: Seq[Info],
    val sidebarContents: Seq[Info],
    private[ContentTuple] val mainWidget: Widget,
    private[ContentTuple] val sidebarWidget: contentType.SidebarWidget
):
  contentType.sync(mainWidget, sidebarWidget)

  def setResponder(responder: contentType.Value => Unit): Unit =
    contentType.syncWith(mainWidget, sidebarWidget, responder)

  def value(using NotGiven[contentType.Value =:= Nothing]): contentType.Value = contentType.getValue(mainWidget)

  def setTextColor(color: Int)(using Widget =:= EditBox, contentType.SidebarWidget =:= EditBox): Unit =
    mainWidget.setTextColor(color)
    sidebarWidget.setTextColor(color)

  def setMaxLength(maxLength: Int)(using Widget =:= EditBox, contentType.SidebarWidget =:= EditBox): Unit =
    mainWidget.setMaxLength(maxLength)
    sidebarWidget.setMaxLength(maxLength)

  def fallbackValue[A](varType: VariableType[A])(using Widget =:= SliderInputWidget): A = varType match
    case VariableType.Float => mainWidget.getValue.toFloat
    case VariableType.Int   => mainWidget.getValueInt
