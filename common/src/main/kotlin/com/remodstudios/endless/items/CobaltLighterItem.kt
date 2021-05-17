package com.remodstudios.endless.items

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class CobaltLighterItem(settings: Settings?) : Item(settings) {
    override fun use(world: World?, playerEntity: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        return super.use(world, playerEntity, hand)
    }
}