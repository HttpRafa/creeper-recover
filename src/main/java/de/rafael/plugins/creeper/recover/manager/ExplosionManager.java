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

package de.rafael.plugins.creeper.recover.manager;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import de.rafael.plugins.creeper.recover.classes.Explosion;
import de.rafael.plugins.creeper.recover.CreeperRecover;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class ExplosionManager {

    private final List<Explosion> explosionList = new ArrayList<>();

    public void handle(Explosion explosion) {
        this.explosionList.add(explosion);
    }

    public void recoverBlock() {
        if(this.explosionList.size() > 0) {
            Bukkit.getScheduler().runTask(CreeperRecover.getCreeperRecover(), () -> {
                Iterator<Explosion> iterator = this.explosionList.iterator();
                if(iterator.hasNext()) {
                    Explosion explosion = iterator.next();
                    explosion.recoverBlock();
                    if (explosion.isFinished()) {
                        explosion.finished();
                        try {
                            iterator.remove();
                        } catch (ConcurrentModificationException ignored) {

                        }
                    }
                }
            });
        }
    }

    public int recoverBlocks() {
        int recovered = 0;
        Iterator<Explosion> iterator = this.explosionList.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();
            recovered += explosion.recoverBlocks();
            if (explosion.isFinished()) {
                explosion.finished();
                iterator.remove();
            }
        }
        return recovered;
    }

    public int recoverBlocks(int amount) {
        int recovered = 0;
        Iterator<Explosion> iterator = this.explosionList.iterator();
        while (iterator.hasNext()) {
            if (recovered >= amount) {
                break;
            }
            Explosion explosion = iterator.next();
            recovered += explosion.recoverBlocks((amount - recovered));
            if (explosion.isFinished()) {
                explosion.finished();
                iterator.remove();
            }
        }
        return recovered;
    }

}
