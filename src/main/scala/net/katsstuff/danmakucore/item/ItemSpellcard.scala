/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.item

import java.util

import javax.annotation.Nullable

import scala.collection.JavaConverters._

import net.katsstuff.danmakucore.SpellcardsCreativeTab
import net.katsstuff.danmakucore.capability.owner.HasOwnerProvider
import net.katsstuff.danmakucore.entity.living.TouhouCharacter
import net.katsstuff.danmakucore.entity.spellcard.Spellcard
import net.katsstuff.danmakucore.lib.LibItemName
import net.katsstuff.danmakucore.lib.data.{LibItems, LibSpellcards}
import net.katsstuff.danmakucore.misc.StringNBTProperty
import net.katsstuff.danmakucore.registry.DanmakuRegistry
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{ActionResult, EnumActionResult, EnumHand, NonNullList, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object ItemSpellcard {
  private val Spellcard =
    StringNBTProperty.ofStack("spellcard", () => LibSpellcards.DELUSION_OF_ENLIGHTENMENT.fullNameString)

  def getSpellcard(stack: ItemStack): Spellcard = {
    val spellcard = DanmakuRegistry.Spellcard.getValue(new ResourceLocation(Spellcard.get(stack)))
    if (spellcard == null) {
      val default = LibSpellcards.DELUSION_OF_ENLIGHTENMENT
      Spellcard.set(default.fullNameString, stack)
      default
    } else spellcard
  }

  def createStack(spellcard: Spellcard): ItemStack = {
    val stack = new ItemStack(LibItems.SPELLCARD, 1)
    Spellcard.set(spellcard.fullNameString, stack)
    stack
  }
}
class ItemSpellcard extends ItemBase(LibItemName.SPELLCARD) {
  maxStackSize = 1
  setMaxDamage(0)
  setCreativeTab(SpellcardsCreativeTab)

  override def getSubItems(tab: CreativeTabs, subItems: NonNullList[ItemStack]): Unit = {
    if(isInCreativeTab(tab)) {
      subItems.addAll(DanmakuRegistry.Spellcard.getValues.asScala.sorted.map(ItemSpellcard.createStack).asJava)
    }
  }

  override def onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    val stack     = player.getHeldItem(hand)
    val spellcard = ItemSpellcard.getSpellcard(stack)

    if (!world.isRemote) {
      val result = spellcard.create(player, firstAttack = true)
      result.fold(new ActionResult(EnumActionResult.FAIL, stack))(
        _ => new ActionResult(EnumActionResult.SUCCESS, stack)
      )
    } else super.onItemRightClick(world, player, hand)
  }

  override def getUnlocalizedName(stack: ItemStack): String =
    s"${super.getUnlocalizedName}.${ItemSpellcard.getSpellcard(stack).unlocalizedName}"

  @SideOnly(Side.CLIENT)
  override def addInformation(
      stack: ItemStack,
      @Nullable world: World,
      list: util.List[String],
      flag: ITooltipFlag
  ): Unit = {
    super.addInformation(stack, world, list, flag)
    val spellcard = ItemSpellcard.getSpellcard(stack)
    val item      = "item.spellcard"

    list.add(s"${I18n.format(s"$item.level")} ${spellcard.level} ${I18n.format(s"$item.spellcard")}")
    list.add(s"${I18n.format(s"$item.user")} : ${I18n.format(spellcard.unlocalizedName)}")
    list.add(s"${I18n.format(s"$item.removeTime")} : ${spellcard.removeTime}")
    list.add(s"${I18n.format(s"$item.endTime")} : ${spellcard.endTime}")
  }

  override def initCapabilities(stack: ItemStack, nbt: NBTTagCompound): ICapabilityProvider =
    HasOwnerProvider(ItemSpellcard.getSpellcard(stack).touhouUser)
}
