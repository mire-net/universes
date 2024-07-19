package net.radstevee.universes

import com.mojang.serialization.Decoder
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Encoder
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.radstevee.universes.command.UniversesCommand
import net.radstevee.universes.schematic.SchematicManager
import net.radstevee.universes.world.Universe
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.brigadier.BrigadierSetting
import org.incendo.cloud.brigadier.CloudBrigadierManager
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.caption.Caption
import org.incendo.cloud.caption.CaptionProvider
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import java.util.UUID
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * The universes library.
 */
object Universes {
    /**
     * The plugin.
     */
    internal lateinit var plugin: JavaPlugin

    /**
     * The list of universes.
     */
    val universes = mutableListOf<Universe>()

    /**
     * The location to save universes.
     */
    var location: UniverseLocation? = null

    /**
     * Initialises the library.
     * @param plugin The plugin.
     */
    fun init(plugin: JavaPlugin) {
        if (location == null) error("Universe location must be set!")
        if (location!!.worldNameBuilder == null) error("Universe world name builder must be set!")
        this.plugin = plugin
        Bukkit.getPluginManager().registerEvents(UniversesListener, plugin)
        Bukkit.getPluginManager().addPermissions(listOf(
            "universes.command.schematic.save",
            "universes.command.schematic.place",
            "universes.command.schematic.delete"
        ).map {
            Permission(it, PermissionDefault.OP)
        })

        commandManager = PaperCommandManager.createNative(
            plugin, ExecutionCoordinator.simpleCoordinator()
        )
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) commandManager.registerBrigadier()
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) commandManager.registerAsynchronousCompletions()
        brigManager = commandManager.brigadierManager()
        val brigSettings = brigManager.settings()
        brigSettings.set(BrigadierSetting.FORCE_EXECUTABLE, true)
        listOf(
            CaptionProvider.constantProvider<CommandSender>(
                Caption.of("universes.command.invalid_schematic"), "That schematic is corrupted or invalid!"
            ), CaptionProvider.constantProvider(
                Caption.of("universes.command.invalid_schematic_key"), "invalid schematic key!"
            )
        ).forEach {
            commandManager.captionRegistry().registerProvider(it)
        }
        UniversesCommand.register()
        SchematicManager.load()
    }

    /**
     * Gets a universe by its initial id.
     * @param initialId The initial id.
     * @return The universe, if it exists
     */
    fun getUniverse(initialId: NamespacedKey) = universes.find { it.initialId == initialId }

    /**
     * Gets a universe by its UUID.
     * @param uuid The uuid.
     * @return The universe, if it exists.
     */
    fun getUniverse(uuid: UUID) = universes.find { it.uuid == uuid }

    /**
     * Gets a universe by its actual id.
     * @param id The actual id.
     * @return The universe, if it exists.
     */
    fun getUniverseByActualId(id: NamespacedKey) = universes.find { it.id == id }

    /**
     * The command manager.
     */
    internal lateinit var commandManager: PaperCommandManager<CommandSender>

    /**
     * The brigadier command manager.
     */
    private lateinit var brigManager: CloudBrigadierManager<CommandSender, *>
}

/**
 * Converts a [Location] to a [BlockPos].
 */
fun Location.toBlockPos() = BlockPos(floor(x).roundToInt(), floor(y).roundToInt(), floor(z).roundToInt())

/**
 * Compares this block position with another and returns an ordered pair.
 * Stolen from [mcbrawls/blueprint](https://github.com/mcbrawls/blueprint/blob/1.21/src/main/kotlin/net/mcbrawls/blueprint/BlueprintMod.kt)
 */
fun BlockPos.asExtremeties(other: BlockPos): Pair<BlockPos, BlockPos> {
    val box = AABB(Vec3.atLowerCornerOf(this), Vec3.atLowerCornerOf(other))
    val min = BlockPos.containing(box.minX, box.minY, box.minZ)
    val max = BlockPos.containing(box.maxX, box.maxY, box.maxZ)
    return min to max
}

/**
 * Encodes to a dynamic ops format.
 * Stolen from [mcbrawls/codex](https://github.com/mcbrawls/codex/blob/1.21/src/main/kotlin/dev/andante/codex/Codex.kt)
 */
fun <A, T> Encoder<A>.encodeQuick(ops: DynamicOps<T>, input: A): T? {
    return encodeStart(ops, input).result().orElse(null)
}

/**
 * Decodes from a dynamic ops format.
 * Stolen from [mcbrawls/codex](https://github.com/mcbrawls/codex/blob/1.21/src/main/kotlin/dev/andante/codex/Codex.kt)
 */
fun <A, T> Decoder<A>.decodeQuick(ops: DynamicOps<T>, input: T): A? {
    return parse(ops, input).result().orElse(null)
}

/**
 * Adds a vector to a block position.
 */
fun BlockPos.add(vector: Vec3i) = offset(vector)