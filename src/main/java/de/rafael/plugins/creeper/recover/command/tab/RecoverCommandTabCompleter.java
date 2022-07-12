package de.rafael.plugins.creeper.recover.command.tab;

//------------------------------
//
// This class was developed by Rafael K.
// On 2/23/2022 at 7:40 PM
// In the project CreeperRecover
//
//------------------------------

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecoverCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("fix", "stats");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("fix")) {
            return Arrays.asList("all", "10", "100", "1000", "10000");
        } else {
            return new ArrayList<>();
        }
    }

}
