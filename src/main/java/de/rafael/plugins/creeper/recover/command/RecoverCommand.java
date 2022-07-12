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
