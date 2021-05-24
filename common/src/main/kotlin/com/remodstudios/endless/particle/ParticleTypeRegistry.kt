package com.remodstudios.endless.particle

import com.remodstudios.endless.Endless
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.particle.ParticleType
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object ParticleTypeRegistry {
    private val REGISTRY = Endless.registry(Registry.PARTICLE_TYPE_KEY)

    private fun register(path: String, supplier: Supplier<ParticleType<*>>): RegistrySupplier<ParticleType<*>> {
        return REGISTRY.register(path, supplier);
    }

    val STATIC_SPARK = register("static_spark")
    { ModDefaultParticleType(false) }

    fun register() {
        /* clinit */
        REGISTRY.register()
    }
}