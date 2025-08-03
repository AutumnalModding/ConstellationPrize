package xyz.lilyflower.epsilon.text.command.type;

import xyz.lilyflower.epsilon.entity.component.PlayerEventFlagsComponent;
import xyz.lilyflower.epsilon.init.EPSILONClient;
import xyz.lilyflower.epsilon.init.EPSILONComponents;
import xyz.lilyflower.epsilon.text.EngineState;
import xyz.lilyflower.epsilon.text.command.AEC;
import xyz.lilyflower.epsilon.text.util.CommandHelper;

@SuppressWarnings("DataFlowIssue")
public class EventFlagCommands extends EngineState {
    AEC ENABLE_FLAG = AEC.init(AEC.Type.EVENT_FLAG, 0x00, 2, argv -> {
        PlayerEventFlagsComponent component = EPSILONComponents.EVENT_FLAGS.get(EPSILONClient.CLIENT_INSTANCE.player);
        char flag = CommandHelper.combine(argv);
        component.set(flag, true);
    }),

    DISABLE_FLAG = AEC.init(AEC.Type.EVENT_FLAG, 0x01, 2, argv -> {
        PlayerEventFlagsComponent component = EPSILONComponents.EVENT_FLAGS.get(EPSILONClient.CLIENT_INSTANCE.player);
        char flag = CommandHelper.combine(argv);
        component.set(flag, false);
    }),

    LOAD_FLAG = AEC.init(AEC.Type.EVENT_FLAG, 0x03, 2, argv -> {
        PlayerEventFlagsComponent component = EPSILONComponents.EVENT_FLAGS.get(EPSILONClient.CLIENT_INSTANCE.player);
        char flag = CommandHelper.combine(argv);
        char value = (char) (component.get(flag) ? 1 : 0);
        STACK.addFirst(value);
    }),

    DEBUG_FLAG = AEC.init(AEC.Type.EVENT_FLAG, 0x04, 2, argv -> {
        PlayerEventFlagsComponent component = EPSILONComponents.EVENT_FLAGS.get(EPSILONClient.CLIENT_INSTANCE.player);
        char flag = CommandHelper.combine(argv);
        char value = (char) (component.get(flag) ? 1 : 0);
        System.out.println(value);
    });
}
