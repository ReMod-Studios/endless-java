package com.remodstudios.endless.blocks

import com.remodstudios.endless.Endless
import com.remodstudios.endless.Endless.id
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.block.MaterialColor
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.registry.Registry.BLOCK_KEY
import java.util.function.Supplier

object BlockRegistry {
    private val REGISTRY = Endless.REGISTRIES[BLOCK_KEY]

    private fun register(path: String, supplier: Supplier<Block>): RegistrySupplier<Block> {
        return REGISTRY.registerSupplied(id(path), supplier)
    }

    val COBALT_ORE = register("cobalt_ore")
    {
        Block(Settings.copy(Blocks.END_STONE))
    }
    val COBALT_BLOCK = register("cobalt_block")
    {
        Block(Settings.copy(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE))
    }
    val STATIC_CHARGE = register("static_charge")
    {
        StaticChargeBlock(Settings.of(Material.PLANT, MaterialColor.YELLOW).sounds(BlockSoundGroup.WOOL).nonOpaque())
    }
    val COBALT_REPEL = register("cobalt_repel")
    {
        CobaltRepelBlock(Settings.of(Material.GLASS, MaterialColor.BLUE).nonOpaque())
    }
    val THERAKIUM_ORE = register("therakium_ore")
    {
        Block(Settings.copy(Blocks.END_STONE))
    }
    val THERAKIUM_BLOCK = register("therakium_block")
    {
        Block(Settings.copy(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.STONE))
    }
    val RHYOLITE = register("rhyolite")
    {
        Block(Settings.copy(Blocks.GRANITE))
    }
    val POLISHED_RHYOLITE = register("polished_rhyolite")
    {
        Block(Settings.copy(Blocks.POLISHED_GRANITE))
    }

    fun register() {
        /* clinit */
    }
}