package io.github.ricciow.format

import io.github.ricciow.Pridge.CONFIG_I
import io.github.ricciow.util.TextParser.parse
import io.github.ricciow.util.message.PagedMessageFactory
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

class FormatResult {
    private var finalText: Component? = null
    var discordText: Boolean
    var botText: Boolean
    var officer: Boolean

    private var disableOutput = false

    constructor(finalText: Component, discordText: Boolean = false, botText: Boolean = false, officer: Boolean = false) {
        this.finalText = finalText
        this.discordText = discordText
        this.botText = botText
        this.officer = officer
    }

    constructor(finalText: String, discordText: Boolean = false, botText: Boolean = false, officer: Boolean = false) : this(
        parse(finalText),
        discordText,
        botText,
        officer
    )

    /**
     * Doesnt modify the message at all
     * @param originalString the original string
     */

    /**
     * Result for a paged message with a singular title
     */
    constructor(
        pages: MutableList<Component>,
        title: Component,
        arrowColor: TextColor,
        disabledArrowColor: TextColor,
        prefix: Component?,
        discordText: Boolean = false,
        botText: Boolean = false,
        officer: Boolean = false
    ) {
        this.discordText = discordText
        this.botText = botText
        this.officer = officer

        val finalPrefix = getPrefix()
        if (prefix != null) {
            finalPrefix.append(" ")
            finalPrefix.append(prefix)
        }

        disableOutput = true
        PagedMessageFactory.createPagedMessage(pages, title, arrowColor, disabledArrowColor, finalPrefix)
    }

    /**
     * Result for a paged message with multiple titles
     */
    constructor(
        pages: MutableList<Component>,
        title: MutableList<Component>,
        arrowColor: TextColor,
        disabledArrowColor: TextColor,
        prefix: Component?,
        discordText: Boolean = false,
        botText: Boolean = false,
        officer: Boolean = false
    ) {
        this.discordText = discordText
        this.botText = botText
        this.officer = officer

        val finalPrefix = getPrefix()
        if (prefix != null) {
            finalPrefix.append(" ")
            finalPrefix.append(prefix)
        }

        disableOutput = true
        PagedMessageFactory.createPagedMessage(pages, title, arrowColor, disabledArrowColor, finalPrefix)
    }


    fun getPrefix(): MutableComponent {
        val prefix = StringBuilder(if (this.officer) {
            CONFIG_I.guildCategory.officerName
        } else {
            CONFIG_I.guildCategory.name
        })

        if (botText) {
            prefix.append(" ").append(CONFIG_I.botCategory.name)
        }
        if (discordText) {
            prefix.append(" ").append(CONFIG_I.discordCategory.representation)
        }

        return parse(prefix.toString())
    }

    fun getText(): Component? {
        if (disableOutput) return null

        val mainText = getPrefix()
        mainText.append(" ")
        if (finalText != null) {
            mainText.append(finalText!!)
        }
        return mainText
    }

    override fun toString(): String {
        if (disableOutput) return "Text output disabled for this result - Probably a paged message"
        return getText()!!.string
    }
}