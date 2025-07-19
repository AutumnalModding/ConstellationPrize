package xyz.lilyflower.conpri.text.command;

import java.util.Random;
import xyz.lilyflower.conpri.text.EngineState;
import xyz.lilyflower.conpri.text.util.CommandHelper;

public class StackManipulationCommands extends EngineState {
    public static final AbstractEngineCommand REGISTER_LOAD = AbstractEngineCommand.init(AbstractEngineCommand.Type.STACK_MANIPULATION, 0x00, 2, argv -> {
        STACK.add(MEMORY.get(CommandHelper.directCombine(argv)));
    }),

    REGISTER_STORE = AbstractEngineCommand.init(AbstractEngineCommand.Type.STACK_MANIPULATION, 0x01, 3, argv -> {
        MEMORY.put(CommandHelper.directCombine(argv), STACK.get(CommandHelper.stackify(argv[1])));
    }),

    ENABLE_STACK = AbstractEngineCommand.init(AbstractEngineCommand.Type.STACK_MANIPULATION, 0x02, 0, argv -> {
        STACK_ENABLED = true;
    }),

    DISABLE_STACK = AbstractEngineCommand.init(AbstractEngineCommand.Type.STACK_MANIPULATION, 0x03, 0, argv -> {
        STACK_ENABLED = false;
    }),

    CLEAR_STACK = AbstractEngineCommand.init(AbstractEngineCommand.Type.STACK_MANIPULATION, 0x04, 0, argv -> {
        STACK.clear();
    }),

    RNG = AbstractEngineCommand.init(AbstractEngineCommand.Type.STACK_MANIPULATION, 0x05, 0, argv -> {
        STACK.add((char) new Random().nextInt(Character.MIN_VALUE, Character.MAX_VALUE));
    }),

    RNG_MULTIPLE = AbstractEngineCommand.init(AbstractEngineCommand.Type.STACK_MANIPULATION, 0x06, 1, argv -> {
        for (int index = 0; index < CommandHelper.stackify(argv[0]); index++) {
            STACK.add((char) new Random().nextInt(Character.MIN_VALUE, Character.MAX_VALUE));
        }
    });
}
