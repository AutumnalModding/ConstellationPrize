package xyz.lilyflower.epsilon.text;

import java.util.Arrays;
import java.util.HashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.epsilon.client.display.util.MagicNumbers;
import xyz.lilyflower.epsilon.init.EPSILONClient;
import xyz.lilyflower.epsilon.text.command.AEC;
import xyz.lilyflower.epsilon.text.command.VPC;
import xyz.lilyflower.epsilon.text.util.ReadaheadParser;

import static xyz.lilyflower.epsilon.text.EngineState.*;

public class TextEngine {
    private static final Logger LOGGER = LogManager.getLogger("EPSILON Text Engine");

    public static void load(Message message) {
        EngineState.setPosition(MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE + 6, MagicNumbers.PORTRAIT_Y + 1);

        LINE_INDEX = 0;
        LINE_POSITION = 0;
        LINE_CONTENT.clear();

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
            EPSILONClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(message.voiceline, 1.0F, 1.0F));
        }

        STATUS = Status.RUNNING;
        CURRENT_MESSAGE = message;
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
                    LINE_DELTA += counter.getDynamicDeltaTicks();

                    if (LINE_DELTA > LINE_SPEED) {
                        LINE_DELTA = 0;

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
                                char upper = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
                                char lower = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
                                AEC.Type[] types = AEC.Type.values();
                                AEC command = AEC.get(types[Math.clamp(upper - 1, 0, types.length)], lower);

                                int argc = switch (command) {
                                    case VPC variable -> {
                                        int absolute = Math.abs(variable.argc);
                                        char[] readahead = new char[absolute];
                                        System.arraycopy(LINE_CONTENT.get(LINE_INDEX), LINE_POSITION + 1, readahead, 0, absolute);
                                        yield variable.argn.applyAsInt(readahead) + absolute;
                                    }

                                    case AEC ignored -> command.argc;
                                };

                                char[] arguments = new char[argc];
                                for (int arg = 0; arg < arguments.length; arg++) {
                                    arguments[arg] = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
                                }

                                command.executor.run(arguments);
                            } else {
                                LINE_ARRAY[LINE_INDEX].append(next);
                                if (CURRENT_MESSAGE.talksound != null) {
                                    EPSILONClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(CURRENT_MESSAGE.talksound, 1.0F, 1.0F));
                                }
                            }

                            LINE_POSITION++;
                        } catch (IndexOutOfBoundsException exception) {
                            try {
                                StackTraceElement[] elements = exception.getStackTrace();

                                //noinspection DataFlowIssue
                                if (elements[0].getLineNumber() == 100 && elements[0].getFileName().equals("Preconditions.java")) { // This just kind of happens?
                                    break;
                                }

                                whoSetUsUpTheBomb(exception);
                                LINE_INDEX = 0;
                                LINE_CONTENT.clear();
                                CHARACTER_COLOURS.clear();

                                for (int index = 0; index < LINE_ARRAY.length; index++) {
                                    LINE_ARRAY[index] = new StringBuilder();
                                }
                                STATUS = Status.INACTIVE;
                            } catch (ArrayIndexOutOfBoundsException ohno) {
                                LOGGER.fatal("You fucked up HARD. No stacktrace detected.");
                            } catch (NullPointerException ignored) {}
                        }
                    }
                } catch (ArithmeticException ignored) {}
            }

            case PAUSED -> {
                PAUSE_DELTA += counter.getDynamicDeltaTicks();
                if (PAUSE_DELTA > LINE_SPEED) {
                    PAUSE_DELTA = 0;
                    PAUSE_DURATION_ELAPSED++;

                    if (PAUSE_DURATION_ELAPSED >= PAUSE_DURATION_MAX) {
                        STATUS = Status.RUNNING;
                        PAUSE_DURATION_MAX = 0;
                        PAUSE_DURATION_ELAPSED = 0;
                    }
                }
            }

            case WAITING -> {
                try {
                    BLINK_DELTA_DISPLAY++;
                    if (BLINK_DELTA_DISPLAY % (EPSILONClient.CLIENT_INSTANCE.getCurrentFps() / 48) == 0) {
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

    @SuppressWarnings("DataFlowIssue")
    private static void whoSetUsUpTheBomb(Exception exception) {
        ClientPlayerEntity player = EPSILONClient.CLIENT_INSTANCE.player;
        String accusation = FabricLoader.getInstance().isDevelopmentEnvironment() ? I18n.translate("chat.epsilon.accusation_dev") : I18n.translate("chat.epsilon.accusation_prod");
        player.sendMessage(Text.translatable("chat.epsilon.fuckup_1", accusation).formatted(Formatting.RED), false);
        player.sendMessage(Text.translatable("chat.epsilon.fuckup_2", LINE_INDEX, LINE_POSITION).formatted(Formatting.RED), false);
        player.sendMessage(Text.translatable("chat.epsilon.fuckup_3", exception.getMessage()).formatted(Formatting.RED), false);
        String where = "[NO DATA]";
        try {
            StackTraceElement element = exception.getStackTrace()[0];
            where = element.getFileName() + ":" + element.getLineNumber();
        } catch (ArrayIndexOutOfBoundsException ignored) {}
        player.sendMessage(Text.translatable("chat.epsilon.fuckup_4", where).formatted(Formatting.RED), false);
        player.sendMessage(Text.literal(""), false);
    }

    @SuppressWarnings("unused")
    public static class LineSpeed {
        public static final float FAST = 0.75f;
        public static final float MEDIUM = 1.25f;
        public static final float SLOW = 1.75f;
        public static final float SNAIL = 2.5f;
        public static final float VISCOUS = 3.25f;
    }

    public enum Status {
        PAUSED,
        HALTED,
        WAITING,
        RUNNING,
        INACTIVE
    }

    public record Message(String portrait, SoundEvent voiceline, SoundEvent talksound, String... lines) {
        public static final HashMap<Character, Message> REGISTRY = new HashMap<>();

        public static void register(char index, Message message) {
            REGISTRY.put(index, message);
        }

        public static String[] translate(String pointer, int lines) {
            String[] text = new String[lines];
            for (int line = 1; line <= lines; line++) {
                String key = "dialogue.epsilon." + pointer + "_" + line;
                text[line - 1] = I18n.translate(key);
            }
            return text;
        }

        public Message(String portrait, SoundEvent voiceline, SoundEvent talksound, String... lines) {
            this.portrait = portrait;
            this.voiceline = voiceline;
            this.talksound = talksound;
            this.lines = ReadaheadParser.parse(lines);
        }

        public Message(String portrait, String pointer, int lines) {
            this(portrait, null, null, translate(pointer, lines));
        }

        public Message(String portrait, SoundEvent sound, boolean type, String pointer, int lines) {
            this(portrait, (!type ? sound : null), (type ? null : sound), translate(pointer, lines));
        }

        public Message(String portrait, SoundEvent voiceline, SoundEvent talksound, String pointer, int lines) {
            this(portrait, voiceline, talksound, translate(pointer, lines));
        }

        public Message(String portrait, SoundEvent sound, boolean type, String... lines) {
            this(portrait, (!type ? sound : null), (type ? null : sound), lines);
        }

        public Message(String portrait, String... lines) {
            this(portrait, null, null, lines);
        }
    }
}

