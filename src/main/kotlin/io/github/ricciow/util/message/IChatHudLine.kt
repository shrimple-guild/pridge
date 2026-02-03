package io.github.ricciow.util.message

import net.minecraft.client.GuiMessage

interface IChatHudLine {
    fun `pridge$getIdentifier`(): Int

    fun `pridge$setIdentifier`(identifier: Int)
}

inline var GuiMessage.pridgeId
    get() = cast().`pridge$getIdentifier`()
    set(value) = cast().`pridge$setIdentifier`(value)

fun GuiMessage.cast(): IChatHudLine {
    return this as IChatHudLine
}