package xyz.lilyflower.conpri.util;

import java.util.ArrayList;
import net.minecraft.client.gui.DrawContext;

public class TextRenderer {
    public static int LINE_COUNT = 0;
    public static int LINE_POSITION = 0;
    public static int LINE_INDEX = 0;
    public static int LINE_SPEED = LineSpeed.FAST;
    public static int LINE_DELTA = 0;
    public static StringBuilder[] LINE_ARRAY;
    public static ArrayList<char[]> LINE_CONTENT = new ArrayList<>();

    public static int DRAW_POSITION_X = 0;
    public static int DRAW_POSITION_Y = 0;

    public static boolean SHOULD_UPDATE = true;
    public static net.minecraft.client.font.TextRenderer RENDERER_INSTANCE;

    public static void init(String... lines) {
        LINE_ARRAY = new StringBuilder[lines.length];
        LINE_COUNT = lines.length;

        for (int line = 0; line < lines.length; line++) {
            LINE_ARRAY[line] = new StringBuilder();

            char[] text = new char[lines[line].length()];
            for (int i = 0; i < text.length; i++) {
                text[i] = lines[line].charAt(i);
            }

            LINE_CONTENT.add(text);
        }
    }

    public static void update(DrawContext context) {
        LINE_DELTA++;

        if (SHOULD_UPDATE) {
            for (int line = 0; line <= LINE_INDEX; line++) {
                context.drawText(RENDERER_INSTANCE, LINE_ARRAY[line].toString(), DRAW_POSITION_X, DRAW_POSITION_Y + (line * 11), 0xFFFFFF, true);
            }

            if (LINE_DELTA % LINE_SPEED == 0) {
                LINE_POSITION++;

                if (LINE_POSITION >= LINE_CONTENT.get(LINE_INDEX).length) {
                    LINE_POSITION = 0;
                    LINE_INDEX++;

                    if (LINE_INDEX > LINE_COUNT) {
                        LINE_INDEX = 0;
                        LINE_CONTENT.clear();

                        for (int index = 0; index < LINE_ARRAY.length; index++) {
                            LINE_ARRAY[index] = new StringBuilder();
                        }

                        SHOULD_UPDATE = false;
                    }
                }
            }
        }
    }

    public static void parseCommand(int command) {
        switch (command) {
            case RendererCommand.END_LINE -> {} // TODO Implement waiting for input at the end of a line
            case RendererCommand.END_TEXT_BLOCK -> {} // TODO Implement waiting for input at the end of a text block
            case RendererCommand.LINE_SPEED_FAST -> LINE_SPEED = LineSpeed.FAST;
            case RendererCommand.LINE_SPEED_MEDIUM -> LINE_SPEED = LineSpeed.MEDIUM;
            case RendererCommand.LINE_SPEED_SLOW -> LINE_SPEED = LineSpeed.SLOW;
            case RendererCommand.LINE_SPEED_SNAIL -> LINE_SPEED = LineSpeed.SNAIL;
        }
    }

    public static class LineSpeed {
        public static final int FAST = 1;
        public static final int MEDIUM = 2;
        public static final int SLOW = 3;
        public static final int SNAIL = 4;
    }

    public static class RendererCommand {
        public static final int END_LINE = 0x3;
        public static final int END_TEXT_BLOCK = 0x4;
        public static final int LINE_SPEED_FAST = 0x11;
        public static final int LINE_SPEED_MEDIUM = 0x12;
        public static final int LINE_SPEED_SLOW = 0x13;
        public static final int LINE_SPEED_SNAIL = 0x14;
    }
}
