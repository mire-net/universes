package net.radstevee.universes.command

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.minecraft.core.BlockBox
import net.minecraft.nbt.CompoundTag
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
import org.incendo.cloud.bukkit.parser.NamespacedKeyParser
import org.incendo.cloud.bukkit.parser.location.LocationParser
import org.incendo.cloud.kotlin.extension.buildAndRegister
import org.incendo.cloud.parser.standard.BooleanParser
import org.incendo.cloud.parser.standard.IntegerParser
import org.incendo.cloud.parser.standard.StringParser
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
                                "Schematic placed at ${location.x.roundToInt()}, ${location.y.roundToInt()}, ${location.z.roundToInt()}!", GREEN
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
                        SelectionType.entries.forEach { player.inventory.addItem(it.wand) }
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
                        val regions = SelectionManager[player]?.filter { it.first.type == SelectionType.REGION }
                            ?.associate { it.first.key to Region(it.first.key, BlockBox(it.first.start, it.first.end), it.first.data) } ?: mapOf()
                        val markers = SelectionManager[player]?.filter { it.first.type == SelectionType.MARKER }
                            ?.associate { it.first.key to Marker(it.first.key, it.first.start, it.first.data) } ?: mapOf()

                        SchematicManager.save(start, end, selection.key, regions, markers, selection.data)
                        handler.unregister()
                        selection.task.cancel()
                        SelectionType.entries.forEach { player.inventory.remove(it.wand) }
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
                        player.sendMessage(
                            text(
                                "Region selection has been created! Use the wand to select an area and use /universes chematic selection finish ${selection.key} to finish it!", GREEN
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
                        player.sendMessage(
                            text(
                                "Marker selection has been created! Use the wand to select a block and use /universes chematic selection finish ${selection.key} to finish it!", GREEN
                            )
                        )
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("selection").build {
                    literal("finish").build {
                        permission("universes.command.schematic.selection.finish")

                        handler { ctx ->
                            val player = ctx.sender() as? Player ?: return@handler
                            val (selection, handler) = SelectionManager[player]?.last { !it.first.finished } ?: run {
                                player.sendMessage(text("You do not have an open selection!", RED))
                                return@handler
                            }

                            selection.finished = true
                            handler.unregister()
                            player.sendMessage(
                                text(
                                    "Selection for ${selection.key} has been marked as finished! Use /universes chematic selection reopen ${selection.key} to reopen it!",
                                    GREEN
                                )
                            )
                        }
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("selection").build {
                    literal("reopen").build {
                        permission("universes.command.schematic.selection.reopen")
                        required("element", SchematicElementParser.schematicElementParser())

                        handler { ctx ->
                            val player = ctx.sender() as? Player ?: return@handler
                            val elementKey = ctx.get<NamespacedKey>("element")
                            val (selection, handler) = SelectionManager[player]!!.find { it.first.key == elementKey }!!

                            selection.finished = false
                            handler.register()

                            player.sendMessage(
                                text(
                                    "Selection for ${selection.key} has been reopened! Use /universes chematic selection finish ${selection.key} to finish it!",
                                    GREEN
                                )
                            )
                        }
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("data").build {
                    literal("add").build {
                        literal("string").build {
                            permission("universes.command.schematic.data.add.literal")
                            required("element", SchematicElementParser.schematicElementParser())
                            required("data-key", NamespacedKeyParser.namespacedKeyParser(true, "universes"))
                            required("string", StringParser.greedyStringParser())

                            handler { ctx ->
                                val player = ctx.sender() as? Player ?: return@handler
                                val elementKey = ctx.get<NamespacedKey>("element")
                                val dataKey = ctx.get<NamespacedKey>("data-key")
                                val string = ctx.get<String>("string")
                                val (selection, _) = SelectionManager[player]!!.find { it.first.key == elementKey }!!

                                val tag = CompoundTag()
                                tag.putString("value", string)
                                selection.data[dataKey] = tag

                                player.sendMessage(
                                    text(
                                        "Data successfully added!", GREEN
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("data").build {
                    literal("add").build {
                        literal("int").build {
                            permission("universes.command.schematic.data.add.int")
                            required("element", SchematicElementParser.schematicElementParser())
                            required("data-key", NamespacedKeyParser.namespacedKeyParser(true, "universes"))
                            required("int", IntegerParser.integerParser())

                            handler { ctx ->
                                val player = ctx.sender() as? Player ?: return@handler
                                val elementKey = ctx.get<NamespacedKey>("element")
                                val dataKey = ctx.get<NamespacedKey>("data-key")
                                val int = ctx.get<Int>("int")
                                val (selection, _) = SelectionManager[player]!!.find { it.first.key == elementKey }!!

                                val tag = CompoundTag()
                                tag.putInt("value", int)
                                selection.data[dataKey] = tag

                                player.sendMessage(
                                    text(
                                        "Data successfully added!", GREEN
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("data").build {
                    literal("add").build {
                        literal("bool").build {
                            permission("universes.command.schematic.data.add.int")
                            required("element", SchematicElementParser.schematicElementParser())
                            required("data-key", NamespacedKeyParser.namespacedKeyParser(true, "universes"))
                            required("bool", BooleanParser.booleanParser(true))

                            handler { ctx ->
                                val player = ctx.sender() as? Player ?: return@handler
                                val elementKey = ctx.get<NamespacedKey>("element")
                                val dataKey = ctx.get<NamespacedKey>("data-key")
                                val bool = ctx.get<Boolean>("bool")
                                val (selection, _) = SelectionManager[player]!!.find { it.first.key == elementKey }!!

                                val tag = CompoundTag()
                                tag.putBoolean("value", bool)
                                selection.data[dataKey] = tag

                                player.sendMessage(
                                    text(
                                        "Data successfully added!", GREEN
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}