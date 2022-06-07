package com.turtlearmymc.panoramatweaker.mixin.compat;

import com.turtlearmymc.panoramatweaker.PanoramaTweaker;
import me.juancarloscp52.bedrockify.client.features.panoramaBackground.BedrockifyRotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BedrockifyRotatingCubeMapRenderer.class)
public abstract class BedrockifyRotatingCubeMapRendererMixin {
    @Shadow
    private float time;

    @ModifyArgs(method = "render(FZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/CubeMapRenderer;draw(Lnet/minecraft/client/MinecraftClient;FFF)V", shift = At.Shift.AFTER), expect = 0)
    protected void render(Args args) {
        args.set(1, PanoramaTweaker.config.calcDrawX(time));
        args.set(2, PanoramaTweaker.config.calcDrawY(time));
    }
}
