package com.remodstudios.endless.world

import com.remodstudios.endless.Endless
import com.remodstudios.endless.blocks.BlockRegistry
import me.shedaniel.architectury.registry.BiomeModifications
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.block.Blocks
import net.minecraft.structure.rule.BlockMatchRuleTest
import net.minecraft.util.registry.Registry.CONFIGURED_FEATURE_WORLDGEN
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.OreFeatureConfig
import java.util.function.Supplier

object WorldGenFeatureRegistry {
    private val REGISTRY = Endless.registry(CONFIGURED_FEATURE_WORLDGEN)

    private val RULE_END_STONE = BlockMatchRuleTest(Blocks.END_STONE)

    private fun register(path: String, supplier: Supplier<ConfiguredFeature<*, *>>): RegistrySupplier<ConfiguredFeature<*, *>> {
        return REGISTRY.register(path, supplier)
    }

    private val COBALT_ORE = register("cobalt_ore") {
        TODO("FIGURE OUT WHY THIS CAUSES AN NPE?!")
        println("heyo registering cobalt ore, BlockRegistry.COBALT_ORE = ${BlockRegistry.COBALT_ORE}, present = ${BlockRegistry.COBALT_ORE.isPresent}")
        println("RULE_END_STONE = $RULE_END_STONE")
        println("Cobalt Ore default state = ${BlockRegistry.COBALT_ORE.get().defaultState}")
        val a = Feature.ORE.configure(OreFeatureConfig(RULE_END_STONE, BlockRegistry.COBALT_ORE.get().defaultState, 9))
            .spreadHorizontally().repeat(10)
        println("our ore feature is $a!")
        return@register a
    }
    private val THERAKIUM_ORE = register("therakium_ore") {
        println("heyo registering therakium ore")
        return@register Feature.ORE.configure(OreFeatureConfig(RULE_END_STONE, BlockRegistry.THERAKIUM_ORE.get().defaultState, 8))
            .spreadHorizontally()
    }

    fun register() {
        /* clinit */
        REGISTRY.register()

        BiomeModifications.addProperties({ bCtx -> bCtx.properties.category == Biome.Category.THEEND },
            { _, bProps ->
                run {
                    bProps.generationProperties.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, COBALT_ORE.get())
                    bProps.generationProperties.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, THERAKIUM_ORE.get())
                }
            })
    }
}