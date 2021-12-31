package net.rafadev.plugins.creeper.recover.classes;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;

public class ExplodedBlock {

    private Location location;

    private Material material;
    private BlockData data;
    private ExplodedBlockInventory inventory;

    public ExplodedBlock(Location location, Material material, BlockData data, ExplodedBlockInventory inventory) {
        this.location = location;
        this.material = material;
        this.data = data;
        this.inventory = inventory;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public BlockData getData() {
        return data;
    }

    public void setData(BlockData data) {
        this.data = data;
    }

    public ExplodedBlockInventory getInventory() {
        return inventory;
    }

    public void setInventory(ExplodedBlockInventory inventory) {
        this.inventory = inventory;
    }

    public void recover() {
        Block block = this.location.getBlock();
        block.setType(this.material, false);
        block.setBlockData(this.data, false);
        block.getState().update(true, false);
        if(inventory != null) {
            if(block.getState() instanceof Container container) {
                for (Integer slot : inventory.getItems().keySet()) {
                    container.getInventory().setItem(slot, inventory.get(slot).clone());
                }
            }
        }

        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ROOTED_DIRT_PLACE, 0.5f, 1f);
    }

}
