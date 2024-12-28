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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import xyz.lilyflower.conpri.client.renderer.module.NeuralDisplayModule;
import xyz.lilyflower.conpri.init.ConstellationPrize;

public class NDMM {
    private static final List<NeuralDisplayModule> MODULES = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger("Neural Display Module Manager");

    public static final NDMM INSTANCE = new NDMM();
    public static boolean GLASSES_EQUIPPED = false;

    public static void load() {
        if (MODULES.isEmpty()) {
            LOGGER.info("Initializing...");
        }

        String[] pkg = Thread.currentThread().getStackTrace()[2].getClassName().split("\\.");

        String path = switch (pkg.length) {
            case 2 -> pkg[0] + "." + pkg[1];
            case 3 -> pkg[0] + "." + pkg[1] + "." + pkg[2];
            default -> pkg[0];
        };

        Reflections reflections = new Reflections(path);
        Set<Class<? extends NeuralDisplayModule>> modules = reflections.getSubTypesOf(NeuralDisplayModule.class);

        for (Class<? extends NeuralDisplayModule> clazz : modules) {
            ConstellationPrize.LOGGER.info("Found diplay module {}", clazz.getSimpleName());
            try {
                Constructor<? extends NeuralDisplayModule> constructor = clazz.getConstructor();
                NeuralDisplayModule module = constructor.newInstance();
                MODULES.add(module);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public void run(DrawContext context, RenderTickCounter counter) {
        for (NeuralDisplayModule module : MODULES) {
            if (GLASSES_EQUIPPED && module.shouldRender()) {
                module.render(context, counter);
            } else if (NeuralDisplayModule.DEBUG_MODE) {
                module.renderDebug(context, counter);
            }
        }
    }

    public static class Constants {
        public static Window GAME_WINDOW;
        public static InGameHud HUD_INSTANCE;
    }
}
