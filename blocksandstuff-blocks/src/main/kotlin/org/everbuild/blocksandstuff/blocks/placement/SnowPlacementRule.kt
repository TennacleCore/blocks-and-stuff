package org.everbuild.blocksandstuff.blocks.placement

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.instance.block.rule.BlockPlacementRule

class SnowPlacementRule(block: Block) : BlockPlacementRule(block) {
    // vanilla (BlockSnow.canPlaceBlockAt): ice/packed ice never support snow; a snow pile of 7+ layers does
    private fun supportsSnow(below: Block): Boolean {
        if (below.compare(Block.ICE) || below.compare(Block.PACKED_ICE)) return false
        if (below.compare(Block.SNOW) && (below.getProperty("layers")?.toIntOrNull() ?: 0) >= 7) return true
        return below.registry()!!.collisionShape().isFaceFull(BlockFace.TOP)
    }

    override fun blockPlace(placementState: PlacementState): Block? {
        val bottomSupport = placementState.instance.getBlock(placementState.placePosition.add(0.0, -1.0, 0.0))
        val snow = placementState.instance.getBlock(placementState.placePosition)
        if (!supportsSnow(bottomSupport)) return null
        if (snow.compare(Block.SNOW)) {
            val snowLayer = snow.getProperty("layers")!!.toInt()
            return placementState.block().withProperty("layers", (snowLayer + 1).toString())
        }
        return placementState.block
    }

    override fun isSelfReplaceable(replacement: Replacement): Boolean {
        val snowLayers = replacement.block.getProperty("layers")?.toIntOrNull() ?: return false
        if (replacement.blockFace != BlockFace.TOP) return false
        if (snowLayers >= 8) return false
        if (replacement.block.compare(Block.SNOW)) {
            return true
        }
        return super.isSelfReplaceable(replacement)
    }

    override fun blockUpdate(updateState: UpdateState): Block {
        val bottomSupport = updateState.instance.getBlock(updateState.blockPosition.add(0.0, -1.0, 0.0))
        if (supportsSnow(bottomSupport)) return updateState.currentBlock
        return Block.AIR
    }
}