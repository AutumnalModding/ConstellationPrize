package xyz.lilyflower.conpri.feature;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static xyz.lilyflower.conpri.feature.TextEngine.ParserVariables.*;

public class TextEngine {
    public static class ParserVariables {
        public static int LINE_COUNT = 0;
        public static int LINE_POSITION = 0;
        public static int LINE_INDEX = 0;
        public static int LINE_SPEED = Parser.Speed.FAST;
        public static int LINE_DELTA = 0;
        public static StringBuilder[] LINE_ARRAY;
        public static ArrayList<char[]> LINE_CONTENT = new ArrayList<>();
        public static HashMap<ImmutablePair<Integer, Integer>, Integer> COLOURS = new HashMap<>();
        public static int DRAW_POSITION_X = 0;
        public static int DRAW_POSITION_Y = 0;
        public static Parser.State STATE = Parser.State.INACTIVE;
        public static int PAUSE_DURATION_MAX = 0;
        public static int PAUSE_DURATION_ELAPSED = 0;
    }

    public static class Parser {
        @SuppressWarnings("unused")
        public enum State {
            PAUSED,
            RUNNING,
            WAITING,
            INACTIVE
        }

        @SuppressWarnings("unused")
        public static class Speed {
            public static final int FAST = 2;
            public static final int MEDIUM = 4;
            public static final int SLOW = 6;
            public static final int SNAIL = 8;
            public static final int VISCOUS = 10;
        }


        public static void init(String... lines) { // TODO: Replace this with a registry system
            ArrayList<char[]> content = LINE_CONTENT;
            StringBuilder[] array = new StringBuilder[lines.length];

            LINE_COUNT = lines.length;
            for (int line = 0; line < lines.length; line++) {
                array[line] = new StringBuilder();

                char[] text = new char[lines[line].length()];
                for (int i = 0; i < text.length; i++) {
                    text[i] = lines[line].charAt(i);
                }

                content.add(text);
            }

            STATE = State.RUNNING;
            LINE_ARRAY = array;
        }

        public static void update(DrawContext context) {

//            for (int line = 0; line <= LINE_INDEX; line++) {
//                int offset = 0;
//                String[] text = LINE_ARRAY[line].toString().split("");
//                for (int index = 0; index < text.length; index++) {
//                    String character = text[index];
//                    int colour = CHARACTER_COLOURS.getOrDefault(new ImmutablePair<>(index, line), 0xFFFFFF);
//
//                    context.drawText(VANILLA_RENDERER, character, DRAW_POSITION_X + offset, DRAW_POSITION_Y + (line * 11), colour, true);
//                    offset += VANILLA_RENDERER.getWidth(character);
//                }
//            }

            switch (STATE) {
                case RUNNING -> __RunNormal();
                case PAUSED -> __RunPaused();
            }
        }

        private static void __ParseControlCode() {
            char header = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];

            for (ControlCode command : ControlCode.REGISTRY) {
                if (command.header == header) {
                    char[] arguments = new char[command.arguments];
                    for (int arg = 0; arg < arguments.length; arg++) {
                        arguments[arg] = LINE_CONTENT.get(LINE_INDEX)[++LINE_POSITION];
                    }
                    command.executor.run(arguments);

                    break;
                }
            }
        }

        private static void __IncrementIndex() {
            LINE_POSITION++;

            if (LINE_POSITION >= LINE_CONTENT.get(LINE_INDEX).length) {
                LINE_POSITION = 0;
                LINE_INDEX++;

                if (LINE_INDEX >= LINE_COUNT) {
                    LINE_INDEX = 0;
                    LINE_CONTENT.clear();
                    COLOURS.clear();

                    for (int index = 0; index < LINE_ARRAY.length; index++) {
                        LINE_ARRAY[index] = new StringBuilder();
                    }

                    STATE = State.INACTIVE;
                }
            }
        }

        private static void __RunNormal() {
            int index = LINE_INDEX;
            int pos = LINE_POSITION;
            StringBuilder[] lines = LINE_ARRAY;
            ArrayList<char[]> content = LINE_CONTENT;

            LINE_DELTA++;

            if (LINE_DELTA % LINE_SPEED == 0) {
                char next = content.get(index)[pos];
                if (next == 0x00) { __ParseControlCode(); } else { lines[index].append(next); }
                __IncrementIndex();
            }
        }

        private static void __RunPaused() {
            PAUSE_DURATION_ELAPSED++;

            if (PAUSE_DURATION_ELAPSED >= PAUSE_DURATION_MAX) {
                STATE = State.RUNNING;
                PAUSE_DURATION_MAX = 0;
                PAUSE_DURATION_ELAPSED = 0;
            }
        }
    }

    @SuppressWarnings("unused") // for the love of fuck IDEA please shush :3
    public static class ControlCode {
        public static final ArrayList<ControlCode> REGISTRY = new ArrayList<>();

        @FunctionalInterface
        public interface Executor {
            void run(char... arguments);
        }

        public final char header;
        public final int arguments;
        public final Executor executor;

        private ControlCode(int header, int arguments, Executor executor) {
            this.header = (char) header;
            this.arguments = arguments;
            this.executor = executor;

            REGISTRY.add(this);
        }

        public static final ControlCode HALT_WITH_PROMPT = new ControlCode(0x03, 0x0, arguments -> {}); // TODO: Window system
        public static final ControlCode TOGGLE_FLAG_ENABLED = new ControlCode(0x04, 0x01, arguments -> {}); // TODO: Event flags
        public static final ControlCode TOGGLE_FLAG_DISABLED = new ControlCode(0x05, 0x01, arguments -> {}); // TODO: Event flags
        public static final ControlCode FLAG_BASED_JUMP = new ControlCode(0x06, 0x03, arguments1 -> {}); // TODO: Text registry
        public static final ControlCode GET_FLAG_STATE = new ControlCode(0x07, 0x01, arguments -> {}); // TODO: Window memory system
        public static final ControlCode CALL_ADDRESS = new ControlCode(0x08, 0x02, arguments -> {}); // TODO: Text registry
        // [09 XX (YY YY YY YY)] -> not fucking doing multiple address jump. nope. fuck that. not happening.
        public static final ControlCode JUMP_ADDRESS = new ControlCode(0x0A, 0x02, arguments -> {}); // TODO: Text registry
        public static final ControlCode BOOLEAN_EQUALITY_TRUE = new ControlCode(0x0B, 0x01, arguments -> {}); // TODO: Window memory system
        public static final ControlCode BOOLEAN_EQUALITY_FALSE = new ControlCode(0x0C, 0x01, arguments -> {}); // TODO: Window memory system
        public static final ControlCode COPY_TO_ARGMEM = new ControlCode(0x0D, 0x01, arguments -> {}); // TODO: Window memory system
        public static final ControlCode STORE_TO_SECMEM = new ControlCode(0x0E, 0x01, arguments -> {}); // TODO: Window memory system
        public static final ControlCode PAUSE_FOR_FRAMES = new ControlCode(0x10, 0x01, arguments -> {
            PAUSE_DURATION_MAX = arguments[0];
            STATE = Parser.State.PAUSED;
        });
        // [11] -> not doing menus. FUCK that. (ok I might do it later idk)
        public static final ControlCode CLEAR_LINE = new ControlCode(0x12, 0x00, arguments -> {});
        public static final ControlCode HALT_WITHOUT_PROMPT = new ControlCode(0x13, 0x00, arguments -> {}); // TODO: Window system
        // [14] -> No need for a second Halt Parsing With Prompt control code. This is a relic of EB's weird-ass parser.
        // [15] -> No need for compressed text blocks. ROM space isn't a thing we have to worry about~
        // [16] -> See above
        // [17] -> See above

    }

//    public enum ParserCommand { // TODO: document these properly
//        WAIT_FOR_INPUT(new char[]{0x01, 0x00}, 0, arguments -> STATE = Parser.State.WAITING),
//
//        PAUSE_FOR_FRAMES(new char[]{0x01, 0x01}, 1, arguments -> {
//            PAUSE_DURATION_MAX = arguments[0];
//            STATE = Parser.State.PAUSED;
//        }),
//
//        SET_LINE_SPEED(new char[]{0x01, 0x02}, 1, arguments -> {
//            LINE_SPEED = arguments[0] == 0x00 ? 0 : arguments[0]; // TODO: line speed config
//        }),
//
//        SET_COLOUR(new char[]{0x02, 0x00}, 5, arguments -> {
//            int red = arguments[0];
//            int green = arguments[1];
//            int blue = arguments[2];
//            int index = arguments[3];
//            int line = arguments[4];
//
//            int colour = red << 16 | green << 8 | blue;
//
//            COLOURS.put(new ImmutablePair<>(index, line), colour);
//        }),
//
//        CLEAR_COLOURS(new char[]{0x02, 0x01}, 0, arguments -> {
//            COLOURS.clear();
//        })
//
//        ;
//        private final char[] header;
//        private final int arguments;
//
//        private final Executor executor;
//
//        ParserCommand(char[] header, int arguments, Executor executor) {
//            this.header = header;
//            this.arguments = arguments;
//            this.executor = executor;
//        }
//
//        @FunctionalInterface
//        public interface Executor {
//            void run(char... arguments);
//        }
//    }
}
