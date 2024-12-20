package xyz.lilyflower.conpri.client.renderer.neural;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import xyz.lilyflower.conpri.client.renderer.Renderer;

@SuppressWarnings("JavaReflectionInvocation")
public abstract class DisplayModule implements Renderer {

    abstract boolean shouldRender();

    static {
    }
}
