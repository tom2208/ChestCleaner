package me.tom2208.chestCleaner.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InfoSubCommand extends SubCommand {


    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Shows information about the plugin.";
    }

    @Override
    public String getSyntax() {
        return "/chestcleaner info";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            player.sendMessage(Component.text("ChestCleaner version 3.0-SNAPSHOT").color(NamedTextColor.AQUA));
            player.sendMessage(Component.text("Author: tom2208").color(NamedTextColor.GRAY));
        } else {
          sender.sendMessage("This command can only be run by a player.");
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }

}
