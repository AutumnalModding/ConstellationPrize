package xyz.lilyflower.conpri.init;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.conpri.feature.Statistics;
import xyz.lilyflower.conpri.item.NeuralInterfaceGlasses;

public class ConstellationPrize implements ModInitializer {
    public static final Item NEURAL_INTERFACE = new NeuralInterfaceGlasses();
    public static final Logger LOGGER = LogManager.getLogger("Constellation Prize");

    @Override
    public void onInitialize() {
        Statistics.init();

        Registry.register(Registries.ITEM, Identifier.of("conpri", "neural_interface"), NEURAL_INTERFACE);
    }
}