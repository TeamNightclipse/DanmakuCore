package net.katsstuff.danmakucore.blocks

import net.katsstuff.danmakucore.client.gui.screen.DanmakuEditorScreen
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.minecraft.world.level.block.state.{BlockBehaviour, BlockState}
import net.minecraft.world.level.block.{Block, SoundType}
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.{BlockGetter, Level}
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.{CollisionContext, Shapes, VoxelShape}
import net.minecraft.world.{InteractionHand, InteractionResult}

//noinspection ScalaDeprecation
class BlockDanmakuCraftingTable
    extends Block(
      BlockBehaviour.Properties
        .of()
        .mapColor(MapColor.COLOR_RED)
        .instrument(NoteBlockInstrument.BASS)
        .strength(2.5F)
        .sound(SoundType.WOOD)
        .ignitedByLava()
    ) {

  private val BoundingBoxAABB: VoxelShape = Block.box(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D)

  override def use(
      state: BlockState,
      level: Level,
      pos: BlockPos,
      player: Player,
      hand: InteractionHand,
      hit: BlockHitResult
  ): InteractionResult = {
    if (level.isClientSide) //this.openScreen(level, pPos, pPlayer)
      Minecraft.getInstance().setScreen(new DanmakuEditorScreen)

    InteractionResult.sidedSuccess(level.isClientSide)

  }
  /*
    if level.isClientSide then InteractionResult.SUCCESS
    else {
      player.openMenu(state.getMenuProvider(level, pos))
      InteractionResult.CONSUME
    }*/

  override def getShape(
      pState: BlockState,
      pLevel: BlockGetter,
      pPos: BlockPos,
      pContext: CollisionContext
  ): VoxelShape = Shapes.or(
    Block.box(1, 7, 1, 15, 8, 15),
    Block.box(5, 1, 5, 11, 7, 11),
    Block.box(3, 0, 3, 13, 1, 13)
  )

  /*
  override def getMenuProvider(pState: BlockState, pLevel: Level, pPos: BlockPos): MenuProvider = {
    return new SimpleMenuProvider(
      (p_52229: Int, p_52230: Inventory, p_52231: Player) => {
        new CraftingMenu(p_52229_, p_52230_, ContainerLevelAccess.create(pLevel, pPos))
      },
      Component.literal("Danmaku Crafting Table")
    )
  }
   */
}
