package xyz.lilyflower.conpri.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;

@Mixin(MinecraftClient.class)
public class RendererRecomputer {
    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    public void recompute(CallbackInfo ci) {
        if (ConstellationPrizeClient.WINDOW == null) {
            ConstellationPrizeClient.WINDOW = ConstellationPrizeClient.CLIENT_INSTANCE.getWindow();
            ConstellationPrizeClient.TEXT_RENDERER = ConstellationPrizeClient.CLIENT_INSTANCE.textRenderer;
        }
    }
}
