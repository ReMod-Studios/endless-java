package io.github.remodstudios.endless

import io.github.remodstudios.endless.blocks.EndlessBlocks
import io.github.remodstudios.endless.items.EndlessItems
import io.github.remodstudios.endless.world.EndlessWorld
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
        EndlessBlocks.register()
        EndlessItems.register()
        EndlessWorld.register()
        //EndlessParticleTypes.register()   // TODO figure out how to particle in Architectury
    }

    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }

    fun <T> registry(key: RegistryKey<Registry<T>>)
            = DeferredRegister.create(MOD_ID, key)
}