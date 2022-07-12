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
