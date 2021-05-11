package com.remodstudios.endless.fabric

import com.remodstudios.endless.Endless
import com.remodstudios.endless.client.EndlessClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer

@Suppress("unused")
object EndlessFabric: ModInitializer {
    override fun onInitialize() {
        Endless.init()
    }
}

@Suppress("unused")
@Environment(EnvType.CLIENT)
object EndlessFabricClient: ClientModInitializer {
    override fun onInitializeClient() {
        EndlessClient.init()
    }
}