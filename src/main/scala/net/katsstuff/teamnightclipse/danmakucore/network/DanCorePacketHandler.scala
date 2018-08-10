/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.network

import net.katsstuff.teamnightclipse.danmakucore.lib.LibMod
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.ScalaNetworkWrapper

object DanCorePacketHandler extends ScalaNetworkWrapper(LibMod.Id) {
  private[danmakucore] def load(): Unit = {
    registerMessages {
      for {
        _ <- registerMessage[ChargeSpherePacket]
        _ <- registerMessage[DanCoreDataPacket]
        _ <- registerMessage[PhaseDataPacket]
        _ <- registerMessage[SpellcardInfoPacket]
        _ <- registerMessage[DanmakuCreatePacket]
        _ <- registerMessage[DanmakuForceUpdatePacket]
        _ <- registerMessage[DanmakuUpdatePacket]
      } yield ()
    }
  }
}
