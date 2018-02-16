/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku

import io.netty.buffer.ByteBuf
import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.danmaku.DanmakuState.{Add, NOOP, PlayerOperation, Remove}
import net.katsstuff.danmakucore.data.{MovementData, OrientedBoundingBox, Quat, RotationData, ShotData, Vector3}
import net.katsstuff.danmakucore.danmaku.subentity.SubEntity
import net.katsstuff.danmakucore.helper.MathUtil._
import net.katsstuff.danmakucore.network.{DanCorePacketHandler, DanmakuForceUpdatePacket}
import net.katsstuff.danmakucore.network.scalachannel.MessageConverter
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.{Entity, EntityLivingBase, EntityTracker}
import net.minecraft.util.math.{AxisAlignedBB, MathHelper}
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager

case class DanmakuEntityData(
    id: Int,
    world: World,
    ticksExisted: Int,
    renderBrightness: Float,
    pos: Vector3,
    prevPos: Vector3,
    orientation: Quat,
    prevOrientation: Quat,
    motion: Vector3,
    direction: Vector3,
    rawBoundingBoxes: Seq[OrientedBoundingBox],
    rawEncompassingAABB: AxisAlignedBB
) {

  lazy val boundingBoxes: Seq[OrientedBoundingBox] =
    rawBoundingBoxes.map(
      obb => obb.copy(aabb = obb.aabb.offset(pos.x, pos.y, pos.z), orientation = obb.orientation * orientation, pos = pos)
    )
  lazy val encompassingAABB: AxisAlignedBB = rawEncompassingAABB.offset(pos.x, pos.y, pos.z)
}

case class ExtraDanmakuData(
    user: Option[EntityLivingBase],
    source: Option[Entity],
    shot: ShotData,
    subEntity: SubEntity,
    movement: MovementData,
    rotation: RotationData
)

case class TrackerData(
    trackingPlayers: Set[EntityPlayerMP],
    updateCounter: Int,
    updateFrequency: Int,
    range: Int,
    maxRange: Int,
    encodedPosX: Long,
    encodedPosY: Long,
    encodedPosZ: Long,
    lastTrackedPos: Vector3,
    ticksSinceLastForcedTeleport: Int,
    updatedPlayerVisibility: Boolean
)
object TrackerData {
  def apply(pos: Vector3, range: Int, maxRange: Int, updateFrequency: Int): TrackerData =
    new TrackerData(
      Set.empty,
      0,
      updateFrequency,
      range,
      maxRange,
      EntityTracker.getPositionLong(pos.x),
      EntityTracker.getPositionLong(pos.y),
      EntityTracker.getPositionLong(pos.z),
      pos,
      0,
      false
    )
}

case class DanmakuState(entity: DanmakuEntityData, extra: ExtraDanmakuData, tracking: TrackerData) {

  def id:                  Int                      = entity.id
  def world:               World                    = entity.world
  def ticksExisted:        Int                      = entity.ticksExisted
  def renderBrightness:    Float                    = entity.renderBrightness
  def pos:                 Vector3                  = entity.pos
  def prevPos:             Vector3                  = entity.prevPos
  def orientation:         Quat                     = entity.orientation
  def prevOrientation:     Quat                     = entity.prevOrientation
  def motion:              Vector3                  = entity.motion
  def direction:           Vector3                  = entity.direction
  def rawBoundingBoxes:    Seq[OrientedBoundingBox] = entity.rawBoundingBoxes
  def boundingBoxes:       Seq[OrientedBoundingBox] = entity.boundingBoxes
  def rawEncompassingAABB: AxisAlignedBB            = entity.rawEncompassingAABB
  def encompassingAABB:    AxisAlignedBB            = entity.encompassingAABB

  def user:      Option[EntityLivingBase] = extra.user
  def source:    Option[Entity]           = extra.source
  def shot:      ShotData                 = extra.shot
  def subEntity: SubEntity                = extra.subEntity
  def movement:  MovementData             = extra.movement
  def rotation:  RotationData             = extra.rotation

  lazy val chunkPosX: Int = MathHelper.floor(pos.x / 16D)
  lazy val chunkPosZ: Int = MathHelper.floor(pos.z / 16D)

  //noinspection MutatorLikeMethodIsParameterless
  def updateForm: DanmakuUpdate =
    shot.form.onTick(this)

  //noinspection MutatorLikeMethodIsParameterless
  def updateSubEntity: DanmakuUpdate = subEntity.subEntityTick(this)

  def update: DanmakuUpdate =
    if (ticksExisted > shot.end) DanmakuUpdate.empty
    else updateSubEntity.andThen(_.updateForm)

  def currentSpeed: Double = motion.length

  def accelerate: Vector3 = {
    val speedAccel      = movement.speedAcceleration
    val upperSpeedLimit = movement.upperSpeedLimit
    val lowerSpeedLimit = movement.lowerSpeedLimit
    if (currentSpeed >=~ upperSpeedLimit && speedAccel >= 0D) setSpeed(upperSpeedLimit)
    else if (currentSpeed <=~ lowerSpeedLimit && speedAccel <= 0D) setSpeed(lowerSpeedLimit)
    else {
      val newMotion       = addSpeed(speedAccel)
      val newCurrentSpeed = newMotion.length
      if (newCurrentSpeed >~ upperSpeedLimit) setSpeed(upperSpeedLimit)
      else if (newCurrentSpeed <~ lowerSpeedLimit) setSpeed(lowerSpeedLimit)
      else newMotion
    }
  }

  def setSpeed(speed: Double): Vector3 = direction * speed
  def addSpeed(speed: Double): Vector3 = motion + direction * speed

  def resetMotion: Vector3 = setSpeed(movement.speedOriginal)

  def copy(
      entity: DanmakuEntityData = entity,
      extra: ExtraDanmakuData = extra,
      tracking: TrackerData = tracking
  ): DanmakuState = {
    val oldShot     = this.extra.shot
    val newShot     = extra.shot
    val oldMovement = this.extra.movement
    val newMovement = extra.movement
    val oldRotation = this.extra.rotation
    val newRotation = extra.rotation

    val shotChanged     = newShot != oldShot
    val movementChanged = newMovement != oldMovement
    val rotationChanged = newRotation != oldRotation

    val specialDataChanged = shotChanged || movementChanged || rotationChanged

    val usedShot =
      if (shotChanged)
        extra.subEntity.onShotDataChange(oldShot, oldShot.form.onShotDataChange(oldShot, newShot), newShot)
      else oldShot

    val usedMovement =
      if (movementChanged)
        extra.subEntity.onMovementDataChange(
          oldMovement,
          usedShot.form.onMovementDataChange(oldMovement, newMovement),
          newMovement
        )
      else oldMovement

    val usedRotation =
      if (rotationChanged)
        extra.subEntity.onRotationDataChange(
          oldRotation,
          usedShot.form.onRotationDataChange(oldRotation, newRotation),
          newRotation
        )
      else oldRotation

    val usedExtra = if (specialDataChanged) {
      extra.copy(shot = usedShot, movement = usedMovement, rotation = usedRotation)
    } else extra

    DanmakuState(entity, usedExtra, tracking)
  }

  private[danmakucore] def updatePlayerList(players: Seq[EntityPlayerMP]): TrackerData = {
    val tracking                     = this.tracking
    var trackingPlayers              = tracking.trackingPlayers
    var updatedPlayerVisibility      = tracking.updatedPlayerVisibility
    var lastTrackedPos               = tracking.lastTrackedPos
    var ticksSinceLastForcedTeleport = tracking.ticksSinceLastForcedTeleport
    var encodedPosX                  = tracking.encodedPosX
    var encodedPosY                  = tracking.encodedPosY
    var encodedPosZ                  = tracking.encodedPosZ

    if (!updatedPlayerVisibility || pos.distanceSquared(lastTrackedPos) > 16D) {
      updatedPlayerVisibility = true
      trackingPlayers = newTrackingPlayers(players)
      lastTrackedPos = pos
    }

    if (tracking.updateCounter % tracking.updateFrequency == 0) {
      ticksSinceLastForcedTeleport += 1
      val x         = EntityTracker.getPositionLong(pos.x)
      val y         = EntityTracker.getPositionLong(pos.y)
      val z         = EntityTracker.getPositionLong(pos.z)
      val diffX     = x - encodedPosX
      val diffY     = y - encodedPosY
      val diffZ     = z - encodedPosZ
      val changePos = diffX * diffX + diffY * diffY + diffZ * diffZ >= 128L || tracking.updateCounter % 60 == 0
      if (tracking.updateCounter > 0) {
        if (x < -32768L || x >= 32768L || y < -32768L || y >= 32768L || z < -32768L || z >= 32768L || ticksSinceLastForcedTeleport > 400) {
          updatedPlayerVisibility = false
          ticksSinceLastForcedTeleport = 0
          tracking.trackingPlayers.foreach { player =>
            DanCorePacketHandler.sendTo(DanmakuForceUpdatePacket(this), player)
          }
        }
      }

      if (changePos) {
        encodedPosX = x
        encodedPosY = y
        encodedPosZ = z
      }
    }

    tracking.copy(
      updatedPlayerVisibility = updatedPlayerVisibility,
      trackingPlayers = trackingPlayers,
      lastTrackedPos = lastTrackedPos,
      ticksSinceLastForcedTeleport = ticksSinceLastForcedTeleport,
      encodedPosX = encodedPosX,
      encodedPosY = encodedPosY,
      encodedPosZ = encodedPosZ,
      updateCounter = tracking.updateCounter + 1
    )
  }

  private[danmakucore] def newTrackingPlayers(players: Seq[EntityPlayerMP]): Set[EntityPlayerMP] = {
    val byOperation = players
      .map { player =>
        playerEntityChange(tracking, player) -> player
      }
      .groupBy(_._1)
      .mapValues(_.map(_._2))

    val added   = byOperation.get(Add).fold(tracking.trackingPlayers)(players => tracking.trackingPlayers ++ players)
    val removed = byOperation.get(Remove).fold(added)(players => added ++ players)

    removed
  }

  private[danmakucore] def playerEntityChange(tracking: TrackerData, player: EntityPlayerMP): PlayerOperation = {
    if (isVisibleTo(player)) {
      val isTracked = tracking.trackingPlayers.contains(player)
      if (!isTracked && isPlayerWatchingThisChunk(player)) Add
      else if (isTracked) Remove
      else NOOP
    } else NOOP
  }

  private def isVisibleTo(player: EntityPlayerMP): Boolean = {
    val x     = player.posX - tracking.encodedPosX / 4096.0D
    val z     = player.posZ - tracking.encodedPosZ / 4096.0D
    val range = Math.min(tracking.range, tracking.maxRange)
    x >= -range && x <= range && z >= -range && z <= range
  }

  private def isPlayerWatchingThisChunk(playerMP: EntityPlayerMP) =
    playerMP.getServerWorld.getPlayerChunkMap.isPlayerWatchingChunk(playerMP, chunkPosX, chunkPosZ)

  def isShotEndTime: Boolean = shot.end == ticksExisted
}
object DanmakuState {
  private[danmakucore] sealed trait PlayerOperation
  private[danmakucore] case object Add    extends PlayerOperation
  private[danmakucore] case object Remove extends PlayerOperation
  private[danmakucore] case object NOOP   extends PlayerOperation

  private var id: Int = 0

  def nextId(): Int = {
    val next = id
    id += 1
    next
  }

  implicit val converter: MessageConverter[DanmakuState] = new MessageConverter[DanmakuState] {

    override def writeBytes(state: DanmakuState, buf: ByteBuf): Unit = {
      import MessageConverter.Ops

      buf.write(state.entity.id)
      buf.write(state.entity.world.provider.getDimension)
      buf.write(state.entity.ticksExisted)
      buf.write(state.entity.renderBrightness)
      buf.write(state.entity.pos)
      buf.write(state.entity.prevPos)
      buf.write(state.entity.orientation)
      buf.write(state.entity.prevOrientation)
      buf.write(state.entity.motion)
      buf.write(state.entity.direction)

      buf.write(state.entity.rawBoundingBoxes.size)

      for (box <- state.entity.rawBoundingBoxes) {
        buf.write(box.aabb.minX)
        buf.write(box.aabb.minY)
        buf.write(box.aabb.minZ)
        buf.write(box.aabb.maxX)
        buf.write(box.aabb.maxY)
        buf.write(box.aabb.maxZ)
        buf.write(box.pos)
        buf.write(box.orientation)
      }

      buf.write(state.entity.rawEncompassingAABB.minX)
      buf.write(state.entity.rawEncompassingAABB.minY)
      buf.write(state.entity.rawEncompassingAABB.minZ)
      buf.write(state.entity.rawEncompassingAABB.maxX)
      buf.write(state.entity.rawEncompassingAABB.maxY)
      buf.write(state.entity.rawEncompassingAABB.maxZ)

      buf.write(state.extra.user.map(_.getEntityId))
      buf.write(state.extra.source.map(_.getEntityId))
      buf.write(state.extra.shot)
      buf.write(state.extra.movement)
      buf.write(state.extra.rotation)
    }

    override def readBytes(buf: ByteBuf): DanmakuState = {
      import MessageConverter.Ops

      val entityData = DanmakuEntityData(
        id = buf.read[Int],
        world = Option(DimensionManager.getWorld(buf.read[Int])).getOrElse(DanmakuCore.proxy.defaultWorld),
        ticksExisted = buf.read[Int],
        renderBrightness = buf.read[Float],
        pos = buf.read[Vector3],
        prevPos = buf.read[Vector3],
        orientation = buf.read[Quat],
        prevOrientation = buf.read[Quat],
        motion = buf.read[Vector3],
        direction = buf.read[Vector3],
        rawBoundingBoxes = {
          Seq.fill(buf.read[Int])(
            OrientedBoundingBox(
              aabb = new AxisAlignedBB(
                buf.read[Double],
                buf.read[Double],
                buf.read[Double],
                buf.read[Double],
                buf.read[Double],
                buf.read[Double]
              ),
              pos = buf.read[Vector3],
              orientation = buf.read[Quat]
            )
          )
        },
        rawEncompassingAABB = new AxisAlignedBB(
          buf.read[Double],
          buf.read[Double],
          buf.read[Double],
          buf.read[Double],
          buf.read[Double],
          buf.read[Double]
        )
      )

      val user = buf.read[Option[Int]].flatMap { id =>
        Option(entityData.world.getEntityByID(id)).collect {
          case living: EntityLivingBase => living
        }
      }

      val source = buf.read[Option[Int]].flatMap { id =>
        Option(entityData.world.getEntityByID(id))
      }

      val shot = buf.read[ShotData]

      val extraData = ExtraDanmakuData(
        user = user,
        source = source,
        shot = shot,
        subEntity = shot.subEntity.instantiate,
        movement = buf.read[MovementData],
        rotation = buf.read[RotationData]
      )

      val first = DanmakuState(
        entityData,
        extraData,
        TrackerData(Set.empty, 0, 0, 0, 80, 0, 0, 0, Vector3.Zero, 0, updatedPlayerVisibility = false)
      )
      extraData.subEntity.onInstantiate(first)
    }
  }

  def createRawBoundingBox(shot: ShotData, rotate: Boolean, orientation: Quat): AxisAlignedBB = {
    if (rotate) {
      val size  = new Vector3(shot.sizeX, shot.sizeY, shot.sizeZ).rotate(orientation)
      val xSize = size.x / 2F
      val ySize = size.y / 2F
      val zSize = size.z / 2F
      new AxisAlignedBB(-xSize, -ySize, -zSize, xSize, ySize, zSize)
    } else {
      val xSize = shot.sizeX / 2F
      val ySize = shot.sizeY / 2F
      val zSize = shot.sizeZ / 2F
      new AxisAlignedBB(-xSize, -ySize, -zSize, xSize, ySize, zSize)
    }
  }

  def createRawEncompassingBB(boxes: Seq[OrientedBoundingBox]): AxisAlignedBB = {
    boxes.foldLeft(new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D)) { (aabb, obb) =>
      val withoutPos = if (obb.pos == Vector3.Zero) obb.aabb else obb.aabb.offset(-obb.pos.x, -obb.pos.y, -obb.pos.z)

      val min = withoutPos.minX.min(withoutPos.minY).min(withoutPos.minZ)
      val max = withoutPos.maxX.max(withoutPos.maxY).max(withoutPos.maxZ)

      aabb.union(new AxisAlignedBB(min, min, min, max, max, max))
    }
  }
}
