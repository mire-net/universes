package net.radstevee.universes

import org.bukkit.NamespacedKey
import java.io.File
import java.util.UUID

/**
 * The location to save universes at.
 * @param rootLocation The root location.
 */
data class UniverseLocation(
    var rootLocation: File? = null,
) {
    /**
     * A world name parameter.
     */
    private sealed class Parameter {
        data object UUID : Parameter()
        data object KEY : Parameter()
        data object NAMESPACE : Parameter()
        data class LITERAL(val text: String) : Parameter()
    }

    /**
     * A world name builder.
     */
    inner class WorldNameBuilder {
        private val params = mutableListOf<Parameter>()
        private fun add(parameter: Parameter) = params.add(parameter)

        /**
         * Appends the UUID.
         */
        fun uuid() = params.add(Parameter.UUID)

        /**
         * Appends the key.
         */
        fun key() = params.add(Parameter.KEY)

        /**
         * Appends the namespace.
         */
        fun namespace() = params.add(Parameter.NAMESPACE)

        /**
         * Appends a literal.
         * @param text The text.
         */
        fun literal(text: String) = params.add(Parameter.LITERAL(text))

        /**
         * Gets a world name.
         * @param key The key for the world.
         * @param uuid The world UUID.
         */
        internal fun getWorldName(key: NamespacedKey, uuid: UUID) = buildString {
            append(rootLocation?.path ?: error("No root path set!"))
            append(File.separator)
            params.forEach {
                when (it) {
                    Parameter.UUID -> append(uuid)
                    Parameter.KEY -> append(key.key)
                    Parameter.NAMESPACE -> append(key.namespace)
                    is Parameter.LITERAL -> append(it.text)
                }
            }
        }
    }

    /**
     * The world name builder.
     */
    var worldNameBuilder: WorldNameBuilder? = null
        private set

    /**
     * Sets the [worldNameBuilder].
     */
    fun worldName(factory: WorldNameBuilder.() -> Unit) {
        worldNameBuilder = WorldNameBuilder().apply(factory)
    }

    companion object {
        /**
         * Builds a [net.radstevee.universes.UniverseLocation].
         */
        inline fun universeLocation(factory: UniverseLocation.() -> Unit) = UniverseLocation().apply(factory)
    }
}
