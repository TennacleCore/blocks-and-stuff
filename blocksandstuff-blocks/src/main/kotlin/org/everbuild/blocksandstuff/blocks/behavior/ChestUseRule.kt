package org.everbuild.blocksandstuff.blocks.behavior

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockHandler

// <@AI_UNREVIEWED>
/**
 * A chest consumes a click the way vanilla's open does: a block only lands off it when the player sneaks
 * with something in the clicking hand. Nothing opens yet.
 */
class ChestUseRule(private val block: Block) : BlockHandler {
    override fun getKey(): Key {
        return block.key()
    }

    override fun onInteract(interaction: BlockHandler.Interaction): Boolean {
        return interaction.player.isSneaking && !interaction.player.getItemInHand(interaction.hand).isAir
    }
}
// </<@AI_UNREVIEWED>
