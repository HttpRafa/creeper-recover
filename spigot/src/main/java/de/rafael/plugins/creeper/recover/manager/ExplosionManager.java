/*
 * Copyright (c) 2022-2023. All rights reserved.
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

package de.rafael.plugins.creeper.recover.manager;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import de.rafael.plugins.creeper.recover.CreeperRecover;
import de.rafael.plugins.creeper.recover.classes.Explosion;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ExplosionManager {

    private final List<Location> suppressedLocations = new ArrayList<>();
    private final List<Explosion> explosionList = new ArrayList<>();

    public void handle(Explosion explosion) {
        this.explosionList.add(explosion);
        explosion.getBlocks().forEach(explodedBlock -> this.suppressBlock(explodedBlock.getLocation()));
        List<Explosion> explosions = Collections.singletonList(explosion);
        Bukkit.getScheduler().runTaskTimerAsynchronously(CreeperRecover.getCreeperRecover(), task -> {
            recoverBlocks(explosions, false, 1);
            if (explosion.isFinished()) task.cancel();
        }, CreeperRecover.getCreeperRecover().getConfigManager().getRecoverDelay(), CreeperRecover.getCreeperRecover().getConfigManager().getRecoverSpeed());
    }

    public int recoverBlocks(int amount) {
        return recoverBlocks(this.explosionList, true, amount);
    }

    public synchronized int recoverBlocks(List<Explosion> explosions, boolean removeFinished, int amount) {
        if (explosions.size() > 0) {
            Iterator<Explosion> iterator = explosions.iterator();
            int recovered = 0;
            while (recovered < amount && iterator.hasNext()) {
                Explosion explosion = iterator.next();
                recovered += explosion.recoverBlocks(amount);
                if (removeFinished && explosion.isFinished()) iterator.remove();
            }
            return recovered;
        } else {
            return 0;
        }
    }

    public boolean hasSuppressedBlocks() {
        return this.suppressedLocations.size() > 0;
    }

    public void suppressBlock(Location location) {
        this.suppressedLocations.add(location);
    }

    public void freeBlock(Location location) {
        this.suppressedLocations.remove(location);
    }

    public boolean isBlockSuppressed(Location location) {
        return this.suppressedLocations.contains(location);
    }

}
