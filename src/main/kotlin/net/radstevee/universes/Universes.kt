package net.radstevee.universes

import net.radstevee.universes.world.Universe
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

object Universes {
    private lateinit var plugin: JavaPlugin
    internal val universes = mutableListOf<Universe>()

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
        Bukkit.getPluginManager().registerEvents(UniversesListener, plugin)
    }
}