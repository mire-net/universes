package net.radstevee.universes.schematic

import org.bukkit.NamespacedKey

/**
 * Represents an element inside of a schematic, such as a marker or region. Also includes schematics themselves.
 */
interface SchematicElement {
    /**
     * The element key.
     */
    val key: NamespacedKey
}