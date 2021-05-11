@file:JvmName("PlatformInterface")
package com.remodstudios.endless

import me.shedaniel.architectury.annotations.ExpectPlatform

@ExpectPlatform
fun printHelloWorld(): Unit = throw AssertionError()