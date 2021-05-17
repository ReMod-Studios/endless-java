package com.remodstudios.endless

import com.remodstudios.endless.blocks.BlockRegistry
import com.remodstudios.endless.items.ItemRegistry
import me.shedaniel.architectury.registry.Registries
import net.minecraft.util.Identifier

object Endless {
    const val MOD_ID = "endless"
    val REGISTRIES by lazy { Registries.get(MOD_ID) }

    fun init() {
        printHelloWorld()
        BlockRegistry.register();
        ItemRegistry.register();
    }

    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }
}