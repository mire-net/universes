package net.radstevee.universes.schematic

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag

/**
 * Represents a block entity within a schematic.
 * @param pos The position of the block entity.
 * @param nbt The nbt data.
 */
data class SchematicBlockEntity(
    val pos: BlockPos,
    val nbt: CompoundTag,
) {
    companion object {
        /**
         * The codec for this class.
         */
        val CODEC: Codec<SchematicBlockEntity> =
            RecordCodecBuilder.create { instance ->
                instance
                    .group(
                        BlockPos.CODEC
                            .fieldOf("pos")
                            .forGetter(SchematicBlockEntity::pos),
                        CompoundTag.CODEC
                            .fieldOf("nbt")
                            .forGetter(SchematicBlockEntity::nbt),
                    ).apply(instance, ::SchematicBlockEntity)
            }
    }
}
