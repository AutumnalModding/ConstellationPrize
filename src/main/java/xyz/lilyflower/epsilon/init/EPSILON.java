package xyz.lilyflower.epsilon.init;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.epsilon.text.TextEngine;
import xyz.lilyflower.epsilon.util.Statistics;
import xyz.lilyflower.epsilon.item.NeuralInterfaceGlasses;

public class EPSILON implements ModInitializer {
    public static final Item NEURAL_INTERFACE = new NeuralInterfaceGlasses();
    public static final Logger LOGGER = LogManager.getLogger("EPSILON");

    @Override
    public void onInitialize() {
        Statistics.init();

        Registry.register(Registries.ITEM, Identifier.of("epsilon", "neural_interface"), NEURAL_INTERFACE);


        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            TextEngine.Message.register((char) 0x00, new TextEngine.Message("textures/gui/portrait/pancakes.png", "debug", 5));
            TextEngine.Message.register((char) 0x01, new TextEngine.Message("textures/gui/portrait/pancakes.png", "multidebug", 2));
            TextEngine.Message.register((char) 0x02, new TextEngine.Message("textures/gui/portrait/pancakes.png", "test", 1));
        });
    }
}