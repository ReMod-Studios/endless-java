package com.remodstudios.endless.world.biome

import com.remodstudios.endless.Endless
import com.remodstudios.remodcore.biome.BiomeTemplate
import com.remodstudios.remodcore.registry.BiomeRegistryHelper
import net.minecraft.sound.BiomeMoodSound
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders

object EndlessBiomes : BiomeRegistryHelper(Endless.MOD_ID) {
    private val END_TEMPLATE = BiomeTemplate.create {
        category = Biome.Category.THEEND
        precipitation = Biome.Precipitation.NONE
        depth = 0.1F
        scale = 0.2F
        temperature = 0.5F
        downfall = 0.5F
        effects {
            fogColor = 0xA080A0
            waterColor = 0x3F76E4
            waterFogColor = 0x050533
            skyColor = 0x000000
            moodSound = BiomeMoodSound.CAVE
        }
        generation {
            surfaceBuilder = ConfiguredSurfaceBuilders.END
            features(GenerationStep.Feature.SURFACE_STRUCTURES) {
                +ConfiguredFeatures.END_SPIKE
            }
        }
        spawns {
            vanilla.endMobs()
        }
    }

    @JvmField
    val TEST_BIOME = add("test_biome", END_TEMPLATE.build {
        effects {
            fogColor = 0x123456
        }
    })
}