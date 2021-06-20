@file:JvmName("PlatformInterface")
package io.github.remodstudios.endless

import me.shedaniel.architectury.annotations.ExpectPlatform

@ExpectPlatform
fun printHelloWorld(): Unit = throw AssertionError()