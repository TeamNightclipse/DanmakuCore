/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network

import net.katsstuff.danmakucore.lib.LibMod
import net.katsstuff.danmakucore.network.scalachannel.ScalaNetworkWrapper

object DanCorePacketHandler extends ScalaNetworkWrapper(LibMod.Id) {
  private[danmakucore] def load(): Unit = {
    registerMessages {
      for {
        _ <- registerMessage[ChargeSpherePacket]
        _ <- registerMessage[DanCoreDataPacket]
        _ <- registerMessage[ParticlePacket]
        _ <- registerMessage[PhaseDataPacket]
        _ <- registerMessage[SpellcardInfoPacket]
      } yield ()
    }
  }
}
