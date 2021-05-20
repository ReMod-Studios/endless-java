package com.remodstudios.endless.blocks

import com.remodstudios.endless.ModDamageSource
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class StaticChargeBlock(settings: Settings): Block(settings) {
    private val damageSource: DamageSource = ModDamageSource("shock").setUnblockable()
    private val shape: VoxelShape = createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)

    override fun onEntityCollision(blockState: BlockState?, world: World?, blockPos: BlockPos?, entity: Entity?) {
        if (world == null || blockPos == null || entity == null || world.isClient())
            return
        world.setBlockState(blockPos, Blocks.AIR.defaultState, 3)
        world.playSound(
            blockPos.x.toDouble(),
            blockPos.y.toDouble(),
            blockPos.z.toDouble(),
            SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
            SoundCategory.BLOCKS,
            1F,
            1F,
            true
        )
        // TODO particle effect
        entity.damage(damageSource, 12F)
        if (entity is LivingEntity)
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 100, 5))
    }

    override fun getOutlineShape(
        blockState: BlockState?,
        blockView: BlockView?,
        blockPos: BlockPos?,
        shapeContext: ShapeContext?
    ): VoxelShape {
        return shape
    }

    override fun getStateForNeighborUpdate(
        blockState: BlockState,
        direction: Direction?,
        blockState2: BlockState?,
        worldAccess: WorldAccess?,
        blockPos: BlockPos?,
        blockPos2: BlockPos?
    ): BlockState? {
        return if (!blockState.canPlaceAt(
                worldAccess,
                blockPos
            )
        ) Blocks.AIR.defaultState else super.getStateForNeighborUpdate(
            blockState,
            direction,
            blockState2,
            worldAccess,
            blockPos,
            blockPos2
        )
    }

    override fun canPlaceAt(blockState: BlockState?, worldView: WorldView, blockPos: BlockPos): Boolean {
        return isValidPlace(worldView, blockPos)
    }

    override fun canReplace(blockState: BlockState?, itemPlacementContext: ItemPlacementContext?): Boolean {
        return true
    }

    companion object {
        @JvmStatic
        fun isValidPlace(worldView: WorldView, blockPos: BlockPos): Boolean {
            return worldView.getBlockState(blockPos.down()).isFullCube(worldView, blockPos)
        }
    }
}