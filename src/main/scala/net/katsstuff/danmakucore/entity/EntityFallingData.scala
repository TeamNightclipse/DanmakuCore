/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity

import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.entity.EntityFallingData.DataType
import net.katsstuff.danmakucore.entity.EntityFallingData.DataType.{BigPower, Bomb, Life, Power, ScoreBlue, ScoreGreen}
import net.katsstuff.danmakucore.helper.NBTHelper
import net.katsstuff.danmakucore.lib.{LibEntityName, LibSounds}
import net.katsstuff.danmakucore.misc.LogicalSideOnly
import net.katsstuff.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.{DataSerializer, DataSerializers, EntityDataManager}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side

/**
  * JAVA API
  *
  * Allows access to the different data types from Java.
  */
object FallingDataTypes {
  def scoreGreen: DataType = ScoreGreen
  def scoreBlue:  DataType = ScoreBlue
  def power:      DataType = Power
  def bigPower:   DataType = BigPower
  def life:       DataType = Life
  def bomb:       DataType = Bomb
}
object EntityFallingData {
  trait DataType
  object DataType {
    case object ScoreGreen extends DataType
    case object ScoreBlue  extends DataType
    case object Power      extends DataType
    case object BigPower   extends DataType
    case object Life       extends DataType
    case object Bomb       extends DataType

    def fromId(id: Byte): Option[DataType] = id match {
      case 0 => Some(ScoreGreen)
      case 1 => Some(ScoreBlue)
      case 2 => Some(Power)
      case 3 => Some(BigPower)
      case 4 => Some(Life)
      case 5 => Some(Bomb)
      case _ => None
    }

    def toId(obj: DataType): Byte = obj match {
      case ScoreGreen => 0
      case ScoreBlue  => 1
      case Power      => 2
      case BigPower   => 3
      case Life       => 4
      case Bomb       => 5
    }
  }

  val DataTypeSerializer: DataSerializer[DataType] =
    DataSerializers.BYTE.transform(DataType.fromId(_).get, DataType.toId)
  private val DataTypeKey = EntityDataManager.createKey(classOf[EntityFallingData], DataTypeSerializer)

  implicit val info: EntityInfo[EntityFallingData] = new EntityInfo[EntityFallingData] {
    override def name:                 String            = LibEntityName.FALLING_DATA
    override def create(world: World): EntityFallingData = new EntityFallingData(world)
    override def tracking:             TrackingInfo      = TrackingInfo(range = 40, updateFrequency = 3)
  }
}
class EntityFallingData(
    _world: World,
    @LogicalSideOnly(Side.SERVER) _dataType: DataType,
    @LogicalSideOnly(Side.SERVER) pos: Vector3,
    @LogicalSideOnly(Side.SERVER) var direction: Vector3,
    @LogicalSideOnly(Side.SERVER) var target: Option[Entity],
    @LogicalSideOnly(Side.SERVER) var amount: Float
) extends Entity(_world) {
  if (pos != null && direction != null) {
    setPositionAndRotation(pos.x, pos.y, pos.z, direction.yaw.toFloat, direction.pitch.toFloat)
  }
  if (_dataType != null) {
    dataType = _dataType
  }

  setSize(0.5F, 0.5F)

  def this(world: World) = this(world, null, null, null, None, 0F)

  override protected def entityInit(): Unit =
    dataManager.register(EntityFallingData.DataTypeKey, EntityFallingData.DataType.ScoreGreen)

  override def onUpdate(): Unit = {
    super.onUpdate()
    if (!world.isRemote) {
      val motion = target.fold(direction.multiply(0.25))(Vector3.directionToEntity(this, _))
      motionX = motion.x
      motionY = motion.y
      motionZ = motion.z

      if (!world.isAirBlock(new BlockPos(posX + motionX, posY + motionY, posZ + motionZ))) {
        setDead()
        return
      }

      setPosition(posX + motionX, posY + motionY, posZ + motionZ)
    }
  }

  override def onCollideWithPlayer(player: EntityPlayer): Unit = if (!world.isRemote) {
    dataType match {
      case DataType.ScoreGreen | DataType.ScoreBlue =>
        TouhouHelper.changeAndSyncPlayerData(_.addScore(amount.toInt), player)
        player.playSound(LibSounds.SCORE, 1F, 1F)
      case DataType.Power | DataType.BigPower =>
        TouhouHelper.changeAndSyncPlayerData(_.addPower(amount), player)
      case DataType.Life =>
        TouhouHelper.changeAndSyncPlayerData(_.addLives(amount.toInt), player)
        //player.playSound(LibSounds.EXTEND, 1F, 1F) //TODO: Extend
      case DataType.Bomb =>
        TouhouHelper.changeAndSyncPlayerData(_.addBombs(amount.toInt), player)
    }
    setDead()
  }

  def dataType: DataType = dataManager.get(EntityFallingData.DataTypeKey)

  def dataType_=(dataType: DataType): Unit = dataManager.set(EntityFallingData.DataTypeKey, dataType)

  override protected def readEntityFromNBT(compound: NBTTagCompound): Unit = {
    val targetUuid = compound.getUniqueId("target")
    target = if (targetUuid != null) {
      val targetEntity = world.getPlayerEntityByUUID(targetUuid)
      if (targetEntity == null) NBTHelper.getEntityByUUID(targetUuid, world).toOption else Some(targetEntity)
    } else None
    direction = NBTHelper.getVector(compound, "direction")
    amount = compound.getFloat("amount")
    dataType =
      EntityFallingData.DataType.fromId(compound.getByte("dataType")).getOrElse(EntityFallingData.DataType.ScoreGreen)
  }

  override protected def writeEntityToNBT(compound: NBTTagCompound): Unit = {
    target.foreach(t => compound.setUniqueId("target", t.getUniqueID))
    NBTHelper.setVector(compound, "direction", direction)
    compound.setFloat("amount", amount)
    compound.setByte("dataType", EntityFallingData.DataType.toId(dataType))
  }
}
