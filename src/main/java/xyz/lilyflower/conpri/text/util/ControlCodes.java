package xyz.lilyflower.conpri.text.util;

public class ControlCodes {
    private ControlCodes() {}

    public static String BEGIN_CONTROL_CODE = "\000";
    public static String WAIT_FOR_INPUT = BEGIN_CONTROL_CODE + "\001\000";
    public static String PAUSE_FOR_FRAMES = BEGIN_CONTROL_CODE + "\001\001";
    public static String CHANGE_LINE_SPEED = BEGIN_CONTROL_CODE + "\001\002";
    public static String HALT = BEGIN_CONTROL_CODE + "\001\003";
    public static String COLOUR_AT_INDEX = BEGIN_CONTROL_CODE + "\002\000";

    public static String colour(int index, int line, int red, int green, int blue) {
        return String.valueOf((char) red) +
                (char) green +
                (char) blue +
                (char) index +
                (char) line;
    }
}
