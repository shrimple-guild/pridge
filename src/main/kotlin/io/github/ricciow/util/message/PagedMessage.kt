package io.github.ricciow.util.message

import io.github.ricciow.util.toText
import net.minecraft.network.chat.ClickEvent.RunCommand
import net.minecraft.network.chat.HoverEvent.ShowText
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextColor

class PagedMessage {
    val id: Int
    private val prefix: Component
    private val pages: MutableList<Component>
    private val titles: MutableList<Component>
    private val arrowColor: TextColor
    private val disabledArrowColor: TextColor
    private var pageIndex = 0
    private val message: ModifiableMessage

    // Single title constructor
    internal constructor(
        pages: MutableList<Component>,
        title: Component,
        arrowColor: TextColor,
        disabledArrowColor: TextColor,
        prefix: Component
    ) {
        this.pages = pages
        this.titles = mutableListOf(title)
        this.arrowColor = arrowColor
        this.disabledArrowColor = disabledArrowColor
        this.prefix = prefix

        this.id = hashCode()
        this.message = ModifiableMessage(buildText(), id)
    }

    // Multi-title constructor
    internal constructor(
        pages: MutableList<Component>,
        titles: MutableList<Component>,
        arrowColor: TextColor,
        disabledArrowColor: TextColor,
        prefix: Component
    ) {
        this.pages = pages
        this.titles = titles
        this.arrowColor = arrowColor
        this.disabledArrowColor = disabledArrowColor
        this.prefix = prefix

        this.id = hashCode()
        this.message = ModifiableMessage(buildText(), id)
    }

    private fun buildText(): Component {
        var title: Component? = null
        if (pageIndex in 0 until pages.size) {
            title = titles.getOrNull(pageIndex) ?: titles.firstOrNull() ?: "No title found".toText()
        }

        val baseText = prefix.copy()
        baseText.append("<< ".toText(buildLeftStyle()))
        if (title != null) {
            baseText.append(title)
        }
        baseText.append(" >>".toText(buildRightStyle()))
        baseText.append("\n")
        baseText.append(pages[pageIndex])

        return baseText
    }

    private fun buildLeftStyle(): Style {
        val baseStyle = Style.EMPTY.withColor(disabledArrowColor)

        return if (pageIndex != 0) {
            baseStyle.withColor(arrowColor)
                .withClickEvent(RunCommand("pagedmessage $id left"))
                .withHoverEvent(ShowText("Previous page".toText()))
        } else {
            baseStyle.withHoverEvent(ShowText("No pages to the Left!".toText()))
        }
    }

    private fun buildRightStyle(): Style {
        val baseStyle = Style.EMPTY.withColor(disabledArrowColor)

        if (pageIndex < pages.size - 1) {
            return baseStyle
                .withColor(arrowColor)
                .withClickEvent(RunCommand("pagedmessage $id right"))
                .withHoverEvent(ShowText("Next page".toText()))
        }

        return baseStyle.withHoverEvent(ShowText("No pages to the Right!".toText()))
    }

    fun setPage(page: Int) {
        if (page in 0 until pages.size) {
            pageIndex = page
            message.modify(buildText())
        }
    }

    fun nextPage() {
        if (pageIndex < pages.size - 1) {
            setPage(pageIndex + 1)
        }
    }

    fun previousPage() {
        if (pageIndex > 0) {
            setPage(pageIndex - 1)
        }
    }

    override fun toString(): String {
        val titleAndPageList = titles.mapIndexed { index, text ->
            """
            {
                Title ${index + 1}: ${text.string}
                Page ${index + 1}: ${pages.getOrNull(index)?.string ?: "No page available"}
            }
            """.trimIndent()
        }


        val toString =
        """
        PagedMessage: {
            ID: $id
            Prefix: ${prefix.string}
            Pages: {
                ${titleAndPageList.joinToString("\n")}
            }
            Current Page Index: $pageIndex
            Arrow Color: ${arrowColor.serialize()}
            Disabled Arrow Color: ${disabledArrowColor.serialize()}
        }
        """.trimIndent()
        return toString
    }
}