package xyz.lilyflower.conpri.text.command.type;

import xyz.lilyflower.conpri.text.EngineState;
import xyz.lilyflower.conpri.text.command.AEC;
import xyz.lilyflower.conpri.text.command.VPC;

@SuppressWarnings("unused")
public class SpecialCommands extends EngineState {
    public static final VPC EXECUTE_COMMAND = VPC.init(AEC.Type.SPECIAL, 0x00, -2, argv -> {
        AEC.Type[] types = AEC.Type.values();
        AEC command = AEC.get(types[Math.clamp(argv[0] - 1, 0, types.length)], argv[1]);
        char[] arguments = new char[command.argc];
        System.arraycopy(argv, 2, arguments, 0, argv.length - 2);
        command.executor.run(arguments);
    }, argn -> {
        AEC.Type[] types = AEC.Type.values();
        AEC command = AEC.get(types[Math.clamp(argn[0] - 1, 0, types.length)], argn[1]);
        return command.argc;
    });
}
