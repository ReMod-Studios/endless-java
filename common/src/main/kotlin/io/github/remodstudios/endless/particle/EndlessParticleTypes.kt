package io.github.remodstudios.endless.particle

import io.github.remodstudios.endless.Endless
import io.github.remodstudios.remodcore.registry.RegistryHelper
import net.minecraft.particle.ParticleType
import net.minecraft.util.registry.Registry

object EndlessParticleTypes : RegistryHelper<ParticleType<*>>(Endless.registry(Registry.PARTICLE_TYPE_KEY)) {
    private fun add(id: String, particleType: ParticleType<*>)
            = particleType.also { registry.register(id) { it } }

    val STATIC_SPARK = add("static_spark", ModDefaultParticleType(false))
}