package xyz.lilyflower.epsilon.text.command.type;

import xyz.lilyflower.epsilon.text.EngineState;
import xyz.lilyflower.epsilon.text.TextEngine;
import xyz.lilyflower.epsilon.text.command.AEC;
import xyz.lilyflower.epsilon.text.util.CommandHelper;

@SuppressWarnings("unused")
public class ControlCommands extends EngineState {
    AEC CHANGE_TEXT_BLOCK = AEC.init(AEC.Type.CONTROL, 0x00, 2, argv -> {
        char block = CommandHelper.combine(argv[0], argv[1]);
        TextEngine.Message message = TextEngine.Message.REGISTRY.get(block);
        TextEngine.load(message);

        LINE_POSITION--; // why the fuck?
    });
}
