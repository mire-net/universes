package net.radstevee.universes.schematic.selection

import net.minecraft.core.BlockBox
import net.minecraft.core.BlockPos
import net.radstevee.universes.Universes
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.roundToInt

class Selection(val type: SelectionType, val key: NamespacedKey, val player: Player, var start: BlockPos, var end: BlockPos) {
    val color = SelectionManager.getColor(player, this)

    val task = Bukkit.getScheduler().runTaskTimer(Universes.plugin, Runnable {
        displayParticles()
    }, 20, 0)

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

    private fun drawParticleLines(path: Vector, color: Color, vararg origins: Vector) {
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
        override val wandType = Material.BLAZE_ROD
    },
    REGION {
        override val wandType = Material.BAMBOO
    },
    MARKER {
        override val wandType = Material.ARROW
    };

    abstract val wandType: Material
}
