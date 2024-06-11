package net.radstevee.universes.world

import net.kyori.adventure.util.TriState
import net.radstevee.universes.Universes
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator
import java.util.UUID
import kotlin.random.Random

/**
 * A universe.
 * @param initialId The initial ID of the universe. It changes based on the save location.
 * @param uuid The UUID. Defaults to a random UUID.
 * @param biomeProvider The biome provider of the world. Defaults to a void provider.
 * @param environment The world environment. CUSTOM may break. Defaults to NORMAL.
 * @param generateStructures Whether to generate structures or not. Defaults to false.
 * @param generator The chunk generator. Defaults to a void generator.
 * @param keepSpawnLoaded Whether to keep spawn loaded. Defaults to unset.
 * @param generatorSettings The generator settings in a string. Defaults to {}.
 * @param hardcore Whether the world is hardcore or not. Defaults to false.
 * @param seed The seed. Defaults to a random seed.
 * @param type The world type. Defaults to NORMAL.
 */
class Universe(
    val initialId: NamespacedKey,
    val uuid: UUID = UUID.randomUUID(),
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
    /**
     * The actual id of the world, with the path.
     */
    val id = NamespacedKey(initialId.namespace, Universes.location!!.worldNameBuilder!!.getWorldName(initialId, uuid))

    /**
     * The world creator.
     */
    val worldCreator =
        WorldCreator(id).biomeProvider(biomeProvider).environment(environment).generateStructures(generateStructures)
            .generator(generator).keepSpawnLoaded(keepSpawnLoaded).generatorSettings(generatorSettings).seed(seed)
            .hardcore(hardcore).type(type)

    /**
     * The world.
     */
    val world = worldCreator.createWorld() ?: error("Failed to create universe!")

    init {
        Universes.universes.add(this)
    }

    /**
     * Unloads the universe. This gets called inside of the [net.radstevee.universes.UniversesListener], if only the Bukkit world gets unloaded.
     */
    fun unload() {
        Universes.universes.remove(this)
        Bukkit.unloadWorld(world, false)
        world.worldFolder.deleteRecursively()
    }
}