/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku

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
