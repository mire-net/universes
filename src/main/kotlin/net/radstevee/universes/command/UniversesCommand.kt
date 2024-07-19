package net.radstevee.universes.command

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.minecraft.core.BlockBox
import net.radstevee.universes.Universes.commandManager
import net.radstevee.universes.schematic.Marker
import net.radstevee.universes.schematic.Region
import net.radstevee.universes.schematic.Schematic
import net.radstevee.universes.schematic.SchematicManager
import net.radstevee.universes.schematic.selection.Selection
import net.radstevee.universes.schematic.selection.SelectionHandler
import net.radstevee.universes.schematic.selection.SelectionManager
import net.radstevee.universes.schematic.selection.SelectionType
import net.radstevee.universes.toBlockPos
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.incendo.cloud.bukkit.parser.NamespacedKeyParser
import org.incendo.cloud.bukkit.parser.location.LocationParser
import org.incendo.cloud.kotlin.extension.buildAndRegister
import kotlin.math.roundToInt

/**
 * /universes command.
 */
internal object UniversesCommand {
    /**
     * Registers the command.
     */
    fun register() {
        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("place").build {
                    permission("universes.command.schematic.place")
                    required("schematic", SchematicParser.schematicParser())
                    required("location", LocationParser.locationParser())

                    handler { ctx ->
                        val schematic = ctx.get<Schematic>("schematic")
                        val location = ctx.get<Location>("location")

                        schematic.place(location)

                        ctx.sender().sendMessage(
                            text(
                                "Schematic placed at ${location.x.roundToInt()}, ${location.y.roundToInt()}, ${location.z.roundToInt()}!",
                                GREEN
                            )
                        )
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("delete").build {
                    permission("universes.command.schematic.delete")
                    required("schematics", SchematicParser.schematicParser())

                    handler { ctx ->
                        val schematic = ctx.get<Schematic>("schematics")
                        if (!SchematicManager.schematics.containsValue(schematic)) {
                            ctx.sender().sendMessage(text("That schematic doesn't exist!", RED))
                            return@handler
                        }

                        SchematicManager.delete(SchematicManager.schematics.filterValues { it == schematic }.keys.first())

                        ctx.sender().sendMessage(text("Schematic deleted successfully!", GREEN))
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("new").build {
                    permission("universes.command.schematic.new")
                    required("key", NamespacedKeyParser.namespacedKeyParser(true, "universes"))

                    handler { ctx ->
                        val player = ctx.sender() as? Player ?: return@handler
                        val key = ctx.get<NamespacedKey>("key")
                        val selection = Selection(SelectionType.SCHEMATIC, key, player, player.location.toBlockPos(), player.location.toBlockPos())
                        val handler = SelectionHandler(selection)
                        handler.register()
                        if (SelectionManager[player] == null) SelectionManager[player] = mutableListOf(Pair(selection, handler))
                        else SelectionManager[player]!!.add(Pair(selection, handler))
                        player.inventory.addItem(ItemStack(selection.type.wandType))
                        player.sendMessage(
                            text(
                                "Schematic selection has been created! Use the wand to select an area and type /universes schematic save.", GREEN
                            )
                        )
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("save").build {
                    permission("universes.command.schematic.save")

                    handler { ctx ->
                        val player = ctx.sender() as? Player ?: return@handler
                        val (selection, handler) = SelectionManager[player]?.first { it.first.type == SelectionType.SCHEMATIC } ?: run {
                            player.sendMessage(text("You do not have an open selection!", RED))
                            return@handler
                        }
                        val start = Location(player.world, selection.start.x.toDouble(), selection.start.y.toDouble(), selection.start.z.toDouble())
                        val end = Location(player.world, selection.end.x.toDouble(), selection.end.y.toDouble(), selection.end.z.toDouble())
                        val regions =
                            SelectionManager[player]?.filter { it.first.type == SelectionType.REGION }
                                ?.associate { it.first.key to Region(BlockBox(it.first.start, it.first.end)) } ?: mapOf()
                        val markers = SelectionManager[player]?.filter { it.first.type == SelectionType.MARKER }
                            ?.associate { it.first.key to Marker(it.first.start) } ?: mapOf()

                        SchematicManager.save(start, end, selection.key, regions, markers)
                        handler.unregister()
                        selection.task.cancel()
                        SelectionType.entries.forEach { player.inventory.remove(it.wandType) }
                        player.sendMessage(text("Schematic saved to schematics/${selection.key.namespace}/${selection.key.key}.nbt!", GREEN))

                        SelectionManager[player]?.forEach { it.first.task.cancel() }
                        SelectionManager[player] = mutableListOf()
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("region").build {
                    permission("universes.command.schematic.region")
                    required("key", NamespacedKeyParser.namespacedKeyParser(true, "universes"))

                    handler { ctx ->
                        val player = ctx.sender() as? Player ?: return@handler
                        if (SelectionManager[player]?.none { it.first.type == SelectionType.SCHEMATIC } == true) {
                            player.sendMessage(text("You do not have an open selection!", RED))
                            return@handler
                        }
                        val key = ctx.get<NamespacedKey>("key")
                        val selection = Selection(SelectionType.REGION, key, player, player.location.toBlockPos(), player.location.toBlockPos())
                        val handler = SelectionHandler(selection)
                        handler.register()
                        if (SelectionManager[player] == null) SelectionManager[player] = mutableListOf(Pair(selection, handler))
                        else SelectionManager[player]!!.add(Pair(selection, handler))
                        player.inventory.addItem(ItemStack(selection.type.wandType))
                        player.sendMessage(
                            text(
                                "Region selection has been created! Use the wand to select an area and type /universes schematic save.", GREEN
                            )
                        )
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("marker").build {
                    permission("universes.command.schematic.marker")
                    required("key", NamespacedKeyParser.namespacedKeyParser(true, "universes"))

                    handler { ctx ->
                        val player = ctx.sender() as? Player ?: return@handler
                        if (SelectionManager[player]?.none { it.first.type == SelectionType.SCHEMATIC } == true) {
                            player.sendMessage(text("You do not have an open selection!", RED))
                            return@handler
                        }
                        val key = ctx.get<NamespacedKey>("key")
                        val selection = Selection(SelectionType.MARKER, key, player, player.location.toBlockPos(), player.location.toBlockPos())
                        val handler = SelectionHandler(selection)
                        handler.register()
                        if (SelectionManager[player] == null) SelectionManager[player] = mutableListOf(Pair(selection, handler))
                        else SelectionManager[player]!!.add(Pair(selection, handler))
                        player.inventory.addItem(ItemStack(selection.type.wandType))
                        player.sendMessage(
                            text(
                                "Marker selection has been created! Use the wand to select a block and type /universes schematic save.", GREEN
                            )
                        )
                    }
                }
            }
        }
    }
}