package net.radstevee.universes.command

import net.radstevee.universes.schematic.Schematic
import net.radstevee.universes.schematic.SchematicManager
import org.bukkit.NamespacedKey
import org.incendo.cloud.bukkit.BukkitCaptionKeys
import org.incendo.cloud.bukkit.parser.NamespacedKeyParser
import org.incendo.cloud.caption.Caption
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput
import org.incendo.cloud.parser.ArgumentParseResult
import org.incendo.cloud.parser.ArgumentParser
import org.incendo.cloud.parser.ParserDescriptor
import org.incendo.cloud.suggestion.BlockingSuggestionProvider
import org.incendo.cloud.suggestion.Suggestion

/**
 * Command argument parser for schematics.
 */
class SchematicParser<C> :
    ArgumentParser<C, Schematic>,
    BlockingSuggestionProvider<C> {
    override fun parse(
        commandContext: CommandContext<C & Any>,
        commandInput: CommandInput,
    ): ArgumentParseResult<Schematic> {
        val input = commandInput.peekString()
        val split = input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val maxSemi = if (split.size > 1) 1 else 0
        if (input.length - input.replace(":", "").length > maxSemi) {
            // Wrong number of ':'
            return ArgumentParseResult.failure(
                NamespacedKeyParser.NamespacedKeyParseException(
                    BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY,
                    input,
                    commandContext,
                ),
            )
        }

        return runCatching {
            val ret =
                when (split.size) {
                    1 -> return@runCatching ArgumentParseResult.failure(
                        NamespacedKeyParser.NamespacedKeyParseException(
                            BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NEED_NAMESPACE,
                            input,
                            commandContext,
                        ),
                    )

                    2 -> NamespacedKey(commandInput.readUntilAndSkip(':'), commandInput.readString())
                    else -> return@runCatching ArgumentParseResult.failure(
                        NamespacedKeyParser.NamespacedKeyParseException(
                            BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY,
                            input,
                            commandContext,
                        ),
                    )
                }

            if (!SchematicManager.isValid(ret)) {
                return@runCatching ArgumentParseResult.failure(
                    NamespacedKeyParser.NamespacedKeyParseException(
                        Caption.of("universes.command.invalid_schematic"),
                        input,
                        commandContext,
                    ),
                )
            }

            // Success!
            ArgumentParseResult.success(SchematicManager[ret]!!)
        }.getOrElse {
            ArgumentParseResult.failure(
                NamespacedKeyParser.NamespacedKeyParseException(
                    BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY,
                    input,
                    commandContext,
                ),
            )
        }
    }

    override fun suggestions(
        context: CommandContext<C>,
        input: CommandInput,
    ): MutableIterable<Suggestion> =
        SchematicManager.schematics.keys
            .map {
                Suggestion.suggestion(it.toString())
            }.toMutableList()

    companion object {
        /**
         * Creates a new schematic parser.
         */
        fun <C> schematicParser() = ParserDescriptor.of(SchematicParser<C>(), Schematic::class.java)
    }
}
