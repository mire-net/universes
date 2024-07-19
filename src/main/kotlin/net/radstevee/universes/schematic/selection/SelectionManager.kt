package net.radstevee.universes.schematic.selection

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Player

typealias PlayerSelection = MutableList<Pair<Selection, SelectionHandler>>

object SelectionManager {
    private val _selections = mutableMapOf<Player, PlayerSelection>()
    val editors = mutableMapOf<Player, Pair<Boolean, Location>>()

    operator fun set(
        player: Player,
        value: PlayerSelection,
    ) {
        if (value.count { it.first.type == SelectionType.SCHEMATIC } > 1) error("a player can only select one schematic at a time")
        _selections[player] = value
    }

    operator fun get(player: Player) = _selections[player]

    // TODO: implement actual coloring
    fun getColor(
        player: Player,
        selection: Selection,
    ) = listOf(
        Color.RED,
        Color.AQUA,
        Color.BLUE,
        Color.GRAY,
        Color.GREEN,
        Color.LIME,
        Color.FUCHSIA,
        Color.BLACK,
        Color.ORANGE,
        Color.SILVER,
        Color.MAROON,
        Color.NAVY,
        Color.TEAL,
    ).random()
}
