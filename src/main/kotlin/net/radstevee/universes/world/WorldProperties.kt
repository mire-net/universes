/*
package net.radstevee.universes.world

import com.mojang.serialization.Lifecycle
import net.minecraft.world.Difficulty
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.LevelSettings
import net.minecraft.world.level.storage.PrimaryLevelData
import net.minecraft.world.level.storage.WorldData

class WorldProperties(data: WorldData) : PrimaryLevelData(
    LevelSettings(
        data.levelName,
        data.gameType,
        data.isHardcore,
        data.difficulty,
        data.isAllowCommands,
        data.gameRules,
        data.dataConfiguration
    ), data.worldGenOptions(), SpecialWorldProperty.NONE, Lifecycle.stable() // Why is this deprecated? Why, mojang?
) {
    val gameRules = GameRules()
    override fun getGameRules() = gameRules

    override fun setDayTime(timeOfDay: Long) {}
    override fun getDayTime(): Long = 6000

    override fun setClearWeatherTime(time: Int) {}
    override fun getClearWeatherTime() = 0

    override fun setRaining(raining: Boolean) {}
    override fun isRaining() = false

    override fun setRainTime(time: Int) {}
    override fun getRainTime() = 0

    override fun setThundering(thundering: Boolean) {}
    override fun isThundering() = false

    override fun setThunderTime(time: Int) {}
    override fun getThunderTime() = 0

    override fun getDifficulty() = Difficulty.NORMAL
}*/
