package net.katsstuff.danmakucore.client.gui

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.gui.NodeFactory.IOContentVariant
import net.katsstuff.danmakucore.client.gui.NodeWidget.MutableSpacer
import net.katsstuff.danmakucore.danmaku.DanmakuInstantiation
import net.katsstuff.danmakucore.danmaku.DanmakuInstantiation.VariableType
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.{AbstractWidget, CycleButton, EditBox}
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

object DanmakuInstantiationNodeFactory extends NodeFactory {

  private def spacer(tpe: NodeType, height: Int = 3) = SpacerNodeContentInfo(height, tpe.identifier)

  enum NodeType(val group: Option[String], val identifier: ResourceLocation) extends NodeTypeBase {
    case Input     extends NodeType(Some("IO"), DanmakuCore.resource("input"))
    case Output    extends NodeType(Some("IO"), DanmakuCore.resource("output"))
    case Group     extends NodeType(Some("IO"), DanmakuCore.resource("group"))
    case Operation extends NodeType(Some("IO"), DanmakuCore.resource("group"))

    case Math          extends NodeType(Some("Math"), DanmakuCore.resource("math"))
    case Enumerate     extends NodeType(Some("Math"), DanmakuCore.resource("enumerate"))
    case KnownConstant extends NodeType(Some("Math"), DanmakuCore.resource("known_constant"))
    case Convert       extends NodeType(Some("Math"), DanmakuCore.resource("convert"))
    case Random        extends NodeType(Some("Math"), DanmakuCore.resource("random"))

    override def make: DanmakuInstantiationNodeFactory.NodeInfo = this match
      case NodeType.Input         => new Input
      case NodeType.Output        => new Output
      case NodeType.Group         => new Group
      case NodeType.Operation     => new Operation
      case NodeType.Math          => new Math
      case NodeType.Enumerate     => new Enumerate
      case NodeType.KnownConstant => new KnownConstant
      case NodeType.Convert       => new Convert
      case NodeType.Random        => new Random

    def topColor: Int = group match
      case Some("IO")   => 0xFF00FF00
      case Some("Math") => 0xFF0000FF
      case _            => 0xFFFF0000
  }

  override def allNodeTypes: Seq[NodeType] = NodeType.values.toSeq

  sealed trait NodeInfo(val tpe: NodeType) extends NodeInfoBase {
    protected var listener: () => Unit = () => ()

    override def topColor: Int = tpe.topColor
    override def color: Int    = 0xFFAAAAAA

    override def onContentsChange(listener: () => Unit): Unit = this.listener = listener
  }

  private case object NumberType

  private def tpeToColor(tpe: VariableType[?] | NumberType.type): Int = tpe match
    case VariableType.Float => 0xFF00FF00
    case VariableType.Int   => 0xFF0000FF
    case NumberType         => 0xFF00FFFF

  private def varTpeButtonBuilder = CycleButton
    .builder[VariableType[?]] {
      case VariableType.Float => Component.literal("Float")
      case VariableType.Int   => Component.literal("Int")
    }
    .withValues(VariableType.Float, VariableType.Int)

  private class Input extends NodeInfo(NodeType.Input) {
    var title: Component = Component.literal("Input")
    private val nameBox  = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 10, Component.literal("Name"))
    private val varTpe: CycleButton[VariableType[?]] = varTpeButtonBuilder.create(
      0,
      0,
      50,
      14,
      Component.literal("Type"),
      (_, v: VariableType[?]) => {
        println(s"Changed type to $v")
      }
    )

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(nameBox, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      WrapWidgetNodeContentInfo(varTpe, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Input"),
        tpeToColor(varTpe.getValue),
        IOContentVariant.Output
      )
    )
  }

  private class Output extends NodeInfo(NodeType.Output) {
    var title: Component = Component.literal("Output")
    private val nameBox  = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 10, Component.literal("Name"))

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(nameBox, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(NumberType),
        IOContentVariant.Input
      )
    )
  }

  private class Group extends NodeInfo(NodeType.Group) {
    var title: Component = Component.literal("Group")
    private val nameBox  = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 10, Component.literal("Name"))

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(nameBox, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      ???
    )
  }

  private class Operation extends NodeInfo(NodeType.Operation) {
    var title: Component = Component.literal("Operation")
    private val nameBox  = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 10, Component.literal("Name"))

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(nameBox, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      ???
    )
  }

  private class Math extends NodeInfo(NodeType.Math) {
    var title: Component = Component.literal("Math")
    private val opButton: CycleButton[DanmakuInstantiation.MathOp] = CycleButton
      .builder[DanmakuInstantiation.MathOp] {
        case DanmakuInstantiation.MathOp.Add      => Component.literal("Add")
        case DanmakuInstantiation.MathOp.Subtract => Component.literal("Subtract")
        case DanmakuInstantiation.MathOp.Multiply => Component.literal("Multiply")
        case DanmakuInstantiation.MathOp.Divide   => Component.literal("Divide")
        case DanmakuInstantiation.MathOp.Modulo   => Component.literal("Modulo")
      }
      .withValues(DanmakuInstantiation.MathOp.values*)
      .displayOnlyValue()
      .create(0, 0, 50, 14, Component.literal("Operation"))

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(opButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "a",
        Component.literal("Value A"),
        tpeToColor(NumberType),
        IOContentVariant.Input
      ),
      IONodeContentInfo(
        tpe.identifier,
        "b",
        Component.literal("Value B"),
        tpeToColor(NumberType),
        IOContentVariant.Input
      ),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(NumberType), // TODO
        IOContentVariant.Output
      )
    )
  }

  private class Enumerate extends NodeInfo(NodeType.Enumerate) {
    var title: Component = Component.literal("Enumerate")

    override val contents: Seq[NodeContentInfo] = Seq(
      IONodeContentInfo(
        tpe.identifier,
        "count",
        Component.literal("Count"),
        tpeToColor(VariableType.Int),
        IOContentVariant.Input
      ),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(VariableType.Int),
        IOContentVariant.Output
      )
    )
  }

  private class KnownConstant extends NodeInfo(NodeType.KnownConstant) {
    var title: Component = Component.literal("Known Constant")
    private val constantButton: CycleButton[DanmakuInstantiation.ConstantName] = CycleButton
      .builder[DanmakuInstantiation.ConstantName] {
        case DanmakuInstantiation.ConstantName.Pi  => Component.literal("Pi")
        case DanmakuInstantiation.ConstantName.E   => Component.literal("E")
        case DanmakuInstantiation.ConstantName.Phi => Component.literal("Phi")
      }
      .withValues(DanmakuInstantiation.ConstantName.values*)
      .create(0, 0, 50, 14, Component.literal("Constant"))

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(constantButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(VariableType.Float),
        IOContentVariant.Output
      )
    )
  }

  private class Convert extends NodeInfo(NodeType.Convert) {
    var title: Component = Component.literal("Convert")
    private val fromButton: CycleButton[VariableType[?]] =
      varTpeButtonBuilder.create(0, 0, 50, 14, Component.literal("From"))
    private val toButton: CycleButton[VariableType[?]] =
      varTpeButtonBuilder.create(0, 0, 50, 14, Component.literal("To"))

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(fromButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      WrapWidgetNodeContentInfo(toButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "input",
        Component.literal("Input"),
        tpeToColor(fromButton.getValue),
        IOContentVariant.Input
      ),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(toButton.getValue),
        IOContentVariant.Output
      )
    )
  }

  private class Random extends NodeInfo(NodeType.Random) {
    var title: Component = Component.literal("Random")
    private val tpeButton: CycleButton[VariableType[?]] =
      varTpeButtonBuilder.create(0, 0, 50, 14, Component.literal("Type"))

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(tpeButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "max",
        Component.literal("Min"),
        tpeToColor(tpeButton.getValue),
        IOContentVariant.Input
      ),
      IONodeContentInfo(
        tpe.identifier,
        "max",
        Component.literal("Max"),
        tpeToColor(tpeButton.getValue),
        IOContentVariant.Input
      ),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(tpeButton.getValue),
        IOContentVariant.Output
      )
    )
  }

  sealed trait NodeContentInfo(val coreId: ResourceLocation) extends NodeContentInfoBase
  private case class SpacerNodeContentInfo(height: Int, coreIdLoc: ResourceLocation) extends NodeContentInfo(coreIdLoc) {
    override def widget: MutableSpacer = new MutableSpacer(0, 0, 0, height)
  }
  private case class WrapWidgetNodeContentInfo(
      widget: AbstractWidget,
      coreIdLoc: ResourceLocation,
      layout: LayoutSettings => LayoutSettings = identity
  ) extends NodeContentInfo(coreIdLoc) {

    override def layoutSettings(default: LayoutSettings): LayoutSettings = layout(default)
  }
  private case class IONodeContentInfo(
      coreIdLoc: ResourceLocation,
      identifier: String,
      var title: Component,
      var color: Int,
      variant: IOContentVariant
  ) extends NodeContentInfo(coreIdLoc),
        IONodeContentInfoBase {

    override val widget: AbstractWidget = new NodeWidget.NodeIOWidget(this)
  }

}
