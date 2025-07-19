package xyz.lilyflower.conpri.text.command;

import xyz.lilyflower.conpri.text.EngineState;
import xyz.lilyflower.conpri.text.TextEngine;
import xyz.lilyflower.conpri.text.util.CommandHelper;

import static xyz.lilyflower.conpri.text.command.AbstractEngineCommand.init;

public class StateCommands extends EngineState {
    public static final AbstractEngineCommand WAIT_FOR_INPUT = init(AbstractEngineCommand.Type.STATE, 0x00, 0, argv -> {
        STATUS = TextEngine.Status.WAITING;
    }),

    PAUSE_FOR_FRAMES = init(AbstractEngineCommand.Type.STATE, 0x01,  1, argv -> {
        PAUSE_DURATION_MAX = CommandHelper.stackify(argv[0]);
        STATUS = TextEngine.Status.PAUSED;
    }),

    SET_LINE_SPEED = init(AbstractEngineCommand.Type.STATE, 0x02, 1, argv -> {
        LINE_SPEED = argv[0] == 0x00 ? 0 : argv[0]; // TODO: line speed config
    }),

    HALT = init(AbstractEngineCommand.Type.STATE, 0x03, 0, argv -> {
        STATUS = TextEngine.Status.HALTED;
    });
}
