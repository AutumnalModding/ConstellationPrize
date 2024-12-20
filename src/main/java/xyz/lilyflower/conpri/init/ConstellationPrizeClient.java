package xyz.lilyflower.conpri.init;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import xyz.lilyflower.conpri.client.renderer.Display;

public class ConstellationPrizeClient implements ClientModInitializer {
    public static MinecraftClient CLIENT_INSTANCE;

    @Override
    public void onInitializeClient() {
        CLIENT_INSTANCE = MinecraftClient.getInstance();
        Display.init();

        HudRenderCallback.EVENT.register(Display.INSTANCE::run);
    }
}
