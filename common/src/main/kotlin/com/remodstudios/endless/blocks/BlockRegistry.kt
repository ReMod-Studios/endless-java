package com.remodstudios.endless.blocks

import com.remodstudios.endless.Endless
import com.remodstudios.endless.Endless.id
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.MaterialColor
import net.minecraft.util.registry.Registry.BLOCK_KEY
import java.util.function.Supplier

object BlockRegistry {
    private val REGISTRY = Endless.REGISTRIES.get(BLOCK_KEY);

    private fun register(path: String, supplier: Supplier<Block>): Supplier<Block> {
        return REGISTRY.register(id(path), supplier);
    }

    val TEST_BLOCK = register("test_block") { Block(Settings.of(Material.STONE, MaterialColor.PURPLE)) }

    fun register() {
        /* clinit */
    }
}