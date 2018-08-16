/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package danmaku

import java.util.concurrent.TimeUnit

import scala.util.Random

import org.openjdk.jmh.annotations._

import MathUtil._

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
@Fork(
  value = 1,
  jvmArgsAppend = Array(
    "-XX:+UseSuperWord",
    "-XX:+UnlockDiagnosticVMOptions",
    "-XX:CompileCommand=print,*DanmakuVectorization.alwaysSOA",
    "-XX:-TieredCompilation",
    "-XX:PrintAssemblyOptions=intel"
  )
)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
class DanmakuVectorization {

  //@Param(Array("8", "32", "256", "1024"))
  final val Size = 512

  private var danmakuArr: Array[Danmaku]  = _
  private var danmakuArr2: Array[Danmaku] = _

  var ids: Array[Int]     = _
  var posX: Array[Double] = _
  var posY: Array[Double] = _
  var posZ: Array[Double] = _

  var dirX: Array[Double] = _
  var dirY: Array[Double] = _
  var dirZ: Array[Double] = _

  var motX: Array[Double] = _
  var motY: Array[Double] = _
  var motZ: Array[Double] = _

  var posArr: Array[Vector3] = _
  var dirArr: Array[Vector3] = _
  var motArr: Array[Vector3] = _

  var currentSpeedSq: Array[Double]    = _
  var speedOrig: Array[Double]         = _
  var speedLowerLimit: Array[Double]   = _
  var speedUpperLimit: Array[Double]   = _
  var speedAcceleration: Array[Double] = _

  var gravX: Array[Double] = _
  var gravY: Array[Double] = _
  var gravZ: Array[Double] = _

  var rot: Array[Quat] = _

  var length: Int = 0

  @Setup
  def prepare(): Unit = {
    danmakuArr = Array.tabulate(Size) { i =>
      val pos       = Vector3.randomDirection * (Random.nextInt(), Random.nextInt(), Random.nextInt())
      val direction = Vector3.randomDirection * (Random.nextInt(), Random.nextInt(), Random.nextInt())
      val shot      = ShotData()

      Danmaku(
        shot,
        pos,
        pos,
        direction,
        direction * 0.4D,
        Quat.fromEulerRad(direction.yawRad.toFloat, direction.pitchRad.toFloat, 0F),
        MovementData(0.4D, 0.2D, 0.6D, 0.01D, Vector3.Zero),
        Quat.Identity,
        0,
        0
      )
    }

    length = danmakuArr.length

    danmakuArr2 = new Array[Danmaku](Size)

    ids = new Array[Int](length)

    posX = new Array[Double](length)
    posY = new Array[Double](length)
    posZ = new Array[Double](length)

    dirX = new Array[Double](length)
    dirY = new Array[Double](length)
    dirZ = new Array[Double](length)

    motX = new Array[Double](length)
    motY = new Array[Double](length)
    motZ = new Array[Double](length)

    posArr = new Array[Vector3](length)
    dirArr = new Array[Vector3](length)
    motArr = new Array[Vector3](length)

    currentSpeedSq = new Array[Double](length)
    speedOrig = new Array[Double](length)
    speedLowerLimit = new Array[Double](length)
    speedUpperLimit = new Array[Double](length)
    speedAcceleration = new Array[Double](length)

    gravX = new Array[Double](length)
    gravY = new Array[Double](length)
    gravZ = new Array[Double](length)

    rot = new Array[Quat](length)

    setupData()
  }

  def setupData(): Unit = {
    var i = 0
    while (i < length) {
      val danmaku = danmakuArr(i)
      val delay   = danmaku.currentDelay

      if (delay > 0) {
        if (delay - 1 == 0) {
          if (danmaku.shot.end != 1) {
            //Think we can get away with not sending an update here
            danmakuArr2(i) = danmaku.copy(
              motion = danmaku.direction * danmaku.movement.speedOriginal,
              currentDelay = delay - 1
            )
          }
        } else {
          danmakuArr2(i) = danmaku.copy(
            currentDelay = delay - 1
          )
        }
      } else if (danmaku.ticksExisted <= danmaku.shot.end) {
        posX(i) = danmaku.pos.x
        posY(i) = danmaku.pos.y
        posZ(i) = danmaku.pos.z

        dirX(i) = danmaku.direction.x
        dirY(i) = danmaku.direction.y
        dirZ(i) = danmaku.direction.z

        motX(i) = danmaku.motion.x
        motY(i) = danmaku.motion.y
        motZ(i) = danmaku.motion.z

        speedOrig(i) = danmaku.movement.speedOriginal
        speedLowerLimit(i) = danmaku.movement.lowerSpeedLimit
        speedUpperLimit(i) = danmaku.movement.upperSpeedLimit
        speedAcceleration(i) = danmaku.movement.speedAcceleration

        gravX(i) = danmaku.movement.gravity.x
        gravY(i) = danmaku.movement.gravity.y
        gravZ(i) = danmaku.movement.gravity.z

        rot(i) = danmaku.rotation
      }

      i += 1
    }
  }

  //@Benchmark
  def normalExecution(): Array[DanmakuUpdate] =
    danmakuArr.map(_.update)

  final def accelerate(danmaku: Danmaku): Vector3 = {
    import MathUtil._
    val movement  = danmaku.movement
    val direction = danmaku.direction
    val motion    = danmaku.motion

    val currentSpeed    = motion.length
    val speedAccel      = movement.speedAcceleration
    val upperSpeedLimit = movement.upperSpeedLimit
    val lowerSpeedLimit = movement.lowerSpeedLimit
    if (currentSpeed >=~ upperSpeedLimit && speedAccel >= 0D) direction * upperSpeedLimit
    else if (currentSpeed <=~ lowerSpeedLimit && speedAccel <= 0D) direction * lowerSpeedLimit
    else {
      val newMotion       = motion.offset(direction, speedAccel)
      val newCurrentSpeed = newMotion.length
      if (newCurrentSpeed >~ upperSpeedLimit) direction * upperSpeedLimit
      else if (newCurrentSpeed <~ lowerSpeedLimit) direction * lowerSpeedLimit
      else newMotion
    }
  }

  //@Benchmark
  def traditionalImproved(): Array[Danmaku] = {
    var i = 0
    while (i < length) {
      val danmaku      = danmakuArr(i)
      val newDirection = danmaku.rotation.rotate(danmaku.direction)

      val newMotion = accelerate(danmaku) + danmaku.movement.gravity
      val updated = danmaku.copy(
        motion = newMotion,
        pos = danmaku.pos + newMotion,
        prevPos = danmaku.pos,
        direction = newDirection,
        ticksExisted = danmaku.ticksExisted + 1
      )

      danmakuArr2(i) = updated
      i += 1
    }

    danmakuArr2
  }

  //@Benchmark
  final def rotate(): Unit = {
    var i = 0
    while (i < length) {
      val rotation = rot(i)
      if (rotation ne Quat.Identity) {
        val pure       = Quat(dirX(i), dirY(i), dirZ(i), 0)
        val multiplied = rotation * pure * rotation.conjugate

        dirX(i) = multiplied.x
        dirY(i) = multiplied.y
        dirZ(i) = multiplied.z
      }
      i += 1
    }
  }

  //@Benchmark
  final def calcCurrentSpeed(): Unit = {
    var i = 0
    while (i < length) {
      val x = motX(i)
      val y = motY(i)
      val z = motZ(i)
      currentSpeedSq(i) = x * x + y * y + z * z
      i += 1
    }
  }

  //@Benchmark
  final def calcSpeed(): Unit = {
    calcCurrentSpeed()

    var i = 0
    while (i < length) {
      val speedAccel      = speedAcceleration(i)
      val upperSpeedLimit = speedUpperLimit(i)
      val lowerSpeedLimit = speedLowerLimit(i)
      var speedSq         = currentSpeedSq(i)

      var currentSpeed = Math.sqrt(speedSq)

      if (currentSpeed >=~ upperSpeedLimit && speedAccel >= 0D) {
        motX(i) = dirX(i) * upperSpeedLimit
        motY(i) = dirY(i) * upperSpeedLimit
        motZ(i) = dirZ(i) * upperSpeedLimit
      } else if (currentSpeed <=~ lowerSpeedLimit && speedAccel <= 0D) {
        motX(i) = dirX(i) * lowerSpeedLimit
        motY(i) = dirY(i) * lowerSpeedLimit
        motZ(i) = dirZ(i) * lowerSpeedLimit
      } else {
        val x = motX(i) + dirX(i) * speedAccel
        val y = motY(i) + dirY(i) * speedAccel
        val z = motZ(i) + dirZ(i) * speedAccel

        speedSq = x * x + y * y + z * z
        currentSpeed = Math.sqrt(speedSq)

        if (currentSpeed >~ upperSpeedLimit) {
          motX(i) = dirX(i) * upperSpeedLimit
          motY(i) = dirY(i) * upperSpeedLimit
          motZ(i) = dirZ(i) * upperSpeedLimit
        } else if (currentSpeed <~ lowerSpeedLimit) {
          motX(i) = dirX(i) * lowerSpeedLimit
          motY(i) = dirY(i) * lowerSpeedLimit
          motZ(i) = dirZ(i) * lowerSpeedLimit
        }
      }
      i += 1
    }
  }

  //@Benchmark
  final def addGravity(): Unit = {
    var i = 0
    while (i < length) {
      motX(i) = motX(i) + gravX(i)
      motY(i) = motY(i) + gravY(i)
      motZ(i) = motZ(i) + gravZ(i)
      i += 1
    }
  }

  //@Benchmark
  final def updatePos(): Unit = {
    var i = 0
    while (i < length) {
      posX(i) = posX(i) + motX(i)
      posY(i) = posX(i) + motX(i)
      posZ(i) = posX(i) + motX(i)
      i += 1
    }
  }

  //@Benchmark
  final def buildPos(): Unit = {
    var i = 0
    while (i < length) {
      posArr(i) = Vector3(posX(i), posY(i), posZ(i))
      i += 1
    }
  }

  final def buildDir(): Unit = {
    var i = 0
    while (i < length) {
      dirArr(i) = Vector3(dirX(i), dirY(i), dirZ(i))
      i += 1
    }
  }

  final def buildMot(): Unit = {
    var i = 0
    while (i < length) {
      motArr(i) = Vector3(motX(i), motY(i), motZ(i))
      i += 1
    }
  }

  //@Benchmark
  final def buildResult(): Unit = {
    buildPos()
    buildDir()
    buildMot()

    var i = 0
    while (i < length) {
      val danmaku = danmakuArr(i)

      danmakuArr2(i) = Danmaku(
        danmaku.shot,
        posArr(i),
        danmaku.pos,
        dirArr(i),
        motArr(i),
        danmaku.orientation,
        danmaku.movement,
        danmaku.rotation,
        danmaku.ticksExisted + 1,
        danmaku.currentDelay
      )
      i += 1
    }
  }

  //@Benchmark
  def vectorizedAttempt(): Array[Danmaku] = {
    setupData()
    rotate()
    calcSpeed()
    addGravity()
    updatePos()
    buildResult()

    danmakuArr2
  }

  @Benchmark
  def alwaysSOA(): Array[Danmaku] = {
    rotate()
    calcSpeed()
    addGravity()
    updatePos()

    danmakuArr2
  }
}
