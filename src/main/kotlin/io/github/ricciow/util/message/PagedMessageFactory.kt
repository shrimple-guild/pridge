package io.github.ricciow.util.message

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

object PagedMessageFactory {
    private val pagedMessages = mutableMapOf<Int, PagedMessage>()
    fun getMessageById(id: Int): PagedMessage? {
        return pagedMessages[id]
    }

    /**
     * Creates a paged message, only the last sent paged message will be able to change pages.
     */
    fun createPagedMessage(
        pages: MutableList<Component>,
        title: Component,
        arrowColor: TextColor,
        disabledArrowColor: TextColor,
        prefix: Component
    ) {
        val pagedMessage = PagedMessage(pages, title, arrowColor, disabledArrowColor, prefix)
        pagedMessages[pagedMessage.id] = pagedMessage
    }

    fun createPagedMessage(
        pages: MutableList<Component>,
        titles: MutableList<Component>,
        arrowColor: TextColor,
        disabledArrowColor: TextColor,
        prefix: Component
    ) {
        val pagedMessage = PagedMessage(pages, titles, arrowColor, disabledArrowColor, prefix)
        pagedMessages[pagedMessage.id] = pagedMessage
    }
}