package com.turtlearmymc.panoramatweaker.mixin;

import com.turtlearmymc.panoramatweaker.PanoramaTweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.math.MathHelper;

@Mixin(RotatingCubeMapRenderer.class)
public abstract class RotatingCubeMapRendererMixin {
    @Shadow
    protected CubeMapRenderer cubeMap;

    @Shadow
    protected float time;

    @Redirect(method = "render(FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/CubeMapRenderer;draw(Lnet/minecraft/client/MinecraftClient;FFF)V"))
    protected void draw(CubeMapRenderer cubeMap, MinecraftClient client, float x, float y, float alpha) {
        float drawX = MathHelper
                .sin((this.time * PanoramaTweaker.config.swaySpeed * 0.001F)
                        - ((float) Math.PI * PanoramaTweaker.config.initialSwayProgress))
                * PanoramaTweaker.config.swayAngle - PanoramaTweaker.config.verticalAngle;
        float drawY = -this.time * 0.1F * PanoramaTweaker.config.rotationSpeed
                - PanoramaTweaker.config.startingHorizontalAngle;
        this.cubeMap.draw(client, drawX, drawY, alpha);
    }
}
