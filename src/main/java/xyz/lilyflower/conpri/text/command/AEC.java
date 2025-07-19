package xyz.lilyflower.conpri.text.command;

import java.util.HashMap;
import xyz.lilyflower.conpri.text.command.type.EventFlagCommands;
import xyz.lilyflower.conpri.text.command.type.SpecialCommands;
import xyz.lilyflower.conpri.text.command.type.StackManipulationCommands;
import xyz.lilyflower.conpri.text.command.type.StateCommands;
import xyz.lilyflower.conpri.text.command.type.VisualCommands;

/**
 * Abstract Engine Command
 */
public abstract class AEC {
    public final int argc;
    public final Executor executor;
    private static final HashMap<Type, HashMap<Character, AEC>> REGISTRY = new HashMap<>();

    AEC(Type type, int header, int argc, Executor executor) {
        this.argc = argc;
        this.executor = executor;
        HashMap<Character, AEC> commands = REGISTRY.get(type);
        commands.put((char) header, this);
        REGISTRY.put(type, commands);
    }

    public static AEC init(Type type, int header, int argc, Executor executor) {
        return new AEC(type, header, argc, executor) {};
    }

    public static AEC get(Type type, char lower) {
        AEC command = REGISTRY.get(type).get(lower);
//        System.out.println(command instanceof VPC);
        return command;
    }

    @FunctionalInterface
    public interface Executor {
        void run(char... argv);
    }

    public enum Type {
        STATE,
        VISUAL,
        STACK_MANIPULATION,
        EVENT_FLAG,
        CONDITIONAL,
        SPECIAL
    }

    static {
        for (Type type : Type.values()) {
            REGISTRY.put(type, new HashMap<>());
        }

        new StateCommands();
        new VisualCommands();
        new StackManipulationCommands();
        new EventFlagCommands();

        new SpecialCommands();
    }
}
