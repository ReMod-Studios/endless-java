package com.remodstudios.endless.world

import com.remodstudios.endless.world.biome.BiomeRegistry
import com.remodstudios.endless.world.gen.feature.ConfiguredFeatureRegistry

object WorldRegistries {
    fun register() {
        ConfiguredFeatureRegistry.register()
        BiomeRegistry.register()
    }
}