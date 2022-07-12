package de.rafael.plugins.creeper.recover.classes.data;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ContainerItems implements IBlockData {

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

    @Override
    public void apply(Block block, RecoverPhase phase) {
        if(phase == RecoverPhase.POST_STATE_UPDATE && block.getState() instanceof Container container) {
            for (Integer slot : items.keySet()) {
                container.getInventory().setItem(slot, items.get(slot).clone());
            }
        }
    }

}
