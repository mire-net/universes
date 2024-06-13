package net.radstevee.universes.schematic

import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.radstevee.universes.decodeQuick
import org.bukkit.NamespacedKey
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
}