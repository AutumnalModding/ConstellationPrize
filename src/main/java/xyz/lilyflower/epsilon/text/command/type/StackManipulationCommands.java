package xyz.lilyflower.epsilon.text.command.type;

import java.util.Random;
import xyz.lilyflower.epsilon.text.EngineState;
import xyz.lilyflower.epsilon.text.command.AEC;
import xyz.lilyflower.epsilon.text.util.CommandHelper;

@SuppressWarnings("unused")
public class StackManipulationCommands extends EngineState {
    /// Syntax: \[03 00 XX XX] (S)
    ///
    /// Pushes the contents of register XX XX to the stack.
    /// Pass registers via little-endian ordering.
    AEC REGISTER_LOAD = AEC.init(AEC.Type.STACK_MANIPULATION, 0x00, 1, argv -> {
        STACK.add(MEMORY.get(CommandHelper.combine(argv)));
    }),

    /// Syntax: \[03 01 XX XX] (S)
    ///
    /// Pops the stack and stores it in register XX XX.
    /// Pass registers via little-endian ordering.
    REGISTER_STORE = AEC.init(AEC.Type.STACK_MANIPULATION, 0x01, 3, argv -> {
        MEMORY.put(CommandHelper.combine(argv), STACK.remove(CommandHelper.stackify(argv[2])));
    }),

    /// Enables stack mode.
    /// Syntax: \[03 02]
    /// Any commands marked with an (S) - e.g. \[01 01 XX] (S) will load their parameters from the stack, if given a 00 as input.
    ///
    /// For example: \[03 05] \[03 02] \[01 01 00] will pause for a random number of update frames.
    ENABLE_STACK = AEC.init(AEC.Type.STACK_MANIPULATION, 0x02, 0, argv -> {
        STACK_ENABLED = true;
    }),

    /// Disables stack mode.
    /// Syntax: \[03 03]
    DISABLE_STACK = AEC.init(AEC.Type.STACK_MANIPULATION, 0x03, 0, argv -> {
        STACK_ENABLED = false;
    }),

    /// Clears the stack.
    /// Syntax: \[03 04]
    CLEAR_STACK = AEC.init(AEC.Type.STACK_MANIPULATION, 0x04, 0, argv -> {
        STACK.clear();
    }),

    /// Basic RNG. Only really good for debugging.
    /// Syntax: \[03 05]
    RNG = AEC.init(AEC.Type.STACK_MANIPULATION, 0x05, 0, argv -> {
        STACK.add((char) new Random().nextInt(Character.MIN_VALUE, Character.MAX_VALUE));
    }),

    /// Generate up to 65536 random numbers.
    /// Still only good for debugging.
    /// Syntax: \[03 05 XX XX] (S)
    RNG_MULTIPLE = AEC.init(AEC.Type.STACK_MANIPULATION, 0x06, 2, argv -> {
        for (int index = 0; index < CommandHelper.combine(argv); index++) {
            STACK.add((char) new Random().nextInt(Character.MIN_VALUE, Character.MAX_VALUE));
        }
    });
}
