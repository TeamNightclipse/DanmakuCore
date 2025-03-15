package net.katsstuff.danmakucore.danmaku

import java.util.concurrent.ThreadLocalRandom

import scala.jdk.CollectionConverters.*

import com.google.common.graph.{GraphBuilder, Graphs, ImmutableGraph, Traverser}
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.{Codec, DataResult}
import net.katsstuff.danmakucore.danmaku.form.{DanCoreForms, Form}
import net.katsstuff.danmakucore.util.CodecUtils.*
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

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

  def evalutate(state: DanmakuInstantiation.EvalutationState)(
      implicit registryAccess: RegistryAccess
  ): DanmakuInstantiation.EvalutationState = {
    val operationsMap = operations.map(op => op.id -> op).toMap

    val inputState = DanmakuInstantiation.InternalEvaluationState(
      inputs.view.collect { case DanmakuInstantiation.Input(name, DanmakuInstantiation.VariableType.Float, default) =>
        ("builtin:input", name) -> state.floats.getOrElse(name, Seq(default))
      }.toMap,
      inputs.view.collect { case DanmakuInstantiation.Input(name, DanmakuInstantiation.VariableType.Int, default) =>
        ("builtin:input", name) -> state.ints.getOrElse(name, Seq(default))
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

    val outputValues = DanmakuInstantiation.EvalutationState(
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
object DanmakuInstantiation {
  case class EvalutationState(
      floats: Map[String, Seq[Float]],
      ints: Map[String, Seq[Int]]
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
  }
  case class InternalEvaluationState(
      floats: Map[(String, String), Seq[Float]],
      ints: Map[(String, String), Seq[Int]]
  ) {

    def ++(other: InternalEvaluationState): InternalEvaluationState = InternalEvaluationState(
      floats ++ other.floats,
      ints ++ other.ints
    )

    def getValues(values: Map[String, Value]): EvalutationState = {
      EvalutationState(
        values.flatMap {
          case (id, DanmakuInstantiation.Value.FromVariable(container, name, VariableType.Float)) =>
            Seq((id, floats((container, name))))
          case (id, DanmakuInstantiation.Value.Constant(value, VariableType.Float)) =>
            Seq((id, Seq(value)))
          case _ => Nil
        },
        values.flatMap {
          case (id, DanmakuInstantiation.Value.FromVariable(container, name, VariableType.Int)) =>
            Seq((id, ints((container, name))))
          case (id, DanmakuInstantiation.Value.Constant(value, VariableType.Int)) =>
            Seq((id, Seq(value)))
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
    case Enumerate
    case KnownConstant(constant: ConstantName)
    case Convert(from: VariableType[_], to: VariableType[_])
    case Random[A](tpe: VariableType[A])

    def evaluate(state: EvalutationState): EvalutationState = this match {
      case Math(op) =>
        val aFloat = state.floats.get("a")
        val bFloat = state.floats.get("b")
        val aInt   = state.ints.get("a")
        val bInt   = state.ints.get("b")

        if aFloat.isDefined || bFloat.isDefined then
          val a = aFloat.orElse(aInt.map(_.map(_.toFloat))).getOrElse(Seq(0F))
          val b = bFloat.orElse(bInt.map(_.map(_.toFloat))).getOrElse(Seq(0F))

          val result = op match {
            case MathOp.Add =>
              a.zipAll(b, 0F, 0F).map((a, b) => a + b)
            case MathOp.Subtract =>
              a.zipAll(b, 0F, 0F).map((a, b) => a - b)
            case MathOp.Multiply =>
              a.zipAll(b, 1F, 1F).map((a, b) => a * b)
            case MathOp.Divide =>
              a.zipAll(b, 1F, 1F).map((a, b) => a / b)
            case MathOp.Modulo =>
              a.zipAll(b, 1F, 1F).map((a, b) => a % b)
          }
          EvalutationState(Map("output" -> result), Map.empty)
        else
          val a = aInt.getOrElse(Seq(0))
          val b = bInt.getOrElse(Seq(0))
          val result = op match {
            case MathOp.Add =>
              a.zipAll(b, 0, 0).map((a, b) => a + b)
            case MathOp.Subtract =>
              a.zipAll(b, 0, 0).map((a, b) => a - b)
            case MathOp.Multiply =>
              a.zipAll(b, 1, 1).map((a, b) => a * b)
            case MathOp.Divide =>
              a.zipAll(b, 1, 1).map((a, b) => a / b)
            case MathOp.Modulo =>
              a.zipAll(b, 1, 1).map((a, b) => a % b)
          }
          EvalutationState(Map.empty, Map("output" -> result))

      case Enumerate =>
        val counts = state.ints.getOrElse("count", Seq(1))
        EvalutationState(Map.empty, Map("output" -> counts.flatMap(count => 0 until count)))

      case KnownConstant(constant) =>
        val value = constant match {
          case ConstantName.Pi  => java.lang.Math.PI.toFloat
          case ConstantName.E   => java.lang.Math.E.toFloat
          case ConstantName.Phi => (1 + java.lang.Math.sqrt(5).toFloat) / 2
        }
        EvalutationState(Map("output" -> Seq(value)), Map.empty)

      case Convert(from, to) =>
        (from, to) match {
          case (VariableType.Float, VariableType.Int) =>
            val values = state.floats.getOrElse("value", Seq(0F))
            EvalutationState(Map.empty, Map("output" -> values.map(_.toInt)))
          case (VariableType.Int, VariableType.Float) =>
            val values = state.ints.getOrElse("value", Seq(0))
            EvalutationState(Map("output" -> values.map(_.toFloat)), Map.empty)
          case (from, to) if from == to => state
          case _                        => throw new IllegalStateException("Cannot convert between these types")
        }

      case Random(VariableType.Float) =>
        val mins = state.floats.getOrElse("min", Seq(0F))
        val maxs = state.floats.getOrElse("max", Seq(1F))

        EvalutationState(
          Map("output" -> (for {
            min <- mins
            max <- maxs
          } yield ThreadLocalRandom.current().nextFloat(min, max))),
          Map.empty
        )
      case Random(VariableType.Int) =>
        val mins = state.ints.getOrElse("min", Seq(0))
        val maxs = state.ints.getOrElse("max", Seq(1))
        EvalutationState(
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
    case Math, Enumerate, KnownConstant, Convert, Random
  }
  object FundamentalOpType {
    val codec: Codec[FundamentalOpType] = Codec.STRING.comapFlatMap(
      {
        case "math"      => DataResult.success(Math)
        case "enumerate" => DataResult.success(Enumerate)
        case "constant"  => DataResult.success(KnownConstant)
        case "convert"   => DataResult.success(Convert)
        case "random"    => DataResult.success(Random)
        case _           => DataResult.error(() => "Unknown fundamental op type")
      },
      {
        case Math          => "math"
        case Enumerate     => "enumerate"
        case KnownConstant => "constant"
        case Convert       => "convert"
        case Random        => "random"
      }
    )
  }

  lazy val fundamentalOpCodec: Codec[FundamentalOp] = FundamentalOpType.codec.dispatch[FundamentalOp](
    {
      case FundamentalOp.Math(_)          => FundamentalOpType.Math
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
