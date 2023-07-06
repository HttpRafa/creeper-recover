/*
 * Copyright (c) 2023. All rights reserved.
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

package de.rafael.plugins.creeper.recover.common;

import de.rafael.plugins.creeper.recover.common.command.RecoverCommand;
import de.rafael.plugins.creeper.recover.common.listener.BlockPhysicsListener;
import de.rafael.plugins.creeper.recover.common.listener.EntityExplodeListener;
import de.rafael.plugins.creeper.recover.common.manager.ConfigManager;
import de.rafael.plugins.creeper.recover.common.manager.ExplosionManager;
import de.rafael.plugins.creeper.recover.common.manager.MessageManager;
import de.rafael.plugins.creeper.recover.common.stats.PluginStats;
import de.rafael.plugins.creeper.recover.common.utils.version.PluginVersion;
import de.rafael.plugins.creeper.recover.common.utils.version.UpdateChecker;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Rafael K.
 * @since 06/07/2023
 */

@Getter
public abstract class CreeperPlugin extends JavaPlugin {

    @Getter
    protected static PluginVersion version;
    @Getter
    private static CreeperPlugin instance;
    protected Scheduler scheduler;

    protected ExplosionManager explosionManager;
    protected ConfigManager configManager;
    protected MessageManager messageManager;
    protected PluginStats pluginStats;

    protected UpdateChecker updateChecker;

    public static Scheduler scheduler() {
        return instance.scheduler;
    }

    @Override
    public void onLoad() {
        instance = this;
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
    public void onEnable() {
        this.explosionManager = new ExplosionManager();
        this.updateChecker = new UpdateChecker(98836);
        if (this.configManager.bStats()) {
            int pluginId = 14155;
            Metrics metrics = new Metrics(this, pluginId);
            metrics.addCustomChart(new SingleLineChart("blocksRecovered", () -> this.pluginStats.blocksRecovered()));
            metrics.addCustomChart(new SingleLineChart("explosionsRecovered", () -> this.pluginStats.explosionsRecovered()));
        }
        if (!this.configManager.ignoreUpdates()) {
            this.updateChecker.isLatestVersion(version, aBoolean -> {
                if (!aBoolean) {
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§8--------------------------------------");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + " ");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7The plugin §bCreeperRecover §7has an §aupdate§8.");
                    Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7Current Version§8: §3" + getDescription().getVersion() + " §7Latest Version§8: §a" + this.updateChecker.latestVersion());
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
        registerCommand("recover", "Command to control the plugin", RecoverCommand.class);

        // Events
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPhysicsListener(), this);
    }

    @Override
    public void onDisable() {
        int recovered = this.explosionManager.recoverBlocks(Integer.MAX_VALUE);
        Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§7The plugin recovered §b" + recovered + " §7blocks before the server §cstops§8.");

        this.pluginStats.save();
    }

    private void registerCommand(String name, String description, @NotNull Class<? extends CommandExecutor> commandClass) {
        try {
            var constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand pluginCommand = constructor.newInstance(name, this);
            constructor.setAccessible(false);
            var commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(name, name, pluginCommand);
            commandMapField.setAccessible(false);

            CommandExecutor command = commandClass.getDeclaredConstructor().newInstance();
            pluginCommand.setDescription(description);
            pluginCommand.setExecutor(command);
            if (TabCompleter.class.isAssignableFrom(commandClass)) {
                pluginCommand.setTabCompleter((TabCompleter) command);
            }
        } catch (Throwable throwable) {
            Bukkit.getConsoleSender().sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§cFailed to register command§8!");
            throwable.printStackTrace();
        }
    }

    public interface Scheduler {

        void runOnCorrectThread(Location location, Runnable runnable);

        void runAsync(Runnable runnable);

        void runAsyncAtFixedRate(Consumer<Runnable> runnable, int delay, int period, TimeUnit unit);

    }

}
