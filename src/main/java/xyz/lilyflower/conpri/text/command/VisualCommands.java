package xyz.lilyflower.conpri.text.command;

import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.lilyflower.conpri.text.EngineState;
import xyz.lilyflower.conpri.text.util.CommandHelper;

public class VisualCommands extends EngineState {
    public static final AbstractEngineCommand SET_CHARACTER_COLOUR = AbstractEngineCommand.init(AbstractEngineCommand.Type.VISUAL, 0x00, 5, argv -> {
        int red = CommandHelper.stackify(argv[0]);
        int green = CommandHelper.stackify(argv[1]);
        int blue = CommandHelper.stackify(argv[2]);
        int index = argv[3];
        int line = argv[4];

        int colour = red << 16 | green << 8 | blue;
        CHARACTER_COLOURS.put(new ImmutablePair<>(index, line), colour);
    }),

    CLEAR_ALL_COLOURS = AbstractEngineCommand.init(AbstractEngineCommand.Type.VISUAL, 0x01, 0, argv -> {
        CHARACTER_COLOURS.clear();
    });
}
