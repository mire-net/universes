package net.radstevee.universes.world

import net.minecraft.server.level.progress.ChunkProgressListener
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.status.ChunkStatus

object EmptyGenerationListener : ChunkProgressListener {
    override fun updateSpawnPos(spawnPos: ChunkPos) {}
    override fun onStatusChange(pos: ChunkPos, status: ChunkStatus?) {}
    override fun start() {}
    override fun stop() {}
}