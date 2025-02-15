package net.katsstuff.danmakucore.danmaku

import cats.data.{Validated, ValidatedNel}
import cats.syntax.all.*
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.katsstuff.danmakucore.danmaku.form.Form
import net.katsstuff.danmakucore.util.CodecUtils.*
import org.joml.Matrix4f

case class DanmakuSystem(
    scalars: Seq[DanmakuSystem.Scalar],
    vectors: Seq[DanmakuSystem.Vector],
    operations: Seq[DanmakuSystem.Operation]
) {
  def compile(initialSize: Int): ValidatedNel[String, (CompiledDanmakuSystem, DanmakuSystemPopulator)] = {
    val scalarMap = scalars.zipWithIndex.map { case (scalar, idx) =>
      scalar.name -> idx
    }.toMap
    val vectorsMap = vectors.zipWithIndex.map { case (vector, idx) =>
      vector.name -> idx
    }.toMap

    val scalarsArr = scalars.map(_.default).toArray
    val vectorsArr = vectors.map(vector => Array.fill(initialSize)(vector.default)).toArray

    val mappings = vectors.flatMap(vector => vector.link.map(link => link -> vectorsMap(vector.name))).toMap
    val renderPropertyLinks =
      vectors.flatMap(vector => vector.renderPropertyLink.map(link => link -> vectorsMap(vector.name))).toMap

    val compiledOps = operations
      .traverse { op =>
        val dests = op.dest match
          case s: String      => Seq(s)
          case s: Seq[String] => s

        def cond(cond: Boolean, msg: => String): ValidatedNel[String, Unit] =
          if cond then Validated.valid(())
          else Validated.invalidNel(msg)

        val destHasScalar = dests.exists(scalarMap.contains)
        val destIsScalar  = dests.forall(scalarMap.contains)

        val vectorOperands = op.operands.flatMap(s => vectorsMap.get(s)).toArray
        val scalarOperands = op.operands.flatMap(s => scalarMap.get(s)).toArray

        val conditions = Seq(
          cond(
            !destHasScalar || destIsScalar || op.tpe.combinedVectorScalarInputArgs,
            "Cannot mix scalar and vector destinations in an operation"
          ),
          cond(
            dests.length != op.tpe.outputs,
            s"Operation ${op.tpe} expects ${op.tpe.outputs} outputs, but got ${dests.length}"
          ),
          cond(
            !destIsScalar || scalarOperands.length >= op.tpe.minScalarInputArgs,
            s"Operation ${op.tpe} expects at least ${op.tpe.minScalarInputArgs} scalar inputs, but got ${scalarOperands.length}"
          ),
          cond(
            !destIsScalar || scalarOperands.length <= op.tpe.maxScalarInputArgs,
            s"Operation ${op.tpe} expects at most ${op.tpe.maxScalarInputArgs} scalar inputs, but got ${scalarOperands.length}"
          ),
          cond(
            destIsScalar || vectorOperands.length == op.tpe.vectorInputArgs,
            s"Operation ${op.tpe} expects ${op.tpe.vectorInputArgs} vector inputs, but got ${vectorOperands.length}"
          ),
          cond(
            (op.tpe != DanmakuSystem.OperationType.RgbToColor && op.tpe != DanmakuSystem.OperationType.HsvToColor) || destIsScalar || dests == Seq(
              "builtin_mainColor"
            ) || dests == Seq("builtin_secondaryColor"),
            "Invalid destination for color operation"
          )
        )

        conditions.sequence_.map { _ =>
          val destsIndices = op.tpe match
            case DanmakuSystem.OperationType.RgbToColor | DanmakuSystem.OperationType.HsvToColor if !destIsScalar =>
              if (dests == Seq("builtin_mainColor")) Array(0)
              else if (dests == Seq("builtin_secondaryColor")) Array(1)
              else throw new IllegalStateException("Invalid destination for color operation")

            case _ => dests.map(s => vectorsMap.getOrElse(s, scalarMap(s))).toArray

          CompiledDanmakuSystem.Operation(
            tpe = op.tpe,
            operands = vectorOperands,
            scalarOperands = scalarOperands,
            dest = destsIndices,
            destIsScalar = destIsScalar
          )
        }
      }
      .map(_.toArray)

    val system =
      try {
        Class.forName("jdk.incubator.vector.FloatVector")
        compiledOps.map { ops =>
          new VectorApiCompiledDanmakuSystem(
            _scalars = scalarsArr,
            _vectors = vectorsArr,
            _ticksExisted = new Array[Int](initialSize),
            _endTime = new Array[Int](initialSize),
            _dead = new Array[Boolean](initialSize),
            _mainColor = new Array[Int](initialSize),
            _secondaryColor = new Array[Int](initialSize),
            _transformMats = new Array[Matrix4f](initialSize),
            _modelViewMats = new Array[Matrix4f](initialSize),
            _forms = new Array[Form](initialSize),
            _operations = ops,
            _deadCount = 0,
            _arrayLength = initialSize,
            _currentSize = 0,
            _addValuesFloatArr = new Array[Float](0),
            _addValuesIntArr = new Array[Int](0),
            _mappings = mappings,
            _renderPropertyLinks = renderPropertyLinks
          )
        }
      } catch
        case _: ClassNotFoundException =>
          compiledOps.map { ops =>
            new CompiledDanmakuSystem(
              scalars = scalarsArr,
              vectors = vectorsArr,
              ticksExisted = new Array[Int](initialSize),
              endTime = new Array[Int](initialSize),
              dead = new Array[Boolean](initialSize),
              mainColor = new Array[Int](initialSize),
              secondaryColor = new Array[Int](initialSize),
              transformMats = new Array[Matrix4f](initialSize),
              modelViewMats = new Array[Matrix4f](initialSize),
              forms = new Array[Form](initialSize),
              operations = ops,
              deadCount = 0,
              arrayLength = initialSize,
              currentSize = 0,
              addValuesFloatArr = new Array[Float](0),
              addValuesIntArr = new Array[Int](0),
              mappings = mappings,
              renderPropertyLinks = renderPropertyLinks
            )
          }

    system.map(system => system -> new DanmakuSystemPopulator(vectorsMap, vectors.map(v => v.name -> v.default).toMap, system))
  }
}
object DanmakuSystem {
  case class Scalar(name: String, default: Float)
  case class Vector(name: String, default: Float, link: Seq[VectorLink], renderPropertyLink: Seq[String])
  case class Operation(tpe: OperationType, operands: Seq[String], dest: String | Seq[String])

  enum VectorLink {
    case PosX, PosY, PosZ
    case OldPosX, OldPosY, OldPosZ

    case ScaleX, ScaleY, ScaleZ
    case OldScaleX, OldScaleY, OldScaleZ

    case OrientationX, OrientationY, OrientationZ, OrientationW
    case OldOrientationX, OldOrientationY, OldOrientationZ, OldOrientationW

    case DirectionX, DirectionY, DirectionZ
    case OldDirectionX, OldDirectionY, OldDirectionZ

    // TODO: Base this on more experimentally verified data
    def cost: Int = this match
      case ScaleX | ScaleY | ScaleZ                                              => 0
      case OldScaleX | OldScaleY | OldScaleZ                                     => 3
      case OrientationX | OrientationY | OrientationZ | OrientationW             => 64 / 4
      case OldOrientationX | OldOrientationY | OldOrientationZ | OldOrientationW => 32 / 4
      case PosX | PosY | PosZ                                                    => 12 / 3
      case OldPosX | OldPosY | OldPosZ                                           => 3
      case DirectionX | DirectionY | DirectionZ                                  => 144 / 3
      case OldDirectionX | OldDirectionY | OldDirectionZ                         => 36 / 3
  }

  enum OperationType {
    case Add, Subtract, Multiply, Divide, Fma
    case Assign, NormalizeVec
    case MultiplyQuat, RotateVec
    case Gravity, RgbToColor, HsvToColor

    def combinedVectorScalarInputArgs: Boolean = this match
      case Add | Subtract | Multiply | Divide | Fma => true
      case _                                        => false

    def maxScalarInputArgs: Int = this match
      case Add | Subtract | Multiply | Divide => Int.MaxValue
      case _                                  => vectorInputArgs

    def minScalarInputArgs: Int = this match
      case Add | Subtract | Multiply | Divide => 1
      case _                                  => vectorInputArgs

    def vectorInputArgs: Int = this match
      case Add | Subtract | Multiply | Divide | Fma => 2
      case Assign                                   => 1
      case NormalizeVec                             => 3
      case MultiplyQuat                             => 8
      case RotateVec                                => 7
      case Gravity                                  => 2
      case RgbToColor | HsvToColor                  => 3

    def outputs: Int = this match
      case Add | Subtract | Multiply | Divide | Fma => 1
      case Assign                                   => 1
      case NormalizeVec                             => 3
      case MultiplyQuat                             => 4
      case RotateVec                                => 3
      case Gravity                                  => 1
      case RgbToColor | HsvToColor                  => 1

    // TODO: Base this on more experimentally verified data
    def cost: Int = this match
      case Add | Subtract | Multiply | Divide | Fma => 1
      case Assign                                   => 0
      case NormalizeVec                             => 12
      case MultiplyQuat                             => 28
      case RotateVec                                => 30
      case Gravity                                  => 6
      case RgbToColor                               => 9
      case HsvToColor                               => 20
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
        Codec.STRING.xmap(VectorLink.valueOf, _.toString).seqOf.fieldOf("link").forGetter(_.link),
        Codec.STRING.seqOf.fieldOf("renderPropertyLink").forGetter(_.renderPropertyLink)
      )
      .apply(builder, Vector(_, _, _, _))
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
          .xmap[Either[String, Seq[String]]](_.toScala, _.toMojangEither)
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
