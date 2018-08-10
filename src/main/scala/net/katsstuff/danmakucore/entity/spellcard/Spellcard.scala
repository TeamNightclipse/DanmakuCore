/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard

import javax.annotation.Nullable

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.entity.living.TouhouCharacter
import net.katsstuff.danmakucore.registry.RegistryValueItemCreatable
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.client.renderer.block.model.{ModelResourceLocation => MRL}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityLiving, EntityLivingBase}
import net.minecraft.util.{EnumHand, ResourceLocation}
import net.minecraft.world.World

abstract class Spellcard extends RegistryValueItemCreatable[Spellcard, EntitySpellcard] {

  def this(name: String) {
    this()
    setRegistryName(name)
    DanmakuCore.proxy.bakeSpellcard(this)
  }

  def instantiate(card: EntitySpellcard, @Nullable target: EntityLivingBase): SpellcardEntity =
    instantiate(card, Option(target))

  /**
    * Creates an instance of this spellcard type.
    */
  def instantiate(card: EntitySpellcard, target: Option[EntityLivingBase]): SpellcardEntity

  /**
    * The needed bombs to execute the spellcard.
    */
  def level: Int

  /**
    * The remove time. How long the spellcard will remove danmaku for.
    */
  //noinspection MutatorLikeMethodIsParameterless
  def removeTime: Int

  /**
    * The end time. How long the spellcard will physically exist and do stuff. Can also be
    * though of as cooldown.
    */
  def endTime: Int

  /**
    * The Touhou character that uses this spellcard in the games.
    */
  def touhouUser: TouhouCharacter

  /**
    * Creates and spawns an entity when a player uses this spellcard.
    */
  def create(player: EntityPlayer, firstAttack: Boolean): Option[EntitySpellcard] =
    TouhouHelper.declareSpellcardPlayer(player, this, firstAttack)

  override def create(
      world: World,
      optUser: Option[EntityLivingBase],
      alternateMode: Boolean,
      pos: Vector3,
      direction: Vector3,
      hand: Option[EnumHand]
  ): Option[EntitySpellcard] = {
    optUser.flatMap { user =>
      user match {
        case entityPlayer: EntityPlayer => TouhouHelper.declareSpellcardPlayer(entityPlayer, this, firstAttack = true)
        case _ =>
          val target = user match {
            case living: EntityLiving if living.getAttackTarget != null => living.getAttackTarget
            case _                                                      => user.getRevengeTarget
          }

          TouhouHelper.declareSpellcard(user, Option.apply(target), this, firstAttack = false, addSpellcardName = true)
      }
    }
  }

  /**
    * Called before declaring a spellcard of this type.
    *
    * @return If the spellcard is allowed to be declared.
    */
  def beforeDeclare(user: EntityLivingBase, @Nullable target: EntityLivingBase, firstAttack: Boolean) = true

  override def unlocalizedName: String = s"spellcard.$modId.$name"

  override def itemModel: MRL = new MRL(new ResourceLocation(modId, s"danmaku/spellcard/$name"), "inventory")
}
object Spellcard {
  implicit val ordering: Ordering[Spellcard] = Ordering.by((card: Spellcard) => card.fullNameString)
}
