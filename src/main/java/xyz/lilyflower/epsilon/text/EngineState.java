package xyz.lilyflower.epsilon.text;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.font.TextRenderer;
import org.apache.commons.lang3.tuple.ImmutablePair;

public abstract class EngineState {
    protected static TextEngine.Message CURRENT_MESSAGE;
    protected static int PAUSE_DURATION_ELAPSED = 0;
    protected static int PAUSE_DURATION_MAX = 0;
    protected static float PAUSE_DELTA = 0;
    protected static TextEngine.Status STATUS = TextEngine.Status.INACTIVE;
    protected static int BLINK_DELTA_CHARACTER = 0;
    protected static int BLINK_DELTA_DISPLAY = 0;
    protected static int DRAW_POSITION_Y = 0;
    protected static int DRAW_POSITION_X = 0;
    protected static HashMap<ImmutablePair<Integer, Integer>, Integer> CHARACTER_COLOURS = new HashMap<>();
    protected static ArrayList<char[]> LINE_CONTENT = new ArrayList<>();
    protected static StringBuilder[] LINE_ARRAY;
    protected static float LINE_DELTA = 0;
    protected static float LINE_SPEED = TextEngine.LineSpeed.FAST;
    protected static int LINE_INDEX = 0;
    protected static int LINE_POSITION = 0;
    protected static int LINE_COUNT = 0;
    protected static TextRenderer VANILLA_RENDERER;
    protected static HashMap<Character, Character> MEMORY;
    protected static ArrayList<Character> STACK = new ArrayList<>();
    protected static boolean STACK_ENABLED = false;

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

    public static void setRenderer(TextRenderer renderer) {
        VANILLA_RENDERER = renderer;
    }
}
