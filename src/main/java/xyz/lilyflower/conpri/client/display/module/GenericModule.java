package xyz.lilyflower.conpri.client.display.module;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface GenericModule {
    boolean DEBUG_MODE = FabricLoader.getInstance().isDevelopmentEnvironment();

    boolean shouldRender();
    void render(DrawContext context, RenderTickCounter counter);
    void renderDebug(DrawContext context, RenderTickCounter counter);
}
