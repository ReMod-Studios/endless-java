package io.github.remodstudios.endless.world

import io.github.remodstudios.endless.world.biome.EndlessBiomes
import io.github.remodstudios.endless.world.gen.feature.EndlessConfiguredFeatures

object EndlessWorld {
    fun register() {
        EndlessConfiguredFeatures.register()
        EndlessBiomes.register()  // FIXME figure out this PoS -ADCLeo
    }
}