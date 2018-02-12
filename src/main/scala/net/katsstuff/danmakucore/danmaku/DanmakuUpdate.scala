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
  def none(state: DanmakuState): DanmakuUpdate = DanmakuUpdate(state, Nil, Nil)

  def one(state: DanmakuState, update: DanmakuUpdateSignal):        DanmakuUpdate = DanmakuUpdate(state, Seq(update), Nil)
  def multiple(state: DanmakuState, updates: DanmakuUpdateSignal*): DanmakuUpdate = DanmakuUpdate(state, updates, Nil)

  def andThen(option: Option[DanmakuUpdate])(f: DanmakuState => Option[DanmakuUpdate]): Option[DanmakuUpdate] =
    option.flatMap {
      case DanmakuUpdate(state, signals, callbacks) =>
        f(state).map(
          newUpdate =>
            newUpdate.copy(signals = signals ++ newUpdate.signals, callbacks = callbacks ++ newUpdate.callbacks)
        )
    }
}
case class DanmakuUpdate(state: DanmakuState, signals: Seq[DanmakuUpdateSignal], callbacks: Seq[() => Unit]) {

  def addCallback(f: () => Unit): DanmakuUpdate = copy(callbacks = callbacks :+ f)
}
