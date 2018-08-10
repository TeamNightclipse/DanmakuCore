/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.shape

import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}

/**
  * Something that can be drawn as danmaku over several ticks. Call [[ShapeHandler.createShape]] to create the shape.
  */
@FunctionalInterface
trait Shape {

  /**
    * Draws a shape for the given tick.
    *
    * @param pos The position to draw the shape at.
    * @param orientation The orientation that was registered for this shape.
    * @param tick The tick.
    * @return The result of drawing for this tick.
    */
  def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult

  /**
    * Executes some sort of effect on the danmaku spawned. This can be used
    * as a cheaper [[net.katsstuff.danmakucore.danmaku.subentity.SubEntity]].
    *
    * @param pos The position the shape was drawn at.
    * @param orientation The orientation that was registered for this shape.
    * @param tick The tick.
    * @param resultThisTick The result for this tick.
    * @param allResults All results that have been produces so far.
    */
  def doEffects(
      pos: Vector3,
      orientation: Quat,
      tick: Int,
      resultThisTick: ShapeResult,
      allResults: Set[ShapeResult]
  ): Unit = {
    //Do nothing by default}
  }
}
