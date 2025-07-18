package xyz.lilyflower.conpri.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.client.font.TextRenderer;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class EngineState {
    static TextEngine.Message CURRENT_MESSAGE;
    static int PAUSE_DURATION_ELAPSED = 0;
    static int PAUSE_DURATION_MAX = 0;
    static TextEngine.Status STATUS = TextEngine.Status.INACTIVE;
    static int BLINK_DELTA_CHARACTER = 0;
    static int BLINK_DELTA_DISPLAY = 0;
    static int DRAW_POSITION_Y = 0;
    static int DRAW_POSITION_X = 0;
    static HashMap<ImmutablePair<Integer, Integer>, Integer> CHARACTER_COLOURS = new HashMap<>();
    static ArrayList<char[]> LINE_CONTENT = new ArrayList<>();
    static StringBuilder[] LINE_ARRAY;
    static int LINE_DELTA = 0;
    static int LINE_SPEED = TextEngine.LineSpeed.FAST;
    static int LINE_INDEX = 0;
    static int LINE_POSITION = 0;
    static int LINE_COUNT = 0;
    static TextRenderer VANILLA_RENDERER;
    static HashMap<Character, Character> MEMORY;
    static ArrayList<Character> STACK = new ArrayList<>();
    static boolean STACK_ENABLED = false;

    public static void setPosition(int x, int y) {
        DRAW_POSITION_X = x;
        DRAW_POSITION_Y = y;
    }

    public static boolean isActive() {
        return STATUS != TextEngine.Status.INACTIVE;
    }

    public static boolean isWaiting() {
        return STATUS == TextEngine.Status.HALTED || STATUS == TextEngine.Status.WAITING;
    }

    public static void stopWaiting() {
        switch (STATUS) {
            case HALTED -> STATUS = TextEngine.Status.INACTIVE;
            case WAITING -> STATUS = TextEngine.Status.RUNNING;
        }
    }

    public static void init(TextRenderer renderer) {
        VANILLA_RENDERER = renderer;
    }
}
