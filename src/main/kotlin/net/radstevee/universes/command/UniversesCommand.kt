package net.radstevee.universes.command

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.radstevee.universes.Universes.commandManager
import net.radstevee.universes.command.parser.SchematicParser
import net.radstevee.universes.schematic.Schematic
import net.radstevee.universes.schematic.SchematicManager
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.incendo.cloud.bukkit.parser.NamespacedKeyParser
import org.incendo.cloud.bukkit.parser.location.LocationParser
import org.incendo.cloud.kotlin.extension.buildAndRegister
import kotlin.math.roundToInt

/**
 * /universes command.
 */
object UniversesCommand {
    /**
     * Registers the command.
     */
    fun register() {
        commandManager.buildAndRegister("universes") {
            literal("schematic").build {
                literal("save").build {
                    permission("universes.command.schematic.save")
                    required("corner1", LocationParser.locationParser())
                    required("corner2", LocationParser.locationParser())
                    required("key", NamespacedKeyParser.namespacedKeyParser(true, "universes"))

                    handler { ctx ->
                        val corner1 = ctx.get<Location>("corner1")
                        val corner2 = ctx.get<Location>("corner2")
                        val key = ctx.get<NamespacedKey>("key")
                        SchematicManager.save(corner1, corner2, key)

                        ctx.sender().sendMessage(text("Schematic saved to schematics/${key.namespace}/${key.key}.nbt!", GREEN))
                    }
                }
            }
        }

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
                        if(!SchematicManager.schematics.containsValue(schematic)) {
                            ctx.sender().sendMessage(text("That schematic doesn't exist!", RED))
                            return@handler
                        }

                        SchematicManager.delete(SchematicManager.schematics.filterValues { it == schematic }.keys.first())

                        ctx.sender().sendMessage(text("Schematic deleted successfully!", GREEN))
                    }
                }
            }
        }
    }
}