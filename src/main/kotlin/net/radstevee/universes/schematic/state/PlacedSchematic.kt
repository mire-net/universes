package net.radstevee.universes.schematic.state

import net.minecraft.core.BlockBox
import net.radstevee.universes.add
import net.radstevee.universes.schematic.Schematic
import org.bukkit.NamespacedKey

/**
 * Represents a schematic which has been placed in the world.
 * @param schematic The origin schematic.
 * @param box The block box where it has been placed.
 */
class PlacedSchematic(
    val schematic: Schematic,
    val box: BlockBox,
) {
    /**
     * Gets a marker.
     * @param key The marker key.
     * @return The marker, or null.
     */
    fun getMarker(key: NamespacedKey) =
        schematic.markers[key]?.apply {
            pos = pos.add(box.min)
        }

    /**
     * Gets a region.
     * @param key The region key.
     * @return The region, or null.
     */
    fun getRegion(key: NamespacedKey) =
        schematic.regions[key]?.apply {
            box = BlockBox(this.box.min.add(box.min), this.box.max.add(box.max))
        }
}
