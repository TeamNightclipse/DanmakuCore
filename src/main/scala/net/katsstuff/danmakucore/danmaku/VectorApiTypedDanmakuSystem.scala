package net.katsstuff.danmakucore.danmaku

import java.lang

import jdk.incubator.vector.{FloatVector, VectorMask, VectorSpecies}
import net.katsstuff.danmakucore.danmaku.DanmakuSystem.OperationType
import net.katsstuff.danmakucore.danmaku.TypedDanmakuSystem.Operation
import net.minecraft.resources.ResourceLocation

class VectorApiTypedDanmakuSystem(
    scalars: Array[Float],
    vectors: Array[Array[Float]],
    ticksExisted: Array[Float],
    endTime: Array[Float],
    dead: Array[Boolean],
    operations: Array[Operation],
    deadCount: Int,
    arrayLength: Int
) extends TypedDanmakuSystem(
      scalars,
      vectors,
      ticksExisted,
      endTime,
      dead,
      operations,
      deadCount,
      arrayLength
    ) {
  inline def species: VectorSpecies[lang.Float] = FloatVector.SPECIES_PREFERRED

  inline def vec(inline arr: Array[Float], inline i: Int): FloatVector =
    FloatVector.fromArray(species, arr, i)

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
          val t = vec(ticksExisted, i)

          vec(op0, i)
            .add(vec(op1, i))
            .mul(t.mul(t).div(2F))
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
        vec(ticksExisted, i).add(1F).intoArray(ticksExisted, i)
        i += species.length

      while i < arrayLength do
        ticksExisted(i) += 1F
        i += 1

    local:
      var i = 0
      while i + species.length <= arrayLength do
        val deadMask     = VectorMask.fromArray(species, dead, i)
        val overTime     = vec(endTime, i).lt(vec(ticksExisted, i))
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
