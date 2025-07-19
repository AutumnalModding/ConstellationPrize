package xyz.lilyflower.conpri.text.util;

import xyz.lilyflower.conpri.text.EngineState;

public class CommandHelper extends EngineState {
    public static char directCombine(char... input) {
        char upper = stackify(input[0]);
        char lower = stackify(input[1]);
        return (char) Integer.parseInt(Integer.toHexString(upper) + Integer.toHexString(lower), 16);
    }

    public static char stackify(char input) {
        return input == 0 && STACK_ENABLED ? STACK.removeFirst() : input;
    }
}
