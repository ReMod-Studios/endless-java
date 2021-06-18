package com.remodstudios.endless.items

import com.remodstudios.endless.Endless
import com.remodstudios.endless.Endless.id
import com.remodstudios.endless.blocks.EndlessBlocks
import io.github.remodstudios.remodcore.registry.ItemRegistryHelper
import me.shedaniel.architectury.registry.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object EndlessItems : ItemRegistryHelper(Endless.MOD_ID) {
    private val GROUP: ItemGroup = CreativeTabs.create(id("group")) { ItemStack(COBALT_LIGHTER) }

    override fun defaultSettings(): Item.Settings {
        return Item.Settings().group(GROUP)
    }

    @JvmField
    val COBALT_DUST = add("cobalt_dust")
    @JvmField
    val COBALT_DYNAMITE = addWithFactory("cobalt_dynamite", ::CobaltDynamiteItem)
    @JvmField
    val COBALT_LIGHTER = addWithFactory("cobalt_lighter") { CobaltLighterItem(it.maxDamage(65)) }
    @JvmField
    val THERAKIUM_SHARD = add("therakium_shard")

    // region Block Items
    @JvmField
    val COBALT_REPEL = add("cobalt_repel", EndlessBlocks.COBALT_REPEL)
    @JvmField
    val COBALT_ORE = add("cobalt_ore", EndlessBlocks.COBALT_ORE)
    @JvmField
    val COBALT_BLOCK = add("cobalt_block", EndlessBlocks.COBALT_BLOCK)
    @JvmField
    val THERAKIUM_ORE = add("therakium_ore", EndlessBlocks.THERAKIUM_ORE)
    @JvmField
    val THERAKIUM_BLOCK = add("therakium_block", EndlessBlocks.THERAKIUM_BLOCK)
    @JvmField
    val RHYOLITE = add("rhyolite", EndlessBlocks.RHYOLITE)
    @JvmField
    val POLISHED_RHYOLITE = add("polished_rhyolite", EndlessBlocks.POLISHED_RHYOLITE)
    @JvmField
    val LUNARIC_END_STONE = add("lunaric_end_stone", EndlessBlocks.LUNARIC_END_STONE)
    // endregion
}