package net.katsstuff.danmakucore.client.gui

import java.util.UUID
import scala.reflect.Typeable
import com.google.common.graph.Graph
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.{LayoutElement, LayoutSettings}
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

object NodeFactory {

  trait NodeStyle {
    def topColor: Int
    def color: Int
    def title: Component
    def title_=(title: Component): Unit
    def contents: Seq[NodeContentStyle]

    def onContentsChange(listener: () => Unit): Unit
  }

  trait NodeContentStyle {
    def widget: LayoutElement
    def layoutSettings(default: LayoutSettings): LayoutSettings = default
  }

  trait IONodeContentStyle extends NodeContentStyle {
    def color: Int
    def title: Component
    def title_=(title: Component): Unit
    def variant: IOContentVariant

    override def layoutSettings(default: LayoutSettings): LayoutSettings = variant match
      case IOContentVariant.Input  => default.copy().alignHorizontallyLeft()
      case IOContentVariant.Output => default.copy().alignHorizontallyRight()
  }

  enum IOContentVariant {
    case Input
    case Output
  }
}
trait NodeFactory {

  type NodeType <: NodeTypeBase
  type NodeInfo <: NodeInfoBase
  type NodeContentInfo <: NodeContentInfoBase

  trait NodeTypeBase {
    def group: Option[String]
    def identifier: ResourceLocation

    def make: NodeInfo
  }

  trait NodeInfoBase extends NodeFactory.NodeStyle {
    def tpe: NodeType
    def contents: Seq[NodeContentInfoBase]
  }

  trait NodeContentInfoBase extends NodeFactory.NodeContentStyle {
    def coreId: ResourceLocation
  }

  trait IONodeContentInfoBase extends NodeContentInfoBase, NodeFactory.IONodeContentStyle {
    def identifier: String
  }

  def allNodeTypes: Seq[NodeType]

  /*
  type NodeIdentifier
  type RepresentedObject

  type CoreId <: {
    type EdgeId
  }

  trait CoreBase(val id: CoreId)
  trait InputBase(val to: Core)
  trait OutputBase(val from: Core)

  type Core <: NodeIdentifier & CoreBase
  type Input <: NodeIdentifier & InputBase
  type Output <: NodeIdentifier & OutputBase

  given identifierTypeable: Typeable[NodeIdentifier]
  given coreTypeable: Typeable[Core]
  given inputTypeable: Typeable[Input]
  given outputTypeable: Typeable[Output]

  def core(id: CoreId, uuid: UUID): Core
  def input(to: Core)(id: to.id.EdgeId): Input
  def output(from: Core)(id: from.id.EdgeId): Output
  def independentConnector(uuid: UUID): NodeIdentifier

  case class NodeType(
    identifier: CoreId,
    topColor: Int,
    color: Int,
    title: Component,
    contents: Seq[NodeType.Content]
  )

  object NodeType {
    enum Content {
      case Input(identifier: (coreId: CoreId) => core.EdgeId, title: Component, color: Int)
      case Output(identifier: (coreId: CoreId) => core.EdgeId, title: Component, color: Int)
      case Misc(make: () => AbstractWidget)
    }
  }

  //noinspection UnstableApiUsage
  def buildObject(graph: Graph[NodeIdentifier]): RepresentedObject
   */
}
