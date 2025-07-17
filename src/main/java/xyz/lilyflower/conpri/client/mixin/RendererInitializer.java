package xyz.lilyflower.conpri.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.conpri.client.display.DisplayManager;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;
import xyz.lilyflower.conpri.text.EngineState;

@Mixin(MinecraftClient.class)
public class RendererInitializer {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (DisplayManager.Constants.GAME_WINDOW == null) {
            DisplayManager.Constants.GAME_WINDOW = ConstellationPrizeClient.CLIENT_INSTANCE.getWindow();
            DisplayManager.Constants.HUD_INSTANCE = ConstellationPrizeClient.CLIENT_INSTANCE.inGameHud;

            EngineState.init(ConstellationPrizeClient.CLIENT_INSTANCE.textRenderer);
        }
    }
}
