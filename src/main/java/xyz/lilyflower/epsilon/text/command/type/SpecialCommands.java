package xyz.lilyflower.epsilon.text.command.type;

import xyz.lilyflower.epsilon.text.EngineState;
import xyz.lilyflower.epsilon.text.command.AEC;
import xyz.lilyflower.epsilon.text.command.VPC;
import xyz.lilyflower.epsilon.text.util.CommandHelper;

@SuppressWarnings("unused")
public class SpecialCommands extends EngineState {
    public static final VPC EXECUTE_COMMAND = VPC.init(AEC.Type.SPECIAL, 0x00, -2, argv -> {
        AEC command = get(argv[0], argv[1]);
        char[] arguments = new char[command.argc];
        System.arraycopy(argv, 2, arguments, 0, argv.length - 2);
        command.executor.run(arguments);
    }, argn -> {
        return get(argn[0], argn[1]).argc;
    });

    private static AEC get(char upper, char lower) {
        AEC.Type[] types = AEC.Type.values();
        if (upper == 0 && lower == 0) {
            upper = CommandHelper.stackify(upper);
            lower = CommandHelper.stackify(lower);
        }
        return AEC.get(types[Math.clamp(upper - 1, 0, types.length)], lower);
    }
}
