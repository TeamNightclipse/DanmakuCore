package net.katsstuff.danmakucore.util

import scala.compiletime.ops.int._

object Sized {
  opaque type Sized[+A, I <: Int] = Seq[A]

  def apply[A](a1: A): Sized[A, 1]                      = Seq(a1)
  def apply[A](a1: A, a2: A): Sized[A, 2]               = Seq(a1, a2)
  def apply[A](a1: A, a2: A, a3: A): Sized[A, 3]        = Seq(a1, a2, a3)
  def apply[A](a1: A, a2: A, a3: A, a4: A): Sized[A, 4] = Seq(a1, a2, a3, a4)
  
  val Empty: Sized[Nothing, 0] = Seq.empty

  extension [A, I <: Int](sized: Sized[A, I])
    def apply[J <: Int & Singleton](index: J)(using (J < I) =:= true): A = sized(index)

    def map[B](f: A => B): Sized[B, I] = sized.map(f)

    def zip[B](that: Sized[B, I]): Sized[(A, B), I] = sized.zip(that)

    def asSeq: Seq[A] = sized

    def length: I = sized.length.asInstanceOf[I]
}
export Sized.Sized
