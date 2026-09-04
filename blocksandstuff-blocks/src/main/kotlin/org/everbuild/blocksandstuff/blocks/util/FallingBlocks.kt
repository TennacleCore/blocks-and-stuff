package org.everbuild.blocksandstuff.blocks.util

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Point
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.TaskSchedule

/**
 * Gravity-block fall trigger (vanilla `BlockFalling`: a 2-tick delayed re-check, then a falling entity).
 * The handler is replaceable so an app can spawn its own entity into a virtual world; the default covers
 * plain instances with [FallingBlock].
 */
object FallingBlocks {
    const val FALL_DELAY_TICKS = 2L

    fun interface FallHandler {
        fun onFall(getter: Block.Getter, pos: Point, block: Block)
    }

    @JvmStatic
    fun canFallInto(block: Block): Boolean = block.isAir || block.compare(Block.FIRE) || block.isLiquid

    @Volatile
    private var handler = FallHandler { getter, pos, block ->
        val instance = getter as? Instance ?: return@FallHandler
        instance.scheduler().buildTask {
            if (!instance.getBlock(pos).compare(block)) return@buildTask
            if (!canFallInto(instance.getBlock(pos.add(0.0, -1.0, 0.0)))) return@buildTask
            instance.setBlock(pos, Block.AIR)
            FallingBlock.spawn(block, instance, BlockVec(pos))
        }.delay(TaskSchedule.tick(FALL_DELAY_TICKS.toInt())).schedule()
    }

    @JvmStatic
    fun setHandler(h: FallHandler) {
        handler = h
    }

    @JvmStatic
    fun trigger(getter: Block.Getter, pos: Point, block: Block) = handler.onFall(getter, pos, block)
}
