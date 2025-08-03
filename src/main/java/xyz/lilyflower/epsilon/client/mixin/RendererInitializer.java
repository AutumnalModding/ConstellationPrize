package xyz.lilyflower.epsilon.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.epsilon.client.display.DisplayManager;
import xyz.lilyflower.epsilon.init.EPSILONClient;
import xyz.lilyflower.epsilon.text.EngineState;

@Mixin(MinecraftClient.class)
public class RendererInitializer {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (DisplayManager.Constants.GAME_WINDOW == null) {
            DisplayManager.Constants.GAME_WINDOW = EPSILONClient.CLIENT_INSTANCE.getWindow();
            DisplayManager.Constants.HUD_INSTANCE = EPSILONClient.CLIENT_INSTANCE.inGameHud;

            EngineState.setRenderer(EPSILONClient.CLIENT_INSTANCE.textRenderer);
        }
    }
}
