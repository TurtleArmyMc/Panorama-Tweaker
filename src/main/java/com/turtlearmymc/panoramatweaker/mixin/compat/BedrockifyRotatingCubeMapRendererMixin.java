package com.turtlearmymc.panoramatweaker.mixin.compat;

import com.turtlearmymc.panoramatweaker.PanoramaTweaker;
import me.juancarloscp52.bedrockify.client.features.panoramaBackground.BedrockifyRotatingCubeMapRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BedrockifyRotatingCubeMapRenderer.class)
public abstract class BedrockifyRotatingCubeMapRendererMixin {
    @Shadow
    private CubeMapRenderer cubeMap;

    @Shadow
    private float time;

    @Inject(method = "render(FZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/CubeMapRenderer;draw(Lnet/minecraft/client/MinecraftClient;FFF)V", shift = At.Shift.AFTER), expect = 0)
    protected void render(float alpha, boolean titleScreen, CallbackInfo ci) {
        float drawX = MathHelper
                .sin((time * PanoramaTweaker.config.swaySpeed * 0.001F)
                        - ((float) Math.PI * PanoramaTweaker.config.initialSwayProgress))
                * PanoramaTweaker.config.swayAngle - PanoramaTweaker.config.verticalAngle;
        float drawY = -time * 0.1F * PanoramaTweaker.config.rotationSpeed
                - PanoramaTweaker.config.startingHorizontalAngle;
        cubeMap.draw(MinecraftClient.getInstance(), drawX, drawY, alpha);
    }
}
