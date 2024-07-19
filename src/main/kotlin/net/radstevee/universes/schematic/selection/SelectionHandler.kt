package net.radstevee.universes.schematic.selection

import net.radstevee.universes.Universes
import net.radstevee.universes.toBlockPos
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class SelectionHandler(
    private val selection: Selection,
) : Listener {
    fun register() {
        Bukkit.getPluginManager().registerEvents(this, Universes.plugin)
    }

    fun unregister() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.player != selection.player) return
        if (event.player.inventory.itemInMainHand.type != selection.type.wand.type) return
        val block = event.clickedBlock ?: return

        when (event.action) {
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                if (selection.type == SelectionType.MARKER) return
                selection.end = block.location.toBlockPos()
            }

            else -> {
                if (selection.type == SelectionType.MARKER) selection.end = block.location.toBlockPos()
                selection.start = block.location.toBlockPos()
            }
        }

        selection.nameTag.teleport(selection.center.toLocation(event.player.world))
        event.isCancelled = true
    }
}
