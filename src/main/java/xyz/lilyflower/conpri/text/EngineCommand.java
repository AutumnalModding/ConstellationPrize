package xyz.lilyflower.conpri.text;

import java.util.Random;
import org.apache.commons.lang3.tuple.ImmutablePair;
import static xyz.lilyflower.conpri.text.EngineState.*;

enum EngineCommand {
    WAIT_FOR_INPUT(new char[]{0x01, 0x00}, 0, argv -> STATUS = TextEngine.Status.WAITING),

    PAUSE_FOR_FRAMES(new char[]{0x01, 0x01}, 1, argv -> {
        PAUSE_DURATION_MAX = stackify(argv[0]);
        STATUS = TextEngine.Status.PAUSED;
    }),

    SET_LINE_SPEED(new char[]{0x01, 0x02}, 1, argv -> {
        LINE_SPEED = argv[0] == 0x00 ? 0 : argv[0]; // TODO: line speed config
    }),

    HALT(new char[]{0x01, 0x03}, 0, argv -> {
        STATUS = TextEngine.Status.HALTED;
    }),

    SET_COLOUR(new char[]{0x02, 0x00}, 5, argv -> {
        int red = stackify(argv[0]);
        int green = stackify(argv[1]);
        int blue = stackify(argv[2]);
        int index = argv[3];
        int line = argv[4];

        int colour = red << 16 | green << 8 | blue;
        CHARACTER_COLOURS.put(new ImmutablePair<>(index, line), colour);
    }),

    CLEAR_COLOURS(new char[]{0x02, 0x01}, 0, argv -> {
        CHARACTER_COLOURS.clear();
    }),

    REGISTER_LOAD(new char[]{0x03, 0x00}, 1, argv -> {
        STACK.add(MEMORY.get(stackify(argv[0])));
    }),

    REGISTER_STORE(new char[]{0x03, 0x01}, 2,  argv -> {
        MEMORY.put(stackify(argv[0]), STACK.get(stackify(argv[1])));
    }),

    ENABLE_STACK(new char[]{0x03, 0x02}, 0, argv -> {
        STACK_ENABLED = true;
    }),

    DISABLE_STACK(new char[]{0x03, 0x03}, 0, argv -> {
        STACK_ENABLED = false;
    }),

    CLEAR_STACK(new char[]{0x03, 0x04}, 0, argv -> {
        STACK.clear();
    }),

    RNG(new char[]{0x03, 0x05}, 0, argv -> {
        STACK.add((char) new Random().nextInt(Character.MIN_VALUE, Character.MAX_VALUE));
    }),

    RNG_MULTIPLE(new char[]{0x03, 0x06}, 1, argv -> {
        for (int index = 0; index < stackify(argv[0]); index++) {
            STACK.add((char) new Random().nextInt(Character.MIN_VALUE, Character.MAX_VALUE));
        }
    })

    ;

    final char[] header;
    final int argc;
    final Executor executor;

    EngineCommand(char[] header, int argc, Executor executor) {
        this.header = header;
        this.argc = argc;
        this.executor = executor;
    }

    static char stackify (char input) {
        return input == 0 && STACK_ENABLED ? STACK.removeFirst() : input;
    }

    @FunctionalInterface
    public interface Executor {
        void run(char... argv);
    }
}
