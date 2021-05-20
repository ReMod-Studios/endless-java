package com.remodstudios.endless.items

import com.remodstudios.endless.blocks.BlockRegistry
import com.remodstudios.endless.blocks.StaticChargeBlock
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
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
            world.setBlockState(shockPos, BlockRegistry.STATIC_CHARGE.get().defaultState, 3)
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