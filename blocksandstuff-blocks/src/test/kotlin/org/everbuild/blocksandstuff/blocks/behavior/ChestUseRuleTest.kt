package org.everbuild.blocksandstuff.blocks.behavior

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerHand
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

// <@AI_UNREVIEWED>
internal class ChestUseRuleTest {
    private val rule = ChestUseRule(Block.CHEST)

    @BeforeEach
    fun setUp() {
        MinecraftServer.init()
    }

    @Test
    fun standingClickIsAUse() {
        Assertions.assertFalse(rule.onInteract(click(sneaking = false, held = ItemStack.of(Material.CHEST))))
    }

    @Test
    fun sneakingClickPlaces() {
        Assertions.assertTrue(rule.onInteract(click(sneaking = true, held = ItemStack.of(Material.CHEST))))
    }

    @Test
    fun emptyHandNeverPlaces() {
        Assertions.assertFalse(rule.onInteract(click(sneaking = true, held = ItemStack.AIR)))
    }

    private fun click(sneaking: Boolean, held: ItemStack): BlockHandler.Interaction {
        val player = mock<Player> {
            on { isSneaking } doReturn sneaking
            on { getItemInHand(PlayerHand.MAIN) } doReturn held
        }
        return BlockHandler.Interaction(Block.CHEST, mock<Instance>(), BlockFace.TOP, Pos.ZERO, Pos.ZERO, player, PlayerHand.MAIN)
    }
}
// </<@AI_UNREVIEWED>
