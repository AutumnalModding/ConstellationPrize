package xyz.lilyflower.conpri.text.command;

import xyz.lilyflower.conpri.text.EngineState;
import xyz.lilyflower.conpri.text.TextEngine;
import xyz.lilyflower.conpri.text.util.CommandHelper;

import static xyz.lilyflower.conpri.text.command.AEC.init;

@SuppressWarnings("unused")
public class StateCommands extends EngineState {
    public static final AEC WAIT_FOR_INPUT = init(AEC.Type.STATE, 0x00, 0, argv -> {
        STATUS = TextEngine.Status.WAITING;
    }),

    PAUSE_FOR_FRAMES = init(AEC.Type.STATE, 0x01,  1, argv -> {
        PAUSE_DURATION_MAX = CommandHelper.stackify(argv[0]);
        STATUS = TextEngine.Status.PAUSED;
    }),

    SET_LINE_SPEED = init(AEC.Type.STATE, 0x02, 1, argv -> {
        LINE_SPEED = argv[0] == 0x00 ? 0 : argv[0]; // TODO: line speed config
    }),

    HALT = init(AEC.Type.STATE, 0x03, 0, argv -> {
        STATUS = TextEngine.Status.HALTED;
    });
}
