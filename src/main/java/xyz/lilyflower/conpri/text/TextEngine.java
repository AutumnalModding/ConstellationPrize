package xyz.lilyflower.conpri.text;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.lilyflower.conpri.client.display.module.DialogueModule;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;
import static xyz.lilyflower.conpri.text.EngineState.*;

public class TextEngine {

    public static void init(Message message) {
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

        STATUS = Status.RUNNING;
        CURRENT_MESSAGE = message;
        DialogueModule.CURRENT_PORTRAIT = message.portrait;
    }
    
    public static void update(DrawContext context, RenderTickCounter counter) {
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

        switch (STATUS) {
            case RUNNING -> {
                try {
                    LINE_DELTA++;

                    if (LINE_DELTA % (ConstellationPrizeClient.CLIENT_INSTANCE.getCurrentFps() / LINE_SPEED) == 0) {
                        LINE_DELTA = 1;
                        if (CURRENT_MESSAGE.talksound != null) {
                            ConstellationPrizeClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(CURRENT_MESSAGE.talksound, 1.0F, 1.0F));
                        }

                        if (LINE_POSITION >= LINE_CONTENT.get(LINE_INDEX).length && !isWaiting()) {
                            LINE_POSITION = 0;
                            LINE_INDEX++;

                            if (LINE_INDEX >= LINE_COUNT) {
                                LINE_INDEX = 0;
                                LINE_CONTENT.clear();
                                CHARACTER_COLOURS.clear();

                                for (int index = 0; index < LINE_ARRAY.length; index++) {
                                    LINE_ARRAY[index] = new StringBuilder();
                                }

                                STATUS = Status.INACTIVE;
                            }
                        }

                        try {
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
                        } catch (IndexOutOfBoundsException ignored) {
                            LINE_INDEX = 0;
                            LINE_CONTENT.clear();
                            CHARACTER_COLOURS.clear();

                            for (int index = 0; index < LINE_ARRAY.length; index++) {
                                LINE_ARRAY[index] = new StringBuilder();
                            }

                            STATUS = Status.INACTIVE;

                        }
                    }
                } catch (ArithmeticException ignored) {}
            }

            case PAUSED -> {
                PAUSE_DURATION_ELAPSED++;

                if (PAUSE_DURATION_ELAPSED >= PAUSE_DURATION_MAX) {
                    STATUS = Status.RUNNING;
                    PAUSE_DURATION_MAX = 0;
                    PAUSE_DURATION_ELAPSED = 0;
                }
            }

            case WAITING -> {
                try {
                    BLINK_DELTA_DISPLAY++;
                    if (BLINK_DELTA_DISPLAY % (ConstellationPrizeClient.CLIENT_INSTANCE.getCurrentFps() / 48) == 0) {
                        BLINK_DELTA_CHARACTER++;
                    }

                    String character = " ";
                    if (BLINK_DELTA_CHARACTER <= 35) {
                        character = "⮟";
                    }
                    context.drawText(VANILLA_RENDERER, character, DRAW_POSITION_X + 237, DRAW_POSITION_Y + 55, ColorHelper.getArgb(0xFF, 0xFF, 0xFF), true);
                    if (BLINK_DELTA_CHARACTER >= 70) {
                        BLINK_DELTA_CHARACTER = 0;
                    }
                } catch (ArithmeticException ignored) {}
            }

            case HALTED -> {
                // NOP
            }
        }
    }

    public static class LineSpeed {
        public static final int FAST = 20;
        public static final int MEDIUM = 16;
        public static final int SLOW = 8;
        public static final int SNAIL = 4;
        public static final int VISCOUS = 2;
    }

    public enum Command { // TODO: document these properly

        // [01 00]
        WAIT_FOR_INPUT(new char[]{0x01, 0x00}, 0, arguments -> STATUS = Status.WAITING),

        // [01 01 XX]
        PAUSE_FOR_FRAMES(new char[]{0x01, 0x01}, 1, arguments -> {
            PAUSE_DURATION_MAX = arguments[0];
            STATUS = Status.PAUSED;
        }),

        //
        SET_LINE_SPEED(new char[]{0x01, 0x02}, 1, arguments -> {
            LINE_SPEED = arguments[0] == 0x00 ? 0 : arguments[0]; // TODO: line speed config
        }),

        HALT(new char[]{0x01, 0x03}, 0, arguments -> {
            STATUS = Status.HALTED;
        }),

        SET_COLOUR(new char[]{0x02, 0x00}, 5, arguments -> {
            int red = arguments[0];
            int green = arguments[1];
            int blue = arguments[2];
            int index = arguments[3];
            int line = arguments[4];
            
            int colour = red << 16 | green << 8 | blue;

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

    enum Status {
        PAUSED,
        HALTED,
        WAITING,
        RUNNING,
        INACTIVE
    }

    public record Message(String portrait, SoundEvent voiceline, SoundEvent talksound, String... lines) {}
}
