package net.rafael.plugins.creeper.recover;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import net.rafael.plugins.creeper.recover.command.RecoverCommand;
import net.rafael.plugins.creeper.recover.command.tab.RecoverCommandTabCompleter;
import net.rafael.plugins.creeper.recover.config.ConfigManager;
import net.rafael.plugins.creeper.recover.listener.EntityExplodeListener;
import net.rafael.plugins.creeper.recover.manager.ExplosionManager;
import net.rafael.plugins.creeper.recover.stats.Metrics;
import net.rafael.plugins.creeper.recover.stats.PluginStats;
import net.rafael.plugins.creeper.recover.update.PluginVersion;
import net.rafael.plugins.creeper.recover.update.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class CreeperRecover extends JavaPlugin {

    private static CreeperRecover creeperRecover;
    private static PluginVersion version;

    private final String prefix = "§8➜ §3C§breeperRecover §8● §7";

    private ExplosionManager explosionManager;
    private ConfigManager configManager;
    private PluginStats pluginStats;

    private UpdateChecker updateChecker;

    private boolean paused = false;

    public static PluginVersion getVersion() {
        return version;
    }

    @Override
    public void onLoad() {
        creeperRecover = this;
        version = new PluginVersion().from(getDescription().getVersion());

        Bukkit.getConsoleSender().sendMessage(prefix + "§7Loading §b" + getDescription().getName() + " §7version §3" + version.toString());

        this.configManager = new ConfigManager();
        this.pluginStats = new PluginStats();
        this.pluginStats.load();

        int amount = 0;
        while (!configManager.load()) {
            amount++;
        }
        Bukkit.getConsoleSender().sendMessage(prefix + "§7The config §aloaded §7in §b" + amount + " §7cycles§8.");
    }

    @Override
    public void onDisable() {
        int recovered = this.getExplosionManager().recoverBlocks();
        Bukkit.getConsoleSender().sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§7The plugin recovered §b" + recovered + " §7blocks before the server §cstops§8.");

        this.pluginStats.save();
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }

    public static CreeperRecover getCreeperRecover() {
        return creeperRecover;
    }

    @Override
    public void onEnable() {
        this.explosionManager = new ExplosionManager();
        this.updateChecker = new UpdateChecker(98836);
        if (this.configManager.isbStats()) {
            int pluginId = 14155;
            Metrics metrics = new Metrics(this, pluginId);
            metrics.addCustomChart(new Metrics.SingleLineChart("blocksRecovered", () -> this.pluginStats.getBlocksRecovered()));
            metrics.addCustomChart(new Metrics.SingleLineChart("explosionsRecovered", () -> this.pluginStats.getExplosionsRecovered()));
        }
        if(!this.configManager.isIgnoreUpdates()) {
            this.updateChecker.isLastestVersion(version, aBoolean -> {
                if (!aBoolean) {
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

        // Commands
        Objects.requireNonNull(getCommand("recover")).setExecutor(new RecoverCommand());
        Objects.requireNonNull(getCommand("recover")).setTabCompleter(new RecoverCommandTabCompleter());

        // Events
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), this);

        // Tasks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (!paused) explosionManager.recoverBlock();
        }, 0, this.configManager.getRecoverSpeed());
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            this.pluginStats.tick();
        }, 0, 20 * 60 * 5);
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

    public PluginStats getPluginStats() {
        return pluginStats;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

}
