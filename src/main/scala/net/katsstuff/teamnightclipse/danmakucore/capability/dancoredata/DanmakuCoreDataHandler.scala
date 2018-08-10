/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata

import javax.annotation.Nullable

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DamageSourceDanmaku
import net.katsstuff.teamnightclipse.danmakucore.handler.ConfigHandler
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.TouhouHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.init.SoundEvents
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumFacing, SoundCategory}
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.{PlayerEvent => GamePlayerEvent}

class DanmakuCoreDataProvider extends ICapabilitySerializable[NBTTagCompound] {
  val CoreData: Capability[IDanmakuCoreData] = CapabilityDanCoreDataJ.CORE_DATA

  final private val data = BoundedDanmakuCoreData(
    0F,
    0,
    ConfigHandler.gameplay.defaultLivesAmount,
    ConfigHandler.gameplay.defaultBombsAmount,
    4F,
    9
  )
  override def hasCapability(capability: Capability[_], @Nullable facing: EnumFacing): Boolean =
    capability == CoreData

  override def getCapability[T](capability: Capability[T], @Nullable facing: EnumFacing): T =
    if (capability == CoreData) CoreData.cast(data) else CoreData.cast(null)

  override def serializeNBT: NBTTagCompound = CoreData.writeNBT(data, null).asInstanceOf[NBTTagCompound]

  override def deserializeNBT(nbt: NBTTagCompound): Unit = CoreData.readNBT(data, null, nbt)
}

object DanmakuCoreDataHandler {
  @SubscribeEvent
  def onLogin(event: GamePlayerEvent.PlayerLoggedInEvent): Unit =
    TouhouHelper.getDanmakuCoreData(event.player).foreach { data =>
      event.player match {
        case p: EntityPlayerMP => data.syncTo(p, event.player)
        case _                 =>
      }
    }

  @SubscribeEvent
  def onPlayerChangedDimension(event: GamePlayerEvent.PlayerChangedDimensionEvent): Unit =
    TouhouHelper.getDanmakuCoreData(event.player).foreach { data =>
      event.player match {
        case p: EntityPlayerMP => data.syncTo(p, event.player)
        case _                 =>
      }
    }

  @SubscribeEvent
  def attachPlayer(event: AttachCapabilitiesEvent[Entity]): Unit =
    if (event.getObject.isInstanceOf[EntityPlayer])
      event.addCapability(DanmakuCore.resource("DanmakuCoreData"), new DanmakuCoreDataProvider)

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onDeath(event: LivingDeathEvent): Unit = {
    val living = event.getEntityLiving
    living match {
      case player: EntityPlayer
          if !player.world.isRemote && event.getSource
            .isInstanceOf[DamageSourceDanmaku] && TouhouHelper.getDanmakuCoreData(living).fold(0)(_.lives) >= 1 =>
        TouhouHelper.changeAndSyncPlayerData(data => {
          if (ConfigHandler.gameplay.resetBombsOnDeath) data.setBombs(ConfigHandler.gameplay.defaultBombsAmount)
          data.removeLife()
        }, player)
        player.isDead = false
        player.setHealth(player.getMaxHealth)
        player.hurtResistantTime = 50

        player.world.playSound(
          null,
          player.posX,
          player.posY,
          player.posZ,
          SoundEvents.BLOCK_GLASS_BREAK, //TODO
          SoundCategory.PLAYERS,
          3F,
          1F
        )
        event.setCanceled(true)

      case _ =>
    }
  }

  @SubscribeEvent
  def onPlayerClone(event: PlayerEvent.Clone): Unit = if (!event.isWasDeath) {
    val oldPlayer = event.getOriginal
    val newPlayer = event.getEntityPlayer
    TouhouHelper.getDanmakuCoreData(oldPlayer).foreach { oldData =>
      TouhouHelper.changeAndSyncPlayerData(
        newData => {
          newData.setPower(oldData.getPower)
          newData.setScore(oldData.getScore)
          newData.setBombs(oldData.getBombs)
          newData.setLives(oldData.getLives)
        },
        newPlayer
      )
    }
  }
}
