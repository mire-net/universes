package net.radstevee.universes.world

import net.kyori.adventure.util.TriState
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator
import java.util.UUID
import kotlin.random.Random

class Universe(
    initialId: NamespacedKey,
    uuid: UUID = UUID.randomUUID(),
    biomeProvider: BiomeProvider = EmptyBiomeProvider,
    environment: World.Environment = World.Environment.NORMAL,
    generateStructures: Boolean = false,
    generator: ChunkGenerator = EmptyGenerator,
    keepSpawnLoaded: TriState = TriState.NOT_SET,
    generatorSettings: String = "{}",
    hardcore: Boolean = false,
    seed: Long = Random.nextLong(),
    type: WorldType = WorldType.NORMAL
) {
    val id = NamespacedKey(initialId.namespace, "/tmp/universes/$uuid-${initialId.key}")
    val worldCreator = WorldCreator(id).biomeProvider(biomeProvider).environment(environment)
        .generateStructures(generateStructures).generator(generator).keepSpawnLoaded(keepSpawnLoaded)
        .generatorSettings(generatorSettings).seed(seed).hardcore(hardcore).type(type)
    val world = worldCreator.createWorld() ?: error("Failed to create universe!")
}