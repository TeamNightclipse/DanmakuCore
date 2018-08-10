/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry

import java.util.Random

import scala.collection.JavaConverters._

import jline.internal.Nullable
import net.katsstuff.danmakucore.danmaku.DanmakuVariant
import net.katsstuff.danmakucore.danmaku.form.Form
import net.katsstuff.danmakucore.danmaku.subentity.SubEntityType
import net.katsstuff.danmakucore.entity.living.phase.PhaseType
import net.katsstuff.danmakucore.entity.spellcard.Spellcard
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.{ForgeRegistry, IForgeRegistry}

object DanmakuRegistry {
  val Form: IForgeRegistry[Form]                     = GameRegistry.findRegistry(classOf[Form])
  val SubEntity: IForgeRegistry[SubEntityType]       = GameRegistry.findRegistry(classOf[SubEntityType])
  val DanmakuVariant: IForgeRegistry[DanmakuVariant] = GameRegistry.findRegistry(classOf[DanmakuVariant])
  val Spellcard: IForgeRegistry[Spellcard]           = GameRegistry.findRegistry(classOf[Spellcard])
  val Phase: IForgeRegistry[PhaseType]               = GameRegistry.findRegistry(classOf[PhaseType])

  def getId[T <: RegistryValue[T]](clazz: Class[T], value: T): Int =
    GameRegistry
      .findRegistry(clazz)
      .asInstanceOf[ForgeRegistry[T]]
      .getID(value)

  @Nullable
  def getObjById[T <: RegistryValue[T]](clazz: Class[T], id: Int): T =
    GameRegistry
      .findRegistry(clazz)
      .asInstanceOf[ForgeRegistry[T]]
      .getValue(id)

  def getRandomObject[T <: RegistryValue[T]](clazz: Class[T], rng: Random): T = {
    val values = GameRegistry.findRegistry(clazz).getValuesCollection.asScala.toSeq
    val idx    = rng.nextInt(values.size)
    values(idx)
  }
}
