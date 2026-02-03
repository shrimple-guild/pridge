package io.github.ricciow.util.message

import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.network.chat.Component

interface IdentifiableChatHud {
    fun `pridge$addIdentifiableMessage`(id: Int, message: Component)

    fun `pridge$removeIdentifiableMessage`(id: Int)

    fun `pridge$replaceIdentifiableMessage`(id: Int, message: Component)
}

fun ChatComponent.addIdentifiableMessage(id: Int, message: Component) {
    return (this as IdentifiableChatHud).`pridge$addIdentifiableMessage`(id, message)
}

fun ChatComponent.removeIdentifiableMessage(id: Int) {
    return (this as IdentifiableChatHud).`pridge$removeIdentifiableMessage`(id)
}

fun ChatComponent.replaceIdentifiableMessage(id: Int, message: Component) {
    return (this as IdentifiableChatHud).`pridge$replaceIdentifiableMessage`(id, message)
}
