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

package de.rafael.plugins.creeper.recover.common.command;

//------------------------------
//
// This class was developed by Rafael K.
// On 2/22/2022 at 12:13 AM
// In the project CreeperRecover
//
//------------------------------

import de.rafael.plugins.creeper.recover.common.CreeperPlugin;
import de.rafael.plugins.creeper.recover.common.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecoverCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        MessageManager messageManager = CreeperPlugin.instance().messageManager();
        if (!sender.hasPermission("creeper.recover.command")) {
            sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + messageManager.getMessage(MessageManager.Message.NO_PERMISSION));
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("fix")) {
            try {
                CreeperPlugin.scheduler().runAsync(() -> {
                    if (args[1].equalsIgnoreCase("all")) {
                        int recovered = CreeperPlugin.instance().explosionManager().recoverBlocks(Integer.MAX_VALUE);
                        sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + messageManager.getMessage(MessageManager.Message.BLOCKS_RECOVERED, recovered));
                    } else {
                        int amount = Integer.parseInt(args[1]);
                        int recovered = CreeperPlugin.instance().explosionManager().recoverBlocks(amount);
                        sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + messageManager.getMessage(MessageManager.Message.BLOCKS_RECOVERED, recovered));
                    }
                });
            } catch (NumberFormatException exception) {
                sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + "§c" + exception.getMessage());
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            CreeperPlugin.instance().configManager().load();
            CreeperPlugin.instance().messageManager().load();
            sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + messageManager.getMessage(MessageManager.Message.RELOADED));
        } else if (args.length == 1 && args[0].equalsIgnoreCase("stats")) {
            sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + messageManager.getMessage(MessageManager.Message.STATS_TITLE));
            sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + messageManager.getMessage(MessageManager.Message.STATS_LINE_BLOCKS, CreeperPlugin.instance().pluginStats().blocksRecovered()));
            sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) + messageManager.getMessage(MessageManager.Message.STATS_LINE_EXPLOSIONS, CreeperPlugin.instance().pluginStats().explosionsRecovered()));
        } else {
            showHelp(sender);
        }
        return false;
    }

    public void showHelp(@NotNull CommandSender sender) {
        MessageManager messageManager = CreeperPlugin.instance().messageManager();
        sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) +
                messageManager.getMessage(MessageManager.Message.HELP_LINE_1));
        sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) +
                messageManager.getMessage(MessageManager.Message.HELP_LINE_2));
        sender.sendMessage(messageManager.getMessage(MessageManager.Message.PREFIX) +
                messageManager.getMessage(MessageManager.Message.HELP_LINE_3));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return Arrays.asList("fix", "reload", "stats");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("fix")) {
            return Arrays.asList("all", "10", "100", "1000", "10000");
        } else {
            return new ArrayList<>();
        }
    }

}
