package net.radstevee.universes.schematic

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockBox
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.radstevee.universes.DataHolder
import net.radstevee.universes.decodeQuick
import net.radstevee.universes.encodeQuick
import net.radstevee.universes.toBlockPos
import org.bukkit.Location
import org.bukkit.NamespacedKey

/**
 * Represents a region within a schematic. This can have a name and some custom data.
 * @param box The box of the region.
 * @param name The name of the region.
 * @param data Custom data of the region. This is a Key-Value map of the name, as a key and the serialised
 *                   NBT data as it is type-dynamic.
 */
data class Region(
    var box: BlockBox,
    override val data: MutableMap<NamespacedKey, CompoundTag> = mutableMapOf()
) : DataHolder {
    /**
     * Represents a region within a schematic. This can have a name and some custom data.
     * @param start The start corner.
     * @param end The end corner.
     * @param name The name of the region.
     */
    constructor(start: Location, end: Location) : this(BlockBox(start.toBlockPos(), end.toBlockPos()))

    companion object {
        /**
         * A codec for [BlockBox].
         */
        val BLOCK_BOX_CODEC: Codec<BlockBox> = RecordCodecBuilder.create { instance ->
            instance.group(
                BlockPos.CODEC
                    .fieldOf("min")
                    .forGetter(BlockBox::min),
                BlockPos.CODEC
                    .fieldOf("max")
                    .forGetter(BlockBox::max)
            ).apply(instance, ::BlockBox)
        }

        /**
         * The codec for this class.
         */
        val CODEC: Codec<Region> = RecordCodecBuilder.create { instance ->
            instance.group(
                BLOCK_BOX_CODEC
                    .fieldOf("box")
                    .forGetter(Region::box),
                DataHolder.CODEC
                    .fieldOf("data")
                    .forGetter(Region::data)
            ).apply(instance, ::Region)
        }
    }
}
