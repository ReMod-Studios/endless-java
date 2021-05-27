package com.remodstudios.endless.world.biome

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.sound.BiomeAdditionsSound
import net.minecraft.sound.BiomeMoodSound
import net.minecraft.sound.MusicSound
import net.minecraft.sound.SoundEvent
import net.minecraft.world.biome.*
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.carver.ConfiguredCarver
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.ConfiguredStructureFeature
import net.minecraft.world.gen.feature.DefaultBiomeFeatures
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder
import java.util.function.Consumer

fun biomeTemplate(init: BiomeTemplate.() -> Unit): BiomeTemplate {
    val template = BiomeTemplate()
    template.init()
    return template
}

fun biomeTemplate(original: BiomeTemplate, init: BiomeTemplate.() -> Unit): BiomeTemplate {
    val template = original.copy()
    template.init()
    return template
}

fun biome(template: BiomeTemplate): Biome {
    val builder = Biome.Builder()
    // general
    builder.category(template.category!!)
    builder.depth(template.depth!!)
    builder.scale(template.scale!!)
    builder.temperature(template.temperature!!)
    builder.temperatureModifier(template.temperatureModifier)
    builder.downfall(template.downfall!!)
    // effects
    val effectsBuilder = BiomeEffects.Builder()
    effectsBuilder.fogColor(template.effects!!.fogColor!!)
    effectsBuilder.waterColor(template.effects!!.waterColor!!)
    effectsBuilder.waterFogColor(template.effects!!.waterFogColor!!)
    effectsBuilder.skyColor(template.effects!!.skyColor!!)
    if (template.effects!!.foliageColor != null)
        effectsBuilder.foliageColor(template.effects!!.foliageColor!!)
    if (template.effects!!.grassColor != null)
        effectsBuilder.grassColor(template.effects!!.grassColor!!)
    if (template.effects!!.grassColorModifier != null)
        effectsBuilder.grassColorModifier(template.effects!!.grassColorModifier!!)
    if (template.effects!!.particleConfig != null)
        effectsBuilder.particleConfig(template.effects!!.particleConfig!!)
    if (template.effects!!.loopSound != null)
        effectsBuilder.loopSound(template.effects!!.loopSound!!)
    if (template.effects!!.moodSound != null)
        effectsBuilder.moodSound(template.effects!!.moodSound!!)
    if (template.effects!!.additionsSound != null)
        effectsBuilder.additionsSound(template.effects!!.additionsSound!!)
    if (template.effects!!.music != null)
        effectsBuilder.music(template.effects!!.music!!)
    builder.effects(effectsBuilder.build())
    // spawn
    val spawnBuilder = SpawnSettings.Builder()
    template.spawnSettings!!.spawners.forEach { entry ->
        entry.value.forEach {
            spawnBuilder.spawn(entry.key, it)
        }
    }
    template.spawnSettings!!.spawnCosts.forEach {
        spawnBuilder.spawnCost(it.key, it.value.mass, it.value.gravityLimit)
    }
    spawnBuilder.creatureSpawnProbability(template.spawnSettings!!.creatureSpawnProbability)
    if (template.spawnSettings!!.playerSpawnFriendly)
        spawnBuilder.playerSpawnFriendly()
    template.spawnSettings!!.vanilla.builderModifiers.forEach {
        it.accept(spawnBuilder)
    }
    template.spawnSettings!!.vanilla.apply(spawnBuilder)
    builder.spawnSettings(spawnBuilder.build())
    // generation
    val genBuilder = GenerationSettings.Builder()
    genBuilder.surfaceBuilder(template.generationSettings!!.surfaceBuilder!!)
    template.generationSettings!!.features.forEach { entry ->
        entry.value.forEach {
            genBuilder.feature(entry.key, it)
        }
    }
    template.generationSettings!!.carvers.forEach { entry ->
        entry.value.forEach {
            genBuilder.carver(entry.key, it)
        }
    }
    template.generationSettings!!.structureFeatures.forEach(genBuilder::structureFeature)
    template.generationSettings!!.vanilla.apply(genBuilder)
    builder.generationSettings(genBuilder.build())
    return builder.build()
}

fun biome(init: BiomeTemplate.() -> Unit): Biome {
    return biome(biomeTemplate(init))
}

fun biome(base: BiomeTemplate, init: BiomeTemplate.() -> Unit) : Biome {
    return biome(biomeTemplate(base, init))
}

class BiomeTemplate {
    var category: Biome.Category? = null
    var precipitation: Biome.Precipitation? = null
    var depth: Float? = null
    var scale: Float? = null
    var temperature: Float? = null
    var temperatureModifier: Biome.TemperatureModifier = Biome.TemperatureModifier.NONE
    var downfall: Float? = null
    var effects: SpecialEffects? = null
    var generationSettings: GenerationSettings? = null
    var spawnSettings: SpawnSettings? = null

    class SpecialEffects {
        var fogColor: Int? = null
        var waterColor: Int? = null
        var waterFogColor: Int? = null
        var skyColor: Int? = null
        var foliageColor: Int? = null
        var grassColor: Int? = null
        var grassColorModifier: BiomeEffects.GrassColorModifier? = null
        var particleConfig: BiomeParticleConfig? = null
        var loopSound: SoundEvent? = null
        var moodSound: BiomeMoodSound? = null
        var additionsSound: BiomeAdditionsSound? = null
        var music: MusicSound? = null
        
        fun copy(): SpecialEffects {
            val copy = SpecialEffects()
            copy.fogColor = fogColor
            copy.waterColor = waterColor
            copy.waterFogColor = waterFogColor
            copy.skyColor = skyColor
            copy.foliageColor = foliageColor
            copy.grassColor = grassColor
            copy.grassColorModifier = grassColorModifier
            copy.particleConfig = particleConfig
            copy.loopSound = loopSound
            copy.moodSound = moodSound
            copy.additionsSound = additionsSound
            copy.music = music
            return copy
        }
    }

    class SpawnSettings {
        val vanilla = VanillaSpawns()

        val spawners: Map<SpawnGroup, MutableList<net.minecraft.world.biome.SpawnSettings.SpawnEntry>>
        val spawnCosts: MutableMap<EntityType<*>, Density> = LinkedHashMap()
        var creatureSpawnProbability = 0.1f
        var playerSpawnFriendly = false

        init {
            val a = LinkedHashMap<SpawnGroup, MutableList<net.minecraft.world.biome.SpawnSettings.SpawnEntry>>()
            for (group in SpawnGroup.values())
                a[group] = ArrayList()
            spawners = a.toMap()
        }

        fun spawns(group: SpawnGroup, init: Spawns.() -> Unit): Spawns {
            val spawns = Spawns(this, group)
            spawns.init()
            return spawns
        }

        class Spawns(private var settings: SpawnSettings, private var group: SpawnGroup) {
            fun entry(entityType: EntityType<*>, weight: Int, minGroupSize: Int, maxGroupSize: Int): net.minecraft.world.biome.SpawnSettings.SpawnEntry {
                return net.minecraft.world.biome.SpawnSettings.SpawnEntry(
                    entityType,
                    weight,
                    minGroupSize,
                    maxGroupSize
                )
            }

            operator fun net.minecraft.world.biome.SpawnSettings.SpawnEntry.unaryPlus() {
                settings.spawners[group]?.add(this)
            }
        }

        fun density(entityType: EntityType<*>, mass: Double, gravityLimit: Double) {
            spawnCosts[entityType] = Density(mass, gravityLimit)
        }

        data class Density(val mass: Double, val gravityLimit: Double)

        class VanillaSpawns {
            // TODO the rest of the methods in DefaultBiomeFeatures
            
            val builderModifiers: MutableList<Consumer<net.minecraft.world.biome.SpawnSettings.Builder>> = ArrayList()

            fun apply(builder: net.minecraft.world.biome.SpawnSettings.Builder) {
                builderModifiers.forEach { it.accept(builder) }
            }

            fun defaultEnd() {
                builderModifiers.add(DefaultBiomeFeatures::addEndMobs)
            }
        }

        fun copy(): SpawnSettings {
            val copy = SpawnSettings()
            copy.vanilla.builderModifiers += vanilla.builderModifiers
            spawners.forEach {
                copy.spawners[it.key]?.addAll(it.value)
            }
            spawnCosts.forEach {
                copy.spawnCosts[it.key] = it.value.copy()
            }
            copy.creatureSpawnProbability = creatureSpawnProbability
            copy.playerSpawnFriendly = playerSpawnFriendly
            return copy
        }
    }

    class GenerationSettings {
        val vanilla = VanillaGeneration()

        var surfaceBuilder: ConfiguredSurfaceBuilder<*>? = null
        val carvers: MutableMap<GenerationStep.Carver, MutableList<ConfiguredCarver<*>>> = LinkedHashMap()
        val features: MutableMap<GenerationStep.Feature, MutableList<ConfiguredFeature<*, *>>> = LinkedHashMap()
        val structureFeatures: MutableList<ConfiguredStructureFeature<*, *>> = ArrayList()

        fun carvers(step: GenerationStep.Carver, init: Carvers.() -> Unit): Carvers {
            val carvers = Carvers(this, step)
            carvers.init()
            return carvers
        }

        fun features(step: GenerationStep.Feature, init: Features.() -> Unit): Features {
            val features = Features(this, step)
            features.init()
            return features
        }

        operator fun ConfiguredStructureFeature<*, *>.unaryPlus() {
            structureFeatures.add(this)
        }

        class Carvers(private var settings: GenerationSettings, private var step: GenerationStep.Carver) {
            init {
                settings.carvers.putIfAbsent(step, ArrayList())
            }

            operator fun ConfiguredCarver<*>.unaryPlus() {
                settings.carvers[step]?.add(this)
            }
        }

        class Features(private var settings: GenerationSettings, private var step: GenerationStep.Feature) {
            init {
                settings.features.putIfAbsent(step, ArrayList())
            }

            operator fun ConfiguredFeature<*, *>.unaryPlus() {
                settings.features[step]?.add(this)
            }
        }

        class VanillaGeneration {
            // TODO the rest of the methods in DefaultBiomeFeatures

            val builderModifiers: MutableList<Consumer<net.minecraft.world.biome.GenerationSettings.Builder>> = ArrayList()

            fun apply(builder: net.minecraft.world.biome.GenerationSettings.Builder) {
                builderModifiers.forEach { it.accept(builder) }
            }
        }

        fun copy(): GenerationSettings {
            val copy = GenerationSettings()
            copy.vanilla.builderModifiers += vanilla.builderModifiers
            copy.surfaceBuilder = surfaceBuilder
            carvers.forEach {
                copy.carvers[it.key] = it.value.toMutableList()
            }
            features.forEach {
                copy.features[it.key] = it.value.toMutableList()
            }
            copy.structureFeatures += structureFeatures
            return copy
        }
    }

    fun effects(init: SpecialEffects.() -> Unit): SpecialEffects {
        val effects = SpecialEffects()
        effects.init()
        this.effects = effects
        return effects
    }

    fun spawnSettings(init: SpawnSettings.() -> Unit): SpawnSettings {
        val spawnSettings = SpawnSettings()
        spawnSettings.init()
        this.spawnSettings = spawnSettings
        return spawnSettings
    }

    fun generationSettings(init: GenerationSettings.() -> Unit): GenerationSettings {
        val generationSettings = GenerationSettings()
        generationSettings.init()
        this.generationSettings = generationSettings
        return generationSettings
    }

    fun copy(): BiomeTemplate {
        val copy = BiomeTemplate()
        copy.category = category
        copy.precipitation = precipitation
        copy.depth = depth
        copy.scale = scale
        copy.temperature = temperature
        copy.temperatureModifier = temperatureModifier
        copy.downfall = downfall
        copy.effects = effects?.copy()
        copy.generationSettings = generationSettings?.copy()
        copy.spawnSettings = spawnSettings?.copy()
        return copy
    }
}