package io.github.ricciow.mixin;

import io.github.ricciow.util.message.IChatHudLine;
import net.minecraft.client.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.class)
public class GuiMessageMixin implements IChatHudLine {
    @Unique
    private int pridge$identifier;

    @Override
    public void pridge$setIdentifier(int identifier) {
        pridge$identifier = identifier;
    }

    @Override
    public int pridge$getIdentifier() {
        return pridge$identifier;
    }
}