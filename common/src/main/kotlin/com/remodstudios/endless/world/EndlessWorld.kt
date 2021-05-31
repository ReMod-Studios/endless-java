package com.remodstudios.endless.world

import com.remodstudios.endless.world.biome.EndlessBiomes
import com.remodstudios.endless.world.gen.feature.EndlessConfiguredFeatures

object EndlessWorld {
    fun register() {
        EndlessConfiguredFeatures.register()
        EndlessBiomes.register()  // FIXME figure out this PoS -ADCLeo
    }
}