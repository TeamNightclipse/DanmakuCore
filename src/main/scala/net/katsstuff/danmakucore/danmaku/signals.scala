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
import net.katsstuff.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.katsstuff.mirror.data.{Quat, Vector3}
import net.katsstuff.mirror.network.scalachannel.{Discriminator, MessageConverter}
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.common.FMLCommonHandler

case class ChangedPosDanmaku(pos: Vector3) extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] =
    Some(state.copy(entity = state.entity.copy(pos = pos, prevPos = state.entity.pos)))
}
object ChangedPosDanmaku {
  implicit val discriminator: Discriminator[ChangedPosDanmaku] = Discriminator(0.toByte)
}

case class ChangedMotionDanmaku(motion: Vector3) extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] =
    Some(state.copy(entity = state.entity.copy(motion = motion)))
}
object ChangedMotionDanmaku {
  implicit val discriminator: Discriminator[ChangedMotionDanmaku] = Discriminator(1.toByte)
}

case class ChangedDirectionDanmaku(direction: Vector3) extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] =
    Some(state.copy(entity = state.entity.copy(direction = direction)))
}
object ChangedDirectionDanmaku {
  implicit val discriminator: Discriminator[ChangedDirectionDanmaku] = Discriminator(2.toByte)
}

case class ChangedOrientationDanmaku(orientation: Quat) extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] = Some(
    state
      .copy(entity = state.entity.copy(orientation = orientation, prevOrientation = state.entity.prevOrientation))
  )
}
object ChangedOrientationDanmaku {
  implicit val discriminator: Discriminator[ChangedOrientationDanmaku] = Discriminator(3.toByte)
}

case class ChangedShotDataDanmaku(shotData: ShotData) extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] =
    Some(state.copy(extra = state.extra.copy(shot = shotData)))
}
object ChangedShotDataDanmaku {
  implicit val discriminator: Discriminator[ChangedShotDataDanmaku] = Discriminator(4.toByte)
}

case class ChangedMovementDataDanmaku(movementData: MovementData) extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] =
    Some(state.copy(extra = state.extra.copy(movement = movementData)))
}
object ChangedMovementDataDanmaku {
  implicit val discriminator: Discriminator[ChangedMovementDataDanmaku] = Discriminator(5.toByte)
}

case class ChangedRotationDataDanmaku(rotationData: RotationData) extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] =
    Some(state.copy(extra = state.extra.copy(rotation = rotationData)))
}
object ChangedRotationDataDanmaku {
  implicit val discriminator: Discriminator[ChangedRotationDataDanmaku] = Discriminator(6.toByte)
}

case class SetDeadDanmaku() extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] = None
}
object SetDeadDanmaku {
  implicit val discriminator: Discriminator[SetDeadDanmaku] = Discriminator(7.toByte)
}

case class FinishDanmaku() extends DanmakuUpdateSignal {
  override def process(state: DanmakuState): Option[DanmakuState] = {
    val shot  = state.shot
    val world = state.world
    val pos   = state.pos

    val target =
      state.user.flatMap(u => Option(u.getLastDamageSource)).flatMap(s => Option(s.getImmediateSource)).collect {
        case living: EntityLivingBase => living
      }

    val launchDirection = target.fold(Vector3.Down)(to => Vector3.directionToEntity(pos, to))
    if (!world.isRemote) {
      FMLCommonHandler
        .instance()
        .getMinecraftServerInstance
        .addScheduledTask(() => {
          if (shot.sizeZ > 1F && shot.sizeZ / shot.sizeX > 3 && shot.sizeZ / shot.sizeY > 3) {
            for (zPos <- 0 until shot.sizeZ.toInt) {
              val realPos = pos.offset(launchDirection, zPos)
              world.spawnEntity(TouhouHelper.createScoreGreen(world, target, realPos, launchDirection))
            }
          } else {
            world.spawnEntity(TouhouHelper.createScoreGreen(world, target, pos, launchDirection))
          }
        })
    }

    None
  }
}
object FinishDanmaku {
  implicit val discriminator: Discriminator[FinishDanmaku] = Discriminator(8.toByte)
}

case class DanmakuChanges(id: Int, signals: Seq[DanmakuUpdateSignal])
object DanmakuChanges {
  implicit val converter: MessageConverter[DanmakuChanges] = new MessageConverter[DanmakuChanges] {

    override def writeBytes(a: DanmakuChanges, buf: ByteBuf): Unit = {
      import MessageConverter.Ops
      buf.write(a.id)
      buf.write(a.signals)
    }

    override def readBytes(buf: ByteBuf): DanmakuChanges = {
      import MessageConverter.Ops
      DanmakuChanges(buf.read[Int], buf.read[Seq[DanmakuUpdateSignal]])
    }
  }
}
sealed trait DanmakuUpdateSignal {
  def process(state: DanmakuState): Option[DanmakuState]
}
object DanmakuUpdateSignal {
  implicit val converter: MessageConverter[DanmakuUpdateSignal] = MessageConverter.mkDeriver[DanmakuUpdateSignal].apply
}
