package io.github.ricciow.mixin;

import io.github.ricciow.Pridge;
import io.github.ricciow.util.PridgeLogger;
import net.minecraft.client.sounds.FiniteAudioStream;
import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import com.mojang.blaze3d.audio.SoundBuffer;
//? if < 1.21.11 {
/*import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.resources.Identifier;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundBufferLibrary.class)
public class SoundBufferLibraryMixin {
    //? if < 1.21.11 {
    /*@Inject(method = "getCompleteBuffer(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    private void onLoadStaticSound(ResourceLocation id, CallbackInfoReturnable<CompletableFuture<SoundBuffer>> cir) {
    *///?} else {
    @Inject(method = "getCompleteBuffer(Lnet/minecraft/resources/Identifier;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    private void onLoadStaticSound(Identifier id, CallbackInfoReturnable<CompletableFuture<SoundBuffer>> cir) {
    //?}
        if (id.getNamespace().equals("dynamicsound")) {
            cir.setReturnValue(CompletableFuture.supplyAsync(() -> {
                Path soundFile = Pridge.INSTANCE.getCONFIG_DIR().resolve(id.getPath());
                try (FileInputStream fish = new FileInputStream(soundFile.toFile())) {
                    try (FiniteAudioStream audioStream = new JOrbisAudioStream(fish)) {
                        return new SoundBuffer(audioStream.readAll(), audioStream.getFormat());
                    }
                } catch (IOException e) {
                    PridgeLogger.INSTANCE.error("Failed to read dynamic sound file: " + soundFile, e, null);
                    return null;
                }
            }));
        }
    }
}