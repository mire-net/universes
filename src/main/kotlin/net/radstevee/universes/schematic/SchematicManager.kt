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
    private val _schematics = mutableMapOf<NamespacedKey, Schematic>()

    /**
     * The list of schematics.
     */
    val schematics get() = _schematics.toMap()

    /**
     * Whether a schematic exists.
     * @param key The schematic key.
     * @return Whether it exists or not.
     */
    private fun exists(key: NamespacedKey) =
        Path("schematics/${key.namespace}/${key.key}.nbt").exists() && _schematics.containsKey(key)

    /**
     * Sets a schematic.
     * @param key The schematic key.
     * @param schematic The schematic.
     */
    operator fun set(key: NamespacedKey, schematic: Schematic) {
        _schematics[key] = schematic
    }

    /**
     * Gets a schematic.
     * @param key The schematic key.
     * @return The schematic, if found.
     */
    operator fun get(key: NamespacedKey) = _schematics[key]

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
                val nbt = NbtIo.readCompressed(
                    Path("schematics/${key.namespace}/${key.key}.nbt"),
                    NbtAccounter.unlimitedHeap()
                )
                val result = Schematic.CODEC.decodeQuick(NbtOps.INSTANCE, nbt) ?: return@forEach

                this[key] = result
            }
        }
    }

    /**
     * Deletes a schematic.
     * @param key The schematic key.
     */
    fun delete(key: NamespacedKey) {
        Path("schematics/${key.namespace}/${key.key}.nbt").deleteExisting()
        _schematics.remove(key)
    }

    /**
     * Saves a schematic.
     * @param schematic The schematic.
     * @param key The schematic key.
     */
    fun save(schematic: Schematic, key: NamespacedKey) {
        val nbt = Schematic.CODEC.encodeQuick(NbtOps.INSTANCE, schematic)
        val path = Path("schematics/${key.namespace}/${key.key}.nbt")
        path.parent.toFile().mkdirs()
        NbtIo.writeCompressed(nbt as CompoundTag, path)
    }

    /**
     * Saves a schematic.
     * @param start The start location.
     * @param end The end location.
     * @param key The schematic key.
     */
    fun save(
        start: Location,
        end: Location,
        key: NamespacedKey,
        regions: Map<NamespacedKey, Region> = mapOf(),
        markers: Map<NamespacedKey, Marker> = mapOf(),
        data: Map<NamespacedKey, CompoundTag> = mapOf()
    ) {
        val startPos = start.toBlockPos()
        val endPos = end.toBlockPos()
        val (min, max) = startPos.asExtremeties(endPos)
        val positions = BlockPos.betweenClosed(min, max)
        val palette = mutableListOf<BlockState>()
        val paletteBlockStates = positions.mapNotNull {
            val location = Location(start.world, it.x.toDouble(), it.y.toDouble(), it.z.toDouble())
            val state = (location.block.state as CraftBlockState).handle
            if (state.isAir) return@mapNotNull null
            else {
                if (state !in palette) palette.add(state)
                val relativePos = it.subtract(min)
                val id = palette.indexOf(state)
                PaletteBlockState(relativePos, id)
            }
        }
        val blockBox = BlockBox(min, max)
        val size = Vec3i(blockBox.sizeX(), blockBox.sizeY(), blockBox.sizeZ())
        val schematic = Schematic(palette, paletteBlockStates, size, mutableMapOf(), data.toMutableMap(), mutableMapOf())
        regions.forEach { schematic.addRegion(blockBox, it.key, it.value) }
        markers.forEach { schematic.addMarker(blockBox, it.key, it.value) }
        save(schematic, key)
        this[key] = schematic
    }
}