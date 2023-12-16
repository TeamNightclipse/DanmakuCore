package net.katsstuff.danmakucore.danmaku

import scala.language.existentials

import java.util.UUID

import scala.annotation.tailrec
import scala.collection.mutable

import com.mojang.logging.LogUtils
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler.{
  DanmakuSpawnData,
  RelationshipWithDepth,
  RenderData
}
import net.katsstuff.danmakucore.danmaku.behaviors.Behavior
import net.katsstuff.danmakucore.danmaku.data.ShotData
import net.katsstuff.danmakucore.danmaku.form.Form
import net.katsstuff.danmakucore.math.{MutableMat4, Quat, Vector3}
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class TopDanmakuBehaviorsHandler {

  private val LOGGER              = LogUtils.getLogger
  final private val maxDifference = 2

  private val handlers: mutable.IndexedBuffer[DanmakuBehaviorHandler]             = mutable.IndexedBuffer()
  private val handlersMap: mutable.Map[List[Behavior[_]], DanmakuBehaviorHandler] = mutable.Map()

  private val globalParentMap: mutable.Map[UUID, UUID]       = mutable.Map()
  private val globalFamilyDepthMap: mutable.Map[UUID, Short] = mutable.Map()

  // TODO: Cache?
  @tailrec
  private def diffListsOrdered[A](requiredElems: List[A], that: List[A], extraElementsFound: Int): Int =
    (requiredElems, that) match {
      case (Nil, _) => extraElementsFound + that.length
      case (_, Nil) => Int.MaxValue
      case (requiredElem :: nextRequiredElems, x :: xs) if requiredElem == x =>
        diffListsOrdered(nextRequiredElems, xs, extraElementsFound)
      case (_, _ :: xs) => diffListsOrdered(requiredElems, xs, extraElementsFound + 1)
    }

  private def findHandlerForBehaviors(behaviors: List[Behavior[_]]): Option[DanmakuBehaviorHandler] = {
    val optHandler =
      handlersMap
        .get(behaviors)
        .orElse(handlers.minByOption { handler =>
          if (handler.behaviors.length <= behaviors.length) Int.MaxValue
          else diffListsOrdered(behaviors, handler.behaviors, 0)
        })

    optHandler.flatMap { handler =>
      val difference = diffListsOrdered(behaviors, handler.behaviors, 0)
      Option.when(difference <= maxDifference || !handler.acceptsMore)(handler)
    }
  }

  private def findOrCreateHandlerForBehaviors(behaviorsList: List[Behavior[_]]): DanmakuBehaviorHandler =
    findHandlerForBehaviors(behaviorsList).getOrElse {
      val newHandler = new DanmakuBehaviorHandler(behaviorsList, false, globalParentMap, globalFamilyDepthMap)
      handlers += newHandler
      handlersMap.put(behaviorsList, newHandler)
      newHandler
    }

  private def setChildrenDepth(data: DanmakuSpawnData): DanmakuSpawnData = {
    def setChildrenDepthInner(depth: Int)(child: DanmakuSpawnData): DanmakuSpawnData =
      child.copy(familyDepth = depth + 1, children = child.children.map(setChildrenDepthInner(depth + 1)))

    data.copy(children = data.children.map(setChildrenDepthInner(data.familyDepth)))
  }

  private def setFamilyDepth(danmaku: DanmakuSpawnData): Option[DanmakuSpawnData] = {
    if (danmaku.familyDepth != -1) Some(danmaku)
    else
      danmaku.parent match {
        case None if danmaku.familyDepth == 0 => Some(danmaku)
        case None                             => Some(danmaku.copy(familyDepth = 0))
        case Some(parentId) =>
          globalFamilyDepthMap.get(parentId).map(depth => setChildrenDepth(danmaku.copy(familyDepth = depth)))
      }
  }

  @tailrec
  final def addDanmaku(danmaku: Seq[DanmakuSpawnData]): Unit = {
    val childDanmakuToSpawn =
      danmaku
        .flatMap(setFamilyDepth)
        .groupBy(data => findOrCreateHandlerForBehaviors(data.behaviors.map(_.behavior).toList))
        .flatMap { case (handler, danmaku) =>
          handler.addDanmaku(danmaku)
        }

    if (childDanmakuToSpawn.nonEmpty) {
      addDanmaku(childDanmakuToSpawn.toSeq)
    }
  }

  @SubscribeEvent def onTick(event: ServerTickEvent): Unit = {
    val childDanmakuToSpawn = handlers.flatMap { h =>
      val newSpawn = h.tick()

      val (handlerAdds, normalAdds) = newSpawn
        .map { t =>
          val behaviorsList = t._1.behaviors.map(_.behavior).toList
          (
            t._1,
            t._2,
            findOrCreateHandlerForBehaviors(behaviorsList)
          )
        }
        .partition(t => t._3 == h)

      val childDanmakuToSpawnA = h.addDanmakuWithPreferredIndices(handlerAdds.map(t => (t._1, t._2)))

      val childDanmakuToSpawnB = normalAdds.groupMap(_._3)(t => t._1).flatMap { case (h, spawns) =>
        h.addDanmaku(spawns)
      }

      childDanmakuToSpawnA ++ childDanmakuToSpawnB
    }

    if (childDanmakuToSpawn.nonEmpty) {
      addDanmaku(childDanmakuToSpawn.toSeq)
    }
  }

  def renderData(partialTicks: Float): Vector[RenderData] = {
    val localRenderData = handlers.flatMap(_.computeAndGetRenderData(partialTicks)).to(mutable.Map)

    val remainingRelationships = globalParentMap.view
      .map { case (child, parent) =>
        RelationshipWithDepth(child, parent, globalFamilyDepthMap(child))
      }
      .to(mutable.PriorityQueue)

    // If things go badly here, they go badly, but let's hope they don't
    while (remainingRelationships.nonEmpty) {
      val RelationshipWithDepth(childId, parentId, _) = remainingRelationships.dequeue()
      (localRenderData.get(childId), localRenderData.get(parentId)) match {
        case (Some(child), Some(parent)) => child.modelMat *= parent.modelMat
        case (None, Some(_))             => localRenderData.remove(childId) // Probably not needed
        case (Some(_), None)             => localRenderData.remove(childId)
        case (None, None)                =>                                 // Already gone
      }
    }

    localRenderData.values.toVector
  }

  def cleanup(): Unit =
    handlers.filterInPlace { h =>
      val res = h.alwaysKeep || h.count > 0
      if (!res) {
        LOGGER.debug(s"Deleting DanmakuBehaviorHandler for ${h.behaviors.mkString(", ")}")
      }
      res
    }
}
object TopDanmakuBehaviorsHandler {

  case class DanmakuSpawnData(
      pos: Vector3,
      orientation: Quat,
      shotData: ShotData,
      behaviors: Seq[BehaviorPair[_]],
      nextStage: Seq[DanmakuSpawnData],
      parent: Option[UUID],
      children: Seq[DanmakuSpawnData],
      familyDepth: Int = -1
  )

  case class RenderData(
      form: Form,
      renderProperties: Map[String, Float],
      modelMat: MutableMat4,
      modelViewMat: MutableMat4,
      mainColor: Int,
      secondaryColor: Int,
      ticksExisted: Short,
      endTime: Short,
      distanceFromCamera: Double
  )

  case class BehaviorPair[A](
      behavior: Behavior[A],
      data: A
  )

  private[TopDanmakuBehaviorsHandler] case class RelationshipWithDepth(child: UUID, parent: UUID, depth: Short)
  private[TopDanmakuBehaviorsHandler] object RelationshipWithDepth {
    implicit private[TopDanmakuBehaviorsHandler] val ordering: Ordering[RelationshipWithDepth] =
      Ordering.by[RelationshipWithDepth, Short](_.depth).reverse
  }
}
