package xyz.lilyflower.conpri.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.conpri.client.renderer.NDMM;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;
import xyz.lilyflower.conpri.feature.dialogue.DialogueTextRenderer;

@Mixin(MinecraftClient.class)
public class RendererInitializer {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (NDMM.Constants.GAME_WINDOW == null) {
            NDMM.Constants.GAME_WINDOW = ConstellationPrizeClient.CLIENT_INSTANCE.getWindow();
            NDMM.Constants.HUD_INSTANCE = ConstellationPrizeClient.CLIENT_INSTANCE.inGameHud;

            DialogueTextRenderer.VANILLA_RENDERER = ConstellationPrizeClient.CLIENT_INSTANCE.textRenderer;
        }
    }
}
