package xyz.lilyflower.epsilon.init;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import xyz.lilyflower.epsilon.client.display.DisplayManager;
import xyz.lilyflower.epsilon.text.EngineState;
import xyz.lilyflower.epsilon.text.TextEngine;

@SuppressWarnings("deprecation")
public class EPSILONClient implements ClientModInitializer {
    public static final KeyBinding PROCEED = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.epsilon.input",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_ENTER,
            "category.epsilon.epsilon"
    ));

    public static final KeyBinding DEBUG_TEXT_ENGINE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.epsilon.debug_engine",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_ADD,
            "category.epsilon.epsilon"
    ));

    public static MinecraftClient CLIENT_INSTANCE;

    @Override
    public void onInitializeClient() {
        CLIENT_INSTANCE = MinecraftClient.getInstance();
        DisplayManager.load();
        HudRenderCallback.EVENT.register(DisplayManager.INSTANCE::run);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (PROCEED.wasPressed()) {
                EngineState.stopWaiting();
            }

            if (DEBUG_TEXT_ENGINE.wasPressed()) {
                TextEngine.load(TextEngine.Message.REGISTRY.get((char) 0x00));
            }
        });
    }
}
