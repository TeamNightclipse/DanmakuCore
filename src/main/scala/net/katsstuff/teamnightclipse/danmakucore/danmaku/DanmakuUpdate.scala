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
package net.katsstuff.teamnightclipse.danmakucore.danmaku

object DanmakuUpdate {
  def empty: DanmakuUpdate = DanmakuUpdate(None, Nil, Nil)

  def noUpdates(state: DanmakuState): DanmakuUpdate = DanmakuUpdate(Some(state), Nil, Nil)

  def oneUpdate(state: DanmakuState, update: DanmakuUpdateSignal): DanmakuUpdate =
    DanmakuUpdate(Some(state), Seq(update), Nil)
  def multipleUpdates(state: DanmakuState, updates: DanmakuUpdateSignal*): DanmakuUpdate =
    DanmakuUpdate(Some(state), updates, Nil)
}
case class DanmakuUpdate(state: Option[DanmakuState], signals: Seq[DanmakuUpdateSignal], callbacks: Seq[() => Unit]) {

  @inline def isEmpty: Boolean  = state.isEmpty
  @inline def nonEmpty: Boolean = state.nonEmpty

  def addCallbackFunc(f: () => Unit): DanmakuUpdate = copy(callbacks = callbacks :+ f)
  def addCallback(f: => Unit): DanmakuUpdate        = addCallbackFunc(() => f)
  def addCallback(f: Runnable): DanmakuUpdate       = addCallbackFunc(() => f.run())

  def addCallbackIfFunc(cond: Boolean)(f: () => Unit): DanmakuUpdate =
    if (cond) copy(callbacks = callbacks :+ f) else this

  def addCallbackIf(cond: Boolean)(f: => Unit): DanmakuUpdate  = addCallbackIfFunc(cond)(() => f)
  def addCallbackIf(cond: Boolean, f: Runnable): DanmakuUpdate = addCallbackIfFunc(cond)(() => f.run())

  def andThen(f: DanmakuState => DanmakuUpdate): DanmakuUpdate = state match {
    case Some(danState) =>
      val newUpdate = f(danState)
      newUpdate.copy(signals = signals ++ newUpdate.signals, callbacks = callbacks ++ newUpdate.callbacks)
    case None => this
  }

  def andThenWithCallbacks(defaultState: DanmakuState)(f: DanmakuState => DanmakuUpdate): DanmakuUpdate = state match {
    case Some(danState) =>
      val newUpdate = f(danState)
      newUpdate.copy(signals = signals ++ newUpdate.signals, callbacks = callbacks ++ newUpdate.callbacks)
    case None =>
      val callbackUpdate = f(defaultState)
      copy(callbacks = callbacks ++ callbackUpdate.callbacks)
  }
}
