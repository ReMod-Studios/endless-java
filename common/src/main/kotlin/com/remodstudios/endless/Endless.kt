package com.remodstudios.endless

import net.minecraft.util.Identifier

object Endless {
    const val MOD_ID = "endless"

    fun init() {
        printHelloWorld()
    }

    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }
}