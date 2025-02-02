package net.katsstuff.danmakucore.danmaku

import net.katsstuff.danmakucore.danmaku.DanmakuSystem.OperationType
import net.katsstuff.danmakucore.danmaku.TypedDanmakuSystem.Operation

class TypedDanmakuSystem(
    protected val scalars: Array[Float],
    protected val vectors: Array[Array[Float]],
    protected val ticksExisted: Array[Float],
    protected val endTime: Array[Float],
    protected val dead: Array[Boolean],
    protected val operations: Array[Operation],
    protected var deadCount: Int,
    protected val arrayLength: Int
) {

  private inline def actVec2(
      op: Operation,
      id: Float,
      inline f: (Float, Float) => Float
  ): Unit =
    val op0  = vectors(op.operands(0))
    val op1  = vectors(op.operands(1))
    val dest = vectors(op.dest(0))

    if op.scalarOperands.nonEmpty then
      val scalarPart = op.scalarOperands.map(i => scalars(i)).foldLeft(id)(f)

      var i = 0
      while i < arrayLength do
        dest(i) = f(f(op0(i), op1(i)), scalarPart)
        i += 1
    else
      var i = 0
      while i < arrayLength do
        dest(i) = f(op0(i), op1(i))
        i += 1
  end actVec2

  protected def handleVectorOp(op: Operation): Unit =
    val operands = op.operands.map(i => vectors(i))
    val dest     = vectors(op.dest(0))

    op.tpe match
      case OperationType.Add      => actVec2(op, 0, _ + _)
      case OperationType.Subtract => actVec2(op, 0, _ - _)
      case OperationType.Multiply => actVec2(op, 1, _ * _)
      case OperationType.Divide   => actVec2(op, 1, _ / _)

      case OperationType.Fma =>
        val op0 = operands(0)
        val op1 = operands(1)
        val op2 = operands(2)

        var i = 0
        while i < arrayLength do
          // dest(i) = Math.fma(op0(i), op1(i), op2(i))
          dest(i) = op0(i) * op1(i) + op2(i) // TODO: Test if this is faster
          i += 1
        end while

      case OperationType.Assign =>
        val op0 = operands(0)
        var i   = 0
        while i < arrayLength do
          dest(i) = op0(i)
          i += 1
        end while

      case OperationType.Gravity =>
        val op0 = operands(0)
        val op1 = operands(1)
        val op2 = ticksExisted
        var i   = 0
        while i < arrayLength do
          val t = op2(i)
          dest(i) = op0(i) + op1(i) * ((t * t) / 2F)
          i += 1
        end while

      case OperationType.MultiplyQuat =>
        val xs = operands(0)
        val ys = operands(1)
        val zs = operands(2)
        val ws = operands(3)

        val x2s = operands(4)
        val y2s = operands(5)
        val z2s = operands(6)
        val w2s = operands(7)

        val dx = dest
        val dy = vectors(op.dest(1))
        val dz = vectors(op.dest(2))
        val dw = vectors(op.dest(3))

        var i = 0
        while i < arrayLength do
          val x = xs(i)
          val y = ys(i)
          val z = zs(i)
          val w = ws(i)

          val x2 = x2s(i)
          val y2 = y2s(i)
          val z2 = z2s(i)
          val w2 = w2s(i)

          dx(i) = w * x2 + x * w2 + y * z2 - z * y2
          dy(i) = w * y2 + y * w2 + z * x2 - x * z2
          dz(i) = w * z2 + z * w2 + x * y2 - y * x2
          dw(i) = w * w2 - x * x2 - y * y2 - z * z2
          i += 1
        end while

      case OperationType.RotateVec =>
        val rxs = operands(0)
        val rys = operands(1)
        val rzs = operands(2)
        val rws = operands(3)

        val vxs = operands(4)
        val vys = operands(5)
        val vzs = operands(6)

        val dx = dest
        val dy = vectors(op.dest(1))
        val dz = vectors(op.dest(2))

        var i = 0
        while i < arrayLength do
          val vx = vxs(i)
          val vy = vys(i)
          val vz = vzs(i)
          val rx = rxs(i)
          val ry = rys(i)
          val rz = rzs(i)
          val rw = rws(i)

          val tx = 2 * (ry * vz - rz * vy)
          val ty = 2 * (rz * vx - rx * vz)
          val tz = 2 * (rx * vy - ry * vx)

          val cx = ry * tz - rz * ty
          val cy = rz * tx - rx * tz
          val cz = rx * ty - ry * tx

          dx(i) = vx + rw * tx + cx
          dy(i) = vy + rw * ty + cy
          dz(i) = vz + rw * tz + cz

          i += 1
        end while
  end handleVectorOp

  protected def handleScalarOp(op: Operation): Unit =
    op.tpe match
      case OperationType.Add      => scalars(op.dest(0)) = op.scalarOperands.map(i => scalars(i)).sum
      case OperationType.Subtract => scalars(op.dest(0)) = -op.scalarOperands.map(i => scalars(i)).sum
      case OperationType.Multiply => scalars(op.dest(0)) = op.scalarOperands.map(i => scalars(i)).product
      case OperationType.Divide   => scalars(op.dest(0)) = op.scalarOperands.map(i => scalars(i)).fold(1F)(_ / _)
      case OperationType.Fma =>
        scalars(op.dest(0)) =
          Math.fma(scalars(op.scalarOperands(0)), scalars(op.scalarOperands(1)), scalars(op.scalarOperands(2)))

      case OperationType.Assign  => scalars(op.dest(0)) = scalars(op.scalarOperands(0))
      case OperationType.Gravity => throw new IllegalArgumentException("Gravity operation cannot be scalar")
      case OperationType.MultiplyQuat =>
        val x = scalars(op.scalarOperands(0))
        val y = scalars(op.scalarOperands(1))
        val z = scalars(op.scalarOperands(2))
        val w = scalars(op.scalarOperands(3))

        val x2 = scalars(op.scalarOperands(4))
        val y2 = scalars(op.scalarOperands(5))
        val z2 = scalars(op.scalarOperands(6))
        val w2 = scalars(op.scalarOperands(7))

        scalars(op.dest(0)) = w * x2 + x * w2 + y * z2 - z * y2
        scalars(op.dest(1)) = w * y2 + y * w2 + z * x2 - x * z2
        scalars(op.dest(2)) = w * z2 + z * w2 + x * y2 - y * x2
        scalars(op.dest(3)) = w * w2 - x * x2 - y * y2 - z * z2

      case OperationType.RotateVec =>
        val rx = scalars(op.scalarOperands(0))
        val ry = scalars(op.scalarOperands(1))
        val rz = scalars(op.scalarOperands(2))
        val rw = scalars(op.scalarOperands(3))

        val vx = scalars(op.scalarOperands(4))
        val vy = scalars(op.scalarOperands(5))
        val vz = scalars(op.scalarOperands(6))

        val tx = 2 * (ry * vz - rz * vy)
        val ty = 2 * (rz * vx - rx * vz)
        val tz = 2 * (rx * vy - ry * vx)

        val cx = ry * tz - rz * ty
        val cy = rz * tx - rx * tz
        val cz = rx * ty - ry * tx

        scalars(op.dest(0)) = vx + rw * tx + cx
        scalars(op.dest(1)) = vy + rw * ty + cy
        scalars(op.dest(2)) = vz + rw * tz + cz

  protected def handleOp(op: Operation): Unit =
    if !op.destIsScalar then handleVectorOp(op)
    else handleScalarOp(op)
  end handleOp

  private inline def local[A](inline f: => A): A = f

  protected def endOperation(): Unit =
    local:
      var i = 0
      while i < arrayLength do
        ticksExisted(i) += 1
        i += 1

    local:
      var i = 0
      while i < arrayLength do
        val thisDead = ticksExisted(i) > endTime(i)
        val wasDeadBefore = dead(i)
        if (thisDead && !wasDeadBefore) {
          deadCount += 1
          // TODO: Implement multiple stages here
        }

        dead(i) = dead(i) || thisDead
        i += 1

  end endOperation

  def tick(): Unit =
    var i = 0
    while i < operations.length do
      handleOp(operations(i))
      i += 1

    endOperation()
  end tick
}
object TypedDanmakuSystem {
  case class Operation(
      tpe: OperationType,
      operands: Array[Int],
      scalarOperands: Array[Int],
      dest: Array[Int],
      destIsScalar: Boolean
  )
}
