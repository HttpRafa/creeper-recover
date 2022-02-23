package net.rafael.plugins.creeper.recover.classes;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import net.rafael.plugins.creeper.recover.CreeperRecover;
import net.rafael.plugins.creeper.recover.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.DoubleChestInventory;

import java.util.*;

public class Explosion {

    private final Location location;
    private final List<ExplodedBlock> blocks = new ArrayList<>();

    public Explosion(Location location, List<Block> blocks) {
        this.location = location;
        List<Block> sortedBlocks = blocks.stream().sorted(Comparator.comparingDouble(item -> ((Block) item).getLocation().distance(location)).reversed()).toList();
        List<Block> ignoredBlocks = new ArrayList<>();
        for (Block block : sortedBlocks) {
            if (ignoredBlocks.stream().anyMatch(item -> item.getLocation().distance(block.getLocation()) < 0.1)) {
                continue;
            }
            if (block.getType() == Material.TNT) {
                TNTPrimed tnt = (TNTPrimed) Objects.requireNonNull(block.getLocation().getWorld()).spawnEntity(MathUtils.toCenterLocation(block.getLocation()), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(MathUtils.generateRandomInteger(10, 30));
                tnt.setVelocity(MathUtils.calculateVectorBetween2Locations(MathUtils.toCenterLocation(location.clone()), MathUtils.toCenterLocation(block.getLocation().clone())).normalize().multiply(0.7));
                continue;
            }
            ExplodedBlockInventory inventory = null;
            if (block.getState() instanceof Container container) {
                inventory = new ExplodedBlockInventory();
                for (int i = 0; i < container.getInventory().getStorageContents().length; i++) {
                    if (container.getInventory().getStorageContents()[i] != null) {
                        inventory.set(i, container.getInventory().getStorageContents()[i].clone());
                    }
                }
            }
            ExplodedBlock explodedBlock = new ExplodedBlock(block.getLocation().clone(), block.getType(), block.getBlockData().clone(), inventory);
            if (block.getState() instanceof Chest chest) {
                if (chest.getInventory() instanceof DoubleChestInventory doubleChestInventory) {
                    DoubleChest doubleChest = doubleChestInventory.getHolder();

                    assert doubleChest != null;
                    Chest leftSide = (Chest) doubleChest.getLeftSide();
                    Chest rightSide = (Chest) doubleChest.getRightSide();
                    assert leftSide != null;
                    assert rightSide != null;
                    if (block.getLocation().distance(leftSide.getBlock().getLocation()) < 0.1) {
                        ignoredBlocks.add(rightSide.getBlock());
                        ExplodedBlock extraChest = new ExplodedBlock(rightSide.getBlock().getLocation().clone(), rightSide.getBlock().getType(), rightSide.getBlock().getBlockData().clone(), null);
                        explodedBlock.connectBlock(extraChest);
                    } else if (block.getLocation().distance(rightSide.getBlock().getLocation()) < 0.1) {
                        ignoredBlocks.add(leftSide.getBlock());
                        ExplodedBlock extraChest = new ExplodedBlock(leftSide.getBlock().getLocation().clone(), leftSide.getBlock().getType(), leftSide.getBlock().getBlockData().clone(), null);
                        explodedBlock.connectBlock(extraChest);
                    }
                }
            }
            this.blocks.add(explodedBlock);
        }
    }

    public void recoverBlock() {
        Iterator<ExplodedBlock> iterator = this.blocks.iterator();
        if (iterator.hasNext()) {
            ExplodedBlock block = iterator.next();
            block.recover();
            try {
                iterator.remove();
            } catch (ConcurrentModificationException ignored) {

            }
        } else {
            this.blocks.clear();
        }
    }

    public int recoverBlocks() {
        int recovered = 0;
        Iterator<ExplodedBlock> iterator = this.blocks.iterator();
        while (iterator.hasNext()) {
            ExplodedBlock block = iterator.next();
            block.recover();
            recovered++;
            iterator.remove();
        }
        return recovered;
    }

    public int recoverBlocks(int amount) {
        int recovered = 0;
        Iterator<ExplodedBlock> iterator = this.blocks.iterator();
        while (iterator.hasNext()) {
            if (recovered >= amount) {
                break;
            }
            ExplodedBlock block = iterator.next();
            block.recover();
            recovered++;
            iterator.remove();
        }
        return recovered;
    }

    public boolean isFinished() {
        return this.blocks.size() == 0;
    }

    public void finished() {
        CreeperRecover.getCreeperRecover().getPluginStats().explosionsRecovered();
    }

    public List<ExplodedBlock> getBlocks() {
        return blocks;
    }

    public Location getLocation() {
        return location;
    }

}
