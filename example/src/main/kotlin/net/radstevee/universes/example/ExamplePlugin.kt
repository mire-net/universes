package net.radstevee.universes.example

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.radstevee.universes.UniverseLocation.Companion.universeLocation
import net.radstevee.universes.Universes
import net.radstevee.universes.world.Universe
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        Universes.location = universeLocation {
            rootLocation = File("/tmp/universes")
            worldName {
                literal("temp-world-")
                key()
                literal("-")
                uuid()
            }
        }
        Universes.init(this)
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands = event.registrar()
            commands.register(Commands.literal("universes_load").executes { ctx ->
                val universe = Universe(NamespacedKey("universes", "test_world"))
                val player = (ctx.source.sender as? Player)

                player?.teleport(Location(universe.world, 0.0, 0.0, 0.0))
                player?.gameMode = GameMode.CREATIVE
                0
            }.build())
            commands.register(Commands.literal("universes_unload").executes { ctx ->
                Universes.universes.forEach(Universe::unload)
                0
            }.build())
        }
    }

    override fun onDisable() {

    }
}