package net.radstevee.universes

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import org.bukkit.NamespacedKey

/**
 * Represents something which can hold data, as a Key-Value map of a namespaced key,
 * as the name and serialised NBT as data, as they are type-dynamic.
 */
interface DataHolder {
    /**
     * The data.
     */
    val data: MutableMap<NamespacedKey, CompoundTag>

    /**
     * Adds custom data to this region.
     * @param T The Data type.
     * @param key The key for the attribute.
     * @param value The value.
     * @param codec The codec to be used for serialising the [value] with type [T].
     */
    fun <T> addData(key: NamespacedKey, value: T, codec: Codec<T>) {
        val tag = codec.encodeQuick(NbtOps.INSTANCE, value)
            ?: error("Failed encoding Custom Data $key with value $value with codec $codec!")
        data[key] = tag as CompoundTag
    }

    /**
     * Gets a custom data attribute.
     * @param T The Data type.
     * @param key The key for the attribute.
     * @param codec The type codec to be used for de-serialising the [key] with type [T].
     * @return The value, if it exists.
     */
    fun <T> getData(key: NamespacedKey, codec: Codec<T>): T? {
        return codec.decodeQuick(NbtOps.INSTANCE, data[key] ?: return null)
    }

    /**
     * Removes a custom data attribute.
     * @param key The key for the attribute.
     */
    fun removeData(key: NamespacedKey) {
        data.remove(key)
    }

    companion object {
        /**
         * Codec for [NamespacedKey].
         */
        val NAMESPACED_KEY_CODEC: Codec<NamespacedKey> = Codec.STRING.comapFlatMap(
            { DataResult.success(NamespacedKey.fromString(it)) }, NamespacedKey::toString
        )

        /**
         * The codec for this class.
         */
        val CODEC: UnboundedMapCodec<NamespacedKey, CompoundTag> = Codec.unboundedMap(
            NAMESPACED_KEY_CODEC, CompoundTag.CODEC
        )
    }
}