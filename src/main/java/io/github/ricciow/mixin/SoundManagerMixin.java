package io.github.ricciow.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
//? if < 1.21.11 {
/*import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.resources.Identifier;
//?}
import net.minecraft.util.valueproviders.ConstantFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Unique
    private static final String DYNAMIC_SOUND_NAMESPACE = "dynamicsound";

    @Inject(method = "getSoundEvent", at = @At("HEAD"), cancellable = true)
    //? if < 1.21.11 {
    /*private void onGetSound(ResourceLocation id, CallbackInfoReturnable<WeighedSoundEvents> cir) {
    *///?} else {
    private void onGetSound(Identifier id, CallbackInfoReturnable<WeighedSoundEvents> cir) {
    //?}
        if (!id.getNamespace().equals(DYNAMIC_SOUND_NAMESPACE)) return;

        WeighedSoundEvents soundSet = new WeighedSoundEvents(id, null);
        soundSet.addSound(new Sound(
                id,
                ConstantFloat.of(1.0F),
                ConstantFloat.of(1.0F),
                1,
                Sound.Type.FILE,
                false,
                false,
                16
        ));

        cir.setReturnValue(soundSet);
    }
}