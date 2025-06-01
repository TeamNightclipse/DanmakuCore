package net.katsstuff.danmakucore.client.gui

import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*

import com.google.common.graph.{Graph, Traverser, ValueGraph, ValueGraphBuilder}
import com.mojang.logging.LogUtils
import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.gui
import net.katsstuff.danmakucore.client.gui.NodeFactory.IOContentVariant
import net.katsstuff.danmakucore.client.gui.widgets.{
  MutableSpacer,
  NodeContainer,
  NodeIOWidget,
  NodeIOWidgetSliderInput,
  SliderInputWidget
}
import net.katsstuff.danmakucore.danmaku.DanmakuInstantiation.VariableType
import net.katsstuff.danmakucore.danmaku.form.{DanCoreForms, Form}
import net.katsstuff.danmakucore.danmaku.{DanmakuInstantiation, DanmakuInstantiations}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.{AbstractWidget, CycleButton, EditBox, StringWidget}
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.network.chat.{Component, MutableComponent}
import net.minecraft.resources.ResourceLocation

object DanmakuInstantiationNodeFactory extends NodeFactory { self =>

  private def spacer(tpe: NodeType, height: Int = 3) = SpacerNodeContentInfo(height, tpe.identifier)

  class GlobalInfo extends GlobalInfoBase {
    private val _invalidInfos: mutable.Buffer[NodeInfo] = mutable.Buffer.empty
    var form: Form                                      = DanCoreForms.SphereForm.get()

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

    override def make(
        container: NodeContainer[self.type],
        globalInfo: GlobalInfo
    ): DanmakuInstantiationNodeFactory.NodeInfo = this match
      case NodeType.Input         => new Input(container, globalInfo)
      case NodeType.Output        => new Output(container, globalInfo)
      case NodeType.Group         => new Group(container, globalInfo)
      case NodeType.Operation     => new Operation(container, globalInfo)
      case NodeType.Math          => new Math(container, globalInfo)
      case NodeType.Enumerate     => new Enumerate(container, globalInfo)
      case NodeType.KnownConstant => new KnownConstant(container, globalInfo)
      case NodeType.Convert       => new Convert(container, globalInfo)
      case NodeType.Random        => new Random(container, globalInfo)

    def topColor: Int = group match
      case Some("IO")   => 0xFF00FF00
      case Some("Math") => 0xFF0000FF
      case _            => 0xFFFF0000
  }

  override def makeGlobalInfo: GlobalInfo = new GlobalInfo

  override def allNodeTypes: Seq[NodeType] = NodeType.values.toSeq

  sealed trait NodeInfo(container: NodeContainer[this.type], val globalInfo: GlobalInfo, val tpe: NodeType)
      extends NodeInfoBase {
    protected var listener: () => Unit = () => ()

    protected[this] def labelledSyncedContent[A <: AbstractWidget](
        contentType: ContentType[A],
        label: MutableComponent,
        width: Int = 50,
        height: Int = 10
    ): ContentTuple[A, NodeContentInfo, contentType.type] = {
      val font                     = Minecraft.getInstance.font
      val (mainWidget, sideWidget) = contentType.make(container, label, width, height)
      val labelWithColons          = label.copy().append(":")

      ContentTuple(contentType)(
        mainContents = Seq(
          WrapWidgetNodeContentInfo(
            new container.StringWidget(0, 0, width, height, labelWithColons, font).alignLeft(),
            tpe.identifier,
            _.copy().alignHorizontallyCenter().paddingBottom(1)
          ),
          WrapWidgetNodeContentInfo(mainWidget, tpe.identifier, _.copy().alignHorizontallyCenter().paddingBottom(4))
        ),
        sidebarContents = Seq(
          WrapWidgetNodeContentInfo(
            new StringWidget(0, 0, width, height, labelWithColons, font).alignLeft(),
            tpe.identifier,
            _.copy().alignHorizontallyCenter().paddingBottom(1)
          ),
          WrapWidgetNodeContentInfo(
            sideWidget,
            tpe.identifier,
            _.copy().alignHorizontallyCenter().paddingBottom(4)
          )
        ),
        mainWidget,
        sideWidget
      )
    }

    protected[this] def syncedCycleButton[A](
        contentType: ContentType.CycleButtonType[A],
        label: MutableComponent,
        width: Int = 50,
        height: Int = 14,
        paddingTop: Option[Int] = None
    ): ContentTuple[CycleButton[A], NodeContentInfo, contentType.type] = {
      val (mainWidget, sideWidget) = contentType.make(container, label, width, height)

      def withPaddingTop(layoutSettings: LayoutSettings) = paddingTop.fold(layoutSettings)(layoutSettings.paddingTop)

      ContentTuple(contentType)(
        mainContents = Seq(
          WrapWidgetNodeContentInfo(
            mainWidget,
            tpe.identifier,
            l => withPaddingTop(l.copy().alignHorizontallyCenter())
          )
        ),
        sidebarContents = Seq(
          WrapWidgetNodeContentInfo(
            sideWidget,
            tpe.identifier,
            l => withPaddingTop(l.copy().alignHorizontallyCenter())
          )
        ),
        mainWidget = mainWidget,
        sidebarWidget = sideWidget
      )
    }

    protected[this] def sliderInput(
        identifier: String,
        color: Int,
        contentType: ContentType.SliderNodeInput,
        label: MutableComponent,
        width: Int = 70,
        needsTopPadding: Boolean = false
    ): ContentTuple[SliderInputWidget, NodeContentInfo, contentType.type] = {
      val (mainWidget, sideWidget) = contentType.make(container, label.append(": "), width, 14)

      ContentTuple(contentType)(
        Seq(
          IONodeContentInfoWithSliderFallback(
            tpe.identifier,
            identifier,
            label,
            color,
            mainWidget,
            needsTopPadding
          )
        ),
        Seq(
          WrapWidgetNodeContentInfo(
            sideWidget,
            tpe.identifier,
            _.copy().alignHorizontallyCenter()
          )
        ),
        mainWidget,
        sideWidget
      )
    }

    override def topColor: Int     = tpe.topColor
    override def color: Int        = if globalInfo.invalidInfos.contains(this) then 0xFFFF0000 else 0xFFAAAAAA

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

  private val varTpeContentType: ContentType.CycleButtonType[GraphNumberType] = ContentType.CycleButtonType(
    {
      case GraphType.Float => Component.translatable("danmakucore.gui.nodeEditor.type.float")
      case GraphType.Int   => Component.translatable("danmakucore.gui.nodeEditor.type.int")
    },
    Seq(GraphType.Float, GraphType.Int)
  )

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

    while edges.nonEmpty do
      val edge = edges.dequeue()
      typeGraph.edgeValue(edge).get match
        case t: GraphNumberType => builder.putEdgeValue(edge, t)
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
              builder.putEdgeValue(edge, t)

            case Seq() =>
              builder.putEdgeValue(edge, GraphType.Float)

              edges.enqueue(edge)
            case ts =>
              val floatTypes = ts.count(_ == GraphType.Float)
              val intTypes   = ts.count(_ == GraphType.Int)

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
                  val nodeInputs = node.dynamicContents.collectFirst {
                    case i: IONodeContentInfoWithSliderFallback => i
                    case i: IONodeContentInfo                   => i
                  }
                  val opInputs = op.inputs

                  opInputs
                    .zip(nodeInputs)
                    .map { case (opInput: DanmakuInstantiation.Input[a], nodeInput) =>
                      val fallback: a = nodeInput match
                        case fallback: IONodeContentInfoWithSliderFallback => fallback.fallbackValue(opInput.tpe)
                        case _ =>
                          opInput.tpe match
                            case DanmakuInstantiation.VariableType.Float => 0F
                            case DanmakuInstantiation.VariableType.Int   => 0

                      inputValue(GraphNodeIdentifier.IO(id, opInput.name, isInput = true), fallback, opInput.tpe)
                    }
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
      form = globalInfo.form
    )
  }

  private class Input(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends NodeInfo(container, globalInfo, NodeType.Input) {
    var title: Component = Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.input.title")

    private val nameContent = labelledSyncedContent(
      ContentType.EditBoxType,
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.input.name")
    )
    private val defaultContent = labelledSyncedContent(
      ContentType.EditBoxType,
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.input.default")
    )

    private def setValidityFromValues(): Unit = {
      val default = defaultContent.value

      val nameBoxValid = nameContent.value.nonEmpty
      val defaultValid = graphType match
        case GraphType.Int   => default.toIntOption.isDefined
        case GraphType.Float => default.toFloatOption.isDefined

      defaultContent.setTextColor(if defaultValid then 0xFFE0E0E0 else 0xFFFF0000)
      nameContent.setTextColor(if nameBoxValid then 0xFFE0E0E0 else 0xFFFF0000)
      markValidity(nameBoxValid && defaultValid)
    }

    defaultContent.setResponder(_ => setValidityFromValues())
    nameContent.setResponder(_ => setValidityFromValues())

    markValidity(false)

    private val varTpeContent = syncedCycleButton(
      varTpeContentType.copy(onValueChange = _ => setValidityFromValues()),
      Component.translatable("danmakucore.gui.nodeEditor.type"),
      paddingTop = Some(3)
    )

    def name: String = nameContent.value

    def graphType: GraphNumberType = varTpeContent.value

    def defaultValue: Float = defaultContent.value.toFloatOption.getOrElse(0F)

    override val contents: Seq[NodeContentInfo] =
      nameContent.mainContents ++ defaultContent.mainContents ++ varTpeContent.mainContents ++ Seq(
        IONodeContentInfo(
          tpe.identifier,
          "output",
          Component.translatable("danmakucore.gui.nodeEditor.input"),
          tpeToColor(graphType.asGraphType),
          IOContentVariant.Output,
          needsTopPadding = true
        )
      )

    override def sidebarContents: Seq[DanmakuInstantiationNodeFactory.NodeContentInfoBase] = Seq(
      nameContent.sidebarContents,
      defaultContent.sidebarContents,
      varTpeContent.sidebarContents
    ).flatten
  }

  private class Output(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends NodeInfo(container, globalInfo, NodeType.Output) {
    var title: Component = Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.output.title")

    private val nameContent = labelledSyncedContent(
      ContentType.EditBoxType,
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.output.name")
    )

    nameContent.setResponder { str =>
      nameContent.setTextColor(if str.nonEmpty then 0xFFE0E0E0 else 0xFFFF0000)
      markValidity(str.nonEmpty)
    }
    markValidity(false)

    def name: String = nameContent.value

    override val contents: Seq[NodeContentInfo] = nameContent.mainContents ++ Seq(
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.translatable("danmakucore.gui.nodeEditor.output"),
        tpeToColor(GraphType.Number),
        IOContentVariant.Input,
        needsTopPadding = true
      )
    )

    override def sidebarContents: Seq[DanmakuInstantiationNodeFactory.NodeContentInfoBase] = nameContent.sidebarContents
  }

  sealed abstract private class OtherInstantiationReferencingNodeInfo(
      container: NodeContainer[this.type],
      globalInfo: GlobalInfo,
      tpe: NodeType,
      translateName: String
  ) extends NodeInfo(container, globalInfo, tpe) {
    protected val nameContents: ContentTuple[EditBox, NodeContentInfo, ContentType.EditBoxType.type] =
      labelledSyncedContent(
        ContentType.EditBoxType,
        Component.translatable(s"danmakucore.gui.nodeEditor.danmakuInstantiations.$translateName.name")
      )

    nameContents.setMaxLength(48)

    private def fixedContents: Seq[NodeContentInfo] = nameContents.mainContents ++ Seq(
      spacer(tpe)
    )

    private var _dynamicContents              = Seq.empty[NodeContentInfo]
    private var dynamicSidebarContents        = Seq.empty[NodeContentInfo]
    def dynamicContents: Seq[NodeContentInfo] = _dynamicContents

    private var _lastOperation: Option[DanmakuInstantiation] = None
    def operation: Option[DanmakuInstantiation]              = _lastOperation

    nameContents.setResponder { _ =>
      val op = getInstantiation
      if _lastOperation != op then
        _lastOperation = op
        op match
          case Some(value) =>
            val newInputs = value.inputs.map { input =>
              // TODO: Let the instantiation specify min and max value here
              sliderInput(
                input.name,
                tpeToColor(GraphNumberType.fromVarType(input.tpe).asGraphType),
                ContentType.SliderNodeInput(
                  currentValue = input.default.asInstanceOf[Double]
                ),
                Component.literal(input.name)
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

            _dynamicContents =
              newInputs.flatMap(_.mainContents) ++ Seq(spacer(tpe)).filter(_ => newInputs.nonEmpty) ++ newOutputs
            dynamicSidebarContents = newInputs.flatMap(_.sidebarContents)

          case None => _dynamicContents = Nil

        listener()
      end if
    }

    protected def getInstantiation: Option[DanmakuInstantiation]

    override val contents: Seq[NodeContentInfo] = fixedContents ++ _dynamicContents

    override def sidebarContents: Seq[DanmakuInstantiationNodeFactory.NodeContentInfoBase] = Seq(
      nameContents.sidebarContents,
      dynamicSidebarContents
    ).flatten
  }

  private class Group(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends OtherInstantiationReferencingNodeInfo(container, globalInfo, NodeType.Group, "group") {
    var title: Component = Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.group.title")

    def groupName: String = nameContents.value

    override protected def getInstantiation: Option[DanmakuInstantiation] = globalInfo.getGroup(groupName)
  }

  private class Operation(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends OtherInstantiationReferencingNodeInfo(container, globalInfo, NodeType.Operation, "operation") {
    var title: Component = Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.operation.title")

    def operationName: ResourceLocation = new ResourceLocation(nameContents.value)

    override protected def getInstantiation: Option[DanmakuInstantiation] = {
      val reg = Minecraft.getInstance().level.registryAccess()
      DanmakuInstantiations.registry(reg).getOptional(operationName).toScala
    }
  }

  private class Math(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends NodeInfo(container, globalInfo, NodeType.Math) {
    var title: Component = Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.title")
    private val opContentType: ContentType.CycleButtonType[DanmakuInstantiation.MathOp] = ContentType.CycleButtonType(
      {
        case DanmakuInstantiation.MathOp.Add =>
          Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.add")
        case DanmakuInstantiation.MathOp.Subtract =>
          Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.subtract")
        case DanmakuInstantiation.MathOp.Multiply =>
          Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.multiply")
        case DanmakuInstantiation.MathOp.Divide =>
          Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.divide")
        case DanmakuInstantiation.MathOp.Modulo =>
          Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.modulo")
      },
      DanmakuInstantiation.MathOp.values.toSeq,
      displayOnlyValue = true
    )
    private val opContent = syncedCycleButton(
      opContentType,
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.operation")
    )

    val aInput: ContentTuple[SliderInputWidget, NodeContentInfo, _ <: ContentType.SliderNodeInput] = sliderInput(
      "a",
      tpeToColor(GraphType.Number),
      ContentType.SliderNodeInput(),
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.valueA"),
      needsTopPadding = true
    )
    val bInput: ContentTuple[SliderInputWidget, NodeContentInfo, _ <: ContentType.SliderNodeInput] = sliderInput(
      "b",
      tpeToColor(GraphType.Number),
      ContentType.SliderNodeInput(),
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.math.valueB")
    )

    def mathOp: DanmakuInstantiation.MathOp = opContent.value

    override val contents: Seq[NodeContentInfo] =
      opContent.mainContents ++ aInput.mainContents ++ bInput.mainContents ++ Seq(
        IONodeContentInfo(
          tpe.identifier,
          "output",
          Component.translatable("danmakucore.gui.nodeEditor.output"),
          tpeToColor(GraphType.Number),
          IOContentVariant.Output,
          needsTopPadding = true
        )
      )

    override def sidebarContents: Seq[DanmakuInstantiationNodeFactory.NodeContentInfoBase] = Seq(
      opContent.sidebarContents,
      aInput.sidebarContents,
      bInput.sidebarContents
    ).flatten
  }

  private class Enumerate(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends NodeInfo(container, globalInfo, NodeType.Enumerate) {
    var title: Component = Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.enumerate.title")
    val countInput: ContentTuple[SliderInputWidget, NodeContentInfo, _ <: ContentType.SliderNodeInput] =
      sliderInput(
        "count",
        tpeToColor(GraphType.Int),
        ContentType.SliderNodeInput(),
        Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.enumerate.count")
      )

    override val contents: Seq[NodeContentInfo] = countInput.mainContents ++ Seq(
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.translatable("danmakucore.gui.nodeEditor.output"),
        tpeToColor(GraphType.Int),
        IOContentVariant.Output,
        needsTopPadding = true
      )
    )

    override def sidebarContents: Seq[DanmakuInstantiationNodeFactory.NodeContentInfoBase] = countInput.sidebarContents
  }

  private class KnownConstant(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends NodeInfo(container, globalInfo, NodeType.KnownConstant) {
    var title: Component =
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.knownConstant.title")
    private val constantContentType: ContentType.CycleButtonType[DanmakuInstantiation.ConstantName] =
      ContentType.CycleButtonType(
        {
          case DanmakuInstantiation.ConstantName.Pi =>
            Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.knownConstant.pi")
          case DanmakuInstantiation.ConstantName.E =>
            Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.knownConstant.e")
          case DanmakuInstantiation.ConstantName.Phi =>
            Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.knownConstant.phi")
        },
        DanmakuInstantiation.ConstantName.values.toSeq
      )
    private val constantContent = syncedCycleButton(
      constantContentType,
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.knownConstant.constant")
    )

    def constant: DanmakuInstantiation.ConstantName = constantContent.value

    override val contents: Seq[NodeContentInfo] = constantContent.mainContents ++ Seq(
      IONodeContentInfo(
        tpe.identifier,
        "output",
        Component.translatable("danmakucore.gui.nodeEditor.output"),
        tpeToColor(GraphType.Float),
        IOContentVariant.Output,
        needsTopPadding = true
      )
    )

    override def sidebarContents: Seq[DanmakuInstantiationNodeFactory.NodeContentInfoBase] =
      constantContent.sidebarContents
  }

  private class Convert(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends NodeInfo(container, globalInfo, NodeType.Convert) {
    var title: Component = Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.convert.title")
    private val fromContent = syncedCycleButton(
      varTpeContentType,
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.convert.from")
    )
    private val toContent = syncedCycleButton(
      varTpeContentType,
      Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.convert.to")
    )

    val input: ContentTuple[SliderInputWidget, NodeContentInfo, _ <: ContentType.SliderNodeInput] = sliderInput(
      "input",
      tpeToColor(fromType.asGraphType),
      ContentType.SliderNodeInput(),
      Component.translatable("danmakucore.gui.nodeEditor.input"),
      needsTopPadding = true
    )

    def fromType: GraphNumberType = fromContent.value
    def toType: GraphNumberType   = toContent.value

    override val contents: Seq[NodeContentInfo] =
      fromContent.mainContents ++ toContent.mainContents ++ input.mainContents ++ Seq(
        IONodeContentInfo(
          tpe.identifier,
          "output",
          Component.translatable("danmakucore.gui.nodeEditor.output"),
          tpeToColor(toType.asGraphType),
          IOContentVariant.Output,
          needsTopPadding = true
        )
      )

    override def sidebarContents: Seq[DanmakuInstantiationNodeFactory.NodeContentInfoBase] =
      fromContent.sidebarContents ++ toContent.sidebarContents ++ input.sidebarContents
  }

  private class Random(container: NodeContainer[this.type], globalInfo: GlobalInfo)
      extends NodeInfo(container, globalInfo, NodeType.Random) {
    var title: Component = Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.random.title")
    private val tpeContent = syncedCycleButton(
      varTpeContentType,
      Component.translatable("danmakucore.gui.nodeEditor.type")
    )

    val minInput: ContentTuple[SliderInputWidget, NodeContentInfo, _ <: ContentType.SliderNodeInput] =
      sliderInput(
        "min",
        tpeToColor(graphType.asGraphType),
        ContentType.SliderNodeInput(),
        Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.random.min"),
        needsTopPadding = true
      )
    val maxInput: ContentTuple[SliderInputWidget, NodeContentInfo, _ <: ContentType.SliderNodeInput] =
      sliderInput(
        "max",
        tpeToColor(graphType.asGraphType),
        ContentType.SliderNodeInput(),
        Component.translatable("danmakucore.gui.nodeEditor.danmakuInstantiations.random.max")
      )

    def graphType: GraphNumberType = tpeContent.value

    override val contents: Seq[NodeContentInfo] =
      tpeContent.mainContents ++ minInput.mainContents ++ maxInput.mainContents ++ Seq(
        IONodeContentInfo(
          tpe.identifier,
          "output",
          Component.translatable("danmakucore.gui.nodeEditor.output"),
          tpeToColor(graphType.asGraphType),
          IOContentVariant.Output,
          needsTopPadding = true
        )
      )

    override def sidebarContents: Seq[DanmakuInstantiationNodeFactory.NodeContentInfoBase] = Seq(
      tpeContent.sidebarContents,
      minInput.sidebarContents,
      maxInput.sidebarContents
    ).flatten
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
      variant: IOContentVariant,
      needsTopPadding: Boolean = false
  ) extends NodeContentInfo(coreIdLoc),
        IONodeContentInfoBase {

    override def width: Int = Minecraft.getInstance().font.width(title) + 10

    override def height: Int = 10

    override val widget: AbstractWidget = new NodeIOWidget(this)

    override def layoutSettings(default: LayoutSettings): LayoutSettings =
      val settings = super.layoutSettings(default)
      if needsTopPadding then settings.paddingTop(settings.getExposed.paddingTop + 3) else settings
  }

  private class IONodeContentInfoWithSliderFallback(
      coreIdLoc: ResourceLocation,
      identifier: String,
      title: Component,
      color: Int,
      internals: SliderInputWidget,
      needsTopPadding: Boolean = false
  ) extends IONodeContentInfo(coreIdLoc, identifier, title, color, IOContentVariant.Input, needsTopPadding) {

    override val widget: NodeIOWidgetSliderInput = new NodeIOWidgetSliderInput(
      this,
      internals
    )

    def fallbackValue[A](varType: VariableType[A]): A = varType match
      case VariableType.Float => widget.value.toFloat
      case VariableType.Int   => java.lang.Math.round(widget.value.toFloat)
  }
}
