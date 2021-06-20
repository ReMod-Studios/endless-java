package io.github.remodstudios.endless.blocks

import io.github.remodstudios.endless.Endless
import io.github.remodstudios.remodcore.registry.BlockRegistryHelper
import me.shedaniel.architectury.registry.BlockProperties
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.block.MaterialColor
import net.minecraft.sound.BlockSoundGroup

object EndlessBlocks : BlockRegistryHelper(Endless.MOD_ID) {
    @JvmField
    val COBALT_ORE = addCopy("cobalt_ore", Blocks.END_STONE)
    @JvmField
    val COBALT_BLOCK = addCopyWithInit("cobalt_block", Blocks.IRON_BLOCK) { sounds(BlockSoundGroup.STONE) }
    @JvmField
    val STATIC_CHARGE = addOfProp("static_charge", BlockProperties.of(Material.PLANT, MaterialColor.YELLOW)) {
        StaticChargeBlock(sounds(BlockSoundGroup.WOOL).nonOpaque())
    }
    @JvmField
    val COBALT_REPEL = addOfProp("cobalt_repel", BlockProperties.of(Material.GLASS, MaterialColor.BLUE)) {
        CobaltRepelBlock(nonOpaque())
    }
    @JvmField
    val THERAKIUM_ORE = addCopy("therakium_ore", Blocks.END_STONE)
    @JvmField
    val THERAKIUM_BLOCK = addCopyWithInit("therakium_block", Blocks.IRON_BLOCK) { Block(sounds(BlockSoundGroup.STONE)) }
    @JvmField
    val RHYOLITE = addCopy("rhyolite", Blocks.GRANITE)
    @JvmField
    val POLISHED_RHYOLITE = addCopy("polished_rhyolite", Blocks.POLISHED_GRANITE)
    @JvmField
    val LUNARIC_END_STONE = addCopy("lunaric_end_stone", Blocks.END_STONE)
}