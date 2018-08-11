/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.shape

import java.util

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState

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

  private[danmakucore] val promise: Option[Promise[ShapeResult]] = if (!isDone) Some(Promise[ShapeResult]) else None
  val next: Option[Future[ShapeResult]]                          = promise.map(_.future)

  /**
    * The danmaku the shape spawned for this tick.
    */
  def getSpawnedDanmaku: util.Set[DanmakuState] = spawnedDanmaku.asJava
}
