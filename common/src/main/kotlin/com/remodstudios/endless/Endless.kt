package com.remodstudios.endless

import com.remodstudios.endless.blocks.BlockRegistry
import com.remodstudios.endless.items.ItemRegistry
import com.remodstudios.endless.world.WorldGenFeatureRegistry
import me.shedaniel.architectury.registry.Registries
import net.minecraft.entity.damage.DamageSource
import net.minecraft.util.Identifier

object Endless {
    const val MOD_ID = "endless"
    val REGISTRIES by lazy { Registries.get(MOD_ID) }

    val SHOCK_DAMAGE_SOURCE: DamageSource = ModDamageSource("shock").setUnblockable()

    fun init() {
        printHelloWorld()
        BlockRegistry.register()
        ItemRegistry.register()
        WorldGenFeatureRegistry.register()
        //ParticleTypeRegistry.register() // TODO figure out how to particle in Architectury
    }

    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }
}