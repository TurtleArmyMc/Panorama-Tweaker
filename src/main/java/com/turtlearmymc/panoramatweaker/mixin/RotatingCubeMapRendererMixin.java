package com.turtlearmymc.panoramatweaker.mixin;

import com.turtlearmymc.panoramatweaker.PanoramaTweaker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RotatingCubeMapRenderer.class)
public abstract class RotatingCubeMapRendererMixin {
    @Shadow
    private MinecraftClient client;

    private float time;

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui" + "/CubeMapRenderer;draw(Lnet/minecraft/client/MinecraftClient;FFF)V"))
    protected void draw(Args args, DrawContext context, int width, int height, float delta, float alpha) {
        time += (float) ((double) delta * this.client.options.getPanoramaSpeed().getValue());
        args.set(1, PanoramaTweaker.config.calcDrawX(time));
        args.set(2, PanoramaTweaker.config.calcDrawY(time));
    }
}
