package io.github.remodstudios.endless.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class CobaltRepelBlock(settings: Settings) : Block(settings) {
    private val shape = VoxelShapes.union(
        createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0),  // jar body
        createCuboidShape(7.0, 8.0, 7.0, 9.0, 10.0, 9.0),   // jar neck
        createCuboidShape(6.0, 10.0, 6.0, 10.0, 13.0, 10.0) // cork
    )

    override fun getOutlineShape(
        blockState: BlockState?,
        blockView: BlockView?,
        blockPos: BlockPos?,
        shapeContext: ShapeContext?
    ): VoxelShape {
        return shape
    }
}
