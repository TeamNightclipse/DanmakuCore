package net.katsstuff.danmakucore.danmaku

import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*

import cats.Monad
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.{Codec, DataResult, MapCodec}

case class DanmakuSystem(
    scalars: Seq[DanmakuSystem.Scalar],
    vectors: Seq[DanmakuSystem.Vector],
    operations: Seq[DanmakuSystem.Operation]
) {
  def typeCheck: Either[String, TypedDanmakuSystem] = {

    try {
      Class.forName("jdk.incubator.vector.FloatVector")
      ???
    } catch
      case _: ClassNotFoundException =>
        ???
  }
}
object DanmakuSystem {
  case class Scalar(name: String, default: Float)
  case class Vector(name: String, default: Float, link: Option[VectorLink])
  case class Operation(tpe: OperationType, operands: Seq[String], dest: String | Seq[String])

  enum VectorLink {
    case PosX, PosY, PosZ
    case OldPosX, OldPosY, OldPosZ

    case OrientationX, OrientationY, OrientationZ, OrientationW
    case OldOrientationX, OldOrientationY, OldOrientationZ, OldOrientationW
  }

  enum OperationType {
    case Add, Subtract, Multiply, Divide, Fma, Assign, Gravity, MultiplyQuat, RotateVec
  }

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
    override def pure[A](x: A): DataResult[A]                                           = DataResult.success(x)

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

  private def dataResultToEither[T](dataResult: DataResult[T]): Either[DataResult.PartialResult[T], T] =
    if dataResult.result().isPresent then Right(dataResult.result().get())
    else Left(dataResult.error().get)

  import com.mojang.datafixers.util.Either as MojangEither

  private def toScalaEither[A, B](either: MojangEither[A, B]): Either[A, B] =
    if either.right().isPresent
    then Right(either.right().get())
    else Left(either.left().get())

  private def toMojangEither[A, B](either: Either[A, B]): MojangEither[A, B] = either match
    case Left(value)  => MojangEither.left(value)
    case Right(value) => MojangEither.right(value)

  extension [A](codec: Codec[A]) {
    private def optionFieldOf(name: String): MapCodec[Option[A]] =
      codec.optionalFieldOf(name).xmap[Option[A]](_.toScala, _.toJava)

    private def seqOf: Codec[Seq[A]] = codec.listOf().xmap(_.asScala.toSeq, _.asJava)
  }

  private val scalarCodec: Codec[Scalar] = RecordCodecBuilder.create[Scalar] { builder =>
    builder
      .group(
        Codec.STRING.fieldOf("name").forGetter(_.name),
        Codec.FLOAT.fieldOf("default").forGetter(_.default)
      )
      .apply(builder, Scalar(_, _))
  }

  private val vectorCodec: Codec[Vector] = RecordCodecBuilder.create[Vector] { builder =>
    builder
      .group(
        Codec.STRING.fieldOf("name").forGetter(_.name),
        Codec.FLOAT.fieldOf("default").forGetter(_.default),
        Codec.STRING.xmap(VectorLink.valueOf, _.toString).optionFieldOf("link").forGetter(_.link)
      )
      .apply(builder, Vector(_, _, _))
  }

  private val operationCodec: Codec[Operation] = RecordCodecBuilder.create[Operation] { builder =>
    builder
      .group(
        Codec.STRING.xmap(OperationType.valueOf, _.toString).fieldOf("type").forGetter(_.tpe),
        Codec.STRING.seqOf.fieldOf("operands").forGetter(_.operands),
        Codec
          .either(
            Codec.STRING,
            Codec.STRING.seqOf
          )
          .xmap(toScalaEither(_), toMojangEither(_))
          .xmap(
            _.fold[String | Seq[String]](identity, identity),
            {
              case s: String      => Left(s)
              case s: Seq[String] => Right(s)
            }
          )
          .fieldOf("dest")
          .forGetter(_.dest)
      )
      .apply(builder, Operation(_, _, _))

  }

  val codec: Codec[DanmakuSystem] = RecordCodecBuilder.create[DanmakuSystem] { builder =>
    builder
      .group(
        scalarCodec.seqOf.fieldOf("scalars").forGetter(_.scalars),
        vectorCodec.seqOf.fieldOf("vectors").forGetter(_.vectors),
        operationCodec.seqOf.fieldOf("operations").forGetter(_.operations)
      )
      .apply(builder, DanmakuSystem(_, _, _))
  }
}
