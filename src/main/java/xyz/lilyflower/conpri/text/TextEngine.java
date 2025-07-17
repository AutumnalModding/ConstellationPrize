package xyz.lilyflower.conpri.text;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.lilyflower.conpri.client.display.module.DialogueModule;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;

public class TextEngine {

    public static void init(Message message) {
        EngineState.LINE_ARRAY = new StringBuilder[message.lines.length];
        EngineState.LINE_COUNT = message.lines.length;

        for (int line = 0; line < message.lines.length; line++) {
            EngineState.LINE_ARRAY[line] = new StringBuilder();

            char[] text = new char[message.lines[line].length()];
            for (int i = 0; i < text.length; i++) {
                text[i] = message.lines[line].charAt(i);
            }

            EngineState.LINE_CONTENT.add(text);
        }

        if (message.voiceline != null) {
            ConstellationPrizeClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(message.voiceline, 1.0F, 1.0F));
        }

        EngineState.STATUS = Status.RUNNING;
        EngineState.CURRENT_MESSAGE = message;
        DialogueModule.CURRENT_PORTRAIT = message.portrait;
    }
    
    public static void update(DrawContext context, RenderTickCounter counter) {
        for (int line = 0; line <= EngineState.LINE_INDEX; line++) {
            int offset = 0;
            String[] text = EngineState.LINE_ARRAY[line].toString().split("");
            for (int index = 0; index < text.length; index++) {
                String character = text[index];
                int colour = EngineState.CHARACTER_COLOURS.getOrDefault(new ImmutablePair<>(index, line), 0xFFFFFF);

                context.drawText(EngineState.VANILLA_RENDERER, character, EngineState.DRAW_POSITION_X + offset, EngineState.DRAW_POSITION_Y + (line * 11), colour, true);
                offset += EngineState.VANILLA_RENDERER.getWidth(character);
            }
        }

        switch (EngineState.STATUS) {
            case RUNNING -> {
                if (Float.compare(counter.getTickProgress(false), 0) == 0) {
                    EngineState.LINE_DELTA++;
                    System.out.println("Updating line delta");
                }

                if (EngineState.LINE_DELTA % EngineState.LINE_SPEED == 0) {
                    if (EngineState.CURRENT_MESSAGE.talksound != null) {
                        ConstellationPrizeClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(EngineState.CURRENT_MESSAGE.talksound, 1.0F, 1.0F));
                    }

                    if (EngineState.LINE_POSITION >= EngineState.LINE_CONTENT.get(EngineState.LINE_INDEX).length && !EngineState.isWaiting()) {
                        EngineState.LINE_POSITION = 0;
                        EngineState.LINE_INDEX++;

                        if (EngineState.LINE_INDEX >= EngineState.LINE_COUNT) {
                            EngineState.LINE_INDEX = 0;
                            EngineState.LINE_CONTENT.clear();
                            EngineState.CHARACTER_COLOURS.clear();

                            for (int index = 0; index < EngineState.LINE_ARRAY.length; index++) {
                                EngineState.LINE_ARRAY[index] = new StringBuilder();
                            }

                            EngineState.STATUS = Status.INACTIVE;
                        }
                    }

                    try {
                        char next = EngineState.LINE_CONTENT.get(EngineState.LINE_INDEX)[EngineState.LINE_POSITION];

                        if (next == 0x00) {
                            char header_lower = EngineState.LINE_CONTENT.get(EngineState.LINE_INDEX)[++EngineState.LINE_POSITION];
                            char header_upper = EngineState.LINE_CONTENT.get(EngineState.LINE_INDEX)[++EngineState.LINE_POSITION];

                            for (Command command : Command.values()) {
                                if (command.header[0] == header_lower && command.header[1] == header_upper) {
                                    char[] arguments = new char[command.arguments];
                                    for (int arg = 0; arg < arguments.length; arg++) {
                                        arguments[arg] = EngineState.LINE_CONTENT.get(EngineState.LINE_INDEX)[++EngineState.LINE_POSITION];
                                    }
                                    command.executor.run(arguments);

                                    break;
                                }
                            }
                        } else {
                            EngineState.LINE_ARRAY[EngineState.LINE_INDEX].append(next);
                        }

                        EngineState.LINE_POSITION++;
                    } catch (IndexOutOfBoundsException ignored) {
                        EngineState.LINE_INDEX = 0;
                        EngineState.LINE_CONTENT.clear();
                        EngineState.CHARACTER_COLOURS.clear();

                        for (int index = 0; index < EngineState.LINE_ARRAY.length; index++) {
                            EngineState.LINE_ARRAY[index] = new StringBuilder();
                        }

                        EngineState.STATUS = Status.INACTIVE;

                    }
                }
            }

            case PAUSED -> {
                EngineState.PAUSE_DURATION_ELAPSED++;

                if (EngineState.PAUSE_DURATION_ELAPSED >= EngineState.PAUSE_DURATION_MAX) {
                    EngineState.STATUS = Status.RUNNING;
                    EngineState.PAUSE_DURATION_MAX = 0;
                    EngineState.PAUSE_DURATION_ELAPSED = 0;
                }
            }

            case WAITING -> {
                // TODO: blink character
                EngineState.BLINK_DELTA++;
                String character = " ";
                if (EngineState.BLINK_DELTA <= 35) {
                    character = "⮟";
                }
                context.drawText(EngineState.VANILLA_RENDERER, character, EngineState.DRAW_POSITION_X + 237, EngineState.DRAW_POSITION_Y + 55, ColorHelper.getArgb(0xFF, 0xFF, 0xFF), true);
                if (EngineState.BLINK_DELTA >= 70) {
                    EngineState.BLINK_DELTA = 0;
                }
            }

            case HALTED -> {
                // NOP
            }
        }
    }

    public static class LineSpeed {
        public static final int FAST = 2;
        public static final int MEDIUM = 4;
        public static final int SLOW = 6;
        public static final int SNAIL = 8;
        public static final int VISCOUS = 10;
    }

    public enum Command { // TODO: document these properly

        WAIT_FOR_INPUT(new char[]{0x01, 0x00}, 0, arguments -> EngineState.STATUS = Status.WAITING),

        PAUSE_FOR_FRAMES(new char[]{0x01, 0x01}, 1, arguments -> {
            EngineState.PAUSE_DURATION_MAX = arguments[0];
            EngineState.STATUS = Status.PAUSED;
        }),

        SET_LINE_SPEED(new char[]{0x01, 0x02}, 1, arguments -> {
            EngineState.LINE_SPEED = arguments[0] == 0x00 ? 0 : arguments[0]; // TODO: line speed config
        }),

        HALT(new char[]{0x01, 0x03}, 0, arguments -> {
            EngineState.STATUS = Status.HALTED;
        }),

        SET_COLOUR(new char[]{0x02, 0x00}, 5, arguments -> {
            int red = arguments[0];
            int green = arguments[1];
            int blue = arguments[2];
            int index = arguments[3];
            int line = arguments[4];
            
            int colour = red << 16 | green << 8 | blue;

            EngineState.CHARACTER_COLOURS.put(new ImmutablePair<>(index, line), colour);
        }),

        CLEAR_COLOURS(new char[]{0x02, 0x01}, 0, arguments -> {
            EngineState.CHARACTER_COLOURS.clear();
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
