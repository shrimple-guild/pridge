package io.github.ricciow.rendering

import io.github.ricciow.Pridge.mc
import io.github.ricciow.util.PridgeLogger
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.DeltaTracker
import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.renderer.texture.DynamicTexture
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.ClickEvent.OpenUrl
import net.minecraft.resources.ResourceLocation
import org.apache.commons.io.IOUtils
import org.lwjgl.glfw.GLFW
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

class ImagePreviewRenderer {

    private var currentUrl: String? = null
    private var loadingUrl: String? = null

    private var imageWidth = 100
    private var imageHeight = 100
    private var hasTexture = false

    fun onHudRender(drawContext: GuiGraphics, tickCounter: DeltaTracker) {
        if (!mc.gui.chat.isChatFocused) {
            if (this.hasTexture) {
                clearTexture()
            }
            return
        }

        val mouseX = mc.mouseHandler.xpos() * mc.window.guiScaledWidth.toDouble() / mc.window.screenWidth
        val mouseY = mc.mouseHandler.ypos() * mc.window.guiScaledHeight.toDouble() / mc.window.screenHeight
        val style = mc.gui.chat.getClickedComponentStyleAt(mouseX, mouseY)

        var url: String? = null
        if (style != null) {
            val clickEvent = style.getClickEvent()
            if (clickEvent is OpenUrl) {
                url = clickEvent.uri().toString()
            }
        }

        handleUrl(url)

        if (this.hasTexture) {
            renderPreview(drawContext)
        }
    }

    private fun handleUrl(url: String?) {
        var url = url
        if (url == null) {
            if (this.currentUrl != null) {
                this.currentUrl = null
                clearTexture()
            }
            return
        }
        if (!url.startsWith("http")) {
            return
        }
        if (url.contains("imgur.com/") && !url.contains("i.imgur")) {
            url = "https://i.imgur.com/${url.split("/").last()}.png"
        }
        if (url == this.currentUrl || url == this.loadingUrl) {
            return
        }
        this.loadingUrl = url
        this.currentUrl = url
        clearTexture()
        CompletableFuture.runAsync { loadImage(url) }
    }

    private fun loadImage(url: String) {
        var connection: HttpURLConnection? = null

        try {
            val uri = URI(url)
            val urlConnection = uri.toURL().openConnection() as HttpURLConnection
            connection = urlConnection.apply {
                setRequestProperty("User-Agent", "Pridge Image Previewer/1.0")
                instanceFollowRedirects = true
                connectTimeout = 5000
                readTimeout = 5000
            }

            connection.inputStream.use { stream ->
                val contentType = connection.getHeaderField("Content-Type").orEmpty()

                if ("text/html" in contentType) {
                    val body = IOUtils.toString(stream, StandardCharsets.UTF_8)
                    val imageURL = extractImageUrl(body, url)
                    if (imageURL != null) {
                        loadImage(imageURL)
                        return
                    }
                }

                val image = NativeImage.read(stream)
                mc.execute {
                    if (url == this.loadingUrl) {
                        registerTexture(image)
                        this.loadingUrl = null
                    } else {
                        image.close()
                    }
                }
            }
        } catch (e: Exception) {
            PridgeLogger.error("Failed to load image preview from $url: ${e.message}", e)
            if (url == this.loadingUrl) {
                this.loadingUrl = null
            }
        } finally {
            connection?.disconnect()
        }
    }

    private fun extractImageUrl(html: String, baseUrl: String): String? {
        val matcher = OGP_IMAGE_REGEX.matcher(html)
        val imageUrl = when {
            matcher.find() -> matcher.group("url")
            IMG_TAG_REGEX.matcher(html).find() -> IMG_TAG_REGEX.matcher(html).run {
                find()
                group("url")
            }

            else -> null
        } ?: return null

        return if (imageUrl.startsWith("/")) {
            val baseUri = URI.create(baseUrl)
            "${baseUri.scheme}://${baseUri.host}$imageUrl"
        } else imageUrl
    }


    private fun registerTexture(image: NativeImage) {
        this.imageWidth = image.width
        this.imageHeight = image.height
        val texture = DynamicTexture({ PREVIEW_TEXTURE_ID.toString() }, image)
        mc.textureManager.register(PREVIEW_TEXTURE_ID, texture)
        this.hasTexture = true
    }

    private fun clearTexture() {
        if (this.hasTexture) {
            this.hasTexture = false
            mc.execute {
                if (mc.textureManager.getTexture(PREVIEW_TEXTURE_ID) != null) {
                    mc.textureManager.release(PREVIEW_TEXTURE_ID)
                }
            }
        }
    }

    private fun renderPreview(drawContext: GuiGraphics) {
        val screenWidth = mc.window.guiScaledWidth
        val screenHeight = mc.window.guiScaledHeight
        val aspectRatio = imageWidth.toFloat() / imageHeight
        var desiredWidth = screenWidth * 0.5f

        val handle = mc.window
        if (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT) &&
            InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL)
        ) {
            desiredWidth = screenWidth * 0.75f
        } else {
            if (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT)) {
                desiredWidth = screenWidth * 0.25f
            }

            if (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL)) {
                desiredWidth = this.imageWidth.toFloat()
            }
        }

        var finalWidth = desiredWidth
        var finalHeight = finalWidth / aspectRatio
        if (finalHeight > screenHeight) {
            finalHeight = screenHeight.toFloat()
            finalWidth = finalHeight * aspectRatio
        }
        val intWidth = finalWidth.toInt()
        val intHeight = finalHeight.toInt()
        drawContext.blit(
            RenderPipelines.GUI_TEXTURED,
            PREVIEW_TEXTURE_ID,
            0, 0,
            0.0f, 0.0f,
            intWidth, intHeight,
            intWidth, intHeight
        )
    }

    companion object {
        private val OGP_IMAGE_REGEX =
            Pattern.compile("<meta property=\"(?:og:image|twitter:image)\" content=\"(?<url>.+?)\".*?/?>")
        private val IMG_TAG_REGEX = Pattern.compile("<img.*?src=\"(?<url>.+?)\".*?>")
        private val PREVIEW_TEXTURE_ID = ResourceLocation.fromNamespaceAndPath("image_preview", "preview_texture")
    }
}