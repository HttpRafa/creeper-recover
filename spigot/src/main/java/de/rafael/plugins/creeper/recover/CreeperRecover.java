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

package de.rafael.plugins.creeper.recover;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import de.rafael.plugins.creeper.recover.command.RecoverCommand;
import de.rafael.plugins.creeper.recover.command.tab.RecoverCommandTabCompleter;
import de.rafael.plugins.creeper.recover.manager.ConfigManager;
import de.rafael.plugins.creeper.recover.listener.BlockPhysicsListener;
import de.rafael.plugins.creeper.recover.listener.EntityExplodeListener;
import de.rafael.plugins.creeper.recover.manager.ExplosionManager;
import de.rafael.plugins.creeper.recover.manager.MessageManager;
import de.rafael.plugins.creeper.recover.stats.PluginStats;
import de.rafael.plugins.creeper.recover.utils.version.PluginVersion;
import de.rafael.plugins.creeper.recover.utils.version.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class CreeperRecover extends JavaPlugin {

    private static CreeperRecover creeperRecover;
    private static PluginVersion version;

    private ExplosionManager explosionManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private PluginStats pluginStats;

    private UpdateChecker updateChecker;

    public static PluginVersion getVersion() {
        return version;
    }

    @Override
    public void onLoad() {
        creeperRecover = this;
        version = new PluginVersion().from(getDescription().getVersion());
        this.messageManager = new MessageManager();
        this.messageManager.load();

        Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7Loading §b" + getDescription().getName() + " §7version §3" + version.toString());

        this.configManager = new ConfigManager();
        this.pluginStats = new PluginStats();
        this.pluginStats.load();

        int amount = 0;
        while (!configManager.load()) {
            amount++;
        }
        Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7The config §aloaded §7in §b" + amount + " §7cycles§8.");
    }

    @Override
    public void onDisable() {
        int recovered = this.getExplosionManager().recoverBlocks(Integer.MAX_VALUE);
        Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7The plugin recovered §b" + recovered + " §7blocks before the server §cstops§8.");

        this.pluginStats.save();
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
            metrics.addCustomChart(new SingleLineChart("blocksRecovered", () -> this.pluginStats.getBlocksRecovered()));
            metrics.addCustomChart(new SingleLineChart("explosionsRecovered", () -> this.pluginStats.getExplosionsRecovered()));
        }
        if(!this.configManager.isIgnoreUpdates()) {
            this.updateChecker.isLatestVersion(version, aBoolean -> {
                if (!aBoolean) {
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§8--------------------------------------");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + " ");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7The plugin §bCreeperRecover §7has an §aupdate§8.");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7Current Version§8: §3" + getDescription().getVersion() + " §7Latest Version§8: §a" + this.updateChecker.getLatestVersion());
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + " ");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§cThe older version may contain bugs that could lead to item loss§8.");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + " ");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§8--------------------------------------");
                } else {
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7The §bCreeperRecover §7plugin is §aup to date§8.");
                }
            });
        }

        // Commands
        Objects.requireNonNull(getCommand("recover")).setExecutor(new RecoverCommand());
        Objects.requireNonNull(getCommand("recover")).setTabCompleter(new RecoverCommandTabCompleter());

        // Events
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPhysicsListener(), this);

        // Tasks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> this.pluginStats.tick(), 0, 20 * 60 * 5);
    }

    public ExplosionManager getExplosionManager() {
        return explosionManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public PluginStats getPluginStats() {
        return pluginStats;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

}
