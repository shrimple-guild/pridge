package io.github.ricciow.util.message

import io.github.ricciow.util.ChatUtils
import net.minecraft.network.chat.Component

class ModifiableMessage(private var text: Component, private val id: Int) {

    init {
        updateMessage(false)
    }

    fun modify(text: Component) {
        this.text = text
        updateMessage(true)
    }

    private fun updateMessage(replaceExisting: Boolean) {
        ChatUtils.sendMessage(text, id, replaceExisting)
    }
}