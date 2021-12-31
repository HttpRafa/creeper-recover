package net.rafadev.plugins.creeper.recover.listener;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import net.rafadev.plugins.creeper.recover.CreeperRecover;
import net.rafadev.plugins.creeper.recover.classes.Explosion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;
import java.util.Objects;

public class ExplosionListener implements Listener {

    @EventHandler
    public void on(EntityExplodeEvent event) {

        if(CreeperRecover.getCreeperRecover().getEntityTypes().contains(event.getEntity().getType()) || CreeperRecover.getCreeperRecover().isRecoverEvery()) {
            List<Block> blocks = event.blockList();
            CreeperRecover.getCreeperRecover().getExplosionManager().handle(new Explosion(event.getLocation().clone(), blocks));

            event.setCancelled(true);
            event.setYield(0);

            Objects.requireNonNull(event.getLocation().getWorld()).playSound(event.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
            event.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, event.getLocation(), 0);

            for (Block block : blocks) {
                block.setType(Material.AIR, false);
            }
        }

    }

}
