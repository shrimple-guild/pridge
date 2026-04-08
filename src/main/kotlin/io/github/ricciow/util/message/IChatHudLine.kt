package io.github.ricciow.util.message

//? if < 26.1 {
/*import net.minecraft.client.GuiMessage
*///?} else {
import net.minecraft.client.multiplayer.chat.GuiMessage
//?}

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