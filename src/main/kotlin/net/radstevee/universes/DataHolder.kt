package net.radstevee.universes

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.nbt.CompoundTag
import org.bukkit.NamespacedKey
import java.util.UUID

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
     * Gets an integer from the data map.
     * @param key The key for the attribute.
     * @return The integer value if it exists, null otherwise.
     */
    fun getInt(key: NamespacedKey) = data[key]?.getInt("value")

    /**
     * Gets a byte from the data map.
     * @param key The key for the attribute.
     * @return The byte value if it exists, null otherwise.
     */
    fun getByte(key: NamespacedKey) = data[key]?.getByte("value")

    /**
     * Gets a long from the data map.
     * @param key The key for the attribute.
     * @return The long value if it exists, null otherwise.
     */
    fun getLong(key: NamespacedKey) = data[key]?.getLong("value")

    /**
     * Gets a boolean from the data map.
     * @param key The key for the attribute.
     * @return The boolean value if it exists, null otherwise.
     */
    fun getBoolean(key: NamespacedKey) = data[key]?.getBoolean("value")

    /**
     * Gets a byte array from the data map.
     * @param key The key for the attribute.
     * @return The byte array if it exists, null otherwise.
     */
    fun getByteArray(key: NamespacedKey) = data[key]?.getByteArray("value")

    /**
     * Gets a compound tag from the data map.
     * @param key The key for the attribute.
     * @return The compound tag if it exists, null otherwise.
     */
    fun getCompound(key: NamespacedKey) = data[key]?.getCompound("value")

    /**
     * Gets a double from the data map.
     * @param key The key for the attribute.
     * @return The double value if it exists, null otherwise.
     */
    fun getDouble(key: NamespacedKey) = data[key]?.getDouble("value")

    /**
     * Gets a float from the data map.
     * @param key The key for the attribute.
     * @return The float value if it exists, null otherwise.
     */
    fun getFloat(key: NamespacedKey) = data[key]?.getFloat("value")

    /**
     * Gets an integer array from the data map.
     * @param key The key for the attribute.
     * @return The integer array if it exists, null otherwise.
     */
    fun getIntArray(key: NamespacedKey) = data[key]?.getIntArray("value")

    /**
     * Gets a list from the data map.
     * @param key The key for the attribute.
     * @param type The type of the list elements.
     * @return The list if it exists, null otherwise.
     */
    fun getList(key: NamespacedKey, type: Int) = data[key]?.getList("value", type)

    /**
     * Gets a long array from the data map.
     * @param key The key for the attribute.
     * @return The long array if it exists, null otherwise.
     */
    fun getLongArray(key: NamespacedKey) = data[key]?.getLongArray("value")

    /**
     * Gets a short from the data map.
     * @param key The key for the attribute.
     * @return The short value if it exists, null otherwise.
     */
    fun getShort(key: NamespacedKey) = data[key]?.getShort("value")

    /**
     * Gets a string from the data map.
     * @param key The key for the attribute.
     * @return The string if it exists, null otherwise.
     */
    fun getString(key: NamespacedKey) = data[key]?.getString("value")

    /**
     * Gets a UUID from the data map.
     * @param key The key for the attribute.
     * @return The UUID if it exists, null otherwise.
     */
    fun getUUID(key: NamespacedKey) = data[key]?.getUUID("value")

    /**
     * Puts an integer into the data map.
     * @param key The key for the attribute.
     * @param value The integer value to put.
     */
    fun putInt(key: NamespacedKey, value: Int) {
        data[key] = CompoundTag().apply { putInt("value", value) }
    }

    /**
     * Puts a byte into the data map.
     * @param key The key for the attribute.
     * @param value The byte value to put.
     */
    fun putByte(key: NamespacedKey, value: Byte) {
        data[key] = CompoundTag().apply { putByte("value", value) }
    }

    /**
     * Puts a long into the data map.
     * @param key The key for the attribute.
     * @param value The long value to put.
     */
    fun putLong(key: NamespacedKey, value: Long) {
        data[key] = CompoundTag().apply { putLong("value", value) }
    }

    /**
     * Puts a boolean into the data map.
     * @param key The key for the attribute.
     * @param value The boolean value to put.
     */
    fun putBoolean(key: NamespacedKey, value: Boolean) {
        data[key] = CompoundTag().apply { putBoolean("value", value) }
    }

    /**
     * Puts a byte array into the data map.
     * @param key The key for the attribute.
     * @param value The byte array to put.
     */
    fun putByteArray(key: NamespacedKey, value: ByteArray) {
        data[key] = CompoundTag().apply { putByteArray("value", value) }
    }

    /**
     * Puts a compound tag into the data map.
     * @param key The key for the attribute.
     * @param value The compound tag to put.
     */
    fun putCompound(key: NamespacedKey, value: CompoundTag) {
        data[key] = CompoundTag().apply { put("value", value) }
    }

    /**
     * Puts a double into the data map.
     * @param key The key for the attribute.
     * @param value The double value to put.
     */
    fun putDouble(key: NamespacedKey, value: Double) {
        data[key] = CompoundTag().apply { putDouble("value", value) }
    }

    /**
     * Puts a float into the data map.
     * @param key The key for the attribute.
     * @param value The float value to put.
     */
    fun putFloat(key: NamespacedKey, value: Float) {
        data[key] = CompoundTag().apply { putFloat("value", value) }
    }

    /**
     * Puts an integer array into the data map.
     * @param key The key for the attribute.
     * @param value The integer array to put.
     */
    fun putIntArray(key: NamespacedKey, value: IntArray) {
        data[key] = CompoundTag().apply { putIntArray("value", value) }
    }

    /**
     * Puts a long array into the data map.
     * @param key The key for the attribute.
     * @param value The long array to put.
     */
    fun putLongArray(key: NamespacedKey, value: LongArray) {
        data[key] = CompoundTag().apply { putLongArray("value", value) }
    }

    /**
     * Puts a short into the data map.
     * @param key The key for the attribute.
     * @param value The short value to put.
     */
    fun putShort(key: NamespacedKey, value: Short) {
        data[key] = CompoundTag().apply { putShort("value", value) }
    }

    /**
     * Puts a string into the data map.
     * @param key The key for the attribute.
     * @param value The string to put.
     */
    fun putString(key: NamespacedKey, value: String) {
        data[key] = CompoundTag().apply { putString("value", value) }
    }

    /**
     * Puts a UUID into the data map.
     * @param key The key for the attribute.
     * @param value The UUID to put.
     */
    fun putUUID(key: NamespacedKey, value: UUID) {
        data[key] = CompoundTag().apply { putUUID("value", value) }
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
        val NAMESPACED_KEY_CODEC: Codec<NamespacedKey> = Codec.STRING.comapFlatMap({ DataResult.success(NamespacedKey.fromString(it)) }, NamespacedKey::toString)

        /**
         * The codec for this class.
         */
        val CODEC: UnboundedMapCodec<NamespacedKey, CompoundTag> = Codec.unboundedMap(NAMESPACED_KEY_CODEC, CompoundTag.CODEC)
    }
}