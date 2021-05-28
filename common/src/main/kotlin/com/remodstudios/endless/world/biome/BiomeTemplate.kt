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
import net.minecraft.world.biome.GenerationSettings as MCGenerationSettings
import net.minecraft.world.biome.SpawnSettings as MCSpawnSettings

@DslMarker
private annotation class BiomeTemplateDslMarker

@BiomeTemplateDslMarker
class BiomeTemplate internal constructor() {
    var category: Biome.Category? = null
    var precipitation: Biome.Precipitation? = null
    var depth: Float? = null
    var scale: Float? = null
    var temperature: Float? = null
    var temperatureModifier: Biome.TemperatureModifier = Biome.TemperatureModifier.NONE
    var downfall: Float? = null
    private var effects: SpecialEffects? = null
    private var generationSettings: GenerationSettings? = null
    private var spawnSettings: SpawnSettings? = null

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
        var loopSound: SoundEvent? = null
        var moodSound: BiomeMoodSound? = null
        var additionsSound: BiomeAdditionsSound? = null
        var music: MusicSound? = null

        fun particleConfig(particle: ParticleEffect, probability: Float) {
            particleConfig = BiomeParticleConfig(particle, probability)
        }

        fun moodSound(sound: SoundEvent, cultivationTicks: Int, spawnRange: Int, extraDistance: Double) {
            moodSound = BiomeMoodSound(sound, cultivationTicks, spawnRange, extraDistance)
        }

        fun additionsSound(sound: SoundEvent, chance: Double) {
            additionsSound = BiomeAdditionsSound(sound, chance)
        }

        fun music(sound: SoundEvent, minDelay: Int, maxDelay: Int, replaceCurrentMusic: Boolean) {
            music = MusicSound(sound, minDelay, maxDelay, replaceCurrentMusic)
        }

        // region Internal functions
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
        
        internal fun build(): BiomeEffects {
            val effectsBuilder = BiomeEffects.Builder()
            effectsBuilder.fogColor(fogColor!!)
            effectsBuilder.waterColor(waterColor!!)
            effectsBuilder.waterFogColor(waterFogColor!!)
            effectsBuilder.skyColor(skyColor!!)
            if (foliageColor != null)
                effectsBuilder.foliageColor(foliageColor!!)
            if (grassColor != null)
                effectsBuilder.grassColor(grassColor!!)
            if (grassColorModifier != null)
                effectsBuilder.grassColorModifier(grassColorModifier!!)
            if (particleConfig != null)
                effectsBuilder.particleConfig(particleConfig!!)
            if (loopSound != null)
                effectsBuilder.loopSound(loopSound!!)
            if (moodSound != null)
                effectsBuilder.moodSound(moodSound!!)
            if (additionsSound != null)
                effectsBuilder.additionsSound(additionsSound!!)
            if (music != null)
                effectsBuilder.music(music!!)
            return effectsBuilder.build()
        }
        // endregion
    }

    @BiomeTemplateDslMarker
    class SpawnSettings internal constructor() {
        private data class Density(val mass: Double, val gravityLimit: Double)

        val vanilla = VanillaSpawns()

        private val spawners: Map<SpawnGroup, MutableList<MCSpawnSettings.SpawnEntry>>
        private val spawnCosts: MutableMap<EntityType<*>, Density> = LinkedHashMap()
        var creatureSpawnProbability = 0.1f
        var playerSpawnFriendly = false

        init {
            val a = LinkedHashMap<SpawnGroup, MutableList<MCSpawnSettings.SpawnEntry>>()
            for (group in SpawnGroup.values())
                a[group] = ArrayList()
            spawners = a.toMap()
        }

        fun forGroup(group: SpawnGroup, init: Spawns.() -> Unit): Spawns {
            val spawns = Spawns(group)
            spawns.init()
            return spawns
        }

        @BiomeTemplateDslMarker
        inner class Spawns internal constructor(private var group: SpawnGroup) {
            fun entry(entityType: EntityType<*>, weight: Int, minGroupSize: Int, maxGroupSize: Int): MCSpawnSettings.SpawnEntry {
                return MCSpawnSettings.SpawnEntry(
                    entityType,
                    weight,
                    minGroupSize,
                    maxGroupSize
                )
            }

            operator fun MCSpawnSettings.SpawnEntry.unaryPlus() {
                this@SpawnSettings.spawners[group]?.add(this)
            }
        }

        fun addCost(entityType: EntityType<*>, mass: Double, gravityLimit: Double) {
            spawnCosts[entityType] = Density(mass, gravityLimit)
        }

        inner class VanillaSpawns internal constructor() {
            internal val builderModifiers: MutableList<Consumer<MCSpawnSettings.Builder>> = ArrayList()

            internal fun apply(builder: MCSpawnSettings.Builder) {
                for (it in builderModifiers)
                    it.accept(builder)
            }

            // region Vanilla spawn additions
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
            // endregion
        }

        // region Internal functions
        internal fun copy(): SpawnSettings {
            val copy = SpawnSettings()
            copy.vanilla.builderModifiers += vanilla.builderModifiers
            for ((key, value) in spawners) {
                copy.spawners[key]?.addAll(value)
            }
            for ((key, value) in spawnCosts) {
                copy.spawnCosts[key] = value.copy()
            }
            copy.creatureSpawnProbability = creatureSpawnProbability
            copy.playerSpawnFriendly = playerSpawnFriendly
            return copy
        }
        
        internal fun build(): MCSpawnSettings {
            val spawnBuilder = MCSpawnSettings.Builder()
            for ((key, value) in spawners) {
                for (it in value)
                    spawnBuilder.spawn(key, it)
            }
            for ((key, value) in spawnCosts)
                spawnBuilder.spawnCost(key, value.mass, value.gravityLimit)
            spawnBuilder.creatureSpawnProbability(creatureSpawnProbability)
            if (playerSpawnFriendly)
                spawnBuilder.playerSpawnFriendly()
            vanilla.apply(spawnBuilder)
            return spawnBuilder.build()
        }
        // endregion
    }

    @BiomeTemplateDslMarker
    class GenerationSettings internal constructor() {
        val vanilla = VanillaGeneration()

        var surfaceBuilder: ConfiguredSurfaceBuilder<*>? = null
        private val carvers: MutableMap<GenerationStep.Carver, MutableList<ConfiguredCarver<*>>> = LinkedHashMap()
        private val features: MutableMap<GenerationStep.Feature, MutableList<ConfiguredFeature<*, *>>> = LinkedHashMap()
        private val structureFeatures: MutableList<ConfiguredStructureFeature<*, *>> = ArrayList()

        fun carvers(step: GenerationStep.Carver, init: Carvers.() -> Unit): Carvers {
            val carvers = Carvers(step)
            carvers.init()
            return carvers
        }

        fun features(step: GenerationStep.Feature, init: Features.() -> Unit): Features {
            val features = Features(step)
            features.init()
            return features
        }

        operator fun ConfiguredStructureFeature<*, *>.unaryPlus() {
            structureFeatures.add(this)
        }

        @BiomeTemplateDslMarker
        inner class Carvers internal constructor(private var step: GenerationStep.Carver) {
            init {
                this@GenerationSettings.carvers.putIfAbsent(step, ArrayList())
            }

            operator fun ConfiguredCarver<*>.unaryPlus() {
                this@GenerationSettings.carvers[step]?.add(this)
            }
        }

        @BiomeTemplateDslMarker
        inner class Features internal constructor(private var step: GenerationStep.Feature) {
            init {
                this@GenerationSettings.features.putIfAbsent(step, ArrayList())
            }

            operator fun ConfiguredFeature<*, *>.unaryPlus() {
                this@GenerationSettings.features[step]?.add(this)
            }
        }

        inner class VanillaGeneration internal constructor() {
            internal val builderModifiers: MutableList<Consumer<MCGenerationSettings.Builder>> = ArrayList()

            internal fun apply(builder: MCGenerationSettings.Builder) {
                for (it in builderModifiers)
                    it.accept(builder)
            }

            // region Vanilla generation additions
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
            // endregion
        }

        // region Internal functions
        internal fun copy(): GenerationSettings {
            val copy = GenerationSettings()
            copy.vanilla.builderModifiers += vanilla.builderModifiers
            copy.surfaceBuilder = surfaceBuilder
            for ((key, value) in carvers)
                copy.carvers[key] = value.toMutableList()
            for ((key, value) in features)
                copy.features[key] = value.toMutableList()
            copy.structureFeatures += structureFeatures
            return copy
        }
        
        internal fun build(): MCGenerationSettings {
            val genBuilder = MCGenerationSettings.Builder()
            genBuilder.surfaceBuilder(surfaceBuilder!!)
            for ((key, value) in features) {
                for (it in value)
                    genBuilder.feature(key, it)
            }
            for ((key, value)  in carvers) {
                for (it in value)
                    genBuilder.carver(key, it)
            }
            for (it in structureFeatures)
                genBuilder.structureFeature(it)
            vanilla.apply(genBuilder)
            return genBuilder.build()
        }
        // endregion
    }

    fun effects(init: SpecialEffects.() -> Unit): SpecialEffects {
        val effects = this.effects ?: SpecialEffects()
        effects.init()
        this.effects = effects
        return effects
    }

    fun spawns(init: SpawnSettings.() -> Unit): SpawnSettings {
        val spawnSettings = this.spawnSettings ?: SpawnSettings()
        spawnSettings.init()
        this.spawnSettings = spawnSettings
        return spawnSettings
    }

    fun generation(init: GenerationSettings.() -> Unit): GenerationSettings {
        val generationSettings = this.generationSettings ?: GenerationSettings()
        generationSettings.init()
        this.generationSettings = generationSettings
        return generationSettings
    }
    
    fun build(): Biome {
        val builder = Biome.Builder()
        builder.category(category!!)
        builder.depth(depth!!)
        builder.scale(scale!!)
        builder.temperature(temperature!!)
        builder.temperatureModifier(temperatureModifier)
        builder.downfall(downfall!!)
        builder.effects(effects!!.build())
        builder.spawnSettings(spawnSettings!!.build())
        builder.generationSettings(generationSettings!!.build())
        return builder.build()
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

    fun copy(init: BiomeTemplate.() -> Unit): BiomeTemplate {
        val copy = copy()
        copy.init()
        return copy
    }

    fun build(init: BiomeTemplate.() -> Unit): Biome {
        return copy(init).build()
    }

    companion object {
        @JvmStatic
        fun create(init: BiomeTemplate.() -> Unit): BiomeTemplate {
            val template = BiomeTemplate()
            template.init()
            return template
        }

        @JvmStatic
        fun createBiome(init: BiomeTemplate.() -> Unit): Biome {
            return create(init).build()
        }
    }
}