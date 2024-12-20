package xyz.lilyflower.conpri.client.renderer.neural;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.reflections.Reflections;
import xyz.lilyflower.conpri.client.renderer.Renderer;
import xyz.lilyflower.conpri.init.ConstellationPrize;

public class Display {
    private static final List<DisplayModule> MODULES = new ArrayList<>();
    public static final Display INSTANCE = new Display();
    public static boolean GLASSES_EQUIPPED = false;

    public static void init() {
        ConstellationPrize.LOGGER.info("Initializing neural display...");

        Reflections reflections = new Reflections("xyz.lilyflower.conpri.client.renderer.neural");
        Set<Class<? extends DisplayModule>> modules = reflections.getSubTypesOf(DisplayModule.class);

        for (Class<? extends DisplayModule> clazz : modules) {
            ConstellationPrize.LOGGER.info("Found diplay module {}", clazz.getSimpleName());
            try {
                Constructor<? extends DisplayModule> constructor = clazz.getConstructor();
                DisplayModule module = constructor.newInstance();
                MODULES.add(module);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public void run(DrawContext context, RenderTickCounter counter) {
        for (DisplayModule module : MODULES) {
            if (GLASSES_EQUIPPED && module.shouldRender()) {
                module.render(context, counter);
            } else if (Renderer.DEBUG_MODE) {
                module.renderDebug(context, counter);
            }
        }
    }

    public static class Constants {
        static final int BORDER_PADDING = 8;
        static final int DIALOGUE_BOX_X = BORDER_PADDING + 4;
        static final int PORTRAIT_X = DIALOGUE_BOX_X + 4;
        static final int DIALOGUE_BOX_Y = BORDER_PADDING + 4;
        static final int PORTRAIT_Y = DIALOGUE_BOX_Y + 4;
        static final int BORDER_HEIGHT = 80 + BORDER_PADDING;
        static final int DIALOGUE_BOX_HEIGHT = BORDER_HEIGHT - 4;
        static final int PORTRAIT_SIZE = 64;
        static final int BORDER_WIDTH = 420;
        static final int DIALOGUE_BOX_WIDTH = BORDER_WIDTH - 4;
        static float TICK_DELTA;
    }
}
