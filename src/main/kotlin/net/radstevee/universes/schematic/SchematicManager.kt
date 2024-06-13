package net.radstevee.universes.schematic

import net.minecraft.core.BlockBox
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.world.level.block.state.BlockState
import net.radstevee.universes.asExtremeties
import net.radstevee.universes.decodeQuick
import net.radstevee.universes.encodeQuick
import net.radstevee.universes.schematic.state.PaletteBlockState
import net.radstevee.universes.toBlockPos
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.block.CraftBlockState
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists

/**
 * The schematic manager.
 */
object SchematicManager {
    /**
     * A mutable list of schematics, to be modified internally.
     */
    private val mutableSchematics = mutableMapOf<NamespacedKey, Schematic>()

    /**
     * The list of schematics.
     */
    val schematics get() = mutableSchematics.toMap()

    /**
     * Whether a schematic exists.
     * @param key The schematic key.
     * @return Whether it exists or not.
     */
    private fun exists(key: NamespacedKey) =
        Path("schematics/${key.namespace}/${key.key}.nbt").exists() && mutableSchematics.containsKey(key)

    /**
     * Puts a schematic.
     * @param key The schematic key.
     * @param schematic The schematic.
     */
    fun put(key: NamespacedKey, schematic: Schematic) {
        mutableSchematics[key] = schematic
    }

    /**
     * Gets a schematic.
     * @param key The schematic key.
     * @return The schematic, if found.
     */
    operator fun get(key: NamespacedKey) = mutableSchematics[key]

    /**
     * Checks if a schematic key is valid or not.
     * @param key The schematic key.
     * @return Whether it's valid or not.
     */
    fun isValid(key: NamespacedKey): Boolean {
        if (!exists(key)) return false
        val nbt = NbtIo.readCompressed(Path("schematics/${key.namespace}/${key.key}.nbt"), NbtAccounter.unlimitedHeap())
        val result = Schematic.CODEC.decodeQuick(NbtOps.INSTANCE, nbt)
        return result != null
    }

    /**
     * Loads the schematics from the schematic directory.
     */
    internal fun load() {
        val basePath = Path("schematics")
        if (!basePath.exists()) {
            basePath.toFile().mkdirs()
            return
        }

        basePath.toFile().walkTopDown().forEach { file ->
            if (file.isDirectory) return@forEach

            val relativePath = basePath.relativize(file.toPath()).toString().replace(File.separatorChar, '/')
            val namespace = relativePath.substringBefore('/')
            val keyPath = relativePath.substringAfter('/')

            if (namespace.isNotBlank() && keyPath.isNotBlank() && keyPath.endsWith(".nbt")) {
                val keyName = keyPath.removeSuffix(".nbt")
                val key = NamespacedKey(namespace, keyName)
                val nbt = NbtIo.readCompressed(Path("schematics/${key.namespace}/${key.key}.nbt"), NbtAccounter.unlimitedHeap())
                val result = Schematic.CODEC.decodeQuick(NbtOps.INSTANCE, nbt) ?: return@forEach

                put(key, result)
            }
        }
    }

    /**
     * Deletes a schematic.
     * @param key The schematic key.
     */
    fun delete(key: NamespacedKey) {
        Path("schematics/${key.namespace}/${key.key}.nbt").deleteExisting()
        mutableSchematics.remove(key)
    }

    fun save(corner1: Location, corner2: Location, key: NamespacedKey) {
        val corner1Pos = corner1.toBlockPos()
        val corner2Pos = corner2.toBlockPos()
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
        put(key, schematic)
    }
}