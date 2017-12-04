/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.shape

import java.util

import scala.collection.JavaConverters._

import net.katsstuff.danmakucore.data.{Quat, Vector3}

/**
  * JAVA-API
  *
  * Java API for effect stuff
  */
trait AbstractShape extends Shape {

  /**
    * Executes some sort of effect on the danmaku spawned. This can be used
    * as a cheaper [[net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity]].
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
      allResults: util.Set[ShapeResult]
  ): Unit

  override def doEffects(
      pos: Vector3,
      orientation: Quat,
      tick: Int,
      resultThisTick: ShapeResult,
      allResults: Set[ShapeResult]
  ): Unit = doEffects(pos, orientation, tick, resultThisTick, allResults.asJava)

}
