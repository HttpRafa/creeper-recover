/*
 * Copyright (c) 2022. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *         this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *     * Neither the name of the developer nor the names of its contributors
 *         may be used to endorse or promote products derived from this software
 *         without specific prior written permission.
 *     * Redistributions in source or binary form must keep the original package
 *         and class name.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.rafael.plugins.creeper.recover.classes;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import de.rafael.plugins.creeper.recover.CreeperRecover;
import de.rafael.plugins.creeper.recover.classes.data.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class ExplodedBlock {

    private Location location;

    // Material, BlockData and other data
    private Material material;
    private BlockData data;
    private final List<IBlockData> otherData = new ArrayList<>();

    // Inventory and connectedBlocks
    private final List<ExplodedBlock> connectedBlocks = new ArrayList<>();

    public ExplodedBlock(Location location, Material material, BlockData data) {
        this.location = location;
        this.material = material;
        this.data = data;
    }

    public void connectBlock(ExplodedBlock block) {
        this.connectedBlocks.add(block);
    }

    public void addData(IBlockData data) {
        this.otherData.add(data);
    }

    public List<ExplodedBlock> getConnectedBlocks() {
        return connectedBlocks;
    }

    public List<IBlockData> getOtherData() {
        return otherData;
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

    public void recover() {
        if (Bukkit.isPrimaryThread()) {
            for (ExplodedBlock connectedBlock : this.connectedBlocks) {
                connectedBlock.recoverBasics();
            }
            recoverBasics();
        } else {
            Bukkit.getScheduler().runTask(CreeperRecover.getCreeperRecover(), () -> {
                for (ExplodedBlock connectedBlock : this.connectedBlocks) {
                    connectedBlock.recoverBasics();
                }
                recoverBasics();
            });
        }
    }

    public void recoverBasics() {
        CreeperRecover.getCreeperRecover().getExplosionManager().free(this.location);
        Block block = this.location.getBlock();
        block.setType(this.material, false);
        block.setBlockData(this.data, false);

        for (IBlockData blockData : this.otherData) {
            blockData.apply(block, IBlockData.RecoverPhase.PRE_STATE_UPDATE);
        }

        block.getState().update(true, false);

        for (IBlockData blockData : this.otherData) {
            blockData.apply(block, IBlockData.RecoverPhase.POST_STATE_UPDATE);
        }

        CreeperRecover.getCreeperRecover().getPluginStats().blocksRecovered();
        block.getWorld().playSound(block.getLocation(), CreeperRecover.getCreeperRecover().getConfigManager().getBlockRecoverSound(), 0.5f, 1f);
    }

}
