package io.github.ricciow.util

import io.github.ricciow.Pridge.CONFIG_I
import io.github.ricciow.Pridge.mc
import io.github.ricciow.util.message.pridgeId
//? if < 26.1 {
/*import net.minecraft.client.GuiMessage
import net.minecraft.client.GuiMessageTag
*///?} else {
import net.minecraft.client.multiplayer.chat.GuiMessage
import net.minecraft.client.multiplayer.chat.GuiMessageTag
import net.minecraft.client.multiplayer.chat.GuiMessageSource
//?}
import net.minecraft.network.chat.Component
//~if >= 26.2 'gui.chat' -> 'gui.hud.chat' {
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
            //? if < 26.1 {
            /*mc.gui.hud.chat.addMessage(fullText)
            *///?} else {
            mc.gui.hud.chat.addMessage(fullText, null, GuiMessageSource.SYSTEM_CLIENT, null)
            //?}
        }
    }

    private fun replaceInPlace(text: Component, id: Int) {
        var replaced = false

        val messages = mc.gui.hud.chat.allMessages
        for (i in messages.indices) {
            val currentLine = messages[i]
            val lineId = currentLine.pridgeId
            if (lineId > 0 && id == lineId) {
                //? if < 26.1 {
                /*messages[i] = GuiMessage(
                    currentLine.addedTime(),
                    text,
                    null,
                    if (mc.isSingleplayer) GuiMessageTag.systemSinglePlayer() else GuiMessageTag.system()
                ).apply {
                    pridgeId = id
                }
                *///?} else {
                messages[i] = GuiMessage(
                    currentLine.addedTime(),
                    text,
                    null,
                    GuiMessageSource.SYSTEM_CLIENT,
                    //~if >= 26.2 'isSingleplayer' -> 'mc.singleplayerServer?.isPublished ?: false' {
                    if (mc.singleplayerServer?.isPublished ?: false) GuiMessageTag.systemSinglePlayer() else GuiMessageTag.system()
                    //~}
                ).apply {
                    pridgeId = id
                }
                //?}
                replaced = true
            }
        }

        if (replaced) {
            val tempScrolledLines = mc.gui.hud.chat.chatScrollbarPos
            mc.gui.hud.chat.refreshTrimmedMessages()
            mc.gui.hud.chat.chatScrollbarPos = tempScrolledLines
        }
    }

    private fun deletePreviousMessage(id: Int) {
        val initialSize = mc.gui.hud.chat.allMessages.size

        mc.gui.hud.chat.allMessages.removeIf {
            it.pridgeId == id
        }

        if (initialSize != mc.gui.hud.chat.allMessages.size) {
            mc.gui.hud.chat.refreshTrimmedMessages()
        }
    }
}
//~}