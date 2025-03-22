package net.katsstuff.danmakucore.danmaku

import java.util.concurrent.ThreadLocalRandom

import scala.jdk.CollectionConverters.*
import scala.reflect.ClassTag

import com.google.common.graph.{GraphBuilder, Graphs, ImmutableGraph, Traverser}
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.{Codec, DataResult}
import net.katsstuff.danmakucore.danmaku.form.{DanCoreForms, Form}
import net.katsstuff.danmakucore.math.Vector3
import net.katsstuff.danmakucore.util.CodecUtils.*
import net.katsstuff.danmakucore.util.Sized
import net.katsstuff.danmakucore.util.Sized.Sized
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.{ExtraCodecs, Mth}
import org.joml.{Quaternionf, Vector3f}

//noinspection UnstableApiUsage
case class DanmakuInstantiation(
    inputs: Seq[DanmakuInstantiation.Input[_]],
    operations: Seq[DanmakuInstantiation.Operation],
    outputs: Map[String, DanmakuInstantiation.Value.FromVariable],
    groups: Map[String, DanmakuInstantiation],
    form: Form
) {

  private lazy val graph: ImmutableGraph[String] = {
    val graph = GraphBuilder.directed().allowsSelfLoops(false).immutable[String]()
    graph.addNode("builtin:input")
    operations.foreach { op =>
      graph.addNode(op.id)
      op.inputs.foreach { case (_, value) =>
        value match {
          case DanmakuInstantiation.Value.FromVariable(container, _, _) =>
            graph.putEdge(container, op.id)
          case _ =>
        }
      }
    }
    graph.build()
  }

  private lazy val topoSort: Seq[String] = {
    if Graphs.hasCycle(graph) then throw new IllegalStateException("Graph has a cycle")
    Traverser.forGraph(graph).depthFirstPostOrder("builtin:input").asScala.toSeq.reverse
  }

  def evalutate(state: DanmakuInstantiation.EvaluationState)(
      implicit registryAccess: RegistryAccess
  ): DanmakuInstantiation.EvaluationState = {
    val operationsMap = operations.map(op => op.id -> op).toMap

    val inputState = DanmakuInstantiation.InternalEvaluationState(
      inputs.view.collect { case DanmakuInstantiation.Input(name, DanmakuInstantiation.VariableType.Float, default) =>
        ("builtin:input", name) -> state.floats.getOrElse(name, IArray(default))
      }.toMap,
      inputs.view.collect { case DanmakuInstantiation.Input(name, DanmakuInstantiation.VariableType.Int, default) =>
        ("builtin:input", name) -> state.ints.getOrElse(name, IArray(default))
      }.toMap
    )

    val allValues = topoSort.foldLeft(inputState) {
      case (state, "builtin:input") => state
      case (state, node) =>
        val operation = operationsMap(node)

        val nodeInputValues = state.getValues(operation.inputs)
        val result = operation.behavior match {
          case DanmakuInstantiation.OperationIdentifier.Group(name) =>
            groups(name).evalutate(nodeInputValues)
          case DanmakuInstantiation.OperationIdentifier.NamedOperation(name) =>
            DanmakuInstantiations.registry(registryAccess).get(name).evalutate(nodeInputValues)
          case DanmakuInstantiation.OperationIdentifier.FundamentalOperation(op) =>
            op.evaluate(nodeInputValues)
        }

        state ++ result.asInternal(node)
    }

    val outputValues = DanmakuInstantiation.EvaluationState(
      outputs.view.collect {
        case (id, DanmakuInstantiation.Value.FromVariable(container, name, DanmakuInstantiation.VariableType.Float)) =>
          id -> allValues.floats((container, name))
      }.toMap,
      outputs.view.collect {
        case (id, DanmakuInstantiation.Value.FromVariable(container, name, DanmakuInstantiation.VariableType.Int)) =>
          id -> allValues.ints((container, name))
      }.toMap
    )

    if outputValues.lengthsValid then outputValues
    else throw new IllegalStateException("Output values have different lengths")
  }
}
//noinspection DuplicatedCode, ScalaWeakerAccess
object DanmakuInstantiation {
  case class EvaluationState(
      floats: Map[String, IArray[Float]],
      ints: Map[String, IArray[Int]]
  ) {
    def asInternal(node: String): InternalEvaluationState = InternalEvaluationState(
      floats.map((k, v) => (node, k) -> v),
      ints.map((k, v) => (node, k) -> v)
    )

    lazy val maxLength: Int = Math.max(floats.view.values.map(_.length).max, ints.view.values.map(_.length).max)

    def lengthsValid: Boolean = {
      val maxLength = this.maxLength
      floats.forall((_, v) => v.length == maxLength || v.length == 1) && ints.forall((_, v) =>
        v.length == maxLength || v.length == 1
      )
    }

    def getCompositeArrs[N <: Int](name: String, suffixes: Sized[String, N]): Sized[IArray[Float], N] =
      suffixes.map(suffix => floats.getOrElse(s"$name.$suffix", IArray(0F)))

    inline def mapComposite[Suffixes <: Int, Names <: Int, ExtraNames <: Int, Outputs <: Int, Composite, OpRes](
        names: Sized[String, Names],
        extraNames: Sized[String, ExtraNames],
        suffixes: Sized[String, Suffixes],
        outputs: Sized[String, Outputs],
        inline makeComposite: () => Composite,
        inline setComposite: (Composite, Sized[IArray[Float], Suffixes], Int) => Unit,
        inline setResult: (Composite, OpRes, Sized[Array[Float], Outputs], Int) => Unit,
        inline op: (Sized[Composite, Names], Composite, Sized[IArray[Float], ExtraNames], Int) => OpRes
    ): Map[String, IArray[Float]] = {
      val arrss: Sized[Sized[IArray[Float], Suffixes], Names] = names.map(n => getCompositeArrs(n, suffixes))
      val extraArrs: Sized[IArray[Float], ExtraNames]         = extraNames.map(n => floats.getOrElse(n, IArray(0F)))

      val extraComposite: Composite           = makeComposite()
      val composites: Sized[Composite, Names] = names.map(_ => makeComposite())

      val maxLength = Math.max(arrss.map(_.map(_.length).asSeq.max).asSeq.max, extraArrs.map(_.length).asSeq.max)

      val result: Sized[Array[Float], Outputs] = outputs.map(_ => new Array[Float](maxLength))

      var i = 0
      while i < maxLength do
        arrss
          .zip(composites)
          .asSeq
          .foreach((arrs, composite) => setComposite(composite, arrs, i))

        val opRes = op(composites, extraComposite, extraArrs, i)

        setResult(composites.asSeq.head, opRes, result, i)

        i += 1
      end while

      outputs.zip(result).map((name, result) => name -> result.asInstanceOf[IArray[Float]]).asSeq.toMap
    }

    def hasVec(name: String): Boolean =
      floats.contains(s"$name.vx") && floats.contains(s"$name.vy") && floats.contains(s"$name.vz")

    def hasQuat(name: String): Boolean =
      floats.contains(s"$name.qx") && floats.contains(s"$name.qy") && floats.contains(s"$name.qz") && floats.contains(
        s"$name.qw"
      )

    // noinspection ZeroIndexToHead
    inline def generalMapVec[Names <: Int, ExtraNames <: Int, Outputs <: Int, OpRes](
        names: Sized[String, Names],
        extraNames: Sized[String, ExtraNames],
        outputs: Sized[String, Outputs],
        inline setResult: (Vector3f, OpRes, Sized[Array[Float], Outputs], Int) => Unit,
        inline op: (Sized[Vector3f, Names], Vector3f, Sized[IArray[Float], ExtraNames], Int) => OpRes
    ): Map[String, IArray[Float]] = {
      mapComposite[3, Names, ExtraNames, Outputs, Vector3f, OpRes](
        names,
        extraNames,
        Sized("x", "y", "z"),
        outputs,
        () => new Vector3f(),
        (v, arrs, i) => {
          val x = arrs(0)
          val y = arrs(1)
          val z = arrs(2)

          v.set(x(i % x.length), y(i % y.length), z(i % z.length))
        },
        setResult,
        op
      )
    }

    // noinspection ZeroIndexToHead
    def mapVec(name: String, output: String)(op: Vector3f => Unit): Map[String, IArray[Float]] =
      generalMapVec[1, 0, 3, Unit](
        Sized(name),
        Sized.Empty,
        Sized(s"$output.vx", s"$output.vy", s"$output.vz"),
        (v, _, arr, i) => {
          arr(0)(i) = v.x
          arr(1)(i) = v.y
          arr(2)(i) = v.z
        },
        (v, _, _, _) => op(v(0))
      )

    // noinspection ZeroIndexToHead
    def map2Vec(nameA: String, nameB: String, output: String)(
        op: (Vector3f, Vector3f) => Unit
    ): Map[String, IArray[Float]] = {
      generalMapVec[2, 0, 3, Unit](
        Sized(nameA, nameB),
        Sized.Empty,
        Sized(s"$output.vx", s"$output.vy", s"$output.vz"),
        (v, _, arrs, i) => {
          arrs(0)(i) = v.x
          arrs(1)(i) = v.y
          arrs(2)(i) = v.z
        },
        (v, _, _, _) => op(v(0), v(1))
      )
    }

    // noinspection ZeroIndexToHead
    inline def generalMapQuat[Names <: Int, ExtraNames <: Int, Outputs <: Int, OpRes](
        names: Sized[String, Names],
        extraNames: Sized[String, ExtraNames],
        outputs: Sized[String, Outputs],
        inline setResult: (Quaternionf, OpRes, Sized[Array[Float], Outputs], Int) => Unit,
        inline op: (Sized[Quaternionf, Names], Quaternionf, Sized[IArray[Float], ExtraNames], Int) => OpRes
    ): Map[String, IArray[Float]] = {
      mapComposite[4, Names, ExtraNames, Outputs, Quaternionf, OpRes](
        names,
        extraNames,
        Sized("x", "y", "z", "w"),
        outputs,
        () => new Quaternionf(),
        (v, arrs, i) => {
          val x = arrs(0)
          val y = arrs(1)
          val z = arrs(2)
          val w = arrs(3)

          v.set(x(i % x.length), y(i % y.length), z(i % z.length), w(i % w.length))
        },
        setResult,
        op
      )
    }

    // noinspection ZeroIndexToHead
    def mapQuat(name: String, output: String)(
        op: Quaternionf => Unit
    ): Map[String, IArray[Float]] =
      generalMapQuat[1, 0, 4, Unit](
        Sized(name),
        Sized.Empty,
        Sized(s"$output.qx", s"$output.qy", s"$output.qz", s"$output.qw"),
        (v, _, arr, i) => {
          arr(0)(i) = v.x
          arr(1)(i) = v.y
          arr(2)(i) = v.z
          arr(3)(i) = v.w
        },
        (v, _, _, _) => op(v(0))
      )

    // noinspection ZeroIndexToHead
    def map2Quat(nameA: String, nameB: String, output: String)(
        op: (Quaternionf, Quaternionf) => Unit
    ): Map[String, IArray[Float]] = {
      generalMapQuat[2, 0, 4, Unit](
        Sized(nameA, nameB),
        Sized.Empty,
        Sized(s"$output.qx", s"$output.qy", s"$output.qz", s"$output.qw"),
        (v, _, arr, i) => {
          arr(0)(i) = v.x
          arr(1)(i) = v.y
          arr(2)(i) = v.z
          arr(3)(i) = v.w
        },
        (v, _, _, _) => op(v(0), v(1))
      )
    }
  }
  case class InternalEvaluationState(
      floats: Map[(String, String), IArray[Float]],
      ints: Map[(String, String), IArray[Int]]
  ) {

    def ++(other: InternalEvaluationState): InternalEvaluationState = InternalEvaluationState(
      floats ++ other.floats,
      ints ++ other.ints
    )

    def getValues(values: Map[String, Value]): EvaluationState = {
      EvaluationState(
        values.flatMap {
          case (id, DanmakuInstantiation.Value.FromVariable(container, name, VariableType.Float)) =>
            Seq((id, floats((container, name))))
          case (id, DanmakuInstantiation.Value.Constant(value, VariableType.Float)) =>
            Seq((id, IArray(value)))
          case _ => Nil
        },
        values.flatMap {
          case (id, DanmakuInstantiation.Value.FromVariable(container, name, VariableType.Int)) =>
            Seq((id, ints((container, name))))
          case (id, DanmakuInstantiation.Value.Constant(value, VariableType.Int)) =>
            Seq((id, IArray(value)))
          case _ => Nil
        }
      )
    }
  }

  enum VariableType[A] {
    case Float extends VariableType[Float]
    case Int   extends VariableType[Int]

    def codec: Codec[A] = this match {
      case Float => Codec.FLOAT.xmap(_.toFloat, _.toFloat)
      case Int   => Codec.INT.xmap(_.toInt, _.toInt)
    }
  }
  object VariableType {
    val codec: Codec[VariableType[_]] = Codec.STRING.comapFlatMap(
      {
        case "float" => DataResult.success(Float)
        case "int"   => DataResult.success(Int)
        case _       => DataResult.error(() => "Unknown variable type")
      },
      {
        case Float => "float"
        case Int   => "int"
      }
    )
  }

  case class Input[A](name: String, tpe: VariableType[A], default: A)
  enum Value {
    case FromVariable(container: String, name: String, tpe: VariableType[_])
    case Constant[A](value: A, variableType: VariableType[A])
  }

  enum OperationIdentifier {
    case Group(name: String)
    case NamedOperation(name: ResourceLocation)
    case FundamentalOperation(op: FundamentalOp)
  }
  
  enum FundamentalOp {
    case Math(op: MathOp)
    case VectorMath(op: VectorMathOp)
    case QuatMath(op: QuatMathOp)

    case Normalize
    case Conjugate

    case Lerp(lerpType: LerpType, tpe: CompositeType)

    case MakeQuatFromLook
    case MakeQuatFromAxisAngle(angleType: AngleType)
    case MakeQuatFromEuler(angleType: AngleType)

    case MakeVec
    case MakeQuat

    case DestructVec
    case DestructQuat

    case GetProperty(property: GetPropertyType)

    case Enumerate
    case KnownConstant(constant: ConstantName)
    case Convert(from: VariableType[_], to: VariableType[_])
    case Random[A](tpe: VariableType[A])

    // Inline important here to not box
    inline def zipAll2Inline[A, B, C: ClassTag](
        valueA: IArray[A],
        valueB: IArray[B]
    )(inline op: (A, B) => C): IArray[C] = {
      var i   = 0
      val res = new Array[C](valueA.length.max(valueB.length))
      while i < valueA.length || i < valueB.length do
        val va = valueA(i % valueA.length)
        val vb = valueB(i % valueB.length)

        res(i) = op(va, vb)
        i += 1

      res.asInstanceOf[IArray[C]]
    }

    // noinspection ZeroIndexToHead
    def evaluate(state: EvaluationState): EvaluationState = this match {
      case Math(op) =>
        val aFloat = state.floats.get("a")
        val bFloat = state.floats.get("b")
        val aInt   = state.ints.get("a")
        val bInt   = state.ints.get("b")

        if aFloat.isDefined || bFloat.isDefined then
          val a = aFloat.orElse(aInt.map(_.map(_.toFloat))).getOrElse(IArray(0F))
          val b = bFloat.orElse(bInt.map(_.map(_.toFloat))).getOrElse(IArray(0F))

          val result = op match {
            case MathOp.Add      => zipAll2Inline(a, b)(_ + _)
            case MathOp.Subtract => zipAll2Inline(a, b)(_ - _)
            case MathOp.Multiply => zipAll2Inline(a, b)(_ * _)
            case MathOp.Divide   => zipAll2Inline(a, b)(_ / _)
            case MathOp.Modulo   => zipAll2Inline(a, b)(_ % _)
          }
          EvaluationState(Map("output" -> result), Map.empty)
        else
          val a = aInt.getOrElse(IArray(0))
          val b = bInt.getOrElse(IArray(0))
          val result = op match {
            case MathOp.Add      => zipAll2Inline(a, b)(_ + _)
            case MathOp.Subtract => zipAll2Inline(a, b)(_ - _)
            case MathOp.Multiply => zipAll2Inline(a, b)(_ * _)
            case MathOp.Divide   => zipAll2Inline(a, b)(_ / _)
            case MathOp.Modulo   => zipAll2Inline(a, b)(_ % _)
          }
          EvaluationState(Map.empty, Map("output" -> result))

      case VectorMath(op) =>
        val res = op match
          case VectorMathOp.Add      => state.map2Vec("a", "b", "output")(_.add(_))
          case VectorMathOp.Subtract => state.map2Vec("a", "b", "output")(_.sub(_))
          case VectorMathOp.Multiply => state.map2Vec("a", "b", "output")(_.mul(_))
          case VectorMathOp.Divide   => state.map2Vec("a", "b", "output")(_.div(_))
          case VectorMathOp.Dot      => state.map2Vec("a", "b", "output")(_.dot(_))
          case VectorMathOp.Cross    => state.map2Vec("a", "b", "output")(_.cross(_))
          case VectorMathOp.Reflect  => state.map2Vec("a", "b", "output")(_.reflect(_))

        EvaluationState(res, Map.empty)

      case QuatMath(op) =>
        val res = op match
          case QuatMathOp.Multiply => state.map2Quat("a", "b", "output")(_.mul(_))
          case QuatMathOp.Dot      => state.map2Quat("a", "b", "output")(_.dot(_))

        EvaluationState(res, Map.empty)

      case Normalize =>
        val res =
          if (state.hasQuat("input")) state.mapQuat("input", "output")(_.normalize())
          else state.mapVec("input", "output")(_.normalize())

        EvaluationState(res, Map.empty)

      case Conjugate => EvaluationState(state.mapQuat("input", "output")(_.normalize()), Map.empty)

      case Lerp(lerpType, tpe) =>
        val res = tpe match
          case CompositeType.Vec =>
            state.generalMapVec[2, 1, 3, Unit](
              Sized("a", "b"),
              Sized("t"),
              Sized("output.x", "output.y", "output.z"),
              (v, _, arr, i) => { arr(0)(i) = v.x; arr(1)(i) = v.y; arr(2)(i) = v.z },
              (v1, _, v2, i) => {
                val ts = v2(0)

                lerpType match
                  case LerpType.Linear => v1(0).lerp(v1(1), ts(i % ts.length))
                  case LerpType.Spherical =>
                    val a = v1(0)
                    val b = v1(1)
                    val r = Vector3(a.x, a.y, a.z).slerp(Vector3(b.x, b.y, b.z), ts(i % ts.length))
                    a.set(r.x, r.y, r.z)
              }
            )

          case CompositeType.Quat =>
            state.generalMapQuat[2, 1, 4, Unit](
              Sized("a", "b"),
              Sized("t"),
              Sized("output.x", "output.y", "output.z", "output.w"),
              (v, _, arr, i) => {
                arr(0)(i) = v.x
                arr(1)(i) = v.y
                arr(2)(i) = v.z
                arr(3)(i) = v.w
              },
              (v1, _, v2, i) => {
                val ts = v2(0)

                lerpType match
                  case LerpType.Linear    => v1(0).nlerp(v1(1), ts(i % ts.length))
                  case LerpType.Spherical => v1(0).slerp(v1(1), ts(i % ts.length))
              }
            )

        EvaluationState(res, Map.empty)

      case MakeVec =>
        val res = state.generalMapVec[0, 3, 3, Vector3f](
          Sized.Empty,
          Sized("x", "y", "z"),
          Sized("output.vx", "output.vy", "output.vz"),
          (_, v, arr, i) => {
            arr(0)(i) = v.x
            arr(1)(i) = v.y
            arr(2)(i) = v.z
          },
          (_, v, vs, i) =>
            val x = vs(0)
            val y = vs(1)
            val z = vs(2)
            v.set(x(i % x.length), y(i % y.length), z(i % z.length))
        )
        EvaluationState(res, Map.empty)

      case MakeQuat =>
        val res = state.generalMapQuat[0, 4, 4, Quaternionf](
          Sized.Empty,
          Sized("x", "y", "z", "w"),
          Sized("output.qx", "output.qy", "output.qz", "output.qw"),
          (_, v, arr, i) => {
            arr(0)(i) = v.x
            arr(1)(i) = v.y
            arr(2)(i) = v.z
            arr(3)(i) = v.w
          },
          (_, q, vs, i) =>
            val x = vs(0)
            val y = vs(1)
            val z = vs(2)
            val w = vs(3)
            q.set(x(i % x.length), y(i % y.length), z(i % z.length), w(i % w.length))
        )
        EvaluationState(res, Map.empty)
      case DestructVec =>
        val res = state.generalMapVec[1, 0, 3, Unit](
          Sized("input"),
          Sized.Empty,
          Sized("x", "y", "z"),
          (v, _, arr, i) => {
            arr(0)(i) = v.x
            arr(1)(i) = v.y
            arr(2)(i) = v.z
          },
          (_, _, _, _) => ()
        )
        EvaluationState(res, Map.empty)

      case DestructQuat => 
        val res = state.generalMapQuat[1, 0, 4, Unit](
          Sized("input"),
          Sized.Empty,
          Sized("x", "y", "z", "w"),
          (v, _, arr, i) => {
            arr(0)(i) = v.x
            arr(1)(i) = v.y
            arr(2)(i) = v.z
            arr(3)(i) = v.w
          },
          (_, _, _, _) => ()
        )
        EvaluationState(res, Map.empty)

      case MakeQuatFromLook =>
        val res = state.generalMapVec[2, 0, 4, Float](
          Sized("forward", "up"),
          Sized.Empty,
          Sized("output.qx", "output.qy", "output.qz", "output.qw"),
          (v, w, arr, i) => {
            arr(0)(i) = v.x
            arr(1)(i) = v.y
            arr(2)(i) = v.z
            arr(3)(i) = w
          },
          (vs, _, _, _) => {
            val forward = vs(0)
            val up      = vs(1)
            val quat    = new Quaternionf().lookAlong(forward, up)
            forward.set(quat.x, quat.y, quat.z)
            quat.w
          }
        )
        EvaluationState(res, Map.empty)

      case MakeQuatFromAxisAngle(angleType) =>
        val res = state.generalMapVec[1, 1, 4, Float](
          Sized("axis"),
          Sized("angle"),
          Sized("output.qx", "output.qy", "output.qz", "output.qw"),
          (v, w, arr, i) => {
            arr(0)(i) = v.x
            arr(1)(i) = v.y
            arr(2)(i) = v.z
            arr(3)(i) = w
          },
          (v1, _, v2, i) => {
            val axis   = v1(0)
            val angles = v2(0)
            val angle  = angles(i % angles.length)
            val quat = angleType match
              case AngleType.Radians => new Quaternionf().fromAxisAngleRad(axis, angle)
              case AngleType.Degrees => new Quaternionf().fromAxisAngleDeg(axis, angle)

            axis.set(quat.x, quat.y, quat.z)
            quat.w
          }
        )
        EvaluationState(res, Map.empty)

      case MakeQuatFromEuler(angleType) =>
        val res = state.generalMapVec[1, 0, 4, Float](
          Sized("euler"),
          Sized.Empty,
          Sized("output.qx", "output.qy", "output.qz", "output.qw"),
          (v, w, arr, i) => {
            arr(0)(i) = v.x
            arr(1)(i) = v.y
            arr(2)(i) = v.z
            arr(3)(i) = w
          },
          (v1, _, _, _) => {
            val euler = v1(0)
            val quat = angleType match
              case AngleType.Radians => new Quaternionf().rotationZYX(euler.z, euler.y, euler.x)
              case AngleType.Degrees =>
                new Quaternionf().rotationZYX(
                  scala.math.toRadians(euler.z).toFloat,
                  scala.math.toRadians(euler.y).toFloat,
                  scala.math.toRadians(euler.x).toFloat
                )

            euler.set(quat.x, quat.y, quat.z)
            quat.w
          }
        )
        EvaluationState(res, Map.empty)

      case GetProperty(property) =>
        val res =
          if (state.hasQuat("input"))
            state.generalMapQuat[1, 0, 1, Float](
              Sized("input"),
              Sized.Empty,
              Sized("output"),
              (_, opRes, arr, i) => arr(0)(i) = opRes,
              (v, _, _, _) => {
                property match
                  case GetPropertyType.Length        => Mth.sqrt(v(0).lengthSquared)
                  case GetPropertyType.LengthSquared => v(0).lengthSquared
              }
            )
          else
            state.generalMapVec[1, 0, 1, Float](
              Sized("input"),
              Sized.Empty,
              Sized("output"),
              (_, opRes, arr, i) => arr(0)(i) = opRes,
              (v, _, _, _) => {
                property match
                  case GetPropertyType.Length        => v(0).length
                  case GetPropertyType.LengthSquared => v(0).lengthSquared
              }
            )

        EvaluationState(res, Map.empty)

      case Enumerate =>
        val counts = state.ints.getOrElse("count", IArray(1))
        EvaluationState(Map.empty, Map("output" -> counts.flatMap(count => IArray.tabulate(count)(identity))))

      case KnownConstant(constant) =>
        val value = constant match {
          case ConstantName.Pi  => java.lang.Math.PI.toFloat
          case ConstantName.E   => java.lang.Math.E.toFloat
          case ConstantName.Phi => (1 + java.lang.Math.sqrt(5).toFloat) / 2
        }
        EvaluationState(Map("output" -> IArray(value)), Map.empty)

      case Convert(from, to) =>
        (from, to) match {
          case (VariableType.Float, VariableType.Int) =>
            val values = state.floats.getOrElse("value", IArray(0F))
            EvaluationState(Map.empty, Map("output" -> values.map(_.toInt)))
          case (VariableType.Int, VariableType.Float) =>
            val values = state.ints.getOrElse("value", IArray(0))
            EvaluationState(Map("output" -> values.map(_.toFloat)), Map.empty)
          case (from, to) if from == to => state
          case _                        => throw new IllegalStateException("Cannot convert between these types")
        }

      case Random(VariableType.Float) =>
        val mins = state.floats.getOrElse("min", IArray(0F))
        val maxs = state.floats.getOrElse("max", IArray(1F))

        EvaluationState(
          Map("output" -> (for {
            min <- mins
            max <- maxs
          } yield ThreadLocalRandom.current().nextFloat(min, max))),
          Map.empty
        )
      case Random(VariableType.Int) =>
        val mins = state.ints.getOrElse("min", IArray(0))
        val maxs = state.ints.getOrElse("max", IArray(1))
        EvaluationState(
          Map.empty,
          Map("output" -> (for {
            min <- mins
            max <- maxs
          } yield ThreadLocalRandom.current().nextInt(min, max)))
        )

    }
  }

  enum MathOp {
    case Add, Subtract, Multiply, Divide, Modulo
  }

  enum VectorMathOp {
    case Add, Subtract, Multiply, Divide, Dot, Cross, Reflect
  }

  enum QuatMathOp {
    case Multiply, Dot
  }

  enum AngleType {
    case Radians, Degrees
  }

  enum LerpType {
    case Linear, Spherical
  }

  enum CompositeType {
    case Vec, Quat
  }

  enum GetPropertyType {
    case Length, LengthSquared
  }

  enum ConstantName {
    case Pi, E, Phi
  }

  case class Operation(id: String, behavior: OperationIdentifier, inputs: Map[String, Value])

  lazy val inputCodec: Codec[Input[_]] = VariableType.codec.dispatch[Input[_]](
    (i: Input[_]) => i.tpe,
    { case tpe: VariableType[a] =>
      RecordCodecBuilder.create[Input[_]] { instance =>
        instance
          .group(
            Codec.STRING.fieldOf("name").forGetter(_.name),
            tpe.codec.fieldOf("default").forGetter(_.default.asInstanceOf[a])
          )
          .apply(instance, Input(_, tpe, _))
      }
    }
  )

  enum FundamentalOpType {
    case Math, VectorMath, QuatMath, Normalize, Conjugate, Lerp, GetProperty
    case MakeVec, MakeQuat, DestructVec, DestructQuat
    case MakeQuatFromLook, MakeQuatFromAxisAngle, MakeQuatFromEuler
    case Enumerate, KnownConstant, Convert, Random
  }
  object FundamentalOpType {
    val codec: Codec[FundamentalOpType] = Codec.STRING.comapFlatMap(
      {
        case "math"       => DataResult.success(Math)
        case "vectorMath" => DataResult.success(VectorMath)
        case "quatMath"   => DataResult.success(QuatMath)
        case "normalize"  => DataResult.success(Normalize)
        case "conjugate"  => DataResult.success(Conjugate)
        case "lerp"       => DataResult.success(Lerp)

        case "makeVec"      => DataResult.success(MakeVec)
        case "makeQuat"     => DataResult.success(MakeQuat)
        case "destructVec"  => DataResult.success(DestructVec)
        case "destructQuat" => DataResult.success(DestructQuat)

        case "makeQuatFromLook"      => DataResult.success(MakeQuatFromLook)
        case "makeQuatFromAxisAngle" => DataResult.success(MakeQuatFromAxisAngle)
        case "makeQuatFromEuler"     => DataResult.success(MakeQuatFromEuler)

        case "getProperty" => DataResult.success(GetProperty)
        case "enumerate"   => DataResult.success(Enumerate)
        case "constant"    => DataResult.success(KnownConstant)
        case "convert"     => DataResult.success(Convert)
        case "random"      => DataResult.success(Random)
        case _             => DataResult.error(() => "Unknown fundamental op type")
      },
      {
        case Math       => "math"
        case VectorMath => "vectorMath"
        case QuatMath   => "quatMath"
        case Normalize  => "normalize"
        case Conjugate  => "conjugate"
        case Lerp       => "lerp"

        case MakeVec      => "makeVec"
        case MakeQuat     => "makeQuat"
        case DestructVec  => "destructVec"
        case DestructQuat => "destructQuat"

        case MakeQuatFromLook      => "makeQuatFromLook"
        case MakeQuatFromAxisAngle => "makeQuatFromAxisAngle"
        case MakeQuatFromEuler     => "makeQuatFromEuler"

        case GetProperty   => "getProperty"
        case Enumerate     => "enumerate"
        case KnownConstant => "constant"
        case Convert       => "convert"
        case Random        => "random"
      }
    )
  }

  lazy val fundamentalOpCodec: Codec[FundamentalOp] = FundamentalOpType.codec.dispatch[FundamentalOp](
    {
      case FundamentalOp.Math(_)       => FundamentalOpType.Math
      case FundamentalOp.VectorMath(_) => FundamentalOpType.VectorMath
      case FundamentalOp.QuatMath(_)   => FundamentalOpType.QuatMath
      case FundamentalOp.Normalize     => FundamentalOpType.Normalize
      case FundamentalOp.Conjugate     => FundamentalOpType.Conjugate
      case FundamentalOp.Lerp(_, _)    => FundamentalOpType.Lerp

      case FundamentalOp.MakeVec      => FundamentalOpType.MakeVec
      case FundamentalOp.MakeQuat     => FundamentalOpType.MakeQuat
      case FundamentalOp.DestructVec  => FundamentalOpType.DestructVec
      case FundamentalOp.DestructQuat => FundamentalOpType.DestructQuat

      case FundamentalOp.MakeQuatFromLook         => FundamentalOpType.MakeQuatFromLook
      case FundamentalOp.MakeQuatFromAxisAngle(_) => FundamentalOpType.MakeQuatFromAxisAngle
      case FundamentalOp.MakeQuatFromEuler(_)     => FundamentalOpType.MakeQuatFromEuler

      case FundamentalOp.GetProperty(_)   => FundamentalOpType.GetProperty
      case FundamentalOp.Enumerate        => FundamentalOpType.Enumerate
      case FundamentalOp.KnownConstant(_) => FundamentalOpType.KnownConstant
      case FundamentalOp.Convert(_, _)    => FundamentalOpType.Convert
      case FundamentalOp.Random(_)        => FundamentalOpType.Random
    },
    {
      case FundamentalOpType.Math =>
        RecordCodecBuilder.create[FundamentalOp.Math] { instance =>
          instance
            .group(
              Codec.STRING
                .fieldOf("op")
                .xmap(MathOp.valueOf, _.toString)
                .forGetter(_.op)
            )
            .apply(instance, FundamentalOp.Math(_))
        }
      case FundamentalOpType.VectorMath =>
        RecordCodecBuilder.create[FundamentalOp.VectorMath] { instance =>
          instance
            .group(
              Codec.STRING
                .fieldOf("op")
                .xmap(VectorMathOp.valueOf, _.toString)
                .forGetter(_.op)
            )
            .apply(instance, FundamentalOp.VectorMath(_))
        }
      case FundamentalOpType.QuatMath =>
        RecordCodecBuilder.create[FundamentalOp.QuatMath] { instance =>
          instance
            .group(
              Codec.STRING
                .fieldOf("op")
                .xmap(QuatMathOp.valueOf, _.toString)
                .forGetter(_.op)
            )
            .apply(instance, FundamentalOp.QuatMath(_))
        }

      case FundamentalOpType.Normalize => Codec.unit(FundamentalOp.Normalize)
      case FundamentalOpType.Conjugate => Codec.unit(FundamentalOp.Conjugate)

      case FundamentalOpType.Lerp =>
        RecordCodecBuilder.create[FundamentalOp.Lerp] { instance =>
          instance
            .group(
              Codec.STRING.fieldOf("lerpType").xmap(LerpType.valueOf, _.toString).forGetter(_.lerpType),
              Codec.STRING.fieldOf("tpe").xmap(CompositeType.valueOf, _.toString).forGetter(_.tpe)
            )
            .apply(instance, FundamentalOp.Lerp(_, _))
        }

      case FundamentalOpType.MakeVec      => Codec.unit(FundamentalOp.MakeVec)
      case FundamentalOpType.MakeQuat     => Codec.unit(FundamentalOp.MakeQuat)
      case FundamentalOpType.DestructVec  => Codec.unit(FundamentalOp.DestructVec)
      case FundamentalOpType.DestructQuat => Codec.unit(FundamentalOp.DestructQuat)

      case FundamentalOpType.MakeQuatFromLook => Codec.unit(FundamentalOp.MakeQuatFromLook)
      case FundamentalOpType.MakeQuatFromAxisAngle =>
        RecordCodecBuilder.create[FundamentalOp.MakeQuatFromAxisAngle] { instance =>
          instance
            .group(
              Codec.STRING
                .fieldOf("angleType")
                .xmap(AngleType.valueOf, _.toString)
                .forGetter(_.angleType)
            )
            .apply(instance, FundamentalOp.MakeQuatFromAxisAngle(_))
        }

      case FundamentalOpType.MakeQuatFromEuler =>
        RecordCodecBuilder.create[FundamentalOp.MakeQuatFromEuler] { instance =>
          instance
            .group(
              Codec.STRING
                .fieldOf("angleType")
                .xmap(AngleType.valueOf, _.toString)
                .forGetter(_.angleType)
            )
            .apply(instance, FundamentalOp.MakeQuatFromEuler(_))
        }

      case FundamentalOpType.GetProperty =>
        RecordCodecBuilder.create[FundamentalOp.GetProperty] { instance =>
          instance
            .group(
              Codec.STRING
                .fieldOf("property")
                .xmap(GetPropertyType.valueOf, _.toString)
                .forGetter(_.property)
            )
            .apply(instance, FundamentalOp.GetProperty(_))
        }

      case FundamentalOpType.Enumerate => Codec.unit(FundamentalOp.Enumerate)
      case FundamentalOpType.KnownConstant =>
        RecordCodecBuilder.create[FundamentalOp.KnownConstant] { instance =>
          instance
            .group(
              Codec.STRING
                .fieldOf("constant")
                .xmap(ConstantName.valueOf, _.toString)
                .forGetter(_.constant)
            )
            .apply(instance, FundamentalOp.KnownConstant(_))
        }
      case FundamentalOpType.Convert =>
        RecordCodecBuilder.create[FundamentalOp.Convert] { instance =>
          instance
            .group(
              VariableType.codec.fieldOf("from").forGetter(_.from),
              VariableType.codec.fieldOf("to").forGetter(_.to)
            )
            .apply(instance, FundamentalOp.Convert(_, _))
        }

      case FundamentalOpType.Random =>
        RecordCodecBuilder.create[FundamentalOp.Random[?]] { instance =>
          instance
            .group(
              VariableType.codec.fieldOf("type").forGetter(_.tpe)
            )
            .apply(instance, FundamentalOp.Random(_))
        }
    }
  )

  enum OperationIdentifierType {
    case Named, Fundamental, Group
  }
  object OperationIdentifierType {
    val codec: Codec[OperationIdentifierType] = Codec.STRING.comapFlatMap(
      {
        case "named"       => DataResult.success(Named)
        case "fundamental" => DataResult.success(Fundamental)
        case "inline"      => DataResult.success(Group)
        case _             => DataResult.error(() => "Unknown operation identifier type")
      },
      {
        case Named       => "named"
        case Fundamental => "fundamental"
        case Group       => "inline"
      }
    )
  }

  lazy val operationIdentifierCodec: Codec[OperationIdentifier] =
    OperationIdentifierType.codec.dispatch(
      {
        case OperationIdentifier.NamedOperation(_)       => OperationIdentifierType.Named
        case OperationIdentifier.FundamentalOperation(_) => OperationIdentifierType.Fundamental
        case OperationIdentifier.Group(_)                => OperationIdentifierType.Group
      },
      {
        case OperationIdentifierType.Named =>
          ResourceLocation.CODEC.xmap[OperationIdentifier.NamedOperation](OperationIdentifier.NamedOperation(_), _.name)
        case OperationIdentifierType.Fundamental =>
          fundamentalOpCodec
            .xmap[OperationIdentifier.FundamentalOperation](OperationIdentifier.FundamentalOperation(_), _.op)
        case OperationIdentifierType.Group =>
          Codec.STRING.xmap[OperationIdentifier.Group](OperationIdentifier.Group(_), _.name)
      }
    )

  lazy val valueFromVariableCodec: Codec[Value.FromVariable] = RecordCodecBuilder.create[Value.FromVariable] { instance =>
    instance
      .group(
        Codec.STRING.fieldOf("container").forGetter(_.container),
        Codec.STRING.fieldOf("name").forGetter(_.name),
        VariableType.codec.fieldOf("type").forGetter(_.tpe)
      )
      .apply(instance, Value.FromVariable(_, _, _))
  }

  lazy val constantCodec: Codec[Value.Constant[_]] =
    VariableType.codec
      .dispatch[Value.Constant[_]](
        (v: Value.Constant[_]) => v.variableType,
        { case t: VariableType[a] =>
          RecordCodecBuilder.create { instance =>
            instance
              .group(
                t.codec.fieldOf("value").forGetter(_.value.asInstanceOf[a])
              )
              .apply(instance, Value.Constant(_, t))
          }
        }
      )

  lazy val valueCodec: Codec[Value] = Codec
    .either(
      valueFromVariableCodec,
      constantCodec
    )
    .xmap(
      _.toScala.merge,
      {
        case v: Value.FromVariable => Left(v).toMojangEither
        case v: Value.Constant[_]  => Right(v).toMojangEither
      }
    )

  lazy val operationCodec: Codec[Operation] = RecordCodecBuilder.create { instance =>
    instance
      .group(
        Codec.STRING.fieldOf("id").forGetter(_.id),
        operationIdentifierCodec.fieldOf("behavior").forGetter(_.behavior),
        Codec
          .unboundedMap(Codec.STRING, valueCodec)
          .xmap[Map[String, Value]](_.asScala.toMap, _.asJava)
          .fieldOf("inputs")
          .forGetter(_.inputs)
      )
      .apply(instance, Operation(_, _, _))
  }

  lazy val codec: Codec[DanmakuInstantiation] = ExtraCodecs.lazyInitializedCodec(() =>
    RecordCodecBuilder.create { instance =>
      instance
        .group(
          inputCodec.seqOf.fieldOf("inputs").forGetter(_.inputs),
          operationCodec.seqOf.fieldOf("operations").forGetter(_.operations),
          Codec
            .unboundedMap(Codec.STRING, valueFromVariableCodec)
            .xmap[Map[String, Value.FromVariable]](_.asScala.toMap, _.asJava)
            .fieldOf("outputs")
            .forGetter(_.outputs),
          Codec
            .unboundedMap(Codec.STRING, codec)
            .xmap[Map[String, DanmakuInstantiation]](_.asScala.toMap, _.asJava)
            .fieldOf("groups")
            .forGetter(_.groups),
          DanCoreForms.registry.getCodec.fieldOf("form").forGetter(_.form)
        )
        .apply(instance, DanmakuInstantiation(_, _, _, _, _))
    }
  )
}
