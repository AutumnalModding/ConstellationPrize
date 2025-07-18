package xyz.lilyflower.conpri.text.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ReadaheadParser {
    private static final Predicate<String> IS_CONTROL_CODE = Pattern.compile("[0-9A-F][0-9A-F] [0-9A-F][0-9A-F]").asMatchPredicate();

    public static String[] parse(String... input) {
        String[] parsed = new String[input.length];
        StringBuilder[] builders = new StringBuilder[parsed.length];
        for (int builder = 0; builder < builders.length; builder++) {
            builders[builder] = new StringBuilder();
        }

        int position = 0;
        for (String str : input) {
            char[] text = str.toCharArray();
            for (int index = 0; index < text.length; index++) {
                String target = "";
                if (text[index] == '[') {
                    StringBuilder maybeCode = new StringBuilder();
                    for (int readahead = 1; readahead <= 5; readahead++) {
                        try {
                            char there = text[index + readahead];
                            if (there == ']') break; // fail early for small strings
                            maybeCode.append(there);
                        } catch (IndexOutOfBoundsException ignored) {}
                    }
                    if (IS_CONTROL_CODE.test(String.valueOf(maybeCode))) {
                        target = "";
                        StringBuilder code = new StringBuilder();
                        builders[position].append((char) 0x00);
                        for (int readahead = 1; readahead <= text.length - index; readahead++) {
                            char there = text[index + readahead];
                            if (there == ']') break;
                            code.append(there);
                        }
                        int end = position;
                        for (int pos = 0; pos < code.length(); pos++) {
                            end = pos + 2;
                            try {
                                try {
                                    char segment = (char) Integer.parseInt(code.substring(pos, end), 16);
                                    builders[position].append(segment);
                                } catch (NumberFormatException ignored) {}
                            } catch (IndexOutOfBoundsException ignored) {
                                break;
                            }
                            pos = end;
                        }
                        index += (end + 1);
                    } else {
                        target = "[" + maybeCode;
                        index += maybeCode.length();
                    }
                } else {
                    target = String.valueOf(text[index]);
                }
                builders[position].append(target);
            }
            position++;
        }

        for (int index = 0; index < builders.length; index++) {
            parsed[index] = builders[index].toString();
        }
        return parsed;
    }
}
