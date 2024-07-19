package net.radstevee.universes.schematic

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.radstevee.universes.DataHolder
import net.radstevee.universes.DataHolder.Companion.NAMESPACED_KEY_CODEC
import org.bukkit.NamespacedKey

/**
 * Represents a marker inside of a schematic.
 */
data class Marker(
    override val key: NamespacedKey,
    var pos: BlockPos,
    override val data: MutableMap<NamespacedKey, CompoundTag> = mutableMapOf(),
) : DataHolder,
    SchematicElement {
    companion object {
        /**
         * The codec for this class.
         */
        val CODEC: Codec<Marker> =
            RecordCodecBuilder.create { instance ->
                instance
                    .group(
                        NAMESPACED_KEY_CODEC
                            .fieldOf("key")
                            .forGetter(Marker::key),
                        BlockPos.CODEC
                            .fieldOf("pos")
                            .forGetter(Marker::pos),
                        DataHolder.CODEC
                            .fieldOf("data")
                            .forGetter(Marker::data),
                    ).apply(instance, ::Marker)
            }
    }
}
