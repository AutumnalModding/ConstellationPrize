package xyz.lilyflower.conpri.init;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import xyz.lilyflower.conpri.client.renderer.NDMM;

public class ConstellationPrizeClient implements ClientModInitializer {
    public static MinecraftClient CLIENT_INSTANCE;

    @Override
    public void onInitializeClient() {
        CLIENT_INSTANCE = MinecraftClient.getInstance();
        NDMM.load();
        HudRenderCallback.EVENT.register(NDMM.INSTANCE::run);
    }
}
