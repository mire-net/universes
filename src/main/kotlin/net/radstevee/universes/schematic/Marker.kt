package net.radstevee.universes.schematic

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.radstevee.universes.DataHolder
import org.bukkit.NamespacedKey

/**
 * Represents a marker inside of a schematic.
 */
data class Marker(
    var pos: BlockPos,
    override val data: MutableMap<NamespacedKey, CompoundTag> = mutableMapOf()
) : DataHolder {
    companion object {
        /**
         * The codec for this class.
         */
        val CODEC: Codec<Marker> = RecordCodecBuilder.create { instance ->
            instance.group(
                BlockPos.CODEC
                    .fieldOf("pos")
                    .forGetter(Marker::pos),
                DataHolder.CODEC
                    .fieldOf("data")
                    .forGetter(Marker::data)
            ).apply(instance, ::Marker)
        }
    }
}
