package net.radstevee.universes

import net.radstevee.universes.Universes.universes
import net.radstevee.universes.world.Universe
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldUnloadEvent

object UniversesListener : Listener {
    @EventHandler
    fun onWorldUnload(event: WorldUnloadEvent) {
        if (event.world.key in universes.map(Universe::id)) {
            event.world.worldFolder.deleteRecursively()
        }
    }
}