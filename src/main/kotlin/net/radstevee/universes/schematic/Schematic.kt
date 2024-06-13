package net.radstevee.universes.schematic

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.block.state.BlockState
import net.radstevee.universes.schematic.state.PaletteBlockState
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.block.CraftBlockState
import java.util.function.BiConsumer

/**
 * A schematic.
 * @param palette The block states.
 * @param paletteStates The paletted block states.
 * @param size The size of the schematic.
 */
data class Schematic(
    val palette: List<BlockState>,
    val paletteStates: List<PaletteBlockState>,
    val size: Vec3i,
) {
    /**
     * The center location of this schematic.
     */
    val center = BlockPos(size.x / 2, size.y / 2, size.z / 2)

    /**
     * Places the schematic at a specific location.
     * @param location The location.
     */
    fun place(location: Location) {
        forEach { offset, state ->
            val position = location.clone().add(offset.x.toDouble(), offset.y.toDouble(), offset.z.toDouble())
            position.block.type = state.bukkitMaterial
            (position.block.state as CraftBlockState).setData(state)
        }
    }

    /**
     * Executes an action on each block state.
     * @param action The action.
     */
    private fun forEach(action: BiConsumer<BlockPos, BlockState>) {
        paletteStates.forEach { (offset, i) ->
            action.accept(offset, palette[i])
        }
    }

    companion object {
        /**
         * The codec for this class.
         */
        val CODEC: Codec<Schematic> = RecordCodecBuilder.create { instance ->
            instance.group(
                BlockState.CODEC.listOf()
                    .fieldOf("palette")
                    .forGetter(Schematic::palette),
                PaletteBlockState.CODEC.listOf()
                    .fieldOf("states")
                    .forGetter(Schematic::paletteStates),
                Vec3i.CODEC
                    .fieldOf("size")
                    .forGetter(Schematic::size)
            ).apply(instance, ::Schematic)
        }

        /**
         * An empty schematic.
         */
        val EMPTY = Schematic(emptyList(), emptyList(), Vec3i.ZERO)
    }
}