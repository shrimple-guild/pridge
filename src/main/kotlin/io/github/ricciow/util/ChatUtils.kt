package io.github.ricciow.util

import io.github.ricciow.Pridge.CONFIG_I
import io.github.ricciow.Pridge.mc
import io.github.ricciow.util.message.pridgeId
import net.minecraft.client.GuiMessage
import net.minecraft.client.GuiMessageTag
import net.minecraft.network.chat.Component

object ChatUtils {
    var nextMessageId = -1

    fun info(string: String, id: Int = 0) = sendMessage(CONFIG_I.guildCategory.name.toText().append(string), id)

    fun info(text: Component, id: Int = 0) = sendMessage(CONFIG_I.guildCategory.name.toText().append(text), id)

    fun sendMessage(string: String?, id: Int = 0) = sendMessage(string?.toText(), id)

    fun sendMessage(text: Component?, id: Int = 0, replaceInPlace: Boolean = false) {
        var fullText = text ?: return

        if (CONFIG_I.developerCategory.devEnabled) {
            fullText = "{$id}".toText().append(text)
        }

        mc.executeIfPossible {
            if (id != 0) {
                if (replaceInPlace) {
                    replaceInPlace(fullText, id)
                    return@executeIfPossible
                } else {
                    deletePreviousMessage(id)
                }
            }
            nextMessageId = id
            mc.gui.chat.addMessage(fullText)
        }
    }

    private fun replaceInPlace(text: Component, id: Int) {
        var replaced = false

        val messages = mc.gui.chat.allMessages
        for (i in messages.indices) {
            val currentLine = messages[i]
            val lineId = currentLine.pridgeId
            if (lineId > 0 && id == lineId) {
                messages[i] = GuiMessage(
                    currentLine.addedTime(),
                    text,
                    null,
                    if (mc.isSingleplayer) GuiMessageTag.systemSinglePlayer() else GuiMessageTag.system()
                ).apply {
                    pridgeId = id
                }
                replaced = true
            }
        }

        if (replaced) {
            val tempScrolledLines = mc.gui.chat.chatScrollbarPos
            mc.gui.chat.refreshTrimmedMessages()
            mc.gui.chat.chatScrollbarPos = tempScrolledLines
        }
    }

    private fun deletePreviousMessage(id: Int) {
        val initialSize = mc.gui.chat.allMessages.size

        mc.gui.chat.allMessages.removeIf {
            it.pridgeId == id
        }

        if (initialSize != mc.gui.chat.allMessages.size) {
            mc.gui.chat.refreshTrimmedMessages()
        }
    }
}