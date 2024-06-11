package net.radstevee.universes

import net.radstevee.universes.world.Universe
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

/**
 * The universes library.
 */
object Universes {
    /**
     * The plugin.
     */
    private lateinit var plugin: JavaPlugin

    /**
     * The list of universes.
     */
    val universes = mutableListOf<Universe>()

    /**
     * The location to save universes.
     */
    var location: UniverseLocation? = null

    /**
     * Initialises the library.
     * @param plugin The plugin.
     */
    fun init(plugin: JavaPlugin) {
        if (location == null) error("Universe location must be set!")
        if (location!!.worldNameBuilder == null) error("Universe world name builder must be set!")
        this.plugin = plugin
        Bukkit.getPluginManager().registerEvents(UniversesListener, plugin)
    }

    /**
     * Gets a universe by its initial id.
     * @param initialId The initial id.
     * @return The universe, if it exists
     */
    fun getUniverse(initialId: NamespacedKey) = universes.find { it.initialId == initialId }

    /**
     * Gets a universe by its UUID.
     * @param uuid The uuid.
     * @return The universe, if it exists.
     */
    fun getUniverse(uuid: UUID) = universes.find { it.uuid == uuid }

    /**
     * Gets a universe by its actual id.
     * @param id The actual id.
     * @return The universe, if it exists.
     */
    fun getUniverseByActualId(id: NamespacedKey) = universes.find { it.id == id }
}