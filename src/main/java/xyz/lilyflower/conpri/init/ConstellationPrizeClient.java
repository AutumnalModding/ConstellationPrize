package xyz.lilyflower.conpri.init;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xyz.lilyflower.conpri.client.display.DisplayManager;
import xyz.lilyflower.conpri.text.EngineState;

@SuppressWarnings("deprecation")
public class ConstellationPrizeClient implements ClientModInitializer {
    private static final KeyBinding INPUT_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.conpri.input",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_ENTER,
            "category.conpri.conpri"
    ));

    public static MinecraftClient CLIENT_INSTANCE;

    @Override
    public void onInitializeClient() {
        CLIENT_INSTANCE = MinecraftClient.getInstance();
        DisplayManager.load();
        HudRenderCallback.EVENT.register(DisplayManager.INSTANCE::run);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (INPUT_KEYBIND.wasPressed()) {
                EngineState.stopWaiting();
            }
        });
    }
}
