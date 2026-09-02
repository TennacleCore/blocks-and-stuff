package org.everbuild.blocksandstuff.blocks.placement

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.instance.block.rule.BlockPlacementRule
import org.everbuild.blocksandstuff.common.item.DroppedItemFactory

class LadderPlacementRule(block: Block) : BlockPlacementRule(block) {
    // vanilla BlockLadder.onBlockPlaced: the clicked side when it is a wall, else the first wall around
    // (a top-face click next to a wall still hangs the ladder; the client predicts exactly this)
    override fun blockPlace(placementState: PlacementState): Block? {
        val clicked = placementState.blockFace()
        val candidates = buildList {
            if (clicked != null && clicked != BlockFace.TOP && clicked != BlockFace.BOTTOM) add(clicked)
            for (face in HORIZONTAL) if (face != clicked) add(face)
        }
        for (face in candidates) {
            val supporting = placementState.placePosition.add(face.oppositeFace.toDirection().vec())
            val shape = placementState.instance.getBlock(supporting).registry()?.collisionShape() ?: continue
            if (shape.isFaceFull(face)) return placementState.block.withProperty("facing", face.name.lowercase())
        }
        return null
    }

    private companion object {
        val HORIZONTAL = listOf(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)
    }

    override fun blockUpdate(updateState: UpdateState): Block {
        val facing = BlockFace.valueOf(updateState.currentBlock.getProperty("facing")!!.uppercase())
        val supportingBlockPos = updateState.blockPosition.add(facing.oppositeFace.toDirection().vec())

        if (!updateState.instance.getBlock(supportingBlockPos).isSolid) {
            DroppedItemFactory.maybeDrop(updateState)
            return Block.AIR
        }
        return updateState.currentBlock
    }
}