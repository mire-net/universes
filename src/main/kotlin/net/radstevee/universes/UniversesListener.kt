package net.radstevee.universes

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldUnloadEvent

/**
 * Listener for universes.
 */
object UniversesListener : Listener {
    /**
     * Used in case somebody wrongly unloaded a universe.
     */
    @EventHandler
    fun onWorldUnload(event: WorldUnloadEvent) {
        Universes.getUniverseByActualId(event.world.key)?.unload()
    }
}