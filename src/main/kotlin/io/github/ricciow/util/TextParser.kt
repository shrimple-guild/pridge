package io.github.ricciow.util

import net.minecraft.network.chat.HoverEvent.ShowText
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting

object TextParser {
    /**
     * Parses a string with legacy '&' color codes into a Minecraft Text component.
     * @param text The string to parse.
     * @return A MutableText object with proper formatting.
     */
    fun parse(text: String): MutableComponent {
        val mainText = "".toText()
        val parts = text.split("(?=[&§])".toRegex()) // Split before each '&' but keep it.
        var currentStyle = Style.EMPTY

        for (part in parts) {
            if ((part.startsWith("&") || part.startsWith("§")) && part.length > 1) {
                val formatting = ChatFormatting.getByCode(part[1])
                if (formatting != null) {
                    currentStyle = if (formatting.isColor) {
                        Style.EMPTY.withColor(formatting)
                    } else {
                        currentStyle.applyFormat(formatting)
                    }
                }
                mainText.append(part.substring(2).toText(currentStyle))
            } else {
                mainText.append(part.toText(currentStyle))
            }
        }
        return mainText
    }

    fun parseHoverable(text: String, hover: String): MutableComponent {
        return parse(text).apply {
            style = Style.EMPTY.withHoverEvent(ShowText(parse(hover)))
        }
    }

    fun parseHoverable(text: String, hover: Component): MutableComponent {
        return parse(text).apply {
            style = Style.EMPTY.withHoverEvent(ShowText(hover))
        }
    }
}