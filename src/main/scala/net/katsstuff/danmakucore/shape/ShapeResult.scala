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
import scala.concurrent.{Future, Promise}

import net.katsstuff.danmakucore.danmaku.DanmakuState

/**
  * The result of a shape drawing for a single tick.
  */
object ShapeResult {

  /**
    * JAVA-API
    *
    * Creates a result signifying that the shape is done.
    *
    * @param spawnedDanmaku The danmaku the shape spawned for this tick.
    */
  def done(spawnedDanmaku: util.Set[DanmakuState]) = new ShapeResult(true, spawnedDanmaku.asScala.toSet)

  /**
    * Creates a result signifying that the shape is done.
    *
    * @param spawnedDanmaku The danmaku the shape spawned for this tick.
    */
  def done(spawnedDanmaku: Set[DanmakuState]) = new ShapeResult(true, spawnedDanmaku)

  /**
    * JAVA-API
    *
    * Creates a result signifying that the shape is not done.
    *
    * @param spawnedDanmaku The danmaku the shape spawned for this tick.
    */
  def notDone(spawnedDanmaku: util.Set[DanmakuState]) = new ShapeResult(false, spawnedDanmaku.asScala.toSet)

  /**
    * Creates a result signifying that the shape is not done.
    *
    * @param spawnedDanmaku The danmaku the shape spawned for this tick.
    */
  def notDone(spawnedDanmaku: Set[DanmakuState]) = new ShapeResult(false, spawnedDanmaku)

  /**
    * Creates a result of s drawn shape. If possible, prefer one of the other static methods.
    */
  def of(done: Boolean, drawn: util.Set[DanmakuState]) = new ShapeResult(done, drawn.asScala.toSet)
}
case class ShapeResult(isDone: Boolean, spawnedDanmaku: Set[DanmakuState]) {

  private[danmakucore] val promise: Option[Promise[ShapeResult]] = if(!isDone) Some(Promise[ShapeResult]) else None
  val next: Option[Future[ShapeResult]] = promise.map(_.future)

  /**
    * The danmaku the shape spawned for this tick.
    */
  def getSpawnedDanmaku: util.Set[DanmakuState] = spawnedDanmaku.asJava
}
