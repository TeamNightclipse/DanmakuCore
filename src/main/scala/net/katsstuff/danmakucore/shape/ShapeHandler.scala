/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.shape

import scala.concurrent.{Future, Promise}

import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object ShapeHandler {
  private var shapeList = Seq.empty[(IShapeEntry, Promise[ShapeResult])]

  /**
    * Creates a new shape with a position as an anchor
    *
    * @return A set that will contain all the danmaku spawned by the shape. The set's content will change over time
    */
  def createShape(shape: Shape, pos: Vector3, orientation: Quat): Future[ShapeResult] =
    createEntry(ShapeEntryPosition(shape, pos, orientation, 0, Set.empty))

  /**
    * Creates a new shape with an entity as an anchor
    *
    * @return A set that will contain all the danmaku spawned by the shape. The set's content will change over time
    */
  def createShape(shape: Shape, anchor: Entity): Future[ShapeResult] =
    createEntry(ShapeEntryEntity(shape, anchor, 0, Set.empty))

  /**
    * Creates a new shape with a living entity(eye height) as an anchor.
    *
    * @return A set that will contain all the danmaku spawned by the shape. The set's content will change over time
    */
  def createShape(shape: Shape, anchor: EntityLivingBase): Future[ShapeResult] =
    createEntry(ShapeEntryEntityLiving(shape, anchor, 0, Set.empty))

  /**
    * Creates a new shape with a from the specific [[ShapeEntry]]
    *
    * @return A set that will contain all the danmaku spawned by the shape. The set's content will change over time
    */
  def createEntry(entry: IShapeEntry): Future[ShapeResult] = {
    val promise = Promise[ShapeResult]
    shapeList = shapeList :+ (entry -> promise)
    promise.future
  }

  @SubscribeEvent
  def onTick(event: TickEvent.WorldTickEvent): Unit = if (event.phase == TickEvent.Phase.START) {
    shapeList = shapeList.flatMap {
      case (entry, promise) =>
        val (result, next) = entry.draw
        promise.success(result)

        for {
          n           <- next
          nextPromise <- result.promise
        } yield n -> nextPromise
    }
  }

  trait IShapeEntry {

    /**
      * Draws this shape, giving it the information it needs.
      *
      * @return If this shape is completed and should be removed.
      */
    def draw: (ShapeResult, Option[IShapeEntry])
  }

  /**
    * A class that specifies how a shape should be drawn.
    */
  trait ShapeEntry extends IShapeEntry {
    def shape: Shape

    def allDrawn: Set[ShapeResult]
    def tick: Int
  }

  abstract private class ShapeEntryDynamicPos[T] extends ShapeEntry {
    def dynPos: T

    override def draw: (ShapeResult, Option[IShapeEntry]) = {
      val pos         = getCurrentPos
      val orientation = getCurrentOrientation
      val ret         = shape.draw(pos, orientation, tick)

      shape.doEffects(pos, orientation, tick, ret, allDrawn)
      if (ret.isDone) (ret, None) else (ret, Some(create(tick + 1, allDrawn + ret)))
    }

    def create(tick: Int, allDrawn: Set[ShapeResult]): IShapeEntry

    protected def getCurrentPos: Vector3
    protected def getCurrentOrientation: Quat
  }

  private case class ShapeEntryEntity(shape: Shape, dynPos: Entity, tick: Int, allDrawn: Set[ShapeResult])
      extends ShapeEntryDynamicPos[Entity] {
    override protected def getCurrentPos                                    = new Vector3(dynPos)
    override protected def getCurrentOrientation: Quat                      = Quat.orientationOf(dynPos)
    override def create(tick: Int, allDrawn: Set[ShapeResult]): IShapeEntry = copy(tick = tick, allDrawn = allDrawn)
  }

  private case class ShapeEntryEntityLiving(
      shape: Shape,
      dynPos: EntityLivingBase,
      tick: Int,
      allDrawn: Set[ShapeResult]
  ) extends ShapeEntryDynamicPos[EntityLivingBase] {
    override protected def getCurrentPos                                    = new Vector3(dynPos)
    override protected def getCurrentOrientation: Quat                      = Quat.orientationOf(dynPos)
    override def create(tick: Int, allDrawn: Set[ShapeResult]): IShapeEntry = copy(tick = tick, allDrawn = allDrawn)
  }

  private case class ShapeEntryPosition(
      shape: Shape,
      pos: Vector3,
      orientation: Quat,
      tick: Int,
      allDrawn: Set[ShapeResult]
  ) extends ShapeEntry {
    override def draw: (ShapeResult, Option[IShapeEntry]) = {
      val ret = shape.draw(pos, orientation, tick)
      shape.doEffects(pos, orientation, tick, ret, allDrawn)
      if (ret.isDone) (ret, None) else (ret, Some(copy(tick = tick + 1, allDrawn = allDrawn + ret)))
    }
  }
}
