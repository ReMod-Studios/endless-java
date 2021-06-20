package io.github.remodstudios.endless.world.gen.feature

import io.github.remodstudios.endless.Endless
import io.github.remodstudios.endless.blocks.EndlessBlocks
import io.github.remodstudios.remodcore.registry.RegistryHelper
import me.shedaniel.architectury.registry.BiomeModifications
import net.minecraft.block.Blocks
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.UniformIntDistribution
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.DiskFeatureConfig
import net.minecraft.world.gen.feature.Feature
import java.util.Collections.singletonList

object EndlessConfiguredFeatures : RegistryHelper<ConfiguredFeature<*, *>>(Endless.registry(Registry.CONFIGURED_FEATURE_WORLDGEN)) {
    private val END_STONE_LIST = singletonList(Blocks.END_STONE.defaultState)
    private val HEIGHTMAP_SURFACE_SQUARE = ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE.spreadHorizontally()

    private fun add(id: String, feature: ConfiguredFeature<*, *>)
            = feature.also { registry.register(id) { feature } }

    private val COBALT_ORE = add("cobalt_ore",
        Feature.DISK.configure(
            DiskFeatureConfig(
                EndlessBlocks.COBALT_ORE.defaultState,
                UniformIntDistribution.of(5, 2), 3, END_STONE_LIST
            )
        )
            .decorate(HEIGHTMAP_SURFACE_SQUARE)
    )
    private val THERAKIUM_ORE = add("therakium_ore",
        Feature.DISK.configure(
            DiskFeatureConfig(
                EndlessBlocks.THERAKIUM_ORE.defaultState,
                UniformIntDistribution.of(3, 2), 2, END_STONE_LIST
            )
        )
            .decorate(HEIGHTMAP_SURFACE_SQUARE)
    )
    private val LUNARIC_END_STONE = add("lunaric_end_stone",
        Feature.DISK.configure(
            DiskFeatureConfig(
                EndlessBlocks.LUNARIC_END_STONE.defaultState,
                UniformIntDistribution.of(4, 3), 1, END_STONE_LIST
            )
        )
            .decorate(HEIGHTMAP_SURFACE_SQUARE)
    )

    override fun register() {
        super.register()

        BiomeModifications.addProperties({ bCtx -> bCtx.properties.category == Biome.Category.THEEND })
        { bCtx, bProps ->
            println("Adding ores to " + bCtx.key)
            println("Heads up: COBALT_ORE id is " + BuiltinRegistries.CONFIGURED_FEATURE.getId(COBALT_ORE))
            bProps.generationProperties
                .addFeature(GenerationStep.Feature.UNDERGROUND_ORES, COBALT_ORE)
                .addFeature(GenerationStep.Feature.UNDERGROUND_ORES, THERAKIUM_ORE)
                .addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, LUNARIC_END_STONE)
        }
    }
}