package net.katsstuff.danmakucore.danmaku.behaviors

import java.util.UUID

import scala.collection.mutable

import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler.DanmakuSpawnData
import net.katsstuff.danmakucore.danmaku.behaviors.MainColumns.RequiredColumns
import net.katsstuff.danmakucore.danmaku.form.Form
import net.katsstuff.danmakucore.math.{MutableQuat, Quat}

//noinspection DuplicatedCode
class MainColumns(maxColumnSize: Int, required: Seq[RequiredColumns]) {
  val id: Array[UUID] = new Array[UUID](maxColumnSize)

  val posX: Array[Float] = if (required.contains(RequiredColumns.PosX)) new Array[Float](maxColumnSize) else null
  val posY: Array[Float] = if (required.contains(RequiredColumns.PosY)) new Array[Float](maxColumnSize) else null
  val posZ: Array[Float] = if (required.contains(RequiredColumns.PosZ)) new Array[Float](maxColumnSize) else null

  def pos(i: Int): Array[Float] = i match {
    case 0 => posX
    case 1 => posY
    case 2 => posZ
  }

  val oldPosX: Array[Float] = if (required.contains(RequiredColumns.PosX)) new Array[Float](maxColumnSize) else null
  val oldPosY: Array[Float] = if (required.contains(RequiredColumns.PosY)) new Array[Float](maxColumnSize) else null
  val oldPosZ: Array[Float] = if (required.contains(RequiredColumns.PosZ)) new Array[Float](maxColumnSize) else null

  def oldPos(i: Int): Array[Float] = i match {
    case 0 => oldPosX
    case 1 => oldPosY
    case 2 => oldPosZ
  }

  val scaleX = new Array[Float](maxColumnSize)
  val scaleY = new Array[Float](maxColumnSize)
  val scaleZ = new Array[Float](maxColumnSize)

  def scale(i: Int): Array[Float] = i match {
    case 0 => scaleX
    case 1 => scaleY
    case 2 => scaleZ
  }

  val oldScaleX = new Array[Float](maxColumnSize)
  val oldScaleY = new Array[Float](maxColumnSize)
  val oldScaleZ = new Array[Float](maxColumnSize)

  def oldScale(i: Int): Array[Float] = i match {
    case 0 => oldScaleX
    case 1 => oldScaleY
    case 2 => oldScaleZ
  }

  val orientation: Array[MutableQuat] =
    if (required.contains(RequiredColumns.Orientation)) Array.fill(maxColumnSize)(Quat.Identity.asMutable) else null
  val oldOrientation: Array[MutableQuat] =
    if (required.contains(RequiredColumns.Orientation)) Array.fill(maxColumnSize)(Quat.Identity.asMutable) else null

  val mainColor: Array[Int]      = new Array[Int](maxColumnSize)
  val secondaryColor: Array[Int] = new Array[Int](maxColumnSize)

  val oldMainColor: Array[Int]      = new Array[Int](maxColumnSize)
  val oldSecondaryColor: Array[Int] = new Array[Int](maxColumnSize)

  val damage: Array[Float]                        = new Array[Float](maxColumnSize)
  val form: Array[Form]                           = new Array[Form](maxColumnSize)
  val renderProperties: Array[Map[String, Float]] = new Array[Map[String, Float]](maxColumnSize)

  val ticksExisted = new Array[Short](maxColumnSize)
  val endTime      = new Array[Short](maxColumnSize)
  val dead         = new Array[Boolean](maxColumnSize)
  val nextStage    = new Array[Seq[DanmakuSpawnData]](maxColumnSize)

  val parent = new Array[UUID](maxColumnSize)

  val currentDead: mutable.Buffer[Int]                           = mutable.Buffer[Int]()
  private val addSpawns: mutable.Buffer[(DanmakuSpawnData, Int)] = mutable.Buffer[(DanmakuSpawnData, Int)]()

  def grabNewSpawns(): Seq[(DanmakuSpawnData, Int)] = {
    val res = addSpawns.toSeq
    addSpawns.clear()
    res
  }

  def addSpawn(spawn: DanmakuSpawnData, prefferedIdx: Int): Unit = addSpawns.addOne((spawn, prefferedIdx))
  def addSpawns(spawns: Seq[DanmakuSpawnData]): Unit             = addSpawns.addAll(spawns.map((_, -1)))

  def allArrays: Seq[Array[_]] = Seq(
    posX,
    posY,
    posZ,
    oldPosX,
    oldPosY,
    oldPosZ,
    scaleX,
    scaleY,
    scaleZ,
    oldScaleX,
    oldScaleY,
    oldScaleZ,
    orientation,
    oldOrientation,
    mainColor,
    secondaryColor,
    oldMainColor,
    oldSecondaryColor,
    damage,
    form,
    ticksExisted,
    dead,
    parent
  ).filter(_ != null)
}
object MainColumns {
  trait RequiredColumns {
    def isPos: Boolean         = false
    def isScale: Boolean       = false
    def isOrientation: Boolean = false
  }
  object RequiredColumns {
    case object PosX extends RequiredColumns { override def isPos: Boolean = true }
    case object PosY extends RequiredColumns { override def isPos: Boolean = true }
    case object PosZ extends RequiredColumns { override def isPos: Boolean = true }

    case object ScaleX extends RequiredColumns { override def isScale: Boolean = true }
    case object ScaleY extends RequiredColumns { override def isScale: Boolean = true }
    case object ScaleZ extends RequiredColumns { override def isScale: Boolean = true }

    case object Orientation extends RequiredColumns { override def isOrientation: Boolean = true }
  }
}
