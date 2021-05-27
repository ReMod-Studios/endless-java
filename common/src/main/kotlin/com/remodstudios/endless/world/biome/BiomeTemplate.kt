package com.remodstudios.endless.world.biome

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.particle.ParticleEffect
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

@DslMarker
private annotation class BiomeTemplateDslMarker

class BiomeTemplate internal constructor() {
    var category: Biome.Category? = null
    var precipitation: Biome.Precipitation? = null
    var depth: Float? = null
    var scale: Float? = null
    var temperature: Float? = null
    var temperatureModifier: Biome.TemperatureModifier = Biome.TemperatureModifier.NONE
    var downfall: Float? = null
    var effects: SpecialEffects? = null
        private set
    var generationSettings: GenerationSettings? = null
        private set
    var spawnSettings: SpawnSettings? = null
        private set

    @BiomeTemplateDslMarker
    class SpecialEffects internal constructor() {
        var fogColor: Int? = null
        var waterColor: Int? = null
        var waterFogColor: Int? = null
        var skyColor: Int? = null
        var foliageColor: Int? = null
        var grassColor: Int? = null
        var grassColorModifier: BiomeEffects.GrassColorModifier? = null
        var particleConfig: BiomeParticleConfig? = null
            private set
        var loopSound: SoundEvent? = null
        var moodSound: BiomeMoodSound? = null
        var additionsSound: BiomeAdditionsSound? = null
            private set
        var music: MusicSound? = null

        fun particleConfig(particle: ParticleEffect, probability: Float) {
            particleConfig = BiomeParticleConfig(particle, probability)
        }

        fun additionsSound(sound: SoundEvent, chance: Double) {
            additionsSound = BiomeAdditionsSound(sound, chance)
        }

        internal fun copy(): SpecialEffects {
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

    @BiomeTemplateDslMarker
    class SpawnSettings internal constructor() {
        val vanilla = VanillaSpawns()

        internal val spawners: Map<SpawnGroup, MutableList<net.minecraft.world.biome.SpawnSettings.SpawnEntry>>
        internal val spawnCosts: MutableMap<EntityType<*>, Density> = LinkedHashMap()
        var creatureSpawnProbability = 0.1f
        var playerSpawnFriendly = false

        init {
            val a = LinkedHashMap<SpawnGroup, MutableList<net.minecraft.world.biome.SpawnSettings.SpawnEntry>>()
            for (group in SpawnGroup.values())
                a[group] = ArrayList()
            spawners = a.toMap()
        }

        fun forGroup(group: SpawnGroup, init: Spawns.() -> Unit): Spawns {
            val spawns = Spawns(this, group)
            spawns.init()
            return spawns
        }

        @BiomeTemplateDslMarker
        class Spawns internal constructor(private var settings: SpawnSettings, private var group: SpawnGroup) {
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

        fun addCost(entityType: EntityType<*>, mass: Double, gravityLimit: Double) {
            spawnCosts[entityType] = Density(mass, gravityLimit)
        }

        internal data class Density(val mass: Double, val gravityLimit: Double)

        class VanillaSpawns {
            internal val builderModifiers: MutableList<Consumer<net.minecraft.world.biome.SpawnSettings.Builder>> = ArrayList()

            internal fun apply(builder: net.minecraft.world.biome.SpawnSettings.Builder) {
                builderModifiers.forEach { it.accept(builder) }
            }

            fun farmAnimals() {
                builderModifiers.add(DefaultBiomeFeatures::addFarmAnimals)
            }

            fun bats() {
                builderModifiers.add(DefaultBiomeFeatures::addBats)
            }

            fun batsAndMonsters() {
                builderModifiers.add(DefaultBiomeFeatures::addBatsAndMonsters)
            }
            
            fun oceanMobs(squidWeight: Int, squidMaxGroupSize: Int, codWeight: Int) {
                builderModifiers.add { DefaultBiomeFeatures.addOceanMobs(it, squidWeight, squidMaxGroupSize, codWeight) }
            }
            
            fun warmOceanMobs(squidWeight: Int, squidMaxGroupSize: Int) {
                builderModifiers.add { DefaultBiomeFeatures.addWarmOceanMobs(it, squidWeight, squidMaxGroupSize) }
            }

            fun plainsMobs() {
                builderModifiers.add(DefaultBiomeFeatures::addPlainsMobs)
            }

            fun snowyMobs() {
                builderModifiers.add(DefaultBiomeFeatures::addSnowyMobs)
            }

            fun desertMobs() {
                builderModifiers.add(DefaultBiomeFeatures::addDesertMobs)
            }
            
            fun monsters(zombieWeight: Int, zombieVillagerWeight: Int, skeletonWeight: Int) {
                builderModifiers.add { DefaultBiomeFeatures.addMonsters(it, zombieWeight, zombieVillagerWeight, skeletonWeight) }
            }

            fun mushroomMobs() {
                builderModifiers.add(DefaultBiomeFeatures::addMushroomMobs)
            }

            fun jungleMobs() {
                builderModifiers.add(DefaultBiomeFeatures::addJungleMobs)
            }

            fun endMobs() {
                builderModifiers.add(DefaultBiomeFeatures::addEndMobs)
            }
        }

        internal fun copy(): SpawnSettings {
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

    @BiomeTemplateDslMarker
    class GenerationSettings internal constructor() {
        val vanilla = VanillaGeneration()

        var surfaceBuilder: ConfiguredSurfaceBuilder<*>? = null
        internal val carvers: MutableMap<GenerationStep.Carver, MutableList<ConfiguredCarver<*>>> = LinkedHashMap()
        internal val features: MutableMap<GenerationStep.Feature, MutableList<ConfiguredFeature<*, *>>> = LinkedHashMap()
        internal val structureFeatures: MutableList<ConfiguredStructureFeature<*, *>> = ArrayList()

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

        @BiomeTemplateDslMarker
        class Carvers internal constructor(private var settings: GenerationSettings, private var step: GenerationStep.Carver) {
            init {
                settings.carvers.putIfAbsent(step, ArrayList())
            }

            operator fun ConfiguredCarver<*>.unaryPlus() {
                settings.carvers[step]?.add(this)
            }
        }

        @BiomeTemplateDslMarker
        class Features internal constructor(private var settings: GenerationSettings, private var step: GenerationStep.Feature) {
            init {
                settings.features.putIfAbsent(step, ArrayList())
            }

            operator fun ConfiguredFeature<*, *>.unaryPlus() {
                settings.features[step]?.add(this)
            }
        }

        class VanillaGeneration internal constructor() {
            internal val builderModifiers: MutableList<Consumer<net.minecraft.world.biome.GenerationSettings.Builder>> = ArrayList()

            internal fun apply(builder: net.minecraft.world.biome.GenerationSettings.Builder) {
                builderModifiers.forEach { it.accept(builder) }
            }

            fun badlandsUndergroundStructures() {
                builderModifiers.add(DefaultBiomeFeatures::addBadlandsUndergroundStructures)
            }

            fun defaultUndergroundStructures() {
                builderModifiers.add(DefaultBiomeFeatures::addDefaultUndergroundStructures)
            }

            fun oceanStructures() {
                builderModifiers.add(DefaultBiomeFeatures::addOceanStructures)
            }

            fun landCarvers() {
                builderModifiers.add(DefaultBiomeFeatures::addLandCarvers)
            }

            fun oceanCarvers() {
                builderModifiers.add(DefaultBiomeFeatures::addOceanCarvers)
            }

            fun defaultLakes() {
                builderModifiers.add(DefaultBiomeFeatures::addDefaultLakes)
            }

            fun desertLakes() {
                builderModifiers.add(DefaultBiomeFeatures::addDesertLakes)
            }

            fun dungeons() {
                builderModifiers.add(DefaultBiomeFeatures::addDungeons)
            }

            fun mineables() {
                builderModifiers.add(DefaultBiomeFeatures::addMineables)
            }

            fun defaultOres() {
                builderModifiers.add(DefaultBiomeFeatures::addDefaultOres)
            }

            fun extraGoldOre() {
                builderModifiers.add(DefaultBiomeFeatures::addExtraGoldOre)
            }

            fun emeraldOre() {
                builderModifiers.add(DefaultBiomeFeatures::addEmeraldOre)
            }

            fun infestedStone() {
                builderModifiers.add(DefaultBiomeFeatures::addInfestedStone)
            }

            fun defaultDisks() {
                builderModifiers.add(DefaultBiomeFeatures::addDefaultDisks)
            }

            fun clay() {
                builderModifiers.add(DefaultBiomeFeatures::addClay)
            }

            fun mossyRocks() {
                builderModifiers.add(DefaultBiomeFeatures::addMossyRocks)
            }

            fun largeFerns() {
                builderModifiers.add(DefaultBiomeFeatures::addLargeFerns)
            }

            fun sweetBerryBushesSnowy() {
                builderModifiers.add(DefaultBiomeFeatures::addSweetBerryBushesSnowy)
            }

            fun sweetBerryBushes() {
                builderModifiers.add(DefaultBiomeFeatures::addSweetBerryBushes)
            }

            fun bamboo() {
                builderModifiers.add(DefaultBiomeFeatures::addBamboo)
            }

            fun bambooJungleTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addBambooJungleTrees)
            }

            fun taigaTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addTaigaTrees)
            }

            fun waterBiomeOakTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addWaterBiomeOakTrees)
            }

            fun birchTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addBirchTrees)
            }

            fun forestTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addForestTrees)
            }

            fun tallBirchTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addTallBirchTrees)
            }

            fun savannaTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addSavannaTrees)
            }

            fun extraSavannaTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addExtraSavannaTrees)
            }

            fun mountainTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addMountainTrees)
            }

            fun extraMountainTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addExtraMountainTrees)
            }

            fun jungleTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addJungleTrees)
            }

            fun jungleEdgeTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addJungleEdgeTrees)
            }

            fun badlandsPlateauTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addBadlandsPlateauTrees)
            }

            fun snowySpruceTrees() {
                builderModifiers.add(DefaultBiomeFeatures::addSnowySpruceTrees)
            }

            fun jungleGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addJungleGrass)
            }

            fun savannaTallGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addSavannaTallGrass)
            }

            fun shatteredSavannaGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addShatteredSavannaGrass)
            }

            fun savannaGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addSavannaGrass)
            }

            fun badlandsGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addBadlandsGrass)
            }

            fun forestFlowers() {
                builderModifiers.add(DefaultBiomeFeatures::addForestFlowers)
            }

            fun forestGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addForestGrass)
            }

            fun swampFeatures() {
                builderModifiers.add(DefaultBiomeFeatures::addSwampFeatures)
            }

            fun mushroomFieldsFeatures() {
                builderModifiers.add(DefaultBiomeFeatures::addMushroomFieldsFeatures)
            }

            fun plainsFeatures() {
                builderModifiers.add(DefaultBiomeFeatures::addPlainsFeatures)
            }

            fun desertDeadBushes() {
                builderModifiers.add(DefaultBiomeFeatures::addDesertDeadBushes)
            }

            fun giantTaigaGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addGiantTaigaGrass)
            }

            fun defaultFlowers() {
                builderModifiers.add(DefaultBiomeFeatures::addDefaultFlowers)
            }

            fun extraDefaultFlowers() {
                builderModifiers.add(DefaultBiomeFeatures::addExtraDefaultFlowers)
            }

            fun defaultGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addDefaultGrass)
            }

            fun taigaGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addTaigaGrass)
            }

            fun plainsTallGrass() {
                builderModifiers.add(DefaultBiomeFeatures::addPlainsTallGrass)
            }

            fun defaultMushrooms() {
                builderModifiers.add(DefaultBiomeFeatures::addDefaultMushrooms)
            }

            fun defaultVegetation() {
                builderModifiers.add(DefaultBiomeFeatures::addDefaultVegetation)
            }

            fun badlandsVegetation() {
                builderModifiers.add(DefaultBiomeFeatures::addBadlandsVegetation)
            }

            fun jungleVegetation() {
                builderModifiers.add(DefaultBiomeFeatures::addJungleVegetation)
            }

            fun desertVegetation() {
                builderModifiers.add(DefaultBiomeFeatures::addDesertVegetation)
            }

            fun swampVegetation() {
                builderModifiers.add(DefaultBiomeFeatures::addSwampVegetation)
            }

            fun desertFeatures() {
                builderModifiers.add(DefaultBiomeFeatures::addDesertFeatures)
            }

            fun fossils() {
                builderModifiers.add(DefaultBiomeFeatures::addFossils)
            }

            fun kelp() {
                builderModifiers.add(DefaultBiomeFeatures::addKelp)
            }

            fun seagrassOnStone() {
                builderModifiers.add(DefaultBiomeFeatures::addSeagrassOnStone)
            }

            fun lessKelp() {
                builderModifiers.add(DefaultBiomeFeatures::addLessKelp)
            }

            fun springs() {
                builderModifiers.add(DefaultBiomeFeatures::addSprings)
            }

            fun icebergs() {
                builderModifiers.add(DefaultBiomeFeatures::addIcebergs)
            }

            fun blueIce() {
                builderModifiers.add(DefaultBiomeFeatures::addBlueIce)
            }

            fun frozenTopLayer() {
                builderModifiers.add(DefaultBiomeFeatures::addFrozenTopLayer)
            }

            fun netherMineables() {
                builderModifiers.add(DefaultBiomeFeatures::addNetherMineables)
            }

            fun ancientDebris() {
                builderModifiers.add(DefaultBiomeFeatures::addAncientDebris)
            }
        }

        internal fun copy(): GenerationSettings {
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
        val effects = this.effects ?: SpecialEffects()
        effects.init()
        this.effects = effects
        return effects
    }

    fun spawnSettings(init: SpawnSettings.() -> Unit): SpawnSettings {
        val spawnSettings = this.spawnSettings ?: SpawnSettings()
        spawnSettings.init()
        this.spawnSettings = spawnSettings
        return spawnSettings
    }

    fun generationSettings(init: GenerationSettings.() -> Unit): GenerationSettings {
        val generationSettings = this.generationSettings ?: GenerationSettings()
        generationSettings.init()
        this.generationSettings = generationSettings
        return generationSettings
    }

    internal fun copy(): BiomeTemplate {
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