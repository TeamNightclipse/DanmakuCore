/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku

import javax.annotation.Nullable

import net.katsstuff.danmakucore.data.{MovementData, OrientedBoundingBox, RotationData, ShotData}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler

object DanmakuTemplate {
  def builder = new Builder

  case class Builder(
      var world: World = null,
      @Nullable var user: EntityLivingBase = null,
      @Nullable var source: Entity = null,
      var shot: ShotData = null,
      var pos: Vector3 = null,
      var direction: Vector3 = null,
      var orientation: Quat = null,
      var movement: MovementData = MovementData.constant(0.4D),
      var rotation: RotationData = RotationData.none,
      @Nullable var rawBoundingBoxes: Seq[OrientedBoundingBox] = null
  ) {

    def build: DanmakuTemplate = {
      if (source == null && user != null) source = user

      if (world == null) {
        if (source != null) world = source.world
        else throw new IllegalArgumentException("Could not find a world for builder, and neither source or user is set")
      }

      if (pos == null) {
        if (user != null) pos = new Vector3(user)
        else if (source != null) pos = new Vector3(source)
        else throw new IllegalArgumentException("Could not find a pos for builder, and neither source or user is set")
      }

      if (orientation == null) {
        if (direction != null) orientation = Quat.fromEuler(direction.yaw.toFloat, direction.pitch.toFloat, 0F)
        else if (user != null) orientation = Quat.orientationOf(user)
        else if (source != null) orientation = Quat.orientationOf(source)
        else
          throw new IllegalArgumentException(
            "could not find a orientation for builder, and neither source, user or direction is set"
          )
      }

      if (direction == null) {
        if (user != null) direction = Vector3.directionEntity(user)
        else if (source != null) direction = Vector3.directionEntity(source)
        else
          throw new IllegalArgumentException(
            "could not find a direction for builder, and neither source or user is set"
          )
      }

      if (rawBoundingBoxes == null) {
        rawBoundingBoxes = Seq(
          OrientedBoundingBox(
            DanmakuState.createRawBoundingBox(shot, rotate = false, Quat.Identity),
            Vector3.Zero,
            Quat.Identity
          )
        )
      }

      if (shot == null) throw new IllegalArgumentException("Make sure that shot is set")

      new DanmakuTemplate(
        world,
        Option(user),
        Option(source),
        shot,
        pos,
        direction,
        orientation,
        movement,
        rotation,
        rawBoundingBoxes
      )
    }

    def setWorld(world: World): Builder = {
      this.world = world
      this
    }

    def setUser(@Nullable user: EntityLivingBase): Builder = {
      this.user = user
      this
    }

    def setUser(user: Option[EntityLivingBase]): Builder = {
      this.user = user.orNull
      this
    }

    def setSource(@Nullable source: Entity): Builder = {
      this.source = source
      this
    }

    def setSource(source: Option[Entity]): Builder = {
      this.source = source.orNull
      this
    }

    def setShot(shot: ShotData): Builder = {
      this.shot = shot
      this
    }

    def setPos(pos: Vector3): Builder = {
      this.pos = pos
      this
    }

    def setDirection(direction: Vector3): Builder = {
      this.direction = direction
      this
    }

    def setOrientation(orientation: Quat): Builder = {
      this.orientation = orientation
      this
    }

    def setVariant(variant: DanmakuVariant): Builder = {
      setShot(variant.getShotData)
      setMovementData(variant.getMovementData)
      setRotationData(variant.getRotationData)
      this
    }

    def setMovementData(movementData: MovementData): Builder = {
      this.movement = movementData
      this
    }

    def setMovementData(gravity: Vector3): Builder =
      setMovementData(
        movement
          .copy(
            movement.speedOriginal,
            movement.lowerSpeedLimit,
            movement.upperSpeedLimit,
            movement.speedAcceleration,
            gravity
          )
      )

    def setMovementData(speed: Double): Builder =
      setMovementData(new MovementData(speed, speed, speed, 0D, movement.getGravity))

    def setMovementData(speed: Double, gravity: Vector3): Builder =
      setMovementData(new MovementData(speed, speed, speed, 0D, gravity))

    def setMovementData(speed: Double, speedLimit: Double, speedAcceleration: Double): Builder =
      setMovementData(new MovementData(speed, 0F, speedLimit, speedAcceleration, movement.getGravity))

    def setMovementData(
        speed: Double,
        lowerSpeedLimit: Double,
        upperSpeedLimit: Double,
        speedAcceleration: Double
    ): Builder =
      setMovementData(new MovementData(speed, lowerSpeedLimit, upperSpeedLimit, speedAcceleration, movement.getGravity))

    def setMovementData(
        speed: Double,
        lowerSpeedLimit: Double,
        upperSpeedLimit: Double,
        speedAcceleration: Double,
        gravity: Vector3
    ): Builder =
      setMovementData(new MovementData(speed, lowerSpeedLimit, upperSpeedLimit, speedAcceleration, gravity))

    def setRotationData(rotationData: RotationData): Builder = {
      this.rotation = rotationData
      this
    }

    def setRotationData(rotation: Quat): Builder =
      setRotationData(new RotationData(true, rotation, 9999))

    def setRotationData(axis: Vector3, angle: Float): Builder =
      setRotationData(new RotationData(true, Quat.fromAxisAngle(axis, angle), 9999))

    def setRotationData(axis: Vector3, angle: Float, endTime: Int): Builder =
      setRotationData(new RotationData(true, Quat.fromAxisAngle(axis, angle), endTime))
  }
}

final case class DanmakuTemplate(
    world: World,
    user: Option[EntityLivingBase],
    source: Option[Entity],
    shot: ShotData,
    pos: Vector3,
    direction: Vector3,
    orientation: Quat,
    movement: MovementData,
    rotation: RotationData,
    rawBoundingBoxes: Seq[OrientedBoundingBox]
) {

  def asEntity: DanmakuState = {
    val entityData = DanmakuEntityData(
      id = DanmakuState.nextId(),
      world = world,
      ticksExisted = 0,
      renderBrightness = 1F,
      pos = pos,
      prevPos = pos,
      orientation = orientation,
      prevOrientation = orientation,
      motion = movement.speedOriginal * direction,
      direction = direction,
      rawBoundingBoxes = rawBoundingBoxes,
      rawEncompassingAABB = DanmakuState.createRawEncompassingBB(rawBoundingBoxes)
    )

    val extraData = ExtraDanmakuData(
      user = user,
      source = source,
      shot = shot,
      subEntity = shot.subEntity.instantiate,
      movement = movement,
      rotation = rotation
    )

    val trackingData = TrackerData(
      pos = pos,
      range = (movement.speedOriginal.max(movement.upperSpeedLimit) * shot.end * 1.5D).round.toInt.max(40),
      maxRange = FMLCommonHandler.instance().getMinecraftServerInstance.getPlayerList.getEntityViewDistance,
      updateFrequency = 5
    )

    val first   = DanmakuState(entityData, extraData, trackingData)
    val created = extraData.subEntity.onCreate(first)
    extraData.subEntity.onInstantiate(created)
  }
  def toBuilder =
    DanmakuTemplate.Builder(
      world,
      user.orNull,
      source.orNull,
      shot,
      pos,
      direction,
      orientation,
      movement,
      rotation,
      rawBoundingBoxes
    )
}
