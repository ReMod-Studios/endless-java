package com.remodstudios.endless.blocks

import com.remodstudios.endless.Endless
import com.remodstudios.endless.Endless.id
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.MaterialColor
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.registry.Registry.BLOCK_KEY
import java.util.function.Supplier

object BlockRegistry {
    private val REGISTRY = Endless.REGISTRIES.get(BLOCK_KEY);

    private fun register(path: String, supplier: Supplier<Block>): RegistrySupplier<Block> {
        return REGISTRY.registerSupplied(id(path), supplier);
    }

    val TEST_BLOCK = register("test_block") { Block(Settings.of(Material.STONE, MaterialColor.PURPLE)) }
    val STATIC_CHARGE = register("static_charge")
    { StaticChargeBlock(Settings.of(Material.PLANT, MaterialColor.YELLOW).sounds(BlockSoundGroup.WOOL).nonOpaque()) }

    fun register() {
        /* clinit */
    }
}