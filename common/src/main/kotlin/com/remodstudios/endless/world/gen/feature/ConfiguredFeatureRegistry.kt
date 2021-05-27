package com.remodstudios.endless.world.gen.feature

import com.mojang.serialization.Lifecycle
import com.remodstudios.endless.Endless.id
import com.remodstudios.endless.blocks.BlockRegistry
import me.shedaniel.architectury.event.events.LifecycleEvent
import me.shedaniel.architectury.registry.BiomeModifications
import net.minecraft.block.Blocks
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.MutableRegistry
import net.minecraft.util.registry.Registry.CONFIGURED_FEATURE_WORLDGEN
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.UniformIntDistribution
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.DiskFeatureConfig
import net.minecraft.world.gen.feature.Feature
import java.util.Collections.singletonList
import java.util.function.Supplier

object ConfiguredFeatureRegistry {
    private val END_STONE_LIST = singletonList(Blocks.END_STONE.defaultState)
    private val HEIGHTMAP_SURFACE_SQUARE = ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE.spreadHorizontally()

    data class Registration(val key: RegistryKey<ConfiguredFeature<*, *>>, val feature: ConfiguredFeature<*, *>)
    private val REGISTRATION_LIST = ArrayList<Registration>()

    private fun add(path: String, featureSupplier: Supplier<ConfiguredFeature<*, *>>): ConfiguredFeature<*, *> {
        val feature = featureSupplier.get()
        REGISTRATION_LIST.add(Registration(RegistryKey.of(CONFIGURED_FEATURE_WORLDGEN, id(path)), feature))
        return feature
    }

    private val COBALT_ORE = add("cobalt_ore")
    {
        Feature.DISK.configure(
            DiskFeatureConfig(
                BlockRegistry.COBALT_ORE.get().defaultState,
                UniformIntDistribution.of(5, 2), 3, END_STONE_LIST
            )
        )
            .decorate(HEIGHTMAP_SURFACE_SQUARE)
    }
    private val THERAKIUM_ORE = add("therakium_ore") {
        Feature.DISK.configure(
            DiskFeatureConfig(
                BlockRegistry.THERAKIUM_ORE.get().defaultState,
                UniformIntDistribution.of(3, 2), 2, END_STONE_LIST
            )
        )
            .decorate(HEIGHTMAP_SURFACE_SQUARE)
    }
    private val LUNARIC_END_STONE = add("lunaric_end_stone") {
        Feature.DISK.configure(
            DiskFeatureConfig(
                BlockRegistry.LUNARIC_END_STONE.get().defaultState,
                UniformIntDistribution.of(4, 3), 1, END_STONE_LIST
            )
        )
            .decorate(HEIGHTMAP_SURFACE_SQUARE)
    }

    private fun register(registry: MutableRegistry<ConfiguredFeature<*, *>>) {
        REGISTRATION_LIST.forEach {
            if (!registry.containsId(it.key.value))
                registry.add(it.key, it.feature, Lifecycle.stable())
        }
    }

    private fun registerBuiltin() {
        register(BuiltinRegistries.CONFIGURED_FEATURE as MutableRegistry<ConfiguredFeature<*, *>>)
    }

    private fun registerDynamic(manager: DynamicRegistryManager) {
        register(manager.get(CONFIGURED_FEATURE_WORLDGEN))
    }

    fun register() {
        /* clinit */
        registerBuiltin()

        LifecycleEvent.SERVER_STARTING.register(LifecycleEvent.ServerState { server ->
            with (server) {
                println("yo. registering into $this's DynamicRegistryManager")
                registerDynamic(registryManager)
            }
        })

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