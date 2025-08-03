package xyz.lilyflower.epsilon.text.util;

import xyz.lilyflower.epsilon.text.EngineState;

public class CommandHelper extends EngineState {
    public static char combine(char... input) {
        char upper = input[0];
        char lower = input[1];

        if (input[0] == 0x00 && input[1] == 0x00) {
            upper = stackify(input[0]);
            lower = stackify(input[1]);
        }

        return (char) Integer.parseInt(Integer.toHexString(upper) + Integer.toHexString(lower), 16);
    }

    public static char stackify(char input) {
        return input == 0 && STACK_ENABLED ? STACK.removeFirst() : input;
    }
}
