package net.radstevee.universes.world

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.Random

/**
 * An empty chunk generator.
 */
object EmptyGenerator : ChunkGenerator() {
    override fun getFixedSpawnLocation(
        world: World,
        random: Random,
    ) = Location(world, 0.0, 0.0, 0.0)
}
