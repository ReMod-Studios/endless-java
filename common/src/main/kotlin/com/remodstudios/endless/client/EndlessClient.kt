package com.remodstudios.endless.client

import com.remodstudios.endless.blocks.EndlessBlocks
import me.shedaniel.architectury.registry.RenderTypes
import net.minecraft.client.render.RenderLayer

object EndlessClient {
    fun init() {
        RenderTypes.register(RenderLayer.getCutout(),
            EndlessBlocks.STATIC_CHARGE, EndlessBlocks.COBALT_REPEL)
    }
}