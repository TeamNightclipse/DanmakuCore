package net.katsstuff.danmakucore.items

import net.katsstuff.danmakucore.client.mirrorshaders.ShaderManager
import net.katsstuff.danmakucore.danmaku.TopDanmakuBehaviorsHandler.{BehaviorPair, DanmakuSpawnData}
import net.katsstuff.danmakucore.danmaku.behaviors.{ComplexMotionBehavior, DanmakuBehaviors, RotateOrientationBehavior, SimpleMotionBehavior}
import net.katsstuff.danmakucore.danmaku.data.ShotData
import net.katsstuff.danmakucore.danmaku.form.DanCoreForms
import net.katsstuff.danmakucore.math.{Quat, Vector3}
import net.katsstuff.danmakucore.{DanmakuCore, DanmakuCreativeTab}
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.{Item, ItemStack}
import net.minecraft.world.level.Level
import net.minecraft.world.{InteractionHand, InteractionResult, InteractionResultHolder}

class DanmakuItem(todo: () => Nothing) extends Item(new Item.Properties().tab(DanmakuCreativeTab)) {
  override def getDescriptionId(stack: ItemStack): String =
    super.getDescriptionId(stack) // TODO s"${getDescriptionId()}.${todo}"

  override def onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult =
    super.onItemUseFirst(stack, context)

  override def use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder[ItemStack] = {
    println(player.getRotationVector.x)
    println(player.getRotationVector.y)

    if (player.isCrouching) {
      if (level.isClientSide) {
        ShaderManager.reloadBlocking()
        println("Reloaded shaders")
      }
    } else if (!level.isClientSide) {
      DanmakuCore.spawnDanmaku(
        Seq(
          DanmakuSpawnData(
            Vector3.WrappedVec3d(player.position()).asImmutable.add(0, player.getEyeHeight, 0),
            Quat.orientationOf(player),
            ShotData(
              DanCoreForms.SphereForm.get(),
              endTime = 500
            ),
            Seq(
              BehaviorPair(DanmakuBehaviors.complexMotionBehavior, ComplexMotionBehavior.Data(Vector3.directionEntity(player) * 0.002F)),
              BehaviorPair(DanmakuBehaviors.rotateOrientationBehavior, RotateOrientationBehavior.Data(Quat.Identity)),
              BehaviorPair(DanmakuBehaviors.mandatoryEndBehavior, ())
            ),
            Nil,
            None,
            Nil
          )
        )
      )
    }

    super.use(level, player, hand)
  }

  override def getHighlightTip(item: ItemStack, displayName: Component): Component =
    super.getHighlightTip(item, displayName)
}
