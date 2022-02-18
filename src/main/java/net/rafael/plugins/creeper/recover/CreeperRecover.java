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
import net.rafael.plugins.creeper.recover.stats.Metrics;
import net.rafael.plugins.creeper.recover.update.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CreeperRecover extends JavaPlugin {

    private static CreeperRecover creeperRecover;

    private final String prefix = "§8➜ §3C§breeperRecover §8● §7";

    private ExplosionManager explosionManager;
    private ConfigManager configManager;

    private UpdateChecker updateChecker;

    @Override
    public void onLoad() {
        creeperRecover = this;

        this.configManager = new ConfigManager();
        while(!configManager.load()) {}
    }

    @Override
    public void onEnable() {
        this.explosionManager = new ExplosionManager();
        this.updateChecker = new UpdateChecker(98836);
        if(this.configManager.isbStats()) {
            int pluginId = 14155;
            Metrics metrics = new Metrics(this, pluginId);
        }
        if(!this.configManager.isIgnoreUpdates()) {
            this.updateChecker.isLastestVersion(getDescription().getVersion(), aBoolean -> {
                if(!aBoolean) {
                    Bukkit.getConsoleSender().sendMessage(prefix + "§8--------------------------------------");
                    Bukkit.getConsoleSender().sendMessage(prefix + " ");
                    Bukkit.getConsoleSender().sendMessage(prefix + "§7The plugin §bCreeperRecover §7has an §aupdate§8.");
                    Bukkit.getConsoleSender().sendMessage(prefix + "§7Current Version§8: §3" + getDescription().getVersion() + " §7Latest Version§8: §a" + this.updateChecker.getLatestVersion());
                    Bukkit.getConsoleSender().sendMessage(prefix + " ");
                    Bukkit.getConsoleSender().sendMessage(prefix + "§cThe older version may contain bugs that could lead to item loss§8.");
                    Bukkit.getConsoleSender().sendMessage(prefix + " ");
                    Bukkit.getConsoleSender().sendMessage(prefix + "§8--------------------------------------");
                } else {
                    Bukkit.getConsoleSender().sendMessage(prefix + "§7The §bCreeperRecover §7plugin is §aup to date§8.");
                }
            });
        }

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

    public String getPrefix() {
        return prefix;
    }

    public ExplosionManager getExplosionManager() {
        return explosionManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

}
