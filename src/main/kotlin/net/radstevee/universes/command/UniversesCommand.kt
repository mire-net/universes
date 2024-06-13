package net.radstevee.universes.command

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.minecraft.core.BlockBox
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.world.level.block.state.BlockState
import net.radstevee.universes.Universes.commandManager
import net.radstevee.universes.asExtremeties
import net.radstevee.universes.command.parser.SchematicParser
import net.radstevee.universes.encodeQuick
import net.radstevee.universes.schematic.Schematic
import net.radstevee.universes.schematic.SchematicManager
import net.radstevee.universes.schematic.state.PaletteBlockState
import net.radstevee.universes.toBlockPos
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.block.CraftBlockState
import org.incendo.cloud.bukkit.parser.NamespacedKeyParser
import org.incendo.cloud.bukkit.parser.location.LocationParser
import org.incendo.cloud.kotlin.extension.buildAndRegister
import kotlin.io.path.Path
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
                        val corner1Pos = corner1.toBlockPos()
                        val corner2Pos = corner2.toBlockPos()
                        val key = ctx.get<NamespacedKey>("key")
                        val (min, max) = corner1Pos.asExtremeties(corner2Pos)
                        val positions = BlockPos.betweenClosed(min, max)
                        val palette = mutableListOf<BlockState>()
                        val paletteBlockStates = positions.mapNotNull { pos ->
                            val location = Location(corner1.world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                            val state = (location.block.state as CraftBlockState).handle
                            if (state.isAir) {
                                return@mapNotNull null
                            } else {
                                if (state !in palette) {
                                    palette.add(state)
                                }
                                val relativePos = pos.subtract(min)
                                val id = palette.indexOf(state)
                                PaletteBlockState(relativePos, id)
                            }
                        }
                        val blockBox = BlockBox(min, max)
                        val size = Vec3i(blockBox.sizeX(), blockBox.sizeY(), blockBox.sizeZ())
                        val schematic = Schematic(palette, paletteBlockStates, size)
                        val nbt = Schematic.CODEC.encodeQuick(NbtOps.INSTANCE, schematic)
                        val path = Path("schematics/${key.namespace}/${key.key}.nbt")
                        path.parent.toFile().mkdirs()
                        NbtIo.writeCompressed(nbt as CompoundTag, path)
                        SchematicManager.put(key, schematic)

                        ctx.sender().sendMessage(text("Schematic saved to $path!", GREEN))
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