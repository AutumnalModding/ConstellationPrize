package xyz.lilyflower.conpri;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import xyz.lilyflower.conpri.item.NeuralInterfaceGlasses;

public class ConstellationPrize implements ModInitializer {
    public static final Item NEURAL_INTERFACE = new NeuralInterfaceGlasses();

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, Identifier.of("constellation_prize", "neural_interface"), NEURAL_INTERFACE);
    }
}