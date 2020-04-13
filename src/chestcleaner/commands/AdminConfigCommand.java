package chestcleaner.commands;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.config.serializable.Category;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.CategorizerManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.StringUtils;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A command class representing the SortingConfig command. SortingConfig Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-sortingconfig
 *
 * @author Tom2208
 */
public class AdminConfigCommand implements CommandExecutor, TabCompleter {

    /* sub-commands */
    private final String autosortSubCommand = "autosort";
    private final String categoriesSubCommand = "categories";
    private final String cooldownSubCommand = "cooldown";
    private final String patternSubCommand = "pattern";

    private final String setSubCommand = "set";
    private final String activeSubCommand = "active";
    private final String addFromBookSubCommand = "addFromBook";
    private final String getAsBookSubCommand = "getAsBook";

    private final String[] strCommandList = {autosortSubCommand, categoriesSubCommand, cooldownSubCommand, patternSubCommand};
    private final String[] categoriesSubCommandList = {setSubCommand, addFromBookSubCommand, getAsBookSubCommand};
    private final String[] cooldownSubCommandList = {setSubCommand, activeSubCommand};

    private final String syntax = "/adminconfig <" + String.join("/", strCommandList) + ">";


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_ADMIN_CONTROL.getString())) {
                MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
                        PluginPermissions.CMD_SORTING_CONFIG_ADMIN_CONTROL.getString(), sender);
                return true;
            }
        }

        if (args.length == 1) {
            if (autosortSubCommand.equalsIgnoreCase(args[0])) {
                return getConfig(sender, autosortSubCommand);
            } else if (categoriesSubCommand.equalsIgnoreCase(args[0])) {
                return getConfig(sender, categoriesSubCommand);
            } else if (cooldownSubCommand.equalsIgnoreCase(args[0])) {
                return getConfig(sender, cooldownSubCommand);
            } else if (patternSubCommand.equalsIgnoreCase(args[0])) {
                return getConfig(sender, patternSubCommand);
            }
        } else if (args.length == 2) {
            if (autosortSubCommand.equalsIgnoreCase(args[0])) {
                return setDefaultAutoSort(sender, args[1]);

            } else if (categoriesSubCommand.equalsIgnoreCase(args[0])
                    && addFromBookSubCommand.equalsIgnoreCase(args[1])) {
                return addFromBook(sender, player);

            } else if (cooldownSubCommand.equalsIgnoreCase(args[0])
                    && activeSubCommand.equalsIgnoreCase(args[1])) {
                return getConfig(sender, activeSubCommand);

            } else if (patternSubCommand.equalsIgnoreCase(args[0])) {
                return setDefaultPattern(sender, args[1]);
            }
        } else if (args.length == 3) {
            if (categoriesSubCommand.equalsIgnoreCase(args[0])
                    && setSubCommand.equalsIgnoreCase(args[1])) {
                return setDefaultCategories(sender, args[2]);

            } else if (categoriesSubCommand.equalsIgnoreCase(args[0])
                    && getAsBookSubCommand.equalsIgnoreCase(args[1])) {
                return getBook(sender, player, args[2]);

            } else if (cooldownSubCommand.equalsIgnoreCase(args[0])
                    && activeSubCommand.equalsIgnoreCase(args[1])) {
                return setCooldownActive(sender, args[2]);

            } else if (cooldownSubCommand.equalsIgnoreCase(args[0])
                    && setSubCommand.equalsIgnoreCase(args[1])) {
                return setCooldownTime(sender, args[2]);
            }
        }
        MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntax, sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {

        final List<String> completions = new ArrayList<>();
        final List<String> commandList = Arrays.asList(strCommandList);
        final List<String> categoriesSubCommands = Arrays.asList(categoriesSubCommandList);
        final List<String> cooldownSubCommands = Arrays.asList(cooldownSubCommandList);

        if (args.length <= 1) {
            StringUtil.copyPartialMatches(args[0], commandList, completions);
        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase(autosortSubCommand))
                StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
            else if (args[0].equalsIgnoreCase(categoriesSubCommand))
                StringUtil.copyPartialMatches(args[1], categoriesSubCommands, completions);
            else if (args[0].equalsIgnoreCase(cooldownSubCommand))
                StringUtil.copyPartialMatches(args[1], cooldownSubCommands, completions);
            else if (args[0].equalsIgnoreCase(patternSubCommand))
                StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);

        } else if (args.length == 3) {
            if (categoriesSubCommand.equalsIgnoreCase(args[0])) {
                if (setSubCommand.equalsIgnoreCase(args[1]))
                    StringUtils.copyPartialMatchesCommasNoDuplicates(args[2], CategorizerManager.getAllNames(), completions);
                if (getAsBookSubCommand.equalsIgnoreCase(args[1]))
                    StringUtil.copyPartialMatches(args[2],
                            PluginConfigManager.getAllCategories().stream().map(Category::getName).collect(Collectors.toList()),
                            completions); //
            } else if (cooldownSubCommand.equalsIgnoreCase(args[0])
                    && activeSubCommand.equalsIgnoreCase(args[1])) {
                StringUtil.copyPartialMatches(args[2], StringUtils.getBooleanValueStringList(), completions);
            }
        }
        return completions;
    }

    private boolean getConfig(CommandSender sender, String command) {
        String key = "";
        String value = "";
        switch (command) {
            case autosortSubCommand:
                key = "defaultautosort";
                value = String.valueOf(PluginConfigManager.isDefaultAutoSort());
                break;
            case categoriesSubCommand:
                key = "categories";
                value = PluginConfigManager.getCategoryOrder().toString();
                break;
            case cooldownSubCommand:
                key = "cooldown";
                value = String.valueOf(PluginConfigManager.getCooldown());
                break;
            case activeSubCommand:
                key = "cooldownActive";
                value = String.valueOf(PluginConfigManager.isCooldownActive());
                break;
            case patternSubCommand:
                key = "defaultsortingpattern";
                value = PluginConfigManager.getDefaultPattern().name();
                break;
        }
        MessageSystem.sendMessageToCSWithReplacement(
                MessageType.SUCCESS, MessageID.CURRENT_VALUE, sender, key, value);
        return true;
    }

    private boolean setDefaultAutoSort(CommandSender sender, String bool) {
        if (StringUtils.isStringTrueOrFalse(bool)) {
            boolean b = Boolean.parseBoolean(bool);

            PluginConfigManager.setDefaultAutoSort(b);

            MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.AUTOSORTING_TOGGLED, sender,
                    String.valueOf(b));
        } else {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.NOT_A_BOOLEAN, sender);
        }
        return true;
    }

    private boolean setDefaultPattern(CommandSender sender, String patternName) {
        SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);

        if (pattern != null) {
            PluginConfigManager.setDefaultPattern(pattern);
            MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_PATTER_SET, sender);
        } else {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, sender);
        }
        return true;
    }

    private boolean setDefaultCategories(CommandSender sender, String commaSeperatedCategories) {
        List<String> categories = Arrays.asList(commaSeperatedCategories.split(","));

        if (CategorizerManager.validateExists(categories)) {
            PluginConfigManager.setCategoryOrder(categories);
            MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_CATEGORIES_SET, sender);
        } else {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_CATEGORY_NAME, sender);
        }
        return true;
    }


    private boolean addFromBook(CommandSender sender, Player player) {
        if (player == null) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.YOU_HAVE_TO_BE_PLAYER, sender);
        } else {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType().equals(Material.WRITABLE_BOOK) || itemInHand.getType().equals(Material.WRITTEN_BOOK)) {
                String name = CategorizerManager.addFromBook(((BookMeta) itemInHand.getItemMeta()).getPages());
                MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.NEW_CATEGORY, sender, name);
            }
        }
        return true;
    }

    private boolean getBook(CommandSender sender, Player player, String categoryString) {
        if (player == null) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.YOU_HAVE_TO_BE_PLAYER, sender);
        } else {
            Category category = PluginConfigManager.getCategoryByName(categoryString);
            if (category != null) {
                ItemStack book = category.getAsBook();
                player.getWorld().dropItem(player.getLocation(), book);
            } else {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_CATEGORY_NAME, sender);
            }
        }
        return true;
    }

    private boolean setCooldownTime(CommandSender sender, String arg) {

        if (!sender.hasPermission(PluginPermissions.CMD_COOLDOWN.getString())) {
            MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
                    PluginPermissions.CMD_COOLDOWN.getString(), sender);
            return true;
        }

        try {
            int time = Integer.parseInt(arg);
            PluginConfigManager.setCooldown(time);
            MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.TIMER_TIME, sender,
                    String.valueOf(time));

        } catch (NumberFormatException ex) {
            MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.NOT_AN_INTEGER, sender, arg);
        }

        return true;
    }

    private boolean setCooldownActive(CommandSender sender, String arg) {
        if (!sender.hasPermission(PluginPermissions.CMD_COOLDOWN.getString())) {
            MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
                    PluginPermissions.CMD_COOLDOWN.getString(), sender);
            return true;
        }

        String trueStr = Boolean.TRUE.toString();
        String falseStr = Boolean.FALSE.toString();

        if (arg.equalsIgnoreCase(trueStr) || arg.equalsIgnoreCase(falseStr)) {
            boolean state = Boolean.getBoolean(arg);
            PluginConfigManager.setCooldownActive(state);
            MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.COOLDOWN_TOGGLE, sender,
                    String.valueOf(state));
        } else {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.NOT_A_BOOLEAN, sender);
        }
        return true;
    }

}
