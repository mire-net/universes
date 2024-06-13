package net.radstevee.universes.schematic.state

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos

/**
 * A block state within a palette.
 * @param offset The offset.
 * @param index The palette index.
 */
data class PaletteBlockState(
    val offset: BlockPos,
    val index: Int
) {
    override fun toString(): String {
        return "PaletteBlockState[#$index, $offset]"
    }

    companion object {
        /**
         * The codec for this class.
         */
        val CODEC: Codec<PaletteBlockState> = RecordCodecBuilder.create { instance ->
            instance.group(
                BlockPos.CODEC.fieldOf("offset").forGetter(PaletteBlockState::offset),
                Codec.INT.fieldOf("index").forGetter(PaletteBlockState::index)
            ).apply(instance, ::PaletteBlockState)
        }
    }
}
