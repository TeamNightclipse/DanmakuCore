/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network.scalachannel

import scala.annotation.implicitNotFound

@implicitNotFound("Could not prove that ${A} can be sent to the client")
trait HasClientHandler[-A]

@implicitNotFound("Could not prove that ${A} can be sent to the server")
trait HasServerHandler[-A]
