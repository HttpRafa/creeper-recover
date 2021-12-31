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
import org.bukkit.block.*;

import java.util.*;
import java.util.Comparator;

public class Explosion {

    private final Location location;
    private final List<ExplodedBlock> blocks = new ArrayList<>();

    public Explosion(Location location, List<Block> blocks) {
        this.location = location;
        List<Block> sortedBlocks = blocks.stream().sorted(Comparator.comparingDouble(item -> ((Block)item).getLocation().distance(location)).reversed()).toList();
        for (Block block : sortedBlocks) {
            if (block.getType() == Material.TNT) {
                continue;
            }
            ExplodedBlockInventory inventory = null;
            if(block.getState() instanceof Container container) {
                inventory = new ExplodedBlockInventory();
                for (int i = 0; i < container.getInventory().getStorageContents().length; i++) {
                    if(container.getInventory().getStorageContents()[i] != null) {
                        inventory.set(i, container.getInventory().getStorageContents()[i].clone());
                    }
                }
            }
            this.blocks.add(new ExplodedBlock(block.getLocation().clone(), block.getType(), block.getBlockData().clone(), inventory));
        }
    }

    public void recoverBlock() {
        Iterator<ExplodedBlock> iterator = this.blocks.iterator();
        ExplodedBlock block = iterator.next();
        block.recover();
        iterator.remove();
    }

    public boolean isFinished() {
        return this.blocks.size() == 0;
    }

    public void cleanUp() {
    }

    public List<ExplodedBlock> getBlocks() {
        return blocks;
    }

    public Location getLocation() {
        return location;
    }

}
