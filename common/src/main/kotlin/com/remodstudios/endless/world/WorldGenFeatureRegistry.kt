package com.remodstudios.endless.world

import com.remodstudios.endless.Endless
import com.remodstudios.endless.Endless.id
import com.remodstudios.endless.blocks.BlockRegistry
import me.shedaniel.architectury.registry.BiomeModifications
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.structure.rule.BlockMatchRuleTest
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.OreFeatureConfig
import java.util.function.Supplier

object WorldGenFeatureRegistry {
    private val REGISTRY = Endless.REGISTRIES[Registry.CONFIGURED_FEATURE_WORLDGEN]

    private val RULE_END_STONE = BlockMatchRuleTest(Blocks.END_STONE)

    private lateinit var COBALT_ORE: RegistrySupplier<ConfiguredFeature<*, *>>
    private lateinit var THERAKIUM_ORE: RegistrySupplier<ConfiguredFeature<*, *>>

    private fun register(path: String, supplier: Supplier<ConfiguredFeature<*, *>>): RegistrySupplier<ConfiguredFeature<*, *>> {
        return REGISTRY.registerSupplied(id(path), supplier)
    }

    fun register() {
        COBALT_ORE = register("cobalt_ore") {
            Feature.ORE.configure(OreFeatureConfig(RULE_END_STONE, BlockRegistry.COBALT_ORE.get().defaultState, 9))
                .spreadHorizontally().repeat(10)
        }
        THERAKIUM_ORE = register("therakium_ore") {
            Feature.ORE.configure(OreFeatureConfig(RULE_END_STONE, BlockRegistry.THERAKIUM_ORE.get().defaultState, 8))
                .spreadHorizontally()
        }

        BiomeModifications.addProperties({ bCtx -> bCtx.properties.category == Biome.Category.THEEND },
            { _, bProps ->
                run {
                    bProps.generationProperties.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, COBALT_ORE.get())
                    bProps.generationProperties.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, THERAKIUM_ORE.get())
                }
            })
    }
}