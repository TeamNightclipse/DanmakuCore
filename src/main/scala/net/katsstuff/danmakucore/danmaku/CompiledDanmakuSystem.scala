package net.katsstuff.danmakucore.danmaku

import com.mojang.logging.LogUtils
import net.katsstuff.danmakucore.danmaku.CompiledDanmakuSystem.Operation
import net.katsstuff.danmakucore.danmaku.DanmakuSystem.OperationType
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler.RenderData
import net.katsstuff.danmakucore.danmaku.form.Form
import net.katsstuff.danmakucore.math.Vector3
import net.minecraft.util.{FastColor, Mth}
import org.joml.{Matrix4f, Quaternionf, Vector3f}

//noinspection DuplicatedCode,ScalaWeakerAccess
class CompiledDanmakuSystem(
    protected val scalars: Array[Float],
    protected val vectors: Array[Array[Float]], // n * m * 4
    // Builtin vectors
    protected var ticksExisted: Array[Int],       // n * 4
    protected var endTime: Array[Int],            // n * 4
    protected var dead: Array[Boolean],           // n
    protected var mainColor: Array[Int],          // n * 4
    protected var secondaryColor: Array[Int],     // n * 4
    protected var transformMats: Array[Matrix4f], // n * 16
    protected var modelViewMats: Array[Matrix4f], // n * 16
    protected var forms: Array[Form],
    // Misc
    val vectorDefaults: Seq[Float],
    protected val operations: Array[Operation],
    protected var deadCount: Int,
    protected var arrayLength: Int,
    protected var currentSize: Int,
    protected var addValuesFloatArr: Array[Float],
    protected var addValuesIntArr: Array[Int],
    protected val mappings: Map[DanmakuSystem.VectorLink, Int],
    protected val renderPropertyLinks: Map[String, Int]
) {
  private val logger = LogUtils.getLogger

  protected def resize(newSize: Int): Unit =
    val newTicksExisted   = new Array[Int](newSize)
    val newEndTime        = new Array[Int](newSize)
    val newDead           = new Array[Boolean](newSize)
    val newMainColor      = new Array[Int](newSize)
    val newSecondaryColor = new Array[Int](newSize)
    val newTransformMats  = new Array[Matrix4f](newSize)
    val newModelViewMats  = new Array[Matrix4f](newSize)
    val newForms          = new Array[Form](newSize)

    var i = 0
    while i < vectors.length do
      val newVector = new Array[Float](newSize)
      System.arraycopy(vectors(i), 0, newVector, 0, currentSize)
      vectors(i) = newVector
      i += 1

    System.arraycopy(ticksExisted, 0, newTicksExisted, 0, currentSize)
    System.arraycopy(endTime, 0, newEndTime, 0, currentSize)
    System.arraycopy(dead, 0, newDead, 0, currentSize)
    System.arraycopy(mainColor, 0, newMainColor, 0, currentSize)
    System.arraycopy(secondaryColor, 0, newSecondaryColor, 0, currentSize)
    System.arraycopy(transformMats, 0, newTransformMats, 0, currentSize)
    System.arraycopy(modelViewMats, 0, newModelViewMats, 0, currentSize)
    System.arraycopy(forms, 0, newForms, 0, currentSize)

    ticksExisted = newTicksExisted
    endTime = newEndTime
    dead = newDead
    mainColor = newMainColor
    secondaryColor = newSecondaryColor
    transformMats = newTransformMats
    modelViewMats = newModelViewMats
    forms = newForms
    arrayLength = newSize
  end resize

  protected def compact(): Unit =
    var i = 0
    var j = 0
    while i < arrayLength do
      if !dead(i) then
        if i != j then
          var k = 0
          while k < vectors.length do
            vectors(j)(k) = vectors(i)(k)
            k += 1

          ticksExisted(j) = ticksExisted(i)
          endTime(j) = endTime(i)
          dead(j) = false
          mainColor(j) = mainColor(i)
          secondaryColor(j) = secondaryColor(i)
          transformMats(j) = transformMats(i)
          modelViewMats(j) = modelViewMats(i)
          forms(j) = forms(i)
        j += 1
      i += 1
    end while
    deadCount = 0
    currentSize = j
  end compact

  def compactAndResize(): Unit =
    compact()
    val newSize =
      if Mth.isPowerOfTwo(currentSize)
      then currentSize
      else Mth.smallestEncompassingPowerOfTwo(currentSize)
    resize(newSize)
  end compactAndResize

  def getAddValuesArr(count: Int): (Array[Float], Array[Int]) =
    if count * vectors.length > addValuesFloatArr.length
    then addValuesFloatArr = new Array[Float](count * vectors.length)

    if count * 3 > addValuesIntArr.length
    then addValuesIntArr = new Array[Int](count * 3)

    (addValuesFloatArr, addValuesIntArr)
  end getAddValuesArr

  def addNew(vectorValues: Array[Float], intValues: Array[Int], forms: Array[Form], count: Int): Unit =
    if currentSize + count - 1 >= arrayLength then compactAndResize()
    if currentSize + count - 1 >= arrayLength then resize(Mth.smallestEncompassingPowerOfTwo(arrayLength + 1))

    var i = 0
    while i < count do
      var j = 0
      while j < vectors.length do
        vectors(j)(currentSize + i) = vectorValues(i * vectors.length + j)
        j += 1

      endTime(currentSize + i) = intValues(i * 3)
      mainColor(currentSize + i) = intValues(i * 3 + 1)
      secondaryColor(currentSize + i) = intValues(i * 3 + 1)
      forms(currentSize + i) = forms(i)
      ticksExisted(currentSize + i) = 0
      dead(currentSize + i) = false
      transformMats(currentSize + i) = new Matrix4f()
      modelViewMats(currentSize + i) = new Matrix4f()
      i += 1
    end while

    currentSize += count
  end addNew

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
      while i < currentSize do
        dest(i) = f(f(op0(i), op1(i)), scalarPart)
        i += 1
    else
      var i = 0
      while i < currentSize do
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
        while i < currentSize do
          // dest(i) = Math.fma(op0(i), op1(i), op2(i))
          dest(i) = op0(i) * op1(i) + op2(i) // TODO: Test if this is faster
          i += 1
        end while

      case OperationType.Assign =>
        val op0 = operands(0)
        var i   = 0
        while i < currentSize do
          dest(i) = op0(i)
          i += 1
        end while
        Vector3.Up.normalize

      case OperationType.NormalizeVec =>
        val op0 = operands(0)
        val op1 = operands(1)
        val op2 = operands(2)

        val dest0 = dest
        val dest1 = vectors(op.dest(1))
        val dest2 = vectors(op.dest(2))

        var i = 0
        while i < currentSize do
          val x      = op0(i)
          val y      = op1(i)
          val z      = op2(i)
          val scalar = 1F / Math.sqrt(x * x + y * y + z * z).toFloat
          dest0(i) = x / scalar
          dest1(i) = y / scalar
          dest2(i) = z / scalar
          i += 1
        end while

      case OperationType.Gravity =>
        val op0 = operands(0)
        val op1 = operands(1)
        val op2 = ticksExisted
        var i   = 0
        while i < currentSize do
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
        while i < currentSize do
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
        while i < currentSize do
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

      case OperationType.RgbToColor =>
        val rs = operands(0)
        val gs = operands(1)
        val bs = operands(2)

        val dest = if op.dest(0) == 0 then mainColor else secondaryColor

        var i = 0
        while i < currentSize do
          val r = rs(i)
          val g = gs(i)
          val b = bs(i)

          dest(i) = FastColor.ARGB32.color(255, Mth.floor(r * 255), Mth.floor(g * 255), Mth.floor(b * 255))
          i += 1
        end while

      case OperationType.HsvToColor =>
        val hs = operands(0)
        val ss = operands(1)
        val vs = operands(2)

        val dest = if op.dest(0) == 0 then mainColor else secondaryColor

        var i = 0
        while i < currentSize do
          val h = hs(i)
          val s = ss(i)
          val v = vs(i)

          val c = v * s
          val x = c * (1 - Math.abs((h / 60) % 2 - 1))
          val m = v - c

          val (r, g, b) =
            if h < 60 then (c, x, 0F)
            else if h < 120 then (x, c, 0F)
            else if h < 180 then (0F, c, x)
            else if h < 240 then (0F, x, c)
            else if h < 300 then (x, 0F, c)
            else (c, 0F, x)

          dest(i) =
            FastColor.ARGB32.color(255, Mth.floor((r + m) * 255), Mth.floor((g + m) * 255), Mth.floor((b + m) * 255))
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

      case OperationType.RgbToColor =>
        val r = scalars(op.scalarOperands(0))
        val g = scalars(op.scalarOperands(1))
        val b = scalars(op.scalarOperands(2))

        scalars(op.dest(0)) =
          FastColor.ARGB32.color(255, Mth.floor(r * 255), Mth.floor(g * 255), Mth.floor(b * 255)).toFloat

      case OperationType.HsvToColor =>
        val h = scalars(op.scalarOperands(0))
        val s = scalars(op.scalarOperands(1))
        val v = scalars(op.scalarOperands(2))

        val c = v * s
        val x = c * (1 - Math.abs((h / 60) % 2 - 1))
        val m = v - c

        val (r, g, b) =
          if h < 60 then (c, x, 0F)
          else if h < 120 then (x, c, 0F)
          else if h < 180 then (0F, c, x)
          else if h < 240 then (0F, x, c)
          else if h < 300 then (x, 0F, c)
          else (c, 0F, x)

        scalars(op.dest(0)) = FastColor.ARGB32
          .color(255, Mth.floor((r + m) * 255), Mth.floor((g + m) * 255), Mth.floor((b + m) * 255))
          .toFloat

  end handleScalarOp

  protected def handleOp(op: Operation): Unit =
    if !op.destIsScalar then handleVectorOp(op)
    else handleScalarOp(op)
  end handleOp

  private inline def local[A](inline f: => A): A = f

  protected def endOperation(): Unit =
    local:
      var i = 0
      while i < currentSize do
        ticksExisted(i) += 1
        i += 1

    local:
      var i = 0
      while i < currentSize do
        val thisDead      = ticksExisted(i) > endTime(i)
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

  private def computeTransformMats(partialTicks: Float): Unit = {
    // noinspection DuplicatedCode
    val posX    = vectors(mappings(DanmakuSystem.VectorLink.PosX))
    val posY    = vectors(mappings(DanmakuSystem.VectorLink.PosY))
    val posZ    = vectors(mappings(DanmakuSystem.VectorLink.PosZ))
    val oldPosX = vectors(mappings(DanmakuSystem.VectorLink.OldPosX))
    val oldPosY = vectors(mappings(DanmakuSystem.VectorLink.OldPosY))
    val oldPosZ = vectors(mappings(DanmakuSystem.VectorLink.OldPosZ))

    // noinspection DuplicatedCode
    val scaleX    = vectors(mappings(DanmakuSystem.VectorLink.ScaleX))
    val scaleY    = vectors(mappings(DanmakuSystem.VectorLink.ScaleY))
    val scaleZ    = vectors(mappings(DanmakuSystem.VectorLink.ScaleZ))
    val oldScaleX = vectors(mappings(DanmakuSystem.VectorLink.OldScaleX))
    val oldScaleY = vectors(mappings(DanmakuSystem.VectorLink.OldScaleY))
    val oldScaleZ = vectors(mappings(DanmakuSystem.VectorLink.OldScaleZ))

    val directionX    = vectors(mappings(DanmakuSystem.VectorLink.DirectionX))
    val directionY    = vectors(mappings(DanmakuSystem.VectorLink.DirectionY))
    val directionZ    = vectors(mappings(DanmakuSystem.VectorLink.DirectionZ))
    val oldDirectionX = vectors(mappings(DanmakuSystem.VectorLink.OldDirectionX))
    val oldDirectionY = vectors(mappings(DanmakuSystem.VectorLink.OldDirectionY))
    val oldDirectionZ = vectors(mappings(DanmakuSystem.VectorLink.OldDirectionZ))

    val orientationX = vectors(mappings(DanmakuSystem.VectorLink.OrientationX))
    val orientationY = vectors(mappings(DanmakuSystem.VectorLink.OrientationY))
    val orientationZ = vectors(mappings(DanmakuSystem.VectorLink.OrientationZ))
    val orientationW = vectors(mappings(DanmakuSystem.VectorLink.OrientationW))

    val oldOrientationX = vectors(mappings(DanmakuSystem.VectorLink.OldOrientationX))
    val oldOrientationY = vectors(mappings(DanmakuSystem.VectorLink.OldOrientationY))
    val oldOrientationZ = vectors(mappings(DanmakuSystem.VectorLink.OldOrientationZ))
    val oldOrientationW = vectors(mappings(DanmakuSystem.VectorLink.OldOrientationW))

    val hasPosX = posX != null
    val hasPosY = posY != null
    val hasPosZ = posZ != null

    val hasOldPosX = oldPosX != null
    val hasOldPosY = oldPosY != null
    val hasOldPosZ = oldPosZ != null

    val hasScaleX = scaleX != null
    val hasScaleY = scaleY != null
    val hasScaleZ = scaleZ != null

    val hasOldScaleX = oldScaleX != null
    val hasOldScaleY = oldScaleY != null
    val hasOldScaleZ = oldScaleZ != null

    val hasOrientationX = orientationX != null
    val hasOrientationY = orientationY != null
    val hasOrientationZ = orientationZ != null
    val hasOrientationW = orientationW != null
    val hasOrientation  = hasOrientationX && hasOrientationY && hasOrientationZ && hasOrientationW
    if (hasOrientationX || hasOrientationY || hasOrientationZ || hasOrientationW) && !hasOrientation then
      logger.warn("Orientation is missing one or more components")

    val hasOldOrientationX = oldOrientationX != null
    val hasOldOrientationY = oldOrientationY != null
    val hasOldOrientationZ = oldOrientationZ != null
    val hasOldOrientationW = oldOrientationW != null
    val hasOldOrientation  = hasOldOrientationX && hasOldOrientationY && hasOldOrientationZ && hasOldOrientationW
    if (hasOldOrientationX || hasOldOrientationY || hasOldOrientationZ || hasOldOrientationW) && !hasOldOrientation then
      logger.warn("Old orientation is missing one or more components")

    val hasDirectionX    = directionX != null
    val hasDirectionY    = directionY != null
    val hasDirectionZ    = directionZ != null
    val hasDirection     = hasDirectionX && hasDirectionY && hasDirectionZ
    val hasOldDirectionX = oldDirectionX != null
    val hasOldDirectionY = oldDirectionY != null
    val hasOldDirectionZ = oldDirectionZ != null
    val hasOldDirection  = hasOldDirectionX && hasOldDirectionY && hasOldDirectionZ

    val tempQuat1 = new Quaternionf()
    val tempQuat2 = new Quaternionf()
    val tempQuat3 = new Quaternionf()

    val tempVec1 = Vector3.Zero.asMutable
    val tempVec2 = Vector3.Zero.asMutable
    val tempVec3 = new Vector3f()
    val up       = new Vector3f(0, 1, 0)

    var i: Int = 0
    while i < currentSize do {
      if !dead(i) then {
        val mat = transformMats(i)
        mat.identity()

        mat.scaling(
          if hasScaleX then if hasOldScaleX then Mth.lerp(partialTicks, oldScaleX(i), scaleX(i)) else scaleX(i) else 1F,
          if hasScaleY then if hasOldScaleY then Mth.lerp(partialTicks, oldScaleY(i), scaleY(i)) else scaleY(i) else 1F,
          if hasScaleZ then if hasOldScaleZ then Mth.lerp(partialTicks, oldScaleZ(i), scaleZ(i)) else scaleZ(i) else 1F
        )

        if hasOrientation then
          val orientation = tempQuat1.set(orientationX(i), orientationY(i), orientationZ(i), orientationW(i))
          if hasOldOrientation then
            val oldOrientation =
              tempQuat2.set(oldOrientationX(i), oldOrientationY(i), oldOrientationZ(i), oldOrientationW(i))

            mat.rotate(oldOrientation.slerp(orientation, partialTicks, tempQuat3))
          else mat.rotate(orientation)

        if !hasOrientation && (hasDirectionX || hasDirectionY || hasDirectionZ) then
          if hasDirection && hasOldDirection then
            val direction    = tempVec1.set(directionX(i), directionY(i), directionZ(i))
            val oldDirection = tempVec2.set(oldDirectionX(i), oldDirectionY(i), oldDirectionZ(i))
            oldDirection.slerpTo(direction, partialTicks, tempVec2)
            tempVec3.set(tempVec2.x, tempVec2.y, tempVec2.z)
            mat.lookAlong(tempVec3, up)
          else
            val x =
              if hasDirectionX then
                if hasOldDirectionX then Mth.lerp(partialTicks, oldDirectionX(i), directionX(i)) else directionX(i)
              else 0F
            val y =
              if hasDirectionY then
                if hasOldDirectionY then Mth.lerp(partialTicks, oldDirectionY(i), directionY(i)) else directionY(i)
              else 0F
            val z =
              if hasDirectionZ then
                if hasOldDirectionZ then Mth.lerp(partialTicks, oldDirectionZ(i), directionZ(i)) else directionZ(i)
              else 0F

            mat.lookAlong(x, y, z, 0, 1F, 0F)
          end if

        if (hasPosX || hasPosY || hasPosZ) {
          mat.translate(
            if hasPosX then if hasOldPosX then Mth.lerp(partialTicks, oldPosX(i), posX(i)) else posX(i) else 0F,
            if hasPosY then if hasOldPosY then Mth.lerp(partialTicks, oldPosY(i), posY(i)) else posY(i) else 0F,
            if hasPosZ then if hasOldPosZ then Mth.lerp(partialTicks, oldPosZ(i), posZ(i)) else posZ(i) else 0F
          )
        }
      }

      i += 1
    }
  }

  def getRenderData(partialTicks: Float): Seq[RenderData] = {
    computeTransformMats(partialTicks)
    val renderPropertiesData = renderPropertyLinks.map { case (name, idx) =>
      name -> vectors(idx)
    }

    (0 until currentSize).map { idx =>
      RenderData(
        forms(idx),
        renderPropertiesData.map { case (name, data) => name -> data(idx) },
        transformMats(idx),
        modelViewMats(idx),
        mainColor(idx),
        secondaryColor(idx),
        ticksExisted(idx).toShort, // TODO: Remove once we are ready to remove behaviors
        endTime(idx).toShort,      // TODO: Remove once we are ready to remove behaviors
        -1
      )
    }
  }
}
object CompiledDanmakuSystem {
  case class Operation(
      tpe: OperationType,
      operands: Array[Int],
      scalarOperands: Array[Int],
      dest: Array[Int],
      destIsScalar: Boolean
  )
}
