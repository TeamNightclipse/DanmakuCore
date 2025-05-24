package net.katsstuff.danmakucore.client.gui

import java.util.function.Consumer
import scala.jdk.CollectionConverters.*
import net.katsstuff.danmakucore.client.gui.NodeFactory.IOContentVariant
import net.katsstuff.danmakucore.client.gui.widgets.{NodeContainer, NodeIOWidgetSliderInput}
import net.katsstuff.danmakucore.util.Var
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.{AbstractWidget, CycleButton, EditBox}
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.gui.widget.ForgeSlider

sealed trait ContentType[A <: AbstractWidget]:
  type Value
  type SidebarWidget <: AbstractWidget
  def make[NF <: NodeFactory](
      container: NodeContainer[NF],
      label: Component,
      width: Int,
      height: Int
  ): (A, SidebarWidget)

  def getValue(widget: A): Value

  def sync(main: A, side: SidebarWidget): Unit = syncWith(main, side, _ => ())
  def syncWith(main: A, side: SidebarWidget, responder: Value => Unit): Unit

object ContentType:
  case object EditBoxType extends ContentType[EditBox]:
    override type Value         = String
    override type SidebarWidget = EditBox

    override def make[NF <: NodeFactory](
        container: NodeContainer[NF],
        label: Component,
        width: Int,
        height: Int
    ): (EditBox, EditBox) = (
      new EditBox(Minecraft.getInstance.font, 0, 0, width, height, label),
      new EditBox(Minecraft.getInstance.font, 0, 0, width, height, label)
    )

    override def getValue(widget: EditBox): String = widget.getValue

    override def syncWith(main: EditBox, side: EditBox, responder: String => Unit): Unit =
      inline def mkResponder(otherBox: EditBox, inline otherResponder: Consumer[String]): Consumer[String] =
        (v: String) =>
          otherBox.setResponder(null)
          otherBox.setValue(v)
          responder(v)
          otherBox.setResponder(otherResponder)

      lazy val mainResponder: Consumer[String] = mkResponder(side, sideResponder)
      lazy val sideResponder: Consumer[String] = mkResponder(main, mainResponder)

      main.setResponder(mainResponder)
      side.setResponder(sideResponder)

  case class CycleButtonType[A](
      stringifier: A => Component,
      values: Seq[A],
      displayOnlyValue: Boolean = false,
      initialValue: Option[A] = None,
      onValueChange: A => Unit = (_: A) => ()
  ) extends ContentType[CycleButton[A]]:
    override type Value         = A
    override type SidebarWidget = CycleButton[A]

    override def make[NF <: NodeFactory](
        container: NodeContainer[NF],
        label: Component,
        width: Int,
        height: Int
    ): (CycleButton[A], CycleButton[A]) =
      inline def mkResponder(
          inline other: CycleButton[A],
          respond: Var[Boolean],
          otherRespond: Var[Boolean]
      ): CycleButton.OnValueChange[A] =
        (_, v: A) =>
          if respond.value then
            otherRespond.value = false
            other.setValue(v)
            onValueChange(v)
            otherRespond.value = true

      val respondMain = Var(true)
      val respondSide = Var(true)

      lazy val responderMain: CycleButton.OnValueChange[A] = mkResponder(side, respondMain, respondSide)
      lazy val responderSide: CycleButton.OnValueChange[A] = mkResponder(main, respondSide, respondMain)

      val bMain = container.CycleButton.builder(stringifier).withValues(values)
      initialValue.foreach(bMain.withInitialValue)
      if displayOnlyValue then bMain.displayOnlyValue()
      lazy val main = bMain.create(0, 0, width, height, label, responderMain)

      val bSide = CycleButton.builder(a => stringifier(a)).withValues(values.asJavaCollection)
      initialValue.foreach(bSide.withInitialValue)
      if displayOnlyValue then bSide.displayOnlyValue()
      lazy val side = bSide.create(0, 0, width, height, label, responderSide)

      (main, side)

    override def getValue(widget: CycleButton[A]): A = widget.getValue

    override def syncWith(main: CycleButton[A], side: CycleButton[A], responder: A => Unit): Unit = ()

  case class SliderNodeInput(
      style: NodeFactory.IONodeContentStyle,
      identifier: String,
      color: Int,
      minValue: Double = 0,
      maxValue: Double = 1,
      currentValue: Double = 0,
      stepSize: Double = 0,
      precision: Int = 0
  ) extends ContentType[NodeIOWidgetSliderInput]:
    override type Value         = Double
    override type SidebarWidget = ForgeSlider

    override def make[NF <: NodeFactory](
        container: NodeContainer[NF],
        label: Component,
        width: Int,
        height: Int
    ): (NodeIOWidgetSliderInput, SidebarWidget) = {
      val side = new ForgeSlider(0, 0, width, height, label, Component.empty, minValue, maxValue, currentValue, stepSize, precision, true)
      
      val main = new NodeIOWidgetSliderInput(
        style,
        container,
        width,
        minValue,
        maxValue,
        currentValue,
        stepSize,
        precision
      )

      (main, side)
    }


    override def getValue(widget: NodeIOWidgetSliderInput): Double = widget.value

    override def syncWith(main: NodeIOWidgetSliderInput, side: SidebarWidget, responder: Double => Unit): Unit = ???
