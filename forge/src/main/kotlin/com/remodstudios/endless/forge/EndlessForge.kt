package com.remodstudios.endless.forge

import com.remodstudios.endless.Endless
import com.remodstudios.endless.client.EndlessClient
import me.shedaniel.architectury.platform.forge.EventBuses
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Endless.MOD_ID)
object EndlessForge {
    init {
        EventBuses.registerModEventBus(Endless.MOD_ID, MOD_BUS);
        MOD_BUS.addListener(::onClientSetup)

        Endless.init();
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        EndlessClient.init()
    }
}