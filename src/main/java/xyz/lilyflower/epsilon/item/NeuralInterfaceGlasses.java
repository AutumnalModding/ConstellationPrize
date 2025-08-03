package xyz.lilyflower.epsilon.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import xyz.lilyflower.epsilon.client.display.DisplayManager;

public class NeuralInterfaceGlasses extends TrinketItem {
    public NeuralInterfaceGlasses() {
        super(new Item.Settings().maxCount(1).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("epsilon", "neural_glasses"))));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);
        DisplayManager.GLASSES_EQUIPPED = true;
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);
        DisplayManager.GLASSES_EQUIPPED = false;
    }
}