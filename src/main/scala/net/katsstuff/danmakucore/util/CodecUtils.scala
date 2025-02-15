package net.katsstuff.danmakucore.util

import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*

import cats.Monad
import com.mojang.datafixers.util.Either as MojangEither
import com.mojang.serialization.{Codec, DataResult, MapCodec}

object CodecUtils:

  given Monad[DataResult] with {
    override def map[A, B](fa: DataResult[A])(f: A => B): DataResult[B] = fa.map(a => f(a))

    override def map2[A, B, Z](fa: DataResult[A], fb: DataResult[B])(f: (A, B) => Z): DataResult[Z] =
      fa.apply2((a, b) => f(a, b), fb)

    override def map3[A0, A1, A2, Z](f0: DataResult[A0], f1: DataResult[A1], f2: DataResult[A2])(
        f: (A0, A1, A2) => Z
    ): DataResult[Z] =
      f0.apply3((a, b, c) => f(a, b, c), f1, f2)

    override def ap[A, B](ff: DataResult[A => B])(fa: DataResult[A]): DataResult[B] = fa.ap(ff.map(f => (a: A) => f(a)))

    override def flatMap[A, B](fa: DataResult[A])(f: A => DataResult[B]): DataResult[B] = fa.flatMap(a => f(a))

    override def pure[A](x: A): DataResult[A] = DataResult.success(x)

    @tailrec
    final override def tailRecM[A, B](a: A)(f: A => DataResult[Either[A, B]]): DataResult[B] = {
      val result = f(a)
      if result.error.isPresent then result.asInstanceOf[DataResult[B]]
      else
        result.result.get match {
          case Left(b1) => tailRecM(b1)(f)
          case Right(v) => map(result)(_ => v)
        }
    }
  }

  extension [T](dataResult: DataResult[T])
    def toScalaEither: Either[DataResult.PartialResult[T], T] =
      if dataResult.result().isPresent then Right(dataResult.result().get())
      else Left(dataResult.error().get)

  extension [A, B](either: MojangEither[A, B])
    def toScala: Either[A, B] =
      if either.right().isPresent
      then Right(either.right().get())
      else Left(either.left().get())

  extension [A, B](either: Either[A, B])
    def toMojangEither: MojangEither[A, B] = either match
      case Left(value)  => MojangEither.left(value)
      case Right(value) => MojangEither.right(value)

  extension [A](codec: Codec[A]) {
    def optionFieldOf(name: String): MapCodec[Option[A]] =
      codec.optionalFieldOf(name).xmap[Option[A]](_.toScala, _.toJava)

    def seqOf: Codec[Seq[A]] = codec.listOf().xmap(_.asScala.toSeq, _.asJava)
  }
