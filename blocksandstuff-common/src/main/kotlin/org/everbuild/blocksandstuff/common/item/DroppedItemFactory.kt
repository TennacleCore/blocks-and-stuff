package org.everbuild.blocksandstuff.common.item

import net.minestom.server.coordinate.Point
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.rule.BlockPlacementRule.UpdateState
import net.minestom.server.item.ItemStack

interface DroppedItemFactory {
    fun spawn(instance: Instance, position: Point, block: Block)
    fun spawn(instance: Instance, position: Point, item: ItemStack)

    companion object {
        @JvmStatic
        var current: DroppedItemFactory = DefaultDroppedItemFactory()
        @JvmStatic
        var doDropItems: Boolean = true

        fun maybeDrop(instance: Instance, position: Point, block: Block) {
            if (doDropItems) {
                current.spawn(instance, position, block)
            }
        }

        fun maybeDrop(state: UpdateState) {
            // a block getter that is not the instance (a per-viewer or per-game view) has nowhere to drop into;
            // the rule still takes the block, as ScaffoldingPlacementRule does
            val instance = state.instance as? Instance ?: return
            if (doDropItems) {
                current.spawn(instance, state.blockPosition, state.currentBlock)
            }
        }

        fun maybeDrop(instance: Instance, position: Point, item: ItemStack) {
            if (doDropItems) {
                current.spawn(instance, position, item)
            }
        }
    }
}