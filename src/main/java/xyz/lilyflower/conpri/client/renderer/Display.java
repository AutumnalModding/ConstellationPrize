package xyz.lilyflower.conpri.client.renderer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import org.reflections.Reflections;
import xyz.lilyflower.conpri.client.renderer.module.RendererModule;
import xyz.lilyflower.conpri.init.ConstellationPrize;

public class Display {
    private static final List<RendererModule> MODULES = new ArrayList<>();
    public static final Display INSTANCE = new Display();
    public static boolean GLASSES_EQUIPPED = false;

    public static void init() {
        ConstellationPrize.LOGGER.info("Initializing neural display...");

        Reflections reflections = new Reflections("xyz.lilyflower.conpri.client.renderer.module");
        Set<Class<? extends RendererModule>> modules = reflections.getSubTypesOf(RendererModule.class);

        for (Class<? extends RendererModule> clazz : modules) {
            ConstellationPrize.LOGGER.info("Found diplay module {}", clazz.getSimpleName());
            try {
                Constructor<? extends RendererModule> constructor = clazz.getConstructor();
                RendererModule module = constructor.newInstance();
                MODULES.add(module);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public void run(DrawContext context, RenderTickCounter counter) {
        for (RendererModule module : MODULES) {
            if (GLASSES_EQUIPPED && module.shouldRender()) {
                module.render(context, counter);
            } else if (RendererModule.DEBUG_MODE) {
                module.renderDebug(context, counter);
            }
        }
    }

    public static class Constants {
        public static final int BORDER_PADDING = 8;
        public static final int DIALOGUE_BOX_X = BORDER_PADDING + 4;
        public static final int PORTRAIT_X = DIALOGUE_BOX_X + 4;
        public static final int DIALOGUE_BOX_Y = BORDER_PADDING + 4;
        public static final int PORTRAIT_Y = DIALOGUE_BOX_Y + 4;
        public static final int BORDER_HEIGHT = 80 + BORDER_PADDING;
        public static final int DIALOGUE_BOX_HEIGHT = BORDER_HEIGHT - 4;
        public static final int PORTRAIT_SIZE = 64;
        public static final int BORDER_WIDTH = 420;
        public static final int DIALOGUE_BOX_WIDTH = BORDER_WIDTH - 4;
        public static Window GAME_WINDOW;
        public static InGameHud HUD_INSTANCE;
    }
}
