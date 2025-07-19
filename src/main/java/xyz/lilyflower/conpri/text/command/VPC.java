package xyz.lilyflower.conpri.text.command;

import java.util.function.ToIntFunction;

/**
 * Variable Parameters Command
 */
public class VPC extends AEC {
    public final ToIntFunction<char[]> argn;

    private VPC(Type type, int header, int argc, Executor executor, ToIntFunction<char[]> argn) {
        super(type, header, argc, executor);
        this.argn = argn;
    }

    public static VPC init(Type type, int header, int argc, Executor executor, ToIntFunction<char[]> argn) {
        return new VPC(type, header, argc, executor, argn);
    }
}