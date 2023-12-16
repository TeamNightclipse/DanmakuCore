package net.katsstuff.danmakucore.danmaku

import scala.language.existentials

import java.util.UUID

import scala.annotation.tailrec
import scala.collection.mutable

import com.mojang.logging.LogUtils
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler.{DanmakuSpawnData, RenderData}
import net.katsstuff.danmakucore.danmaku.behaviors.{Behavior, MainColumns}
import net.katsstuff.danmakucore.math.{Mat4, MutableMat4}
import net.minecraft.util.Mth

class DanmakuBehaviorHandler(
    val behaviors: List[Behavior[_]],
    val alwaysKeep: Boolean,
    globalParentMap: mutable.Map[UUID, UUID],
    globalFamilyDepthMap: mutable.Map[UUID, Short]
) {
  private val LOGGER = LogUtils.getLogger
  LOGGER.debug(s"Creating DanmakuBehaviorHandler for ${behaviors.mkString(", ")}")

  private val requiredMainColumns = behaviors.flatMap(_.requiredMainColumns).distinct
  private def hasMainColumn(required: MainColumns.RequiredColumns): Boolean =
    requiredMainColumns.contains(required)

  private val requiresPosX        = hasMainColumn(MainColumns.RequiredColumns.PosX)
  private val requiresPosY        = hasMainColumn(MainColumns.RequiredColumns.PosY)
  private val requiresPosZ        = hasMainColumn(MainColumns.RequiredColumns.PosZ)
  private val requiresScaleX      = hasMainColumn(MainColumns.RequiredColumns.ScaleX)
  private val requiresScaleY      = hasMainColumn(MainColumns.RequiredColumns.ScaleY)
  private val requiresScaleZ      = hasMainColumn(MainColumns.RequiredColumns.ScaleZ)
  private val requiresOrientation = hasMainColumn(MainColumns.RequiredColumns.Orientation)

  private var sizeExp: Int = 7

  private def currentMaxSize: Int = 1 << sizeExp
  private var currentSize: Int    = 0

  private def dead: Int = mainColumns.currentDead.length

  private var transformMats: Array[MutableMat4] = Array.fill(currentMaxSize)(Mat4.Identity.asMutable)
  private var familyDepth: Array[Short]         = new Array[Short](currentMaxSize)

  private val extraDataNameToIdx = behaviors.iterator.flatMap(_.extraColumns).distinct.zipWithIndex.toMap

  private var mainColumns: MainColumns = new MainColumns(currentMaxSize, requiredMainColumns)
  private val extraData: Array[Array[Float]] =
    Array.fill(behaviors.flatMap(_.extraColumns).distinct.length, currentMaxSize)(0F)

  def count: Int = currentSize - dead

  def acceptsMore: Boolean = currentMaxSize < Short.MaxValue || currentSize < currentMaxSize

  def parentMap: Map[UUID, (Short, UUID)] = {
    val dead   = mainColumns.dead
    val id     = mainColumns.id
    val parent = mainColumns.parent

    (0 until currentSize).view
      .filterNot(dead)
      .filter(parent(_) != null)
      .map(i => id(i) -> (familyDepth(i), parent(i)))
      .toMap
  }

  private def shouldResizeUpSoon: Boolean = currentSize + (currentMaxSize * 0.1D) > currentMaxSize
  private def shouldResizeDownSoon: Boolean = {
    val stepDownMaxSize = 1 << (sizeExp - 1)
    sizeExp > 7 && currentSize + (stepDownMaxSize * 0.25D) < stepDownMaxSize
  }

  def shouldResizeSoon: Boolean = shouldResizeUpSoon || shouldResizeDownSoon

  private def mustResizeBeforeAdd(length: Int): Boolean = currentSize + length >= currentMaxSize

  def addDanmakuWithPreferredIndices(danmaku: Seq[(DanmakuSpawnData, Int)]): Seq[DanmakuSpawnData] = {
    val danmakuWithValidIndices      = danmaku.map(t => if (mainColumns.currentDead.contains(t._2)) t else (t._1, -1))
    val numberOfDeadPreferredIndices = danmakuWithValidIndices.count(_._2 != -1)

    if (mustResizeBeforeAdd(danmaku.length - numberOfDeadPreferredIndices)) {
      resize(true)
    }

    def transferData[A](idx: Int, behavior: Behavior[A], data: A): Unit = {
      val arrayData = behavior.extraColumns.map(extraDataNameToIdx.andThen(extraData)).toArray
      behavior.transferData(data, arrayData, idx)
    }

    def transferNoOpData[A](idx: Int, behavior: Behavior[A]): Unit =
      transferData(idx, behavior, behavior.noOpData)

    @tailrec
    def registerBehaviorData(
        idx: Int,
        behaviorsForDanmaku: List[TopDanmakuBehaviorsHandler.BehaviorPair[A] forSome { type A }],
        handlerBehaviors: List[Behavior[_]]
    ): Unit = (behaviorsForDanmaku, handlerBehaviors) match {
      case (Nil, Nil) => ()
      case (Nil, y :: ys) =>
        transferNoOpData(idx, y)
        registerBehaviorData(idx, Nil, ys)
      case (TopDanmakuBehaviorsHandler.BehaviorPair(xb, xd) :: xs, y :: ys) if xb == y =>
        transferData(idx, xb, xd)
        registerBehaviorData(idx, xs, ys)
      case (_ :: xs, y :: ys) =>
        transferNoOpData(idx, y)
        registerBehaviorData(idx, xs, ys)
      case (_, Nil) => sys.error("Unexpected behavior")
    }

    val id = mainColumns.id

    // noinspection DuplicatedCode
    val posX = mainColumns.posX
    val posY = mainColumns.posY
    val posZ = mainColumns.posZ

    val oldPosX = mainColumns.oldPosX
    val oldPosY = mainColumns.oldPosY
    val oldPosZ = mainColumns.oldPosZ

    val orientation    = mainColumns.orientation
    val oldOrientation = mainColumns.oldOrientation

    // noinspection DuplicatedCode
    val scaleX = mainColumns.scaleX
    val scaleY = mainColumns.scaleY
    val scaleZ = mainColumns.scaleZ

    val oldScaleX = mainColumns.oldScaleX
    val oldScaleY = mainColumns.oldScaleY
    val oldScaleZ = mainColumns.oldScaleZ

    val mainColor      = mainColumns.mainColor
    val secondaryColor = mainColumns.secondaryColor

    val oldMainColor      = mainColumns.oldMainColor
    val oldSecondaryColor = mainColumns.oldSecondaryColor

    val damage           = mainColumns.damage
    val form             = mainColumns.form
    val renderProperties = mainColumns.renderProperties

    val ticksExisted = mainColumns.ticksExisted
    val endTime      = mainColumns.endTime
    val dead         = mainColumns.dead
    val nextStage    = mainColumns.nextStage

    val parent = mainColumns.parent

    val transformMats = this.transformMats
    val tempMat       = Mat4.Identity.asMutable

    val familyDepth = this.familyDepth

    val res = danmaku.zipWithIndex.flatMap { case ((danmaku, preferredIdx), i) =>
      val idx = if (preferredIdx != -1) preferredIdx else currentSize + i

      val thisId = UUID.randomUUID()
      id(idx) = thisId

      posX(idx) = danmaku.pos.x.toFloat
      posY(idx) = danmaku.pos.y.toFloat
      posZ(idx) = danmaku.pos.z.toFloat

      oldPosX(idx) = danmaku.pos.x.toFloat
      oldPosY(idx) = danmaku.pos.y.toFloat
      oldPosZ(idx) = danmaku.pos.z.toFloat

      orientation(idx) = danmaku.orientation.asMutable
      oldOrientation(idx) = danmaku.orientation.asMutable

      scaleX(idx) = danmaku.shotData.sizeX
      scaleY(idx) = danmaku.shotData.sizeY
      scaleZ(idx) = danmaku.shotData.sizeZ

      oldScaleX(idx) = danmaku.shotData.sizeX
      oldScaleY(idx) = danmaku.shotData.sizeY
      oldScaleZ(idx) = danmaku.shotData.sizeZ

      mainColor(idx) = danmaku.shotData.mainColor
      secondaryColor(idx) = danmaku.shotData.secondaryColor

      oldMainColor(idx) = danmaku.shotData.mainColor
      oldSecondaryColor(idx) = danmaku.shotData.secondaryColor

      damage(idx) = danmaku.shotData.damage
      form(idx) = danmaku.shotData.form
      renderProperties(idx) = danmaku.shotData.renderProperties

      ticksExisted(idx) = 0
      endTime(idx) = danmaku.shotData.endTime
      dead(idx) = false
      nextStage(idx) = danmaku.nextStage

      parent(idx) = danmaku.parent.orNull
      familyDepth(idx) = danmaku.familyDepth.toShort

      danmaku.parent.foreach(parentId => globalParentMap.put(thisId, parentId))
      globalFamilyDepthMap.put(thisId, danmaku.familyDepth.toShort)

      registerBehaviorData(idx, danmaku.behaviors.toList, behaviors)

      val mat = transformMats(idx)
      mat.setIdentity()

      mat.m00 = scaleX(i)
      mat.m11 = scaleY(i)
      mat.m22 = scaleZ(i)

      danmaku.orientation.toMatTo(tempMat) =* mat

      tempMat.setIdentity()
      tempMat.m03 = danmaku.pos.x
      tempMat.m13 = danmaku.pos.y
      tempMat.m23 = danmaku.pos.z

      tempMat =* mat

      danmaku.children.map(d => d.copy(parent = Some(thisId)))
    }

    currentSize += danmaku.length

    res
  }

  def addDanmaku(danmaku: Seq[DanmakuSpawnData]): Seq[DanmakuSpawnData] =
    addDanmakuWithPreferredIndices(danmaku.map((_, -1)))

  def tick(): Seq[(DanmakuSpawnData, Int)] = {
    behaviors.foreach { behavior =>
      val arrayData = behavior.extraColumns.map(extraDataNameToIdx.andThen(extraData)).toArray
      behavior.act(mainColumns, arrayData, currentSize)
    }

    mainColumns.grabNewSpawns()
  }

  def computeTransformMats(partialTicks: Float): Unit = {
    // If nothing can change, then local space stays constant
    if (requiredMainColumns.nonEmpty) {
      val temp = Mat4.Identity.asMutable

      // noinspection DuplicatedCode
      val posX    = mainColumns.posX
      val posY    = mainColumns.posY
      val posZ    = mainColumns.posZ
      val oldPosX = mainColumns.oldPosX
      val oldPosY = mainColumns.oldPosY
      val oldPosZ = mainColumns.oldPosZ

      // noinspection DuplicatedCode
      val scaleX    = mainColumns.scaleX
      val scaleY    = mainColumns.scaleY
      val scaleZ    = mainColumns.scaleZ
      val oldScaleX = mainColumns.oldScaleX
      val oldScaleY = mainColumns.oldScaleY
      val oldScaleZ = mainColumns.oldScaleZ

      val orientation    = mainColumns.orientation
      val oldOrientation = mainColumns.oldOrientation

      var i: Int = 0
      while (i < currentSize) {
        if (!mainColumns.dead(i)) {
          val mat = transformMats(i)
          mat.setIdentity()

          mat.m00 = if (requiresScaleX) Mth.lerp(partialTicks, oldScaleX(i), scaleX(i)) else scaleX(i)
          mat.m11 = if (requiresScaleY) Mth.lerp(partialTicks, oldScaleY(i), scaleY(i)) else scaleY(i)
          mat.m22 = if (requiresScaleZ) Mth.lerp(partialTicks, oldScaleZ(i), scaleZ(i)) else scaleZ(i)

          if (requiresOrientation) oldOrientation(i).slerp(orientation(i), partialTicks).toMatTo(temp)
          else orientation(i).toMatTo(temp)

          temp =* mat

          if (requiresPosX || requiresPosY || requiresPosZ) {
            temp.setIdentity()
            temp.m03 = if (requiresPosX) Mth.lerp(partialTicks, oldPosX(i), posX(i)) else posX(i)
            temp.m13 = if (requiresPosX) Mth.lerp(partialTicks, oldPosY(i), posY(i)) else posY(i)
            temp.m23 = if (requiresPosX) Mth.lerp(partialTicks, oldPosZ(i), posZ(i)) else posZ(i)

            temp =* mat
          }
        }

        i += 1
      }
    }
  }

  def computeAndGetRenderData(partialTicks: Float): Seq[(UUID, RenderData)] = {
    val form              = mainColumns.form
    val renderProperties  = mainColumns.renderProperties
    val transformMats     = this.transformMats
    val mainColor         = mainColumns.mainColor
    val oldMainColor      = mainColumns.oldMainColor
    val secondaryColor    = mainColumns.secondaryColor
    val oldSecondaryColor = mainColumns.oldSecondaryColor
    val ticksExisted      = mainColumns.ticksExisted
    val endTime           = mainColumns.endTime
    val dead              = mainColumns.dead
    val id                = mainColumns.id

    computeTransformMats(partialTicks)

    (0 until currentSize).view
      .filterNot(dead)
      .map(i => id(i) -> i)
      .map { case (id, idx) =>
        id -> RenderData(
          form(idx),
          renderProperties(idx),
          transformMats(idx),
          Mat4.Identity.asMutable,
          mainColor(idx), // Interpolate with partialTicks color
          secondaryColor(idx),
          ticksExisted(idx),
          endTime(idx),
          -1
        )
      }
      .toSeq
  }

  def resize(forceUp: Boolean = false): Unit = {
    // We compact first before we resize
    if (!forceUp && dead > currentSize * 0.2D) {
      compact()
      return
    } else if (forceUp || shouldResizeUpSoon) {
      sizeExp += 1
    } else if (shouldResizeDownSoon) {
      sizeExp -= 1
    } else {
      // Something weird is going on. Cancel the resizing
      return
    }
    val newMaxSize = currentMaxSize
    LOGGER.debug(s"Doing resize to $newMaxSize")

    val oldMainColumns = mainColumns
    val newMainColumns = new MainColumns(newMaxSize, requiredMainColumns)
    oldMainColumns.allArrays.zip(newMainColumns.allArrays).foreach { case (oldArr, newArr) =>
      System.arraycopy(oldArr, 0, newArr, 0, currentSize)
    }

    mainColumns = newMainColumns

    val oldTransformMats = transformMats
    val newTransformMats = new Array[MutableMat4](newMaxSize)
    System.arraycopy(oldTransformMats, 0, newTransformMats, 0, currentSize)

    {
      var i: Int = currentSize
      while (i < newMaxSize) {
        newTransformMats(i) = Mat4.Identity.asMutable
        i += 1
      }
    }
    transformMats = newTransformMats

    val oldFamilyDepth = familyDepth
    val newFamilyDepth = new Array[Short](newMaxSize)
    System.arraycopy(oldFamilyDepth, 0, newFamilyDepth, 0, currentSize)
    familyDepth = newFamilyDepth

    {
      var i: Int = 0
      while (i < extraData.length) {
        val oldData = extraData(i)
        val newData = new Array[Float](newMaxSize)

        System.arraycopy(oldData, 0, newData, 0, currentSize)
        extraData(i) = newData

        i += 1
      }
    }
  }

  def compact(): Unit = {
    // No need to compact if the amount of dead is not too great
    if (dead < currentSize * 0.1D) {
      return
    }

    val allArrays = mainColumns.allArrays ++ extraData :+ transformMats :+ familyDepth

    def move[A](arr: Array[A], from: Int, to: Int): Unit =
      arr(to) = arr(from)

    {
      var moveAmount = 0
      var i: Int     = mainColumns.currentDead.min // We start at the first dead
      while (i < currentSize) {
        val dead = mainColumns.dead(i)

        if (dead) {
          moveAmount += 1
        } else if (moveAmount != 0) {
          allArrays.foreach(arr => move(arr, i, i - moveAmount))
        }

        i += 1
      }

      mainColumns.currentDead.clear()
    }
  }
}
