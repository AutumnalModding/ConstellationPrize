package xyz.lilyflower.conpri.client.renderer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface Renderer {
    boolean DEBUG_MODE = FabricLoader.getInstance().isDevelopmentEnvironment();

    void render(DrawContext context, RenderTickCounter counter);
    void renderDebug(DrawContext context, RenderTickCounter counter);
}
