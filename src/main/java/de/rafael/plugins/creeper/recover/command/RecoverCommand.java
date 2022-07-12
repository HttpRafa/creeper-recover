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

package de.rafael.plugins.creeper.recover.command;

//------------------------------
//
// This class was developed by Rafael K.
// On 2/22/2022 at 12:13 AM
// In the project CreeperRecover
//
//------------------------------

import de.rafael.plugins.creeper.recover.CreeperRecover;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RecoverCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("creeper.recover.command")) {
            sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§cYou don't have permission to use this command§8.");
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("fix")) {
            try {
                Bukkit.getScheduler().runTaskAsynchronously(CreeperRecover.getCreeperRecover(), () -> {
                    CreeperRecover.getCreeperRecover().pause();
                    if (args[1].equalsIgnoreCase("all")) {
                        int recovered = CreeperRecover.getCreeperRecover().getExplosionManager().recoverBlocks();
                        sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§b" + recovered + " §7blocks recovered§8.");
                    } else {
                        int amount = Integer.parseInt(args[1]);
                        int recovered = CreeperRecover.getCreeperRecover().getExplosionManager().recoverBlocks(amount);
                        sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§b" + recovered + " §7blocks recovered§8.");
                    }
                    Bukkit.getScheduler().runTaskLater(CreeperRecover.getCreeperRecover(), () -> {
                        CreeperRecover.getCreeperRecover().resume();
                    }, 5);
                });
            } catch (NumberFormatException exception) {
                sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§c" + exception.getMessage());
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("stats")) {
            sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§7Daily§8:");
            sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "   §b" + "BlocksRecovered" + " §8» §7" + CreeperRecover.getCreeperRecover().getPluginStats().getBlocksRecovered());
            sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "   §b" + "ExplosionsRecovered" + " §8» §7" + CreeperRecover.getCreeperRecover().getPluginStats().getExplosionsRecovered());
        } else {
            showHelp(sender);
        }
        return false;
    }

    public void showHelp(CommandSender sender) {
        sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§8/§7recover §bfix §8[§3amount of blocks§8/§3all§8]");
        sender.sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§8/§7recover §bstats");
    }

}
