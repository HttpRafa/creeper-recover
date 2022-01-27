package net.rafael.plugins.creeper.recover.classes;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ExplodedBlockInventory {

    private final HashMap<Integer, ItemStack> items = new HashMap<>();

    public void set(int slot, ItemStack stack) {
        if(items.containsKey(slot)) {
            items.replace(slot, stack);
        } else {
            items.put(slot, stack);
        }
    }

    public ItemStack get(int slot) {
        return items.getOrDefault(slot, null);
    }

    public HashMap<Integer, ItemStack> getItems() {
        return items;
    }

}
