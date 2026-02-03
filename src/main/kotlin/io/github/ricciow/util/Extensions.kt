package io.github.ricciow.util

import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.managed.ManagedConfig
import io.github.ricciow.Pridge.mc
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component

enum class ChatType(val prefix: String) {
    ALL("/ac"),
    PARTY("/pc"),
    GUILD("/gc"),
    PRIVATE("/msg")
}

fun LocalPlayer?.chatHypixel(type: ChatType, message: String?) {
    if (message != null) {
        sendCommand("${type.prefix} $message")
    }
}

fun LocalPlayer?.sendCommand(command: String) {
    this?.connection?.sendCommand(command.substring(1))
}

fun LocalPlayer?.sendMessage(message: String) {
    if (message.startsWith("/")) {
        sendCommand(message)
    } else {
        this?.connection?.sendChat(message)
    }
}

fun <T : Config> ManagedConfig<T>.scheduleConfigOpen() {
    mc.schedule { openConfigGui() }
}

fun String.toText(style: Style = Style.EMPTY): MutableComponent = Component.literal(this).setStyle(style)