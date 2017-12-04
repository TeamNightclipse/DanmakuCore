/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku

import java.util.{Optional, Random, UUID}

import javax.annotation.Nullable

import scala.collection.JavaConverters._

import io.netty.buffer.ByteBuf
import net.katsstuff.danmakucore.data.{MovementData, OrientedBoundingBox, Quat, RotationData, ShotData, Vector3}
import net.katsstuff.danmakucore.entity.EntityInfo
import net.katsstuff.danmakucore.entity.danmaku.subentity.{SubEntity, SubEntityType}
import net.katsstuff.danmakucore.handler.ConfigHandler
import net.katsstuff.danmakucore.helper.{LogHelper, MathUtil, NBTHelper}
import net.katsstuff.danmakucore.lib.LibEntityName
import net.katsstuff.danmakucore.lib.data.LibSubEntities
import net.katsstuff.danmakucore.misc.LogicalSideOnly
import net.katsstuff.danmakucore.network.DanCoreDataSerializers
import net.katsstuff.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.minecraft.entity.{Entity, EntityLivingBase, IEntityMultiPart, IProjectile, MoverType, MultiPartEntityPart}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.{DataSerializers, EntityDataManager}
import net.minecraft.util.DamageSource
import net.minecraft.util.math.{AxisAlignedBB, MathHelper, Vec3d}
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object EntityDanmaku {
  implicit val info: EntityInfo[EntityDanmaku] = new EntityInfo[EntityDanmaku] {
    override def create(world: World): EntityDanmaku = new EntityDanmaku(world)

    override def name: String = LibEntityName.DANMAKU
  }

  private val ShotDataKey   = EntityDataManager.createKey(classOf[EntityDanmaku], DanCoreDataSerializers.shotData)
  private val RollKey       = EntityDataManager.createKey(classOf[EntityDanmaku], DataSerializers.FLOAT)
}
class EntityDanmaku(
    world: World,
    @LogicalSideOnly(Side.SERVER) _shot: ShotData,
    @LogicalSideOnly(Side.SERVER) _pos: Vector3,
    @LogicalSideOnly(Side.SERVER) var user: Option[EntityLivingBase],
    @LogicalSideOnly(Side.SERVER) var source: Option[Entity],
    @LogicalSideOnly(Side.SERVER) var direction: Vector3,
    @LogicalSideOnly(Side.SERVER) private var _rotation: RotationData = RotationData.none,
    @LogicalSideOnly(Side.SERVER) private var _movement: MovementData = MovementData.constant(0.4D),
    @LogicalSideOnly(Side.SERVER) _roll: Float,
) extends Entity(world)
    with IProjectile
    with IEntityAdditionalSpawnData
    with IEntityMultiPart {
  private val NbtShotData   = "shotData"
  private val NbtDirection  = "direction"
  private val NbtRotation   = "rotation"
  private val NbtMovement   = "movement"
  private val NbtSourceUUID = "sourceUUID"
  private val NbtUserUUID   = "userUUID"
  private val NBTRoll       = "roll"

  private var subEntity:          SubEntity                  = _
  @Nullable private var hitboxes: Array[MultiPartEntityPart] = _

  isImmuneToFire = true
  ignoreFrustumCheck = true

  setPosition(_pos.x, _pos.y, _pos.z)
  resetMotion()

  if (_shot != null) {
    shotData = _shot
  }

  user.foreach { usr =>
    setLocationAndAngles(usr.posX, usr.posY + usr.getEyeHeight, usr.posZ, usr.rotationYaw, usr.rotationPitch)
    posX -= MathHelper.cos(Math.toRadians(rotationYaw).toFloat) * 0.16F
    posY -= 0.1D
    posZ -= MathHelper.sin(Math.toRadians(rotationYaw).toFloat) * 0.16F
  }

  setRoll(_roll)

  def this(world: World) = this(world, null, null, None, None, null, null, null, 0F)

  override def writeSpawnData(buf: ByteBuf): Unit = getShotData.serializeByteBuf(buf)

  override def readSpawnData(buf: ByteBuf): Unit = {
    val shot = new ShotData(buf)
    shotData = shot
    setSize(shot.sizeX, shot.sizeY, shot.sizeZ)
  }

  @LogicalSideOnly(Side.SERVER)
  def copy = new EntityDanmaku(world, shotData, pos, user, source, direction, rotation, movement, roll)

  @SideOnly(Side.CLIENT)
  override def isInRangeToRenderDist(distance: Double): Boolean = {
    var d0 = getEntityBoundingBox.getAverageEdgeLength * 4.0D
    if (d0.isNaN) d0 = 4.0D
    d0 = d0 * 64.0D
    distance < d0 * d0
  }

  override def shoot(x: Double, y: Double, z: Double, velocity: Float, inaccuracy: Float): Unit = {
    def randPos = rand.nextGaussian * 0.0075D * inaccuracy

    val vec         = new Vector3(x, y, z)
    val transformed = vec.normalize.add(randPos, randPos, randPos) * velocity

    motionX = transformed.x
    motionY = transformed.y
    motionZ = transformed.z

    prevRotationYaw = vec.yaw.toFloat
    rotationYaw = prevRotationYaw
    prevRotationPitch = vec.pitch.toFloat
    rotationPitch = prevRotationPitch

    if (!world.isRemote) direction = vec.normalize
  }

  /**
    * Called to update the entity's position/logic.
    */
  override def onUpdate(): Unit = {
    val shot = shotData
    if (subEntity == null) {
      LogHelper.warn("For some reason the danmaku entity is missing it's subEntity. Will try to create a new subEntity")
      setShotData(shot, forceNewSubEntity = true)
      if (subEntity == null) {
        LogHelper.error("Failed to create new subEntity. Killing entity")
        setDead() //We intentionally don't check side here. It's bad, but don't want to deal with bad stuff flying around either
        return
      }
    }

    //We do the isRemote check inside to make sure that also the client exits here
    if (ticksExisted > shot.end) {
      delete()
    } else if (user.exists(_.isDead)) {
      if (!world.isRemote) danmakuFinishBonus()
    } else {
      super.onUpdate()
      shot.getForm.onTick(this)
      subEntity.subEntityTick()
      setPosition(posX + motionX, posY + motionY, posZ + motionZ)
    }
  }

  @LogicalSideOnly(Side.SERVER)
  def accelerate(currentSpeed: Double): Unit = {
    val speedAccel      = _movement.getSpeedAcceleration
    val upperSpeedLimit = _movement.getUpperSpeedLimit
    val lowerSpeedLimit = _movement.getLowerSpeedLimit
    if (MathUtil.fuzzyCompare(currentSpeed, upperSpeedLimit) >= 0 && speedAccel >= 0D) setSpeed(upperSpeedLimit)
    else if (MathUtil.fuzzyCompare(currentSpeed, lowerSpeedLimit) <= 0 && speedAccel <= 0D) setSpeed(lowerSpeedLimit)
    else {
      addSpeed(speedAccel)
      val newCurrentSpeed = this.currentSpeed
      if (MathUtil.fuzzyCompare(newCurrentSpeed, upperSpeedLimit) > 0) setSpeed(upperSpeedLimit)
      else if (MathUtil.fuzzyCompare(newCurrentSpeed, lowerSpeedLimit) < 0) setSpeed(lowerSpeedLimit)
    }
  }

  /**
    * Updates the motion to the current direction.
    */
  @LogicalSideOnly(Side.SERVER)
  def setSpeed(speed: Double): Unit = {
    motionX = direction.x * speed
    motionY = direction.y * speed
    motionZ = direction.z * speed
  }

  @LogicalSideOnly(Side.SERVER)
  def addSpeed(speed: Double): Unit = {
    motionX += direction.x * speed
    motionY += direction.y * speed
    motionZ += direction.z * speed
  }

  @LogicalSideOnly(Side.SERVER)
  def resetMotion(): Unit = {
    val speedOriginal = movement.getSpeedOriginal
    motionX = direction.x * speedOriginal
    motionY = direction.y * speedOriginal
    motionZ = direction.z * speedOriginal

    prevRotationYaw = direction.yaw.toFloat
    rotationYaw = prevRotationYaw
    prevRotationPitch = direction.pitch.toFloat
    rotationPitch = prevRotationPitch
  }

  override protected def entityInit(): Unit = {
    dataManager.register(EntityDanmaku.ShotDataKey, ShotData.DefaultShotData)
    dataManager.register(EntityDanmaku.RollKey, Float.box(0F))
  }

  def shotData:    ShotData = dataManager.get(EntityDanmaku.ShotDataKey)
  def getShotData: ShotData = shotData

  def shotData_=(shot: ShotData, forceNewSubEntity: Boolean): Unit = setShotData(shot, forceNewSubEntity = false)
  def setShotData(shot: ShotData):                            Unit = shotData = shot

  def setShotData(shot: ShotData, forceNewSubEntity: Boolean): Unit = {
    val oldShot = shotData
    val first   = subEntity == null
    //The first time we call this the subentity isn't created yet
    val toUse =
      if (first) shot
      else subEntity.onShotDataChange(oldShot, oldShot.form.onShotDataChange(oldShot, shot), shot)
    val oldSubEntity = oldShot.subEntity
    dataManager.set(EntityDanmaku.ShotDataKey, toUse)
    if ((toUse.subEntity != oldSubEntity) || first || forceNewSubEntity) {
      subEntity = toUse.subEntity.instantiate(world, this)
    }
  }

  def roll:                    Float = dataManager.get(EntityDanmaku.RollKey)
  def getRoll:                 Float = roll
  def roll_=(newRoll: Float):  Unit  = dataManager.set(EntityDanmaku.RollKey, Float.box(roll))
  def setRoll(newRoll: Float): Unit  = roll = newRoll

  def setSubEntity(`type`: SubEntityType): SubEntity = {
    shotData = shotData.setSubEntity(`type`)
    subEntity
  }

  def pos: Vector3 = new Vector3(this)

  @LogicalSideOnly(Side.SERVER)
  def getUser: Optional[EntityLivingBase] = user.toOptional
  @LogicalSideOnly(Side.SERVER)
  def setUser(@Nullable user: EntityLivingBase): Unit = this.user = Option(user)

  @LogicalSideOnly(Side.SERVER)
  def getSource: Optional[Entity] = source.toOptional
  @LogicalSideOnly(Side.SERVER)
  def setSource(@Nullable source: Entity): Unit = this.source = Option(source)

  @LogicalSideOnly(Side.SERVER)
  def getDirection: Vector3 = direction
  @LogicalSideOnly(Side.SERVER)
  def setDirection(direction: Vector3): Unit = this.direction = direction

  @LogicalSideOnly(Side.SERVER)
  def movement: MovementData = _movement
  @LogicalSideOnly(Side.SERVER)
  def getMovementData: MovementData = movement

  @LogicalSideOnly(Side.SERVER)
  def movement_=(movement: MovementData): Unit = {
    val old = this._movement
    this._movement = subEntity.onMovementDataChange(old, shotData.form.onMovementDataChange(old, movement), movement)
  }
  @LogicalSideOnly(Side.SERVER)
  def setMovementData(movement: MovementData): Unit = this.movement = movement

  @LogicalSideOnly(Side.SERVER)
  def rotation: RotationData = _rotation
  @LogicalSideOnly(Side.SERVER)
  def getRotationData: RotationData = rotation

  @LogicalSideOnly(Side.SERVER)
  def rotation_=(rotation: RotationData): Unit = {
    val old = this._rotation
    this._rotation = subEntity.onRotationDataChange(old, shotData.form.onRotationDataChange(old, rotation), rotation)
  }
  @LogicalSideOnly(Side.SERVER)
  def setRotationData(rotation: RotationData): Unit = this.rotation = rotation

  @LogicalSideOnly(Side.SERVER)
  def currentSpeed: Double = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ)
  @LogicalSideOnly(Side.SERVER)
  def getCurrentSpeed: Double = currentSpeed

  override protected def canTriggerWalking = false

  override def doesEntityNotTriggerPressurePlate = true

  protected def setSize(width: Float, height: Float, length: Float): Unit = {
    val f = this.width
    this.width = width
    this.height = height
    val shot = shotData
    shotData = shot.setSizeX(width).setSizeY(height).setSizeZ(length)
    setEntityBoundingBox(
      new AxisAlignedBB(
        getEntityBoundingBox.minX,
        getEntityBoundingBox.minY,
        getEntityBoundingBox.minZ,
        getEntityBoundingBox.minX + width,
        getEntityBoundingBox.minY + height,
        getEntityBoundingBox.minZ + length
      )
    )
    if (this.width > f && !firstUpdate && !world.isRemote) move(MoverType.SELF, f - width, 0D, f - length)
  }

  override protected def setSize(width: Float, height: Float): Unit = setSize(width, height, width)

  override def setPosition(x: Double, y: Double, z: Double): Unit =
    if (dataManager != null &&
        ConfigHandler.danmaku.useComplexHitbox) {
      this.posX = x
      this.posY = y
      this.posZ = z
      this.setEntityBoundingBox(getRoughScaledBoundingBox(x, y, z))
    } else super.setPosition(x, y, z)

  @Nullable override def getParts: Array[MultiPartEntityPart] = hitboxes

  def setParts(@Nullable parts: Array[MultiPartEntityPart]): Unit = this.hitboxes = parts

  private def getRoughScaledBoundingBox(x: Double, y: Double, z: Double) = {
    val shot            = shotData
    val danmakuRotation = Quat.fromEuler(rotationYaw, rotationPitch, roll)
    val size            = new Vector3(shot.sizeX, shot.sizeY, shot.sizeZ).rotate(danmakuRotation)
    val xSize           = size.x / 2F
    val zSize           = size.z / 2F
    val ySize           = size.y / 2F
    new AxisAlignedBB(x - xSize, y - ySize, z - zSize, x + xSize, y + ySize, z + zSize)
  }

  override def writeEntityToNBT(nbtTag: NBTTagCompound): Unit = {
    user.foreach(living => nbtTag.setUniqueId(NbtUserUUID, living.getUniqueID))
    source.foreach(entity => nbtTag.setUniqueId(NbtSourceUUID, entity.getUniqueID))
    NBTHelper.setVector(nbtTag, NbtDirection, direction)
    nbtTag.setTag(NbtMovement, movement.serializeNBT)
    nbtTag.setTag(NbtRotation, rotation.serializeNBT)
    val shot        = shotData
    val nbtShotData = shot.serializeNBT
    nbtTag.setTag(NbtShotData, nbtShotData)
    nbtTag.setFloat(NBTRoll, roll)
  }

  override def readEntityFromNBT(nbtTag: NBTTagCompound): Unit = {
    direction = NBTHelper.getVector(nbtTag, NbtDirection)
    _movement = MovementData.fromNBT(nbtTag.getCompoundTag(NbtMovement))
    _rotation = RotationData.fromNBT(nbtTag.getCompoundTag(NbtRotation))
    val nbtShotData = nbtTag.getCompoundTag(NbtShotData)
    val shot        = new ShotData(nbtShotData)
    shotData = shot
    setRoll(nbtTag.getFloat(NBTRoll))

    user = Option(nbtTag.getUniqueId(NbtUserUUID)).flatMap { userUUID =>
      Option(world.getPlayerEntityByUUID(userUUID)).orElse(findEntityByUUID(userUUID).collect {
        case living: EntityLivingBase => living
      })
    }

    source = Option(nbtTag.getUniqueId(NbtSourceUUID)).flatMap { sourceUUID =>
      Option(world.getPlayerEntityByUUID(sourceUUID)).orElse(findEntityByUUID(sourceUUID))
    }
  }

  private def findEntityByUUID(uuid: UUID): Option[Entity] =
    world.loadedEntityList.asScala.find(_.getUniqueID == uuid)

  /**
    * Side safe way to remove danmaku.
    */
  def delete(): Unit = if (!world.isRemote) setDead()

  /**
    * Sets the subEntity of this Danmaku to the default.
    */
  def setSubEntityDefault(): Unit = setSubEntity(LibSubEntities.DEFAULT_TYPE)

  /**
    * Tests if this shot will be removed because of the end time.
    */
  def isShotEndTime: Boolean = ticksExisted >= shotData.end

  @LogicalSideOnly(Side.SERVER)
  def danmakuFinishBonus(): Unit = {
    val shot = shotData

    val target = user.flatMap(u => Option(u.getLastDamageSource)).flatMap(s => Option(s.getImmediateSource)).collect {
      case living: EntityLivingBase => living
    }

    val launchDirection = target.fold(Vector3.Down)(to => Vector3.directionToEntity(this, to))
    if (shot.sizeZ > 1F && shot.sizeZ / shot.sizeX > 3 && shot.sizeZ / shot.sizeY > 3) {
      for (zPos <- 0 until shot.sizeZ) {
        val realPos = pos.offset(launchDirection, zPos)
        world.spawnEntity(TouhouHelper.createScoreGreen(world, target, realPos, launchDirection))
      }
      setDead()
    } else {
      world.spawnEntity(TouhouHelper.createScoreGreen(world, target, pos, launchDirection))
      setDead()
    }
  }

  override def getLookVec: Vec3d =
    if (!world.isRemote) direction.toVec3d
    else super.getLookVec

  override def getBrightness = 1.0F

  @SideOnly(Side.CLIENT)
  override def getBrightnessForRender = 0xf000f0

  def getSubEntity: SubEntity = subEntity

  def getRNG: Random = rand

  def getOrientedBoundingBox: OrientedBoundingBox = {
    val orientation = Quat.fromEuler(rotationYaw, rotationPitch, roll)
    val aabb        = getRoughScaledBoundingBox(posX, posY, posZ)
    new OrientedBoundingBox(aabb, new Vector3(this), orientation)
  }

  override def getWorld: World = world

  override def attackEntityFromPart(dragonPart: MultiPartEntityPart, source: DamageSource, damage: Float): Boolean =
    attackEntityFrom(source, damage)
}
