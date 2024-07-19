package net.radstevee.universes.schematic

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockBox
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import net.radstevee.universes.DataHolder
import net.radstevee.universes.DataHolder.Companion.NAMESPACED_KEY_CODEC
import net.radstevee.universes.Universes
import net.radstevee.universes.add
import net.radstevee.universes.schematic.state.PaletteBlockState
import net.radstevee.universes.schematic.state.PlacedSchematic
import net.radstevee.universes.toBlockPos
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.CraftWorld

/**
 * A schematic.
 * @param palette The block states.
 * @param paletteStates The paletted block states.
 * @param size The size of the schematic.
 * @param entityTags The entities to be serialised, if they get saved.
 */
data class Schematic(
    override val key: NamespacedKey,
    val palette: List<BlockState>,
    val paletteStates: List<PaletteBlockState>,
    val blockEntities: Map<BlockPos, SchematicBlockEntity>,
    val size: Vec3i,
    private val _regions: MutableMap<NamespacedKey, Region> = mutableMapOf(),
    override val data: MutableMap<NamespacedKey, CompoundTag> = mutableMapOf(),
    private val _markers: MutableMap<NamespacedKey, Marker> = mutableMapOf(),
) : DataHolder, SchematicElement {
    /**
     * The center location of this schematic.
     */
    val center = BlockPos(size.x / 2, size.y / 2, size.z / 2)

    /**
     * The regions in this schematic.
     */
    val regions get() = _regions.toMap()

    /**
     * The markers in this schematic.
     */
    val markers get() = _markers.toMap()

    /**
     * Places the schematic at a specific location.
     * @param location The location.
     */
    fun place(location: Location): PlacedSchematic {
        val world = (location.world as CraftWorld).handle
        forEach { offset, (state, nbt) ->
            Universes.plugin.launch {
                val position = location.clone().add(offset.x.toDouble(), offset.y.toDouble(), offset.z.toDouble()).toBlockPos()

                world.setBlock(position, state, 0)

                nbt?.let {
                    val blockEntity = world.getBlockEntity(position)
                    blockEntity?.loadWithComponents(nbt, world.registryAccess())
                }
            }
        }
        val blockPos = location.toBlockPos()
        return PlacedSchematic(this, BlockBox(blockPos, blockPos.add(size)))
    }

    /**
     * Executes an action on each block state.
     * @param action The action.
     */
    private fun forEach(action: (BlockPos, Pair<BlockState, CompoundTag?>) -> Unit) {
        paletteStates.forEach { (offset, i) ->
            val blockEntity = blockEntities[offset]?.nbt
            action(offset, palette[i] to blockEntity)
        }
    }

    /**
     * Adds a marker to this schematic.
     * @param key The marker key.
     * @param marker The marker.
     */
    fun addMarker(schematicBox: BlockBox, key: NamespacedKey, marker: Marker) {
        _markers[key] = Marker(key, marker.pos.subtract(schematicBox.min), marker.data)
    }

    /**
     * Adds a region to this schematic.
     * @param key The region key.
     * @param region The region.
     */
    fun addRegion(schematicBox: BlockBox, key: NamespacedKey, region: Region) {
        _regions[key] = Region(key, BlockBox(region.box.min.subtract(schematicBox.min), region.box.max.subtract(schematicBox.max)), region.data)
    }

    companion object {
        /**
         * The codec for this class.
         */
        val CODEC: Codec<Schematic> = RecordCodecBuilder.create { instance ->
            instance.group(
                NAMESPACED_KEY_CODEC
                    .fieldOf("key")
                    .forGetter(Schematic::key),
                BlockState.CODEC.listOf()
                    .fieldOf("palette")
                    .forGetter(Schematic::palette),
                PaletteBlockState.CODEC.listOf()
                    .fieldOf("states")
                    .forGetter(Schematic::paletteStates),
                SchematicBlockEntity.CODEC
                    .listOf()
                    .fieldOf("block_entities")
                    .xmap({ it.associateBy(SchematicBlockEntity::pos) }, { it.values.toList() })
                    .orElseGet(::emptyMap)
                    .forGetter(Schematic::blockEntities),
                Vec3i.CODEC
                    .fieldOf("size")
                    .forGetter(Schematic::size),
                Codec
                    .unboundedMap(
                        NAMESPACED_KEY_CODEC, Region.CODEC
                    )
                    .fieldOf("regions")
                    .forGetter(Schematic::_regions),
                DataHolder.CODEC
                    .fieldOf("data")
                    .forGetter(Schematic::data),
                Codec
                    .unboundedMap(
                        NAMESPACED_KEY_CODEC, Marker.CODEC
                    )
                    .fieldOf("markers")
                    .forGetter(Schematic::_markers)
            ).apply(instance, ::Schematic)
        }
    }
}