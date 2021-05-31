package com.remodstudios.endless.items

import com.remodstudios.endless.blocks.EndlessBlocks
import com.remodstudios.endless.blocks.StaticChargeBlock
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult

class CobaltLighterItem(settings: Settings?) : Item(settings) {
    override fun useOnBlock(ctx: ItemUsageContext?): ActionResult {
        if (ctx == null)
            return ActionResult.PASS
        val player = ctx.player
        val world = ctx.world
        val blockPos = ctx.blockPos
        val blockState = world.getBlockState(blockPos)
        if (blockState.isAir)
            return ActionResult.PASS
        val shockPos = blockPos.offset(ctx.side)
        if (StaticChargeBlock.isValidPlace(world, shockPos)) {
            world.playSound(
                player,
                blockPos,
                SoundEvents.ITEM_FLINTANDSTEEL_USE, // TODO replace with custom sound
                SoundCategory.BLOCKS,
                1.0f,
                RANDOM.nextFloat() * 0.4f + 0.8f
            )
            world.setBlockState(shockPos, EndlessBlocks.STATIC_CHARGE.defaultState, 3)
            if (player is ServerPlayerEntity) {
                Criteria.PLACED_BLOCK.trigger(player as ServerPlayerEntity?, shockPos, ctx.stack)
                ctx.stack.damage(1, player) { playerx -> playerx.sendToolBreakStatus(ctx.hand) }
            }
            if (world.isClient()) {
                // TODO play animation
                return ActionResult.SUCCESS
            } else
                return ActionResult.CONSUME
        }
        return ActionResult.PASS
    }
}