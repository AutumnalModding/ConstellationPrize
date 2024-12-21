package xyz.lilyflower.conpri.util;

import java.util.ArrayList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import xyz.lilyflower.conpri.client.renderer.module.neural.ModuleNeuralDialogue;
import xyz.lilyflower.conpri.init.ConstellationPrize;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;

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

    public static boolean IS_PAUSED = false;
    public static int PAUSE_DURATION_MAX = 0;
    public static int PAUSE_DURATION_ELAPSED = 0;

    public static boolean SHOULD_UPDATE = false;
    public static net.minecraft.client.font.TextRenderer RENDERER_INSTANCE;
    public static Message CURRENT_MESSAGE;

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

        SHOULD_UPDATE = true;
        CURRENT_MESSAGE = message;
        ModuleNeuralDialogue.CURRENT_PORTRAIT = message.portrait;
    }
    
    public static void update(DrawContext context) {
        LINE_DELTA++;

        for (int line = 0; line <= LINE_INDEX; line++) {
            context.drawText(RENDERER_INSTANCE, LINE_ARRAY[line].toString(), DRAW_POSITION_X, DRAW_POSITION_Y + (line * 11), 0xFFFFFF, true);
        }

        if (SHOULD_UPDATE) {
            if (IS_PAUSED) {
                PAUSE_DURATION_ELAPSED++;

                if (PAUSE_DURATION_ELAPSED >= PAUSE_DURATION_MAX) {
                    IS_PAUSED = false;
                    PAUSE_DURATION_MAX = 0;
                    PAUSE_DURATION_ELAPSED = 0;
                } else {
                    return;
                }
            }

            if (LINE_DELTA % LINE_SPEED == 0) {
                if (CURRENT_MESSAGE.talksound != null) {
                    ConstellationPrizeClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(CURRENT_MESSAGE.talksound, 1.0F, 1.0F));
                }

                char next = LINE_CONTENT.get(LINE_INDEX)[LINE_POSITION];

                if (next == 0x00) {
                    char header_lower = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
                    char header_upper = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];

                    for (RendererCommand command : RendererCommand.values()) {
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

                        for (int index = 0; index < LINE_ARRAY.length; index++) {
                            LINE_ARRAY[index] = new StringBuilder();
                        }
                        SHOULD_UPDATE = false;
                    }
                }
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

    public enum RendererCommand {
        WAIT_FOR_INPUT(new char[]{0x01, 0x00}, 0, arguments -> {
            // NOP
        }),

        PAUSE_FOR_FRAMES(new char[]{0x01, 0x01}, 1, arguments -> {
            PAUSE_DURATION_MAX = arguments[0];
            IS_PAUSED = true;
        }),

        SET_LINE_SPEED(new char[]{0x01, 0x02}, 1, arguments -> {
            LINE_SPEED = arguments[0] == 0x00 ? 0 : arguments[0]; // TODO: line speed config
        })

        ;

        private final char[] header;
        private final int arguments;
        private final Executor executor;

        RendererCommand(char[] header, int arguments, Executor executor) {
            this.header = header;
            this.arguments = arguments;
            this.executor = executor;
        }

        @FunctionalInterface
        public interface Executor {
            void run(char... arguments);
        }
    }

    public record Message(String portrait, SoundEvent voiceline, SoundEvent talksound, String... lines) {}
}
