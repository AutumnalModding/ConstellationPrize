package xyz.lilyflower.conpri.text.command.type;

import xyz.lilyflower.conpri.text.EngineState;
import xyz.lilyflower.conpri.text.TextEngine;
import xyz.lilyflower.conpri.text.command.AEC;
import xyz.lilyflower.conpri.text.util.CommandHelper;

@SuppressWarnings("unused")
public class StateCommands extends EngineState {
    public static final AEC WAIT_FOR_INPUT = AEC.init(AEC.Type.STATE, 0x00, 0, argv -> {
        STATUS = TextEngine.Status.WAITING;
    }),

    PAUSE_FOR_FRAMES = AEC.init(AEC.Type.STATE, 0x01,  1, argv -> {
        PAUSE_DURATION_MAX = CommandHelper.stackify(argv[0]);
        STATUS = TextEngine.Status.PAUSED;
    }),

    SET_LINE_SPEED = AEC.init(AEC.Type.STATE, 0x02, 1, argv -> {
        LINE_SPEED = argv[0] == 0x00 ? 0 : argv[0]; // TODO: line speed config
    }),

    HALT = AEC.init(AEC.Type.STATE, 0x03, 0, argv -> {
        STATUS = TextEngine.Status.HALTED;
    });
}
