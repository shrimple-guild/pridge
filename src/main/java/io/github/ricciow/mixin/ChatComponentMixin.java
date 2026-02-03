package io.github.ricciow.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.ricciow.util.ChatUtils;
import io.github.ricciow.util.message.IChatHudLineKt;
import io.github.ricciow.util.message.IdentifiableChatHud;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin implements IdentifiableChatHud {
    @Inject(
            method = "addMessageToQueue(Lnet/minecraft/client/GuiMessage;)V",
            at = @At("HEAD")
    )
    private void onAddNewChatHudLine(GuiMessage line, CallbackInfo ci) {
        var id = ChatUtils.INSTANCE.getNextMessageId();

        if (id != 0) {
            IChatHudLineKt.cast(line).pridge$setIdentifier(id);
            ChatUtils.INSTANCE.setNextMessageId(-1);
        }
    }

    @Inject(
            method = "addMessageToQueue(Lnet/minecraft/client/GuiMessage;)V",
            at = @At("TAIL")
    )
    private void onTail(GuiMessage msg, CallbackInfo ci) {
        ChatUtils.INSTANCE.setNextMessageId(-1);
    }

    @Inject(
            method = "refreshTrimmedMessages",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessageToDisplayQueue(Lnet/minecraft/client/GuiMessage;)V"
            )
    )
    private void onRefresh(CallbackInfo ci, @Local GuiMessage line) {
        var id = IChatHudLineKt.cast(line).pridge$getIdentifier();

        if (id != 0) {
            ChatUtils.INSTANCE.setNextMessageId(id);
        }
    }
}
