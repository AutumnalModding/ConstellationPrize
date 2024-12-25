package xyz.lilyflower.conpri.feature.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;

public class DialogueTextRenderer {
    public static TextRenderer VANILLA_RENDERER;
    static int LINE_COUNT = 0;
    static int LINE_POSITION = 0;
    static int LINE_INDEX = 0;
    static int LINE_SPEED = LineSpeed.FAST;
    static int LINE_DELTA = 0;

    static StringBuilder[] LINE_ARRAY;
    static ArrayList<char[]> LINE_CONTENT = new ArrayList<>();
    static HashMap<ImmutablePair<Integer, Integer>, Integer> CHARACTER_COLOURS = new HashMap<>();

    static int DRAW_POSITION_X = 0;
    static int DRAW_POSITION_Y = 0;

    static State RENDERER_STATE = State.INACTIVE;
    static int PAUSE_DURATION_MAX = 0;
    static int PAUSE_DURATION_ELAPSED = 0;

    static Message CURRENT_MESSAGE;

    static void init(Message message) {
        LINE_ARRAY = new StringBuilder[message.lines.length];
        LINE_COUNT = message.lines.length;

        for (int line = 0; line < message.lines.length; line++) {
            LINE_ARRAY[line] = new StringBuilder();

            char[] text = new char[message.lines[line].length()];
            for (int i = 0; i < text.length; i++) {
                text[i] = message.lines[line].charAt(i);
            }

            LINE_CONTENT.add(text);
        }

        if (message.voiceline != null) {
            ConstellationPrizeClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(message.voiceline, 1.0F, 1.0F));
        }

        RENDERER_STATE = State.RUNNING;
        CURRENT_MESSAGE = message;
        DialogueBoxRenderer.CURRENT_PORTRAIT = message.portrait;
    }
    
    static void update(DrawContext context) {
        for (int line = 0; line <= LINE_INDEX; line++) {
            int offset = 0;
            String[] text = LINE_ARRAY[line].toString().split("");
            for (int index = 0; index < text.length; index++) {
                String character = text[index];
                int colour = CHARACTER_COLOURS.getOrDefault(new ImmutablePair<>(index, line), 0xFFFFFF);

                context.drawText(VANILLA_RENDERER, character, DRAW_POSITION_X + offset, DRAW_POSITION_Y + (line * 11), colour, true);
                offset += VANILLA_RENDERER.getWidth(character);
            }
        }

        switch (RENDERER_STATE) {
            case RUNNING -> {
                LINE_DELTA++;

                if (LINE_DELTA % LINE_SPEED == 0) {
                    if (CURRENT_MESSAGE.talksound != null) {
                        ConstellationPrizeClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(CURRENT_MESSAGE.talksound, 1.0F, 1.0F));
                    }

                    char next = LINE_CONTENT.get(LINE_INDEX)[LINE_POSITION];

                    if (next == 0x00) {
                        char header_lower = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
                        char header_upper = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];

                        for (Command command : Command.values()) {
                            if (command.header[0] == header_lower && command.header[1] == header_upper) {
                                char[] arguments = new char[command.arguments];
                                for (int arg = 0; arg < arguments.length; arg++) {
                                    arguments[arg] = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
                                }
                                command.executor.run(arguments);

                                break;
                            }
                        }
                    } else {
                        LINE_ARRAY[LINE_INDEX].append(next);
                    }

                    LINE_POSITION++;

                    if (LINE_POSITION >= LINE_CONTENT.get(LINE_INDEX).length) {
                        LINE_POSITION = 0;
                        LINE_INDEX++;

                        if (LINE_INDEX >= LINE_COUNT) {
                            LINE_INDEX = 0;
                            LINE_CONTENT.clear();
                            CHARACTER_COLOURS.clear();

                            for (int index = 0; index < LINE_ARRAY.length; index++) {
                                LINE_ARRAY[index] = new StringBuilder();
                            }

                            RENDERER_STATE = State.INACTIVE;
                        }
                    }
                }
            }

            case PAUSED -> {
                PAUSE_DURATION_ELAPSED++;

                if (PAUSE_DURATION_ELAPSED >= PAUSE_DURATION_MAX) {
                    RENDERER_STATE = State.RUNNING;
                    PAUSE_DURATION_MAX = 0;
                    PAUSE_DURATION_ELAPSED = 0;
                }
            }

            case WAITING -> {
                // Do nothing lmao
            }
        }
    }

    static class LineSpeed {
        static final int FAST = 2;
        static final int MEDIUM = 4;
        static final int SLOW = 6;
        static final int SNAIL = 8;
        static final int VISCOUS = 10;
    }

    enum Command { // TODO: document these properly

        WAIT_FOR_INPUT(new char[]{0x01, 0x00}, 0, arguments -> RENDERER_STATE = State.WAITING),

        PAUSE_FOR_FRAMES(new char[]{0x01, 0x01}, 1, arguments -> {
            PAUSE_DURATION_MAX = arguments[0];
            RENDERER_STATE = State.PAUSED;
        }),

        SET_LINE_SPEED(new char[]{0x01, 0x02}, 1, arguments -> {
            LINE_SPEED = arguments[0] == 0x00 ? 0 : arguments[0]; // TODO: line speed config
        }),

        SET_COLOUR(new char[]{0x02, 0x00}, 5, arguments -> {
            int red = arguments[0];
            int green = arguments[1];
            int blue = arguments[2];
            int index = arguments[3];
            int line = arguments[4];
            
            int colour = red << 16 | green << 8 | blue;

            System.out.println("Added ImmutablePair " + index + ", " + line);
            CHARACTER_COLOURS.put(new ImmutablePair<>(index, line), colour);
        }),

        CLEAR_COLOURS(new char[]{0x02, 0x01}, 0, arguments -> {
            CHARACTER_COLOURS.clear();
        })

        ;

        private final char[] header;
        private final int arguments;
        private final Executor executor;

        Command(char[] header, int arguments, Executor executor) {
            this.header = header;
            this.arguments = arguments;
            this.executor = executor;
        }

        @FunctionalInterface
        public interface Executor {
            void run(char... arguments);
        }
    }

    enum State {
        PAUSED,
        RUNNING,
        WAITING,
        INACTIVE
    }

    record Message(String portrait, SoundEvent voiceline, SoundEvent talksound, String... lines) {}
}
