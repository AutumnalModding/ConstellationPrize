package xyz.lilyflower.conpri.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.lilyflower.conpri.client.renderer.Display;

public class NeuralInterfaceGlasses extends TrinketItem {
    public NeuralInterfaceGlasses() {
        super(new Item.Settings().maxCount(1));
    }

    public enum Type {
        XM186_MINDSET,
        XM286_LUNA,
        XM386_SOLEIl
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);
        Display.GLASSES_EQUIPPED = true;
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);
        Display.GLASSES_EQUIPPED = false;
    }
}