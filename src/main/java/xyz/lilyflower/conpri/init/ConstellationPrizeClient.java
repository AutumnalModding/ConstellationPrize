package xyz.lilyflower.conpri.init;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import xyz.lilyflower.conpri.client.renderer.neural.Display;

public class ConstellationPrizeClient implements ClientModInitializer {
    public static MinecraftClient CLIENT_INSTANCE;
    public static Window WINDOW;
    public static int WINDOW_WIDTH = 1280;
    public static int WINDOW_HEIGHT = 1080;
    public static TextRenderer TEXT_RENDERER;

    @Override
    public void onInitializeClient() {
        CLIENT_INSTANCE = MinecraftClient.getInstance();
        Display.init();

        HudRenderCallback.EVENT.register(Display.INSTANCE::run);
    }

    public static void recomputeSize() {
    }
}
