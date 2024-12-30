package xyz.lilyflower.conpri.feature;

import java.util.ArrayList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class Statistics {
    private static final ArrayList<Identifier> STATS = new ArrayList<>();
    private static Identifier add(String name) {
        Identifier identifier = Identifier.of("conpri", name);
        STATS.add(identifier);
        return identifier;
    }

    public static final Identifier STARS_EXPLODED = add("stars_exploded");

    public static void init() {
        for (Identifier stat : STATS) {
            Registry.register(Registries.CUSTOM_STAT, stat.getPath(), stat);
            Stats.CUSTOM.getOrCreateStat(stat);
        }
    }

}
