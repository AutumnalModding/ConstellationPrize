package xyz.lilyflower.conpri.item;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.item.Item;

public class NeuralInterfaceGlasses extends TrinketItem {
    public NeuralInterfaceGlasses() {
        super(new Item.Settings().maxCount(1));
    }

    public enum Type {
        XM186_MINDSET,
        XM287_TSUKIYOMI,
        XM387_AMATERASU
    }
}
