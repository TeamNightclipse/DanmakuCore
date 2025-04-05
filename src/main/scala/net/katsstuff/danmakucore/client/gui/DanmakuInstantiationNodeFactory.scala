package net.katsstuff.danmakucore.client.gui

import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*

import com.google.common.graph.{Graph, Traverser, ValueGraph, ValueGraphBuilder}
import com.mojang.logging.LogUtils
import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.gui
import net.katsstuff.danmakucore.client.gui.NodeFactory.IOContentVariant
import net.katsstuff.danmakucore.client.gui.NodeWidget.MutableSpacer
import net.katsstuff.danmakucore.danmaku.DanmakuInstantiation.VariableType
import net.katsstuff.danmakucore.danmaku.{DanmakuInstantiation, DanmakuInstantiations}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.{AbstractWidget, CycleButton, EditBox}
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

object DanmakuInstantiationNodeFactory extends NodeFactory {

  private def spacer(tpe: NodeType, height: Int = 3) = SpacerNodeContentInfo(height, tpe.identifier)

  class GlobalInfo extends GlobalInfoBase {
    private val _invalidInfos: mutable.Buffer[NodeInfo] = mutable.Buffer.empty

    override def invalidInfos: Seq[NodeInfo] = _invalidInfos.toSeq

    def markValidity(info: NodeInfo, valid: Boolean): Unit =
      if valid then _invalidInfos -= info
      else if !_invalidInfos.contains(info) then _invalidInfos += info

    private val _groups: mutable.Map[String, DanmakuInstantiation] = mutable.Map.empty

    def groups: Map[String, DanmakuInstantiation] = _groups.toMap

    def addGroup(name: String, instantiation: DanmakuInstantiation): Unit =
      _groups += name -> instantiation

    def removeGroup(name: String): Unit = _groups -= name

    def getGroup(name: String): Option[DanmakuInstantiation] = _groups.get(name)
  }

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

    override def make(globalInfo: GlobalInfo): DanmakuInstantiationNodeFactory.NodeInfo = this match
      case NodeType.Input         => new Input(globalInfo)
      case NodeType.Output        => new Output(globalInfo)
      case NodeType.Group         => new Group(globalInfo)
      case NodeType.Operation     => new Operation(globalInfo)
      case NodeType.Math          => new Math(globalInfo)
      case NodeType.Enumerate     => new Enumerate(globalInfo)
      case NodeType.KnownConstant => new KnownConstant(globalInfo)
      case NodeType.Convert       => new Convert(globalInfo)
      case NodeType.Random        => new Random(globalInfo)

    def topColor: Int = group match
      case Some("IO")   => 0xFF00FF00
      case Some("Math") => 0xFF0000FF
      case _            => 0xFFFF0000
  }

  override def makeGlobalInfo: GlobalInfo = new GlobalInfo

  override def allNodeTypes: Seq[NodeType] = NodeType.values.toSeq

  sealed trait NodeInfo(val globalInfo: GlobalInfo, val tpe: NodeType) extends NodeInfoBase {
    protected var listener: () => Unit = () => ()

    override def topColor: Int     = tpe.topColor
    override def color: Int        = 0xFFAAAAAA
    override def defaultWidth: Int = 70

    override def onContentsChange(listener: () => Unit): Unit = this.listener = listener

    def markValidity(valid: Boolean): Unit = globalInfo.markValidity(this, valid)
  }

  sealed trait GraphNumberType { this: GraphType =>
    def toVariableType: VariableType[_] = this match
      case GraphType.Float => VariableType.Float
      case GraphType.Int   => VariableType.Int

    inline def asGraphType: GraphType = this
  }
  object GraphNumberType {
    def fromVarType(tpe: VariableType[_]): GraphNumberType = tpe match
      case VariableType.Float => GraphType.Float
      case VariableType.Int   => GraphType.Int
  }

  enum GraphType {
    case Int   extends GraphType, GraphNumberType
    case Float extends GraphType, GraphNumberType
    case Number
  }

  private def tpeToColor(tpe: GraphType): Int = tpe match
    case GraphType.Float  => 0xFF00FF00
    case GraphType.Int    => 0xFF0000FF
    case GraphType.Number => 0xFF00FFFF

  private def varTpeNumberButtonBuilder: CycleButton.Builder[GraphNumberType] = CycleButton
    .builder[GraphNumberType] {
      case GraphType.Float => Component.literal("Float")
      case GraphType.Int   => Component.literal("Int")
    }
    .withValues(GraphType.Float, GraphType.Int)

  // noinspection UnstableApiUsage
  private def typeGraph(
      graph: Graph[GraphNodeIdentifier],
      nodes: Map[GraphNodeIdentifier.Core, NodeInfo]
  ): ValueGraph[GraphNodeIdentifier, GraphType] = {
    val builder = ValueGraphBuilder
      .directed()
      .expectedNodeCount(graph.nodes.size)
      .build[GraphNodeIdentifier, GraphType]()

    graph.nodes.forEach(n => builder.addNode(n))

    val topoSort = Traverser
      .forGraph(graph)
      .depthFirstPostOrder(graph.nodes.asScala.filter(n => graph.predecessors(n).isEmpty).asJava)
      .asScala
      .toSeq
      .reverse

    topoSort.foreach {
      case GraphNodeIdentifier.Core(_, _) => ()
      case id @ GraphNodeIdentifier.IO(core, ioId, isInput) =>
        core.identifier match {
          case NodeType.Input.identifier =>
            builder.putEdgeValue(core, id, nodes(core).asInstanceOf[Input].graphType.asGraphType)

          case NodeType.Output.identifier =>
            builder.putEdgeValue(id, core, GraphType.Number)

          case NodeType.Group.identifier | NodeType.Operation.identifier =>
            val node = nodes(core).asInstanceOf[OtherInstantiationReferencingNodeInfo]
            node.operation.foreach { instantiation =>
              val node =
                if isInput then instantiation.inputs.find(_.name == ioId).map(_.tpe)
                else instantiation.outputs.find(_._1 == ioId).map(_._2.tpe)

              node.foreach(tpe => builder.putEdgeValue(core, id, GraphNumberType.fromVarType(tpe).asGraphType))
            }

          case NodeType.Math.identifier =>
            ioId match
              case "a" => builder.putEdgeValue(id, core, GraphType.Number)
              case "b" => builder.putEdgeValue(id, core, GraphType.Number)
              case "output" =>
                val aType = builder.edgeValue(GraphNodeIdentifier.IO(core, "a", isInput = true), core).toScala
                val bType = builder.edgeValue(GraphNodeIdentifier.IO(core, "b", isInput = true), core).toScala

                val tpe = Seq(aType, bType).flatten.distinct match {
                  case Seq(_, _) => GraphType.Float
                  case Seq(a)    => a
                  case Seq()     => GraphType.Number
                }
                builder.putEdgeValue(core, id, tpe)

          case NodeType.Enumerate.identifier =>
            ioId match
              case "count" =>
                builder.putEdgeValue(id, core, GraphType.Int)

              case "output" => builder.putEdgeValue(core, id, GraphType.Int)

          case NodeType.KnownConstant.identifier =>
            builder.putEdgeValue(core, id, GraphType.Float)

          case NodeType.Convert.identifier =>
            val node = nodes(core).asInstanceOf[Convert]

            ioId match
              case "input"  => builder.putEdgeValue(id, core, node.fromType.asGraphType)
              case "output" => builder.putEdgeValue(core, id, node.toType.asGraphType)

          case NodeType.Random.identifier =>
            val node = nodes(core).asInstanceOf[Random]

            ioId match
              case "min" | "max" => builder.putEdgeValue(id, core, node.graphType.asGraphType)
              case "output"      => builder.putEdgeValue(core, id, node.graphType.asGraphType)
        }

      case id @ GraphNodeIdentifier.Misc(from, to) =>
        builder.edgeValue(from, id).toScala.foreach { e =>
          builder.putEdgeValue(id, to, e)
        }
    }

    // Once we have filled in all the easy type, we let the types "flow" into the remaining edges
    var iterationsWithNoProgress = 0
    val edges                    = graph.edges.asScala.to(mutable.Queue)
    while edges.nonEmpty && iterationsWithNoProgress < edges.size + 1 do
      val edge = edges.dequeue()
      builder.edgeValue(edge).toScala match
        case Some(_) =>
          iterationsWithNoProgress = 0

        case None =>
          builder
            .predecessors(edge.nodeU())
            .asScala
            .headOption
            .flatMap(n => builder.edgeValue(n, edge.nodeU()).toScala) match
            case Some(value) =>
              iterationsWithNoProgress = 0
              builder.putEdgeValue(edge, value)
            case None =>
              iterationsWithNoProgress += 1
              edges.enqueue(edge)
    end while

    if edges.nonEmpty then
      LogUtils.getLogger.warn(
        s"Could not resolve all types in the graph. Remaining edges: ${edges.map(_.toString).mkString(", ")}"
      )

    val finalBuilder = ValueGraphBuilder
      .from(builder)
      .expectedNodeCount(builder.nodes.size)
      .immutable[GraphNodeIdentifier, GraphType]()

    builder.nodes.forEach(n => finalBuilder.addNode(n))
    builder.edges().forEach(e => finalBuilder.putEdgeValue(e, builder.edgeValue(e).get))

    finalBuilder.build()
  }

  // noinspection DuplicatedCode,UnstableApiUsage
  def resolveTypes(
      typeGraph: ValueGraph[GraphNodeIdentifier, GraphType]
  ): ValueGraph[GraphNodeIdentifier, GraphNumberType] = {
    val builder = ValueGraphBuilder.directed().build[GraphNodeIdentifier, GraphNumberType]()

    typeGraph.nodes.forEach(n => builder.addNode(n))

    val edges = typeGraph.edges.asScala.to(mutable.Queue)

    edges.dequeueAll { edge =>
      typeGraph.edgeValue(edge).get match
        case t: GraphNumberType =>
          builder.putEdgeValue(edge, t)
          true
        case _ => false
    }

    var iterationsWithNoProgress = 0
    while edges.nonEmpty && iterationsWithNoProgress < edges.size + 1 do
      val edge = edges.dequeue()
      typeGraph.edgeValue(edge).get match
        case t: GraphNumberType =>
          builder.putEdgeValue(edge, t)
          iterationsWithNoProgress = 0
        case GraphType.Number =>
          val predecessors = typeGraph.predecessors(edge.nodeU()).asScala.toSeq
          val successors   = typeGraph.successors(edge.nodeV()).asScala.toSeq

          val preTypes = predecessors.flatMap(pre =>
            builder
              .edgeValue(pre, edge.nodeU())
              .toScala
              .map(_.asGraphType)
              .orElse(typeGraph.edgeValue(pre, edge.nodeU()).toScala)
          )
          val sucTypes = successors.flatMap(suc =>
            builder
              .edgeValue(edge.nodeV(), suc)
              .toScala
              .map(_.asGraphType)
              .orElse(typeGraph.edgeValue(edge.nodeV(), suc).toScala)
          )

          val types = (preTypes ++ sucTypes).filter(_ != GraphType.Number)
          types match
            case Seq(t: GraphNumberType) =>
              iterationsWithNoProgress = 0
              builder.putEdgeValue(edge, t)

            case Seq() =>
              iterationsWithNoProgress += 1
              edges.enqueue(edge)
            case ts =>
              val floatTypes = ts.count(_ == GraphType.Float)
              val intTypes   = ts.count(_ == GraphType.Int)

              iterationsWithNoProgress = 0
              if floatTypes > intTypes then builder.putEdgeValue(edge, GraphType.Float)
              else builder.putEdgeValue(edge, GraphType.Int)
    end while

    val finalBuilder = ValueGraphBuilder
      .from(builder)
      .expectedNodeCount(builder.nodes.size)
      .immutable[GraphNodeIdentifier, GraphNumberType]()

    builder.nodes.forEach(n => finalBuilder.addNode(n))
    builder.edges().forEach(e => finalBuilder.putEdgeValue(e, builder.edgeValue(e).get))

    finalBuilder.build()
  }

  override type RepresentedObject = DanmakuInstantiation
  // noinspection UnstableApiUsage
  override def buildObject(
      globalInfo: GlobalInfo,
      graph: Graph[GraphNodeIdentifier],
      nodes: Map[GraphNodeIdentifier.Core, NodeInfo]
  ): DanmakuInstantiation = {

    val types = resolveTypes(typeGraph(graph, nodes))

    def predecessor(id: GraphNodeIdentifier) = {
      val predecessors = graph.predecessors(id)
      if (predecessors.size > 1) {
        throw new IllegalStateException(s"Node $id has more than one predecessor")
      }

      if (predecessors.isEmpty) None
      else Some(predecessors.iterator().next())
    }

    def predecessorIn(id: GraphNodeIdentifier.IO): Option[GraphNodeIdentifier.IO] = {
      predecessor(id).flatMap {
        case GraphNodeIdentifier.Core(_, _)       => throw new IllegalStateException(s"Node $id has core predecessor")
        case io @ GraphNodeIdentifier.IO(_, _, _) => Some(io)
        case GraphNodeIdentifier.Misc(from, _)    => predecessorIn(from)
      }
    }

    def inputValue[A](id: GraphNodeIdentifier.IO, fallbackValue: A, fallbackType: VariableType[A]) =
      id.id -> predecessorIn(id).fold(DanmakuInstantiation.Value.Constant(fallbackValue, fallbackType)) { pred =>
        val tpe = types.edgeValue(pred, id).toScala.getOrElse(sys.error(s"Could not find type for $id"))
        DanmakuInstantiation.Value.FromVariable(
          pred.core.uuid.toString,
          pred.id,
          tpe.toVariableType
        )
      }

    DanmakuInstantiation(
      inputs = graph
        .nodes()
        .asScala
        .collect { case id @ GraphNodeIdentifier.Core(NodeType.Input.identifier, _) =>
          val node = nodes(id).asInstanceOf[Input]

          inline def mk[A](varType: VariableType[A]) =
            DanmakuInstantiation.Input(node.name, varType, default = varType.castFloat(node.defaultValue))

          mk(node.graphType.toVariableType)
        }
        .toSeq,
      operations = graph
        .nodes()
        .asScala
        .collect {
          case id @ GraphNodeIdentifier.Core(NodeType.Group.identifier | NodeType.Operation.identifier, uuid) =>
            val node = nodes(id).asInstanceOf[OtherInstantiationReferencingNodeInfo]

            DanmakuInstantiation
              .Operation(
                uuid.toString,
                node match
                  case group: Group => DanmakuInstantiation.OperationIdentifier.Group(group.groupName)
                  case operation: Operation =>
                    DanmakuInstantiation.OperationIdentifier.NamedOperation(operation.operationName),
                inputs = node.operation.toSeq.flatMap { op =>
                  op.inputs.map(i =>
                    inputValue(GraphNodeIdentifier.IO(id, i.name, isInput = true), fallbackValue = ???, i.tpe)
                  )
                }.toMap
              )

          case id @ GraphNodeIdentifier.Core(NodeType.Math.identifier, uuid) =>
            val node       = nodes(id).asInstanceOf[Math]
            val outId      = GraphNodeIdentifier.IO(id, "output", isInput = false)
            val graphTypes = types.predecessors(outId).asScala.flatMap(from => types.edgeValue(from, outId).toScala)
            val tpe =
              if graphTypes.contains(GraphType.Float) || graphTypes.isEmpty then GraphType.Float.toVariableType
              else GraphType.Int.toVariableType

            DanmakuInstantiation
              .Operation(
                uuid.toString,
                DanmakuInstantiation.OperationIdentifier
                  .FundamentalOperation(DanmakuInstantiation.FundamentalOp.Math(node.mathOp)),
                Map(
                  inputValue(
                    GraphNodeIdentifier.IO(id, "a", isInput = true),
                    fallbackValue = node.aInput.fallbackValue(tpe),
                    fallbackType = tpe
                  ),
                  inputValue(
                    GraphNodeIdentifier.IO(id, "b", isInput = true),
                    fallbackValue = node.bInput.fallbackValue(tpe),
                    fallbackType = tpe
                  )
                )
              )

          case id @ GraphNodeIdentifier.Core(NodeType.Enumerate.identifier, uuid) =>
            val node = nodes(id).asInstanceOf[Enumerate]

            DanmakuInstantiation.Operation(
              uuid.toString,
              DanmakuInstantiation.OperationIdentifier
                .FundamentalOperation(DanmakuInstantiation.FundamentalOp.Enumerate),
              Map(
                inputValue(
                  GraphNodeIdentifier.IO(id, "count", isInput = true),
                  fallbackValue = node.countInput.fallbackValue(VariableType.Int),
                  VariableType.Int
                )
              )
            )

          case id @ GraphNodeIdentifier.Core(NodeType.KnownConstant.identifier, uuid) =>
            val node = nodes(id).asInstanceOf[KnownConstant]

            DanmakuInstantiation.Operation(
              uuid.toString,
              DanmakuInstantiation.OperationIdentifier
                .FundamentalOperation(DanmakuInstantiation.FundamentalOp.KnownConstant(node.constant)),
              Map.empty
            )

          case id @ GraphNodeIdentifier.Core(NodeType.Convert.identifier, uuid) =>
            val node        = nodes(id).asInstanceOf[Convert]
            val fromVarType = node.fromType.toVariableType

            DanmakuInstantiation.Operation(
              uuid.toString,
              DanmakuInstantiation.OperationIdentifier.FundamentalOperation(
                DanmakuInstantiation.FundamentalOp.Convert(fromVarType, node.toType.toVariableType)
              ),
              Map(
                inputValue(
                  GraphNodeIdentifier.IO(id, "input", isInput = true),
                  fallbackValue = node.input.fallbackValue(fromVarType),
                  fromVarType
                )
              )
            )

          case id @ GraphNodeIdentifier.Core(NodeType.Random.identifier, uuid) =>
            val node    = nodes(id).asInstanceOf[Random]
            val varType = node.graphType.toVariableType

            DanmakuInstantiation.Operation(
              uuid.toString,
              DanmakuInstantiation.OperationIdentifier
                .FundamentalOperation(DanmakuInstantiation.FundamentalOp.Random(varType)),
              Map(
                inputValue(
                  GraphNodeIdentifier.IO(id, "min", isInput = true),
                  fallbackValue = node.minInput.fallbackValue(varType),
                  varType
                ),
                inputValue(
                  GraphNodeIdentifier.IO(id, "max", isInput = true),
                  fallbackValue = node.maxInput.fallbackValue(varType),
                  varType
                )
              )
            )
        }
        .toSeq,
      outputs = graph
        .nodes()
        .asScala
        .collect { case id @ GraphNodeIdentifier.Core(NodeType.Output.identifier, _) =>
          val node = nodes(id).asInstanceOf[Output]

          predecessorIn(GraphNodeIdentifier.IO(id, "output", isInput = true)) match
            case Some(pred) =>
              val value: DanmakuInstantiation.Value.FromVariable =
                DanmakuInstantiation.Value.FromVariable(
                  pred.core.uuid.toString,
                  pred.id,
                  types
                    .edgeValue(pred, id)
                    .toScala
                    .getOrElse(sys.error(s"Could not find type for output ${pred.core}"))
                    .toVariableType
                )
              node.name -> value
            case None => throw new IllegalStateException(s"Output node $id has no predecessor")
        }
        .toMap,
      groups = globalInfo.groups,
      form = ???
    )
  }

  private class Input(globalInfo: GlobalInfo) extends NodeInfo(globalInfo, NodeType.Input) {
    var title: Component   = Component.literal("Input")
    private val nameBox    = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 10, Component.literal("Name"))
    private val defaultBox = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 10, Component.literal("Default"))
    private val varTypeButton: CycleButton[GraphNumberType] = varTpeNumberButtonBuilder.create(
      0,
      0,
      50,
      14,
      Component.literal("Type")
    )

    def name: String = nameBox.getValue

    def graphType: GraphNumberType = varTypeButton.getValue
    defaultBox.setResponder { str =>
      val valid = graphType match
        case GraphType.Int   => str.toIntOption.isDefined
        case GraphType.Float => str.toFloatOption.isDefined

      defaultBox.setTextColor(if valid then 0xFFE0E0E0 else 0xFFFF0000)
      markValidity(valid)
    }

    def defaultValue: Float = defaultBox.getValue.toFloatOption.getOrElse(0F)

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(nameBox, tpe.identifier, _.copy().alignHorizontallyCenter()),
      WrapWidgetNodeContentInfo(defaultBox, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      WrapWidgetNodeContentInfo(varTypeButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Input"),
        tpeToColor(graphType.asGraphType),
        IOContentVariant.Output
      )
    )
  }

  private class Output(globalInfo: GlobalInfo) extends NodeInfo(globalInfo, NodeType.Output) {
    var title: Component = Component.literal("Output")
    private val nameBox  = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 10, Component.literal("Name"))

    def name: String = nameBox.getValue

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(nameBox, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(GraphType.Number),
        IOContentVariant.Input
      )
    )
  }

  sealed abstract private class OtherInstantiationReferencingNodeInfo(globalInfo: GlobalInfo, tpe: NodeType)
      extends NodeInfo(globalInfo, tpe) {
    protected val nameBox = new EditBox(Minecraft.getInstance().font, 0, 0, 50, 10, Component.literal("Name"))
    nameBox.setMaxLength(48)
    private var updateListener: () => Unit = () => ()

    private def fixedContents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(nameBox, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe)
    )

    private var dynamicContents                              = Seq.empty[NodeContentInfo]
    private var _lastOperation: Option[DanmakuInstantiation] = None
    def operation: Option[DanmakuInstantiation]              = _lastOperation

    nameBox.setResponder { _ =>
      val op = getInstantiation
      if _lastOperation != op then
        _lastOperation = op
        op match
          case Some(value) =>
            val newInputs = value.inputs.map { input =>
              //TODO: Let the instantiation specify min and max value here
              IONodeContentInfoWithSliderFallback(
                tpe.identifier,
                input.name,
                Component.literal(input.name),
                tpeToColor(GraphNumberType.fromVarType(input.tpe).asGraphType),
                IOContentVariant.Input,
                currentValue = input.default.asInstanceOf[Double]
              )
            }
            val newOutputs = value.outputs.map { case (name, output) =>
              IONodeContentInfo(
                tpe.identifier,
                name,
                Component.literal(name),
                tpeToColor(GraphNumberType.fromVarType(output.tpe).asGraphType),
                IOContentVariant.Output
              )
            }

            dynamicContents = newInputs ++ Seq(spacer(tpe)).filter(_ => newInputs.nonEmpty) ++ newOutputs

          case None => dynamicContents = Nil

        listener()
      end if
    }

    protected def getInstantiation: Option[DanmakuInstantiation]

    override val contents: Seq[NodeContentInfo] = fixedContents ++ dynamicContents

    override def onContentsChange(listener: () => Unit): Unit = updateListener = listener
  }

  private class Group(globalInfo: GlobalInfo)
      extends OtherInstantiationReferencingNodeInfo(globalInfo, NodeType.Group) {
    var title: Component = Component.literal("Group")

    def groupName: String = nameBox.getValue

    override protected def getInstantiation: Option[DanmakuInstantiation] = globalInfo.getGroup(groupName)
  }

  private class Operation(globalInfo: GlobalInfo)
      extends OtherInstantiationReferencingNodeInfo(globalInfo, NodeType.Operation) {
    var title: Component = Component.literal("Operation")

    def operationName: ResourceLocation = new ResourceLocation(nameBox.getValue)

    override protected def getInstantiation: Option[DanmakuInstantiation] = {
      val reg = Minecraft.getInstance().level.registryAccess()
      DanmakuInstantiations.registry(reg).getOptional(operationName).toScala
    }
  }

  private class Math(globalInfo: GlobalInfo) extends NodeInfo(globalInfo, NodeType.Math) {
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
    val aInput: IONodeContentInfoWithSliderFallback = IONodeContentInfoWithSliderFallback(
      tpe.identifier,
      "a",
      Component.literal("Value A"),
      tpeToColor(GraphType.Number),
      IOContentVariant.Input
    )
    val bInput: IONodeContentInfoWithSliderFallback = IONodeContentInfoWithSliderFallback(
      tpe.identifier,
      "b",
      Component.literal("Value B"),
      tpeToColor(GraphType.Number),
      IOContentVariant.Input
    )

    def mathOp: DanmakuInstantiation.MathOp = opButton.getValue

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(opButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      aInput,
      bInput,
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(GraphType.Number),
        IOContentVariant.Output
      )
    )
  }

  private class Enumerate(globalInfo: GlobalInfo) extends NodeInfo(globalInfo, NodeType.Enumerate) {
    var title: Component = Component.literal("Enumerate")
    val countInput: IONodeContentInfoWithSliderFallback = IONodeContentInfoWithSliderFallback(
      tpe.identifier,
      "count",
      Component.literal("Count"),
      tpeToColor(GraphType.Int),
      IOContentVariant.Input
    )

    override val contents: Seq[NodeContentInfo] = Seq(
      countInput,
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(GraphType.Int),
        IOContentVariant.Output
      )
    )
  }

  private class KnownConstant(globalInfo: GlobalInfo) extends NodeInfo(globalInfo, NodeType.KnownConstant) {
    var title: Component = Component.literal("Known Constant")
    private val constantButton: CycleButton[DanmakuInstantiation.ConstantName] = CycleButton
      .builder[DanmakuInstantiation.ConstantName] {
        case DanmakuInstantiation.ConstantName.Pi  => Component.literal("Pi")
        case DanmakuInstantiation.ConstantName.E   => Component.literal("E")
        case DanmakuInstantiation.ConstantName.Phi => Component.literal("Phi")
      }
      .withValues(DanmakuInstantiation.ConstantName.values*)
      .create(0, 0, 50, 14, Component.literal("Constant"))

    def constant: DanmakuInstantiation.ConstantName = constantButton.getValue

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(constantButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(GraphType.Float),
        IOContentVariant.Output
      )
    )
  }

  private class Convert(globalInfo: GlobalInfo) extends NodeInfo(globalInfo, NodeType.Convert) {
    var title: Component = Component.literal("Convert")
    private val fromButton: CycleButton[GraphNumberType] =
      varTpeNumberButtonBuilder.create(0, 0, 50, 14, Component.literal("From"))
    private val toButton: CycleButton[GraphNumberType] =
      varTpeNumberButtonBuilder.create(0, 0, 50, 14, Component.literal("To"))

    val input: IONodeContentInfoWithSliderFallback = IONodeContentInfoWithSliderFallback(
      tpe.identifier,
      "input",
      Component.literal("Input"),
      tpeToColor(fromType.asGraphType),
      IOContentVariant.Input
    )

    def fromType: GraphNumberType = fromButton.getValue
    def toType: GraphNumberType   = toButton.getValue

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(fromButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      WrapWidgetNodeContentInfo(toButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      input,
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(toType.asGraphType),
        IOContentVariant.Output
      )
    )
  }

  private class Random(globalInfo: GlobalInfo) extends NodeInfo(globalInfo, NodeType.Random) {
    var title: Component = Component.literal("Random")
    private val tpeButton: CycleButton[GraphNumberType] =
      varTpeNumberButtonBuilder.create(0, 0, 50, 14, Component.literal("Type"))

    val minInput: IONodeContentInfoWithSliderFallback = IONodeContentInfoWithSliderFallback(
      tpe.identifier,
      "min",
      Component.literal("Min"),
      tpeToColor(graphType.asGraphType),
      IOContentVariant.Input
    )
    val maxInput: IONodeContentInfoWithSliderFallback = IONodeContentInfoWithSliderFallback(
      tpe.identifier,
      "max",
      Component.literal("Max"),
      tpeToColor(graphType.asGraphType),
      IOContentVariant.Input
    )

    def graphType: GraphNumberType = tpeButton.getValue

    override val contents: Seq[NodeContentInfo] = Seq(
      WrapWidgetNodeContentInfo(tpeButton, tpe.identifier, _.copy().alignHorizontallyCenter()),
      spacer(tpe),
      minInput,
      maxInput,
      spacer(tpe),
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.literal("Output"),
        tpeToColor(graphType.asGraphType),
        IOContentVariant.Output
      )
    )
  }

  sealed trait NodeContentInfo(val coreId: ResourceLocation) extends NodeContentInfoBase
  private case class SpacerNodeContentInfo(height: Int, coreIdLoc: ResourceLocation)
      extends NodeContentInfo(coreIdLoc) {
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

    override def width: Int = Minecraft.getInstance().font.width(title) + 10

    override def height: Int = 10

    override val widget: AbstractWidget = new NodeWidget.NodeIOWidget(this)
  }

  private class IONodeContentInfoWithSliderFallback(
      coreIdLoc: ResourceLocation,
      identifier: String,
      title: Component,
      color: Int,
      variant: IOContentVariant,
      minValue: Double = 0,
      maxValue: Double = 1,
      currentValue: Double = 0,
      stepSize: Double = 0,
      precision: Int = 0
  ) extends IONodeContentInfo(coreIdLoc, identifier, title, color, variant) {

    override val widget: NodeWidget.NodeIOWidgetSliderInput =
      new NodeWidget.NodeIOWidgetSliderInput(this, 70, minValue, maxValue, currentValue, stepSize, precision)

    def fallbackValue[A](varType: VariableType[A]): A = varType match
      case VariableType.Float => widget.value.toFloat
      case VariableType.Int   => java.lang.Math.round(width.intValue)
  }
}
