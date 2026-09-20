package org.everbuild.blocksandstuff.blocks.placement

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.instance.block.rule.BlockPlacementRule.PlacementState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * The pair a placement forms, against vanilla ChestBlock: a same-facing single chest CLOCKWISE of the new one
 * makes the new one LEFT, counter-clockwise makes it RIGHT (getChestType), and the partner takes the other half.
 */
internal class ChestPlacementRuleTest {

    /** A world of one block, enough for the rule's neighbour reads and its one setBlock. */
    private class OneBlockWorld(private val at: Point, private var block: Block) : Block.Getter, Block.Setter {
        val writes = mutableMapOf<Point, Block>()
        override fun getBlock(x: Int, y: Int, z: Int, condition: Block.Getter.Condition): Block =
            if (Vec(x.toDouble(), y.toDouble(), z.toDouble()).sameBlock(at)) block else Block.AIR
        override fun setBlock(x: Int, y: Int, z: Int, block: Block) {
            writes[Vec(x.toDouble(), y.toDouble(), z.toDouble())] = block
        }
    }

    /** Places a chest at origin while [neighbour] stands at [neighbourAt]; returns placed state to partner state. */
    private fun place(lookFacing: String, neighbourAt: Point, neighbour: Block?): Pair<Block, Block?> {
        val origin = Vec(0.0, 0.0, 0.0)
        val world = OneBlockWorld(neighbourAt, neighbour ?: Block.AIR)
        // a player looking so that the chest faces `lookFacing`: the rule reads the yaw, so aim the opposite way
        val yaw = when (lookFacing) {
            "north" -> 0f   // looking south (+z), chest faces north
            "south" -> 180f
            "west" -> 270f
            else -> 90f     // east
        }
        val placed = ChestPlacementRule(Block.CHEST).blockPlace(
            PlacementState(world, Block.CHEST, BlockFace.TOP, origin, Vec(0.5, 0.0, 0.5),
                Pos(0.0, 0.0, 0.0, yaw, 0f), null, false)
        )!!
        return placed to world.writes[neighbourAt]
    }

    @Test
    fun aClockwisePartnerMakesTheNewChestLeft() {
        // facing north: clockwise is east
        val (placed, partner) = place("north", Vec(1.0, 0.0, 0.0), Block.CHEST.withProperty("facing", "north"))
        assertEquals("north", placed.getProperty("facing"))
        assertEquals("left", placed.getProperty("type"), "vanilla getChestType: partner clockwise -> LEFT")
        assertEquals("right", partner?.getProperty("type"), "and the partner becomes the other half")
    }

    @Test
    fun aCounterClockwisePartnerMakesTheNewChestRight() {
        // facing north: counter-clockwise is west
        val (placed, partner) = place("north", Vec(-1.0, 0.0, 0.0), Block.CHEST.withProperty("facing", "north"))
        assertEquals("right", placed.getProperty("type"), "vanilla getChestType: partner counter-clockwise -> RIGHT")
        assertEquals("left", partner?.getProperty("type"))
    }

    @Test
    fun aChestFacingAnotherWayStaysSingle() {
        val (placed, partner) = place("north", Vec(1.0, 0.0, 0.0), Block.CHEST.withProperty("facing", "east"))
        assertEquals("single", placed.getProperty("type"), "vanilla pairs only with a SAME-facing single")
        assertEquals(null, partner)
    }

    @Test
    fun aPartnerAlongTheFacingAxisIsNoPair() {
        // facing north, neighbour to the north: vanilla never pairs along the facing axis
        val (placed, partner) = place("north", Vec(0.0, 0.0, -1.0), Block.CHEST.withProperty("facing", "north"))
        assertEquals("single", placed.getProperty("type"))
        assertEquals(null, partner)
    }
}
