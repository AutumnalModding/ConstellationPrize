package xyz.lilyflower.conpri.text;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.conpri.client.display.module.DialogueModule;
import xyz.lilyflower.conpri.init.ConstellationPrize;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;
import xyz.lilyflower.conpri.text.command.AEC;
import xyz.lilyflower.conpri.text.command.VPC;

import static xyz.lilyflower.conpri.text.EngineState.*;

public class TextEngine {
    private static final Logger LOGGER = LogManager.getLogger("Constellation Prize Text Engine");

    public static void load(Message message) {
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
//                            System.out.println("Character Value: " + (int) next);

                            if (next == 0x00) {
                                char upper = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
//                                System.out.println("Upper Value: " + (int) upper);
                                char lower = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
//                                System.out.println("Lower Value: " + (int) lower);
                                AEC.Type[] types = AEC.Type.values();
                                AEC command = AEC.get(types[Math.clamp(upper - 1, 0, types.length)], lower);

                                int argc = switch (command) {
                                    case VPC variable -> {
                                        int absolute = Math.abs(variable.argc);
                                        System.out.println("Reading ahead " + absolute + " times");
                                        char[] readahead = new char[absolute];
                                        for (int param = 0; param <= absolute; param++) {
                                            System.out.println("Readahead");
                                            readahead[param] = LINE_CONTENT.get(LINE_INDEX)[LINE_POSITION + (param + 1)];
                                        }
                                        yield variable.argn.applyAsInt(readahead) + absolute;
                                    }

                                    case AEC ignored -> command.argc;
                                };

                                char[] arguments = new char[argc];
                                for (int arg = 0; arg < arguments.length; arg++) {
                                    arguments[arg] = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
//                                    System.out.println("Argument Value: " + (int) arguments[arg]);
                                }

                                command.executor.run(arguments);
                            } else {
                                LINE_ARRAY[LINE_INDEX].append(next);
                                if (CURRENT_MESSAGE.talksound != null) {
                                    ConstellationPrizeClient.CLIENT_INSTANCE.getSoundManager().play(PositionedSoundInstance.master(CURRENT_MESSAGE.talksound, 1.0F, 1.0F));
                                }
                            }

                            LINE_POSITION++;
                        } catch (IndexOutOfBoundsException exception) {
                            whoSetUsUpTheBomb(exception);
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

    private static void whoSetUsUpTheBomb(Exception exception) {
        String accusation = FabricLoader.getInstance().isDevelopmentEnvironment() ? I18n.translate("chat.conpri.accusation_dev") : I18n.translate("chat.conpri.accusation_prod");
        ClientPlayerEntity player = ConstellationPrizeClient.CLIENT_INSTANCE.player;
        player.sendMessage(Text.translatable("chat.conpri.fuckup_1", accusation).formatted(Formatting.RED), false);
        player.sendMessage(Text.translatable("chat.conpri.fuckup_2", LINE_INDEX, LINE_POSITION).formatted(Formatting.RED), false);
        StackTraceElement element = exception.getStackTrace()[0];
        player.sendMessage(Text.translatable("chat.conpri.fuckup_3", exception.getMessage()).formatted(Formatting.RED), false);
        player.sendMessage(Text.translatable("chat.conpri.fuckup_4", element.getFileName() + ", line " + element.getLineNumber()).formatted(Formatting.RED), false);
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

    public record Message(String portrait, SoundEvent voiceline, SoundEvent talksound, String... lines) {}
}

