package net.katsstuff.danmakucore.danmaku

import java.lang

import jdk.incubator.vector.{FloatVector, IntVector, VectorMask, VectorOperators, VectorSpecies}
import net.katsstuff.danmakucore.danmaku.CompiledDanmakuSystem.Operation
import net.katsstuff.danmakucore.danmaku.DanmakuSystem.OperationType
import net.katsstuff.danmakucore.danmaku.form.Form
import net.katsstuff.danmakucore.math.MutableMat4
import org.joml.Matrix4f

//noinspection DuplicatedCode
class VectorApiCompiledDanmakuSystem(
    _scalars: Array[Float],
    _vectors: Array[Array[Float]], // n * m * 4
    // Builtin vectors
    _ticksExisted: Array[Int],       // n * 4
    _endTime: Array[Int],            // n * 4
    _dead: Array[Boolean],           // n
    _mainColor: Array[Int],          // n * 4
    _secondaryColor: Array[Int],     // n * 4
    _transformMats: Array[Matrix4f], // n * 16
    _modelViewMats: Array[Matrix4f], // n * 16
    _forms: Array[Form],
    // Misc
    _vectorDefaults: Seq[Float],
    _operations: Array[Operation],
    _deadCount: Int,
    _arrayLength: Int,
    _currentSize: Int,
    _addValuesFloatArr: Array[Float],
    _addValuesIntArr: Array[Int],
    _mappings: Map[DanmakuSystem.VectorLink, Int],
    _renderPropertyLinks: Map[String, Int]
) extends CompiledDanmakuSystem(
      _scalars,
      _vectors,
      _ticksExisted,
      _endTime,
      _dead,
      _mainColor,
      _secondaryColor,
      _transformMats,
      _modelViewMats,
      _forms,
      _vectorDefaults,
      _operations,
      _deadCount,
      _arrayLength,
      _currentSize,
      _addValuesFloatArr,
      _addValuesIntArr,
      _mappings,
      _renderPropertyLinks
    ) {
  inline def species: VectorSpecies[lang.Float] = FloatVector.SPECIES_PREFERRED

  inline def vec(inline arr: Array[Float], inline i: Int): FloatVector =
    FloatVector.fromArray(species, arr, i)

  inline def intVec(inline arr: Array[Int], inline i: Int): IntVector =
    IntVector.fromArray(species.withLanes(classOf[lang.Integer]), arr, i)

  private inline def actVec2(
      op: Operation,
      id: Float,
      inline f: (Float, Float) => Float,
      inline fv: (FloatVector, FloatVector) => FloatVector,
      inline fvs: (FloatVector, Float) => FloatVector
  ): Unit =
    val op0  = vectors(op.operands(0))
    val op1  = vectors(op.operands(1))
    val dest = vectors(op.dest(0))

    if op.scalarOperands.nonEmpty then
      val scalarPart = op.scalarOperands.map(i => scalars(i)).foldLeft(id)(f)

      var i = 0
      while i + species.length <= arrayLength do
        fvs(
          fv(vec(op0, i), vec(op1, i)),
          scalarPart
        ).intoArray(dest, i)
        i += species.length

      while i < arrayLength do
        dest(i) = f(f(op0(i), op1(i)), scalarPart)
        i += 1
    else
      var i = 0
      while i + species.length <= arrayLength do
        fv(vec(op0, i), vec(op1, i)).intoArray(dest, i)
        i += species.length

        while i < arrayLength do
          dest(i) = f(op0(i), op1(i))
          i += 1

  end actVec2

  override protected def handleVectorOp(op: Operation): Unit =
    val operands = op.operands.map(i => vectors(i))
    val dest     = vectors(op.dest(0))

    op.tpe match
      case OperationType.Add      => actVec2(op, 0, _ + _, _.add(_), _.add(_))
      case OperationType.Subtract => actVec2(op, 0, _ + _, _.sub(_), _.sub(_))
      case OperationType.Multiply => actVec2(op, 1, _ + _, _.mul(_), _.mul(_))
      case OperationType.Divide   => actVec2(op, 1, _ + _, _.div(_), _.div(_))

      case OperationType.Fma =>
        val op0 = operands(0)
        val op1 = operands(1)
        val op2 = operands(2)

        var i = 0
        while i + species.length <= arrayLength do
          vec(op0, i).fma(vec(op1, i), vec(op2, i)).intoArray(dest, i)
          i += species.length

        while i < arrayLength do
          dest(i) = Math.fma(op0(i), op1(i), op2(i))
          i += 1

      case OperationType.Assign =>
        val op0 = operands(0)

        var i = 0
        while i + species.length <= arrayLength do
          vec(op0, i).intoArray(dest, i)
          i += species.length

        while i < arrayLength do
          dest(i) = op0(i)
          i += 1

      case OperationType.Gravity =>
        val op0 = operands(0)
        val op1 = operands(1)
        var i   = 0
        while i + species.length <= arrayLength do
          val t = intVec(ticksExisted, i)

          vec(op0, i)
            .add(vec(op1, i))
            .mul(t.mul(t).convert(VectorOperators.I2F, 0).div(FloatVector.broadcast(species, 2F)))
            .intoArray(dest, i)

          i += species.length
        end while

        while i < arrayLength do
          val t = ticksExisted(i)
          dest(i) = op0(i) + op1(i) * ((t * t) / 2F)
          i += 1
        end while

      case _ => super.handleVectorOp(op)
  end handleVectorOp

  private inline def local[A](inline f: => A): A = f

  private val recentlyDeadArr: Array[Boolean] = new Array[Boolean](arrayLength)

  override protected def endOperation(): Unit =
    local:
      var i = 0
      while i + species.length <= arrayLength do
        intVec(ticksExisted, i).add(1).intoArray(ticksExisted, i)
        i += species.length

      while i < arrayLength do
        ticksExisted(i) += 1
        i += 1

    local:
      var i = 0
      while i + species.length <= arrayLength do
        val deadMask     = VectorMask.fromArray(species.withLanes(classOf[lang.Integer]), dead, i)
        val overTime     = intVec(endTime, i).lt(intVec(ticksExisted, i))
        val recentlyDead = overTime.andNot(deadMask)

        deadMask.or(overTime).intoArray(dead, i)
        recentlyDead.intoArray(recentlyDeadArr, i)
        deadCount += recentlyDead.trueCount()

        i += species.length
      end while

      while i < arrayLength do
        if endTime(i) < ticksExisted(i) && !dead(i) then
          dead(i) = true
          deadCount += 1
          recentlyDeadArr(i) = true
        else recentlyDeadArr(i) = false
        i += 1
      end while

    local:
      var i = 0
      while i < arrayLength do
        // if recentlyDeadArr(i) then ??? // TODO: Implement multiple stages here
        i += 1

  end endOperation

}
