package com.remodstudios.endless.world.biome

import com.remodstudios.endless.Endless.id
import com.remodstudios.endless.mixin.BuiltinBiomesAccessor
import net.minecraft.sound.BiomeMoodSound
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeEffects
import net.minecraft.world.biome.GenerationSettings
import net.minecraft.world.biome.SpawnSettings
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.DefaultBiomeFeatures
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders

object BiomeRegistry {
    private fun register(path: String, biome: Biome): Biome {
        val key = RegistryKey.of(Registry.BIOME_KEY, id(path))
        BuiltinRegistries.add(BuiltinRegistries.BIOME, key.value, biome)
        val rawId = BuiltinRegistries.BIOME.getRawId(biome)
        BuiltinBiomesAccessor.getIdMap()[rawId] = key
        return biome
    }

    // TODO some sort of BiomeTemplate, because this is gonna get repetitive.
    private fun createEndSpawnSettings(): SpawnSettings {
        val builder = SpawnSettings.Builder()
        DefaultBiomeFeatures.addEndMobs(builder)
        return builder.build()
    }

    val TEST_BIOME = register("test_biome", Biome.Builder()
        .category(Biome.Category.THEEND)
        .precipitation(Biome.Precipitation.NONE)
        .depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F)
        .effects(BiomeEffects.Builder()
            .waterColor(0x3F76E4).waterFogColor(0x050533)
            .fogColor(0x123456).skyColor(0x000000)
            .moodSound(BiomeMoodSound.CAVE)
            .build())
        .generationSettings(GenerationSettings.Builder()
            .surfaceBuilder(ConfiguredSurfaceBuilders.END)
            .feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_SPIKE)
            .build())
        .spawnSettings(createEndSpawnSettings())
        .build())

    fun register() {
        /* clinit */
    }
}