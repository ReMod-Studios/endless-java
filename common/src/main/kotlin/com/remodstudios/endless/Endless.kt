package com.remodstudios.endless

import com.remodstudios.endless.blocks.BlockRegistry
import com.remodstudios.endless.items.ItemRegistry
import com.remodstudios.endless.world.WorldRegistries
import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.entity.damage.DamageSource
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

object Endless {
    const val MOD_ID = "endless"

    @JvmField
    val SHOCK_DAMAGE_SOURCE: DamageSource = ModDamageSource("shock").setUnblockable()

    fun init() {
        printHelloWorld()
        BlockRegistry.register()
        ItemRegistry.register()
        WorldRegistries.register()
        //ParticleTypeRegistry.register()   // TODO figure out how to particle in Architectury
    }

    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }

    fun <T> registry(key: RegistryKey<Registry<T>>): DeferredRegister<T> {
        return DeferredRegister.create(MOD_ID, key)
    }
}