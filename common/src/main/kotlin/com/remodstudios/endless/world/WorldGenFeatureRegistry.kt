package com.remodstudios.endless.world

import com.remodstudios.endless.Endless.id
import com.remodstudios.endless.blocks.BlockRegistry
import me.shedaniel.architectury.registry.BiomeModifications
import net.minecraft.block.Blocks
import net.minecraft.structure.rule.BlockMatchRuleTest
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.OreFeatureConfig

object WorldGenFeatureRegistry {
    private val RULE_END_STONE = BlockMatchRuleTest(Blocks.END_STONE)
    private val HEIGHTMAP_SURFACE_SQUARE = ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE.spreadHorizontally()

    private fun register(path: String, feature: ConfiguredFeature<*, *>): ConfiguredFeature<*, *> {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id(path), feature)
    }

    private val LUNARIC_END_STONE = register("lunaric_end_stone",
        Feature.ORE.configure(OreFeatureConfig(RULE_END_STONE, BlockRegistry.LUNARIC_END_STONE.get().defaultState, 9))
            .decorate(HEIGHTMAP_SURFACE_SQUARE.applyChance(10).repeat(10)))
    private val COBALT_ORE = register("cobalt_ore",
        Feature.ORE.configure(OreFeatureConfig(RULE_END_STONE, BlockRegistry.COBALT_ORE.get().defaultState, 6))
            .decorate(HEIGHTMAP_SURFACE_SQUARE.applyChance(20).repeat(20)))
    private val THERAKIUM_ORE = register("therakium_ore",
        Feature.ORE.configure(OreFeatureConfig(RULE_END_STONE, BlockRegistry.THERAKIUM_ORE.get().defaultState, 4))
            .decorate(HEIGHTMAP_SURFACE_SQUARE.applyChance(40).repeat(10)))

    fun register() {
        /* clinit */

        BiomeModifications.addProperties({ bCtx -> bCtx.properties.category == Biome.Category.THEEND })
        { bCtx, bProps ->
            println("Adding ores to " + bCtx.key)
            with(bProps) {
                generationProperties
                    .addFeature(GenerationStep.Feature.UNDERGROUND_ORES, COBALT_ORE)
                    .addFeature(GenerationStep.Feature.UNDERGROUND_ORES, THERAKIUM_ORE)
                    .addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, LUNARIC_END_STONE)
            }
        }
    }
}