/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.misc

case class IdState[A](run: Int => (Int, A)) {

  def map[B](f: A => B): IdState[B] = IdState { i =>
    val (newId, a) = run(i)
    (newId, f(a))
  }

  def flatMap[B](f: A => IdState[B]): IdState[B] = IdState { i =>
    val (newId, a) = run(i)
    f(a) run newId
  }
}

object IdState {
  def init: IdState[Int] = IdState(i => (i, i))
  def run0[A](state: IdState[A]): A = state.run(0)._2
}