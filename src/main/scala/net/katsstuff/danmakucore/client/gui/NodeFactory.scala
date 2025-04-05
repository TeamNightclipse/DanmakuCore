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
    
    def width: Int
    def height: Int

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

  type GlobalInfo <: GlobalInfoBase
  type NodeType <: NodeTypeBase
  type NodeInfo <: NodeInfoBase
  type NodeContentInfo <: NodeContentInfoBase
  
  trait GlobalInfoBase {
    def invalidInfos: Seq[NodeInfo]
  }

  trait NodeTypeBase {
    def group: Option[String]
    def identifier: ResourceLocation

    def make(globalInfo: GlobalInfo): NodeInfo
  }

  trait NodeInfoBase extends NodeFactory.NodeStyle {
    def tpe: NodeType
    def contents: Seq[NodeContentInfoBase]
    def defaultWidth: Int
  }

  trait NodeContentInfoBase extends NodeFactory.NodeContentStyle {
    def coreId: ResourceLocation
  }

  trait IONodeContentInfoBase extends NodeContentInfoBase, NodeFactory.IONodeContentStyle {
    def identifier: String
  }
  
  def makeGlobalInfo: GlobalInfo

  def allNodeTypes: Seq[NodeType]
  
  type RepresentedObject

  //noinspection UnstableApiUsage
  def buildObject(globalInfo: GlobalInfo, graph: Graph[GraphNodeIdentifier], nodes: Map[GraphNodeIdentifier.Core, NodeInfo]): RepresentedObject
}
