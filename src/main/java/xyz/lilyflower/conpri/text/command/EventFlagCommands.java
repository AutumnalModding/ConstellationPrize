package xyz.lilyflower.conpri.text.command;

import xyz.lilyflower.conpri.entity.component.PlayerEventFlagsComponent;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;
import xyz.lilyflower.conpri.init.ConstellationPrizeComponents;
import xyz.lilyflower.conpri.text.EngineState;
import xyz.lilyflower.conpri.text.util.CommandHelper;

@SuppressWarnings("DataFlowIssue")
public class EventFlagCommands extends EngineState {
    AEC ENABLE_FLAG = AEC.init(AEC.Type.EVENT_FLAG, 0x00, 2, argv -> {
        PlayerEventFlagsComponent component = ConstellationPrizeComponents.EVENT_FLAGS.get(ConstellationPrizeClient.CLIENT_INSTANCE.player);
        char flag = CommandHelper.combine(argv);
        component.updateFlagState(flag, true);
    }),

    DISABLE_FLAG = AEC.init(AEC.Type.EVENT_FLAG, 0x01, 2, argv -> {
        PlayerEventFlagsComponent component = ConstellationPrizeComponents.EVENT_FLAGS.get(ConstellationPrizeClient.CLIENT_INSTANCE.player);
        char flag = CommandHelper.combine(argv);
        component.updateFlagState(flag, false);
    }),

    LOAD_FLAG = AEC.init(AEC.Type.EVENT_FLAG, 0x03, 2, argv -> {
        PlayerEventFlagsComponent component = ConstellationPrizeComponents.EVENT_FLAGS.get(ConstellationPrizeClient.CLIENT_INSTANCE.player);
        char flag = CommandHelper.combine(argv);
        char value = (char) (component.getFlag(flag) ? 1 : 0);
        STACK.addFirst(value);
    }),

    DEBUG_FLAG = AEC.init(AEC.Type.EVENT_FLAG, 0x04, 2, argv -> {
        PlayerEventFlagsComponent component = ConstellationPrizeComponents.EVENT_FLAGS.get(ConstellationPrizeClient.CLIENT_INSTANCE.player);
        char flag = CommandHelper.combine(argv);
        char value = (char) (component.getFlag(flag) ? 1 : 0);
        System.out.println(value);
    });
}
