package org.everbuild.blocksandstuff.blocks.placement

import net.minestom.server.coordinate.Point
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.rule.BlockPlacementRule
import org.everbuild.blocksandstuff.blocks.util.FallingBlocks

/** Sand/gravel gravity (vanilla `BlockFalling`): a fallable-into cell below hands off to [FallingBlocks]. */
class GravityBlockPlacementRule(block: Block) : BlockPlacementRule(block) {
    override fun blockPlace(placementState: PlacementState): Block? {
        maybeFall(placementState.instance, placementState.placePosition, placementState.block)
        return placementState.block
    }

    override fun blockUpdate(updateState: UpdateState): Block {
        maybeFall(updateState.instance, updateState.blockPosition, updateState.currentBlock)
        return updateState.currentBlock
    }

    private fun maybeFall(instance: Block.Getter, pos: Point, block: Block) {
        if (FallingBlocks.canFallInto(instance.getBlock(pos.add(0.0, -1.0, 0.0)))) {
            FallingBlocks.trigger(instance, pos, block)
        }
    }
}
