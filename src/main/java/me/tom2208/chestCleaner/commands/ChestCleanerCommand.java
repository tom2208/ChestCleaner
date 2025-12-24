package me.tom2208.chestCleaner.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChestCleanerCommand implements CommandExecutor, TabCompleter {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public ChestCleanerCommand() {
        subcommands.add(new InfoSubCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Fall 1: Nur "/test" eingegeben -> Hilfe anzeigen
        if (args.length == 0) {
            sender.sendMessage(Component.text("--- Help ---").color(NamedTextColor.GOLD));
            for (SubCommand sub : subcommands) {
                sender.sendMessage(Component.text(sub.getSyntax() + " - " + sub.getDescription()));
            }
            return true;
        }

        // Fall 2: Subcommand suchen (args[0] ist z.B. "info")
        for (SubCommand sub : subcommands) {
            if (args[0].equalsIgnoreCase(sub.getName())) {
                sub.perform(sender, args);
                return true;
            }
        }

        sender.sendMessage(Component.text("Unknown command. Try /chestcleaner for help.").color(NamedTextColor.RED));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            ArrayList<String> suggestions = new ArrayList<>();
            for (SubCommand sub : subcommands) {
                suggestions.add(sub.getName());
            }
            return suggestions;
        }

        else if (args.length > 1) {
            for (SubCommand sub : subcommands) {
                if (args[0].equalsIgnoreCase(sub.getName())) {
                    return sub.getSubcommandArguments(sender, args);
                }
            }
        }

        return null;
    }
}
