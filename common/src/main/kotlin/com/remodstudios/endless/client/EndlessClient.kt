package com.remodstudios.endless.client

import com.remodstudios.endless.blocks.BlockRegistry
import me.shedaniel.architectury.registry.RenderTypes
import net.minecraft.client.render.RenderLayer

object EndlessClient {
    fun init() {
        RenderTypes.register(RenderLayer.getCutout(), BlockRegistry.STATIC_CHARGE.get())
    }
}