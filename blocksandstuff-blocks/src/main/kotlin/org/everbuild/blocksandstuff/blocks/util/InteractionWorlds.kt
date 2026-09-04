package org.everbuild.blocksandstuff.blocks.util

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockHandler

/**
 * The world a block interaction reads and writes. Defaults to the interaction's instance; an app routing
 * interactions through a virtual world installs a resolver returning its own view - a [Block.Getter] that
 * also implements [Block.Setter].
 */
object InteractionWorlds {
    fun interface Resolver {
        fun resolve(interaction: BlockHandler.Interaction): Block.Getter?
    }

    @Volatile
    private var resolver = Resolver { null }

    @JvmStatic
    fun setResolver(r: Resolver) {
        resolver = r
    }

    @JvmStatic
    fun of(interaction: BlockHandler.Interaction): Block.Getter =
        resolver.resolve(interaction) ?: interaction.instance

    @JvmStatic
    fun writer(interaction: BlockHandler.Interaction): Block.Setter {
        val view = resolver.resolve(interaction)
        return if (view is Block.Setter) view else interaction.instance
    }
}
