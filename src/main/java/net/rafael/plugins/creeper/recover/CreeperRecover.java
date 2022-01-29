package net.rafael.plugins.creeper.recover;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import net.rafael.plugins.creeper.recover.config.ConfigManager;
import net.rafael.plugins.creeper.recover.listener.ExplosionListener;
import net.rafael.plugins.creeper.recover.manager.ExplosionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CreeperRecover extends JavaPlugin {

    private static CreeperRecover creeperRecover;

    private ExplosionManager explosionManager;
    private ConfigManager configManager;

    @Override
    public void onLoad() {
        this.configManager = new ConfigManager();
        while(!configManager.load()) {}
    }

    @Override
    public void onEnable() {
        creeperRecover = this;

        this.explosionManager = new ExplosionManager();

        Bukkit.getPluginManager().registerEvents(new ExplosionListener(), this);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            explosionManager.recoverBlock();
        }, 0, this.configManager.getRecoverSpeed());
    }

    @Override
    public void onDisable() {

    }

    public static CreeperRecover getCreeperRecover() {
        return creeperRecover;
    }

    public ExplosionManager getExplosionManager() {
        return explosionManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}
