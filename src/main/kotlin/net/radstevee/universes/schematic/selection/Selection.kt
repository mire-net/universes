package net.radstevee.universes.schematic.selection

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.core.BlockBox
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.radstevee.universes.Universes
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import kotlin.math.roundToInt

class Selection(
    val type: SelectionType,
    val key: NamespacedKey,
    val player: Player,
    var start: BlockPos,
    var end: BlockPos,
    var finished: Boolean = false,
    var data: MutableMap<NamespacedKey, CompoundTag> = mutableMapOf(),
) {
    val color = SelectionManager.getColor(player, this)
    val center: Location get() {
        val box =
            BoundingBox(start.x.toDouble(), start.y.toDouble(), start.z.toDouble(), end.x.toDouble(), end.y.toDouble(), end.z.toDouble())
        val center = box.center
        val x = if (center.x % 1 == 0.0) center.x + 0.5 else center.x
        val y = if (center.y % 1 == 0.0) center.y + 0.5 else center.y
        val z = if (center.z % 1 == 0.0) center.z + 0.5 else center.z
        return Location(player.world, x, y + 1.75, z)
    }
    val nameTag =
        player.world.spawn(center, TextDisplay::class.java) {
            it.text(text(key.toString()))
            it.billboard = Display.Billboard.CENTER
        }
    val task =
        Bukkit.getScheduler().runTaskTimer(
            Universes.plugin,
            Runnable {
                displayParticles()
            },
            20,
            0,
        )

    init {
        Universes.selectionEntities.add(nameTag)
    }

    // Thanks to Coll and lowercasebtw for helping me with this ❤
    fun displayParticles() {
        val box = BlockBox(start, end)
        val pointA = Vector(box.min.x, box.min.y, box.min.z)
        val pointB = Vector(box.max.x + 1, box.min.y, box.max.z + 1)
        val pointC = Vector(box.max.x + 1, box.max.y + 1, box.min.z)
        val pointD = Vector(box.min.x, box.max.y + 1, box.max.z + 1)
        val sizeX = Vector(box.sizeX() + 1, 0, 0)
        val sizeY = Vector(0, box.sizeY() + 1, 0)
        val sizeZ = Vector(0, 0, box.sizeZ() + 1)

        drawParticleLines(sizeX, color, pointA, pointD)
        drawParticleLines(sizeY, color, pointA, pointB)
        drawParticleLines(sizeZ, color, pointA, pointC)

        drawParticleLines(sizeX.clone().multiply(-1), color, pointB, pointC)
        drawParticleLines(sizeY.clone().multiply(-1), color, pointC, pointD)
        drawParticleLines(sizeZ.clone().multiply(-1), color, pointB, pointD)
    }

    private fun drawParticleLines(
        path: Vector,
        color: Color,
        vararg origins: Vector,
    ) {
        origins.forEach { origin ->
            for (distance in 0 until path.length().roundToInt()) {
                val position = origin.clone().add(path.clone().normalize().multiply(distance))

                player.spawnParticle(Particle.DUST, position.toLocation(player.world), 1, DustOptions(color, 1f))
            }
        }
    }
}

enum class SelectionType {
    SCHEMATIC {
        override val wand =
            ItemStack(Material.BLAZE_ROD).apply {
                editMeta {
                    it.displayName(text("Schematic Selector").decoration(TextDecoration.ITALIC, false))
                }
            }
    },
    REGION {
        override val wand =
            ItemStack(Material.BAMBOO).apply {
                editMeta {
                    it.displayName(text("Region Selector").decoration(TextDecoration.ITALIC, false))
                }
            }
    },
    MARKER {
        override val wand =
            ItemStack(Material.ARROW).apply {
                editMeta {
                    it.displayName(text("Marker Selector").decoration(TextDecoration.ITALIC, false))
                }
            }
    }, ;

    abstract val wand: ItemStack
}
