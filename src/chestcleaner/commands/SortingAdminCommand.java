package chestcleaner.commands;

import chestcleaner.commands.datastructures.CommandTree;
import chestcleaner.commands.datastructures.CommandTuple;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A command class representing the SortingConfig command. SortingConfig Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-sortingconfig
 */
public class SortingAdminCommand implements CommandExecutor, TabCompleter {

    /**
     * sortingAdmin(autosort,categories,cooldown,pattern,chatNotification,sortingSound,refill,clickSort)
     * categories(set(), activate, addFromBook, getAsBook, remove())
     */

    /* sub-commands */
    private final String autosortSubCommand = "autosort";
    private final String categoriesSubCommand = "categories";
    private final String cooldownSubCommand = "cooldown";
    private final String patternSubCommand = "pattern";
    private final String chatNotificationSubCommand = "chatNotification";
    private final String sortingSoundSubCommand = "sortingSound";
    private final String refillSubCommand = "refill";
    private final String clickSortSubCommand = "clickSort";

    /* categories sub-commands */
    private final String setSubCommand = "set";
    private final String activeSubCommand = "active";
    private final String addFromBookSubCommand = "addFromBook";
    private final String getAsBookSubCommand = "getAsBook";
    private final String removeSubCommand = "remove";

    private final String autosortProperty = "default autosort";
    private final String categoriesProperty = "default categoryOrder";
    private final String cooldownProperty = "cooldown (in ms)";
    private final String patternProperty = "default sortingpattern";
    private final String activeProperty = "cooldownActive";
    private final String chatNotificationProperty = "chat sorting notification";
    private final String clickSortProperty = "default clicksort";

    private final String[] strCommandList = {autosortSubCommand, categoriesSubCommand, cooldownSubCommand,
            patternSubCommand, chatNotificationSubCommand, sortingSoundSubCommand, refillSubCommand, clickSortSubCommand};
    private final String[] categoriesSubCommandList = {setSubCommand, addFromBookSubCommand, getAsBookSubCommand, removeSubCommand};
    private final String[] cooldownSubCommandList = {setSubCommand, activeSubCommand};
    private final String[] refillSubCommandList =
            {RefillType.BLOCKS.toString(), RefillType.CONSUMABLES.toString(), RefillType.BREAKABLES.toString(),
                    "true", "false"};

    private final CommandTree cmdTree;
    private final String alias = "sortingadmin";

    public SortingAdminCommand() {
        cmdTree = new CommandTree(alias);
        cmdTree.addPath("/sortingadmin autosort", this::getConfig);
        cmdTree.addPath("/sortingadmin autosort true/false", this::setDefaultAutoSort, Boolean.class);

        cmdTree.addPath("/sortingadmin pattern", this::getConfig);
        cmdTree.addPath("/sortingadmin pattern pattern", this::setDefaultPattern, String.class);

        cmdTree.addPath("/sortingadmin categories", this::getConfig);
        cmdTree.addPath("/sortingadmin categories addFromBook", this::addFromBook);
        cmdTree.addPath("/sortingadmin categories remove name", this::removeCategory, String.class);
        cmdTree.addPath("/sortingadmin categories set names", this::setDefaultCategories, String.class);
        cmdTree.addPath("/sortingadmin categories getAsBook name", this::getBook, String.class);

        cmdTree.addPath("/sortingadmin cooldown", this::getConfig);
        cmdTree.addPath("/sortingadmin cooldown active", this::getConfig);
        cmdTree.addPath("/sortingadmin cooldown active true/false", this::setCooldownActive, Boolean.class);
        cmdTree.addPath("/sortingadmin cooldown timeInMilliseconds", this::setCooldownTime, Integer.class);

        cmdTree.addPath("/sortingadmin chatNotification", this::getConfig);
        cmdTree.addPath("/sortingadmin chatNotification true/false", this::setChatNotification, Boolean.class);

        cmdTree.addPath("/sortingadmin sortingSound", this::getConfig);
        cmdTree.addPath("/sortingadmin sortingSound true/false", this::setSound, Boolean.class);

        cmdTree.addPath("/sortingadmin clickSort", this::getConfig);
        cmdTree.addPath("/sortingadmin clickSort true/false", this::setClickSort, Boolean.class);

        cmdTree.addPath("/sortingadmin refill", null, null);
        cmdTree.addPath("/sortingadmin refill type", null, RefillType.class);
        cmdTree.addPath("/sortingadmin refill true/false", this::setAllRefills, Boolean.class);
        cmdTree.addPath("/sortingadmin refill type true/false", this::setRefill, Boolean.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        cmdTree.execute(sender, cmd, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {

        final List<String> completions = new ArrayList<>();

        if (args.length <= 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(strCommandList), completions);
        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase(autosortSubCommand) || args[0].equalsIgnoreCase(chatNotificationSubCommand)
                    || args[0].equalsIgnoreCase(sortingSoundSubCommand) || args[0].equalsIgnoreCase(clickSortSubCommand))
                StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
            else if (args[0].equalsIgnoreCase(categoriesSubCommand))
                StringUtil.copyPartialMatches(args[1], Arrays.asList(categoriesSubCommandList), completions);
            else if (args[0].equalsIgnoreCase(cooldownSubCommand))
                StringUtil.copyPartialMatches(args[1], Arrays.asList(cooldownSubCommandList), completions);
            else if (args[0].equalsIgnoreCase(patternSubCommand))
                StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);
            else if (args[0].equalsIgnoreCase(refillSubCommand))
                StringUtil.copyPartialMatches(args[1], Arrays.asList(refillSubCommandList), completions);

        } else if (args.length == 3) {
            if (categoriesSubCommand.equalsIgnoreCase(args[0])) {
                if (setSubCommand.equalsIgnoreCase(args[1]) || removeSubCommand.equalsIgnoreCase(args[1]))
                    StringUtils.copyPartialMatchesCommasNoDuplicates(args[2], CategorizerManager.getAllNames(),
                            completions);

                if (getAsBookSubCommand.equalsIgnoreCase(args[1]))
                    StringUtil.copyPartialMatches(args[2], PluginConfigManager.getAllCategories().stream()
                            .map(Category::getName).collect(Collectors.toList()), completions); //

            } else if (cooldownSubCommand.equalsIgnoreCase(args[0]) && activeSubCommand.equalsIgnoreCase(args[1])) {
                StringUtil.copyPartialMatches(args[2], StringUtils.getBooleanValueStringList(), completions);

            } else if (refillSubCommand.equalsIgnoreCase(args[0])) {
                if (RefillType.getByName(args[1]) != null) {
                    StringUtil.copyPartialMatches(args[2], StringUtils.getBooleanValueStringList(), completions);
                }
            }
        }
        return completions;
    }

    private Player getPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        return null;
    }

    private void getConfig(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String command = tuple.args[0];

        String key = "";
        String value = "";

        if (command.equalsIgnoreCase(autosortSubCommand)) {
            key = autosortProperty;
            value = String.valueOf(PluginConfigManager.getDefaultAutoSortBoolean());

        } else if (command.equalsIgnoreCase(categoriesSubCommand)) {
            key = categoriesProperty;
            value = PluginConfigManager.getCategoryOrder().toString();

        } else if (command.equalsIgnoreCase(cooldownSubCommand)) {
            if (tuple.args.length >= 2 && tuple.args[1].equalsIgnoreCase(activeSubCommand)) {
                key = activeProperty;
                value = String.valueOf(PluginConfigManager.isCooldownActive());
            } else {
                key = cooldownProperty;
                value = String.valueOf(PluginConfigManager.getCooldown());
            }

        } else if (command.equalsIgnoreCase(patternSubCommand)) {
            key = patternProperty;
            value = PluginConfigManager.getDefaultPattern().name();

        } else if (command.equalsIgnoreCase(chatNotificationSubCommand)) {
            key = chatNotificationProperty;
            value = String.valueOf(PluginConfigManager.getDefaultChatNotificationBoolean());

        } else if (command.equalsIgnoreCase(sortingSoundSubCommand)) {
            key = sortingSoundSubCommand;
            value = String.valueOf(PluginConfigManager.getDefaultSortingSoundBoolean());

        } else if (command.equalsIgnoreCase(clickSortSubCommand)) {
            key = clickSortProperty;
            value = String.valueOf(PluginConfigManager.isDefaultClickSort());
        }

        if (!key.equals("") && !value.equals("")) {
            MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, sender, key, value);
        }
    }

    private void setClickSort(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String bool = tuple.args[1];

        if (StringUtils.isStringBoolean(sender, bool)) {
            boolean b = Boolean.parseBoolean(bool);
            PluginConfigManager.setDefaultClickSort(b);
            MessageSystem.sendChangedValue(sender, clickSortProperty, String.valueOf(bool));
        }
    }

    /**
     * Sets the configuration for a refill option.
     *
     * @param tuple the tuple the sub-command should run on.
     * @return True if the command can get parsed, otherwise false.
     */
    private void setRefill(CommandTuple tuple) {
        CommandSender sender = tuple.sender;
        String arg = tuple.args[1];
        String bool = tuple.args[2];
        System.out.println(arg + " " + bool);
        if (StringUtils.isStringBoolean(sender, bool)) {

            boolean b = Boolean.parseBoolean(bool);
            String property;

            RefillType type = RefillType.getByName(arg);
            System.out.println(type);
            if (type != null) {
                property = type.toString();
                if (!Objects.equals(type, RefillType.BLOCKS)) {
                    if (type.equals(RefillType.CONSUMABLES)) {
                        PluginConfigManager.setDefaultConsumablesRefill(b);
                    } else if (type.equals(RefillType.BREAKABLES)) {
                        PluginConfigManager.setDefaultBreakableRefill(b);
                    }
                } else {
                    PluginConfigManager.setDefaultBlockRefill(b);
                }
                MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, sender, property, b);
            }
        }

    }

    private void setAllRefills(CommandTuple tuple) {
        CommandSender sender = tuple.sender;
        String bool = tuple.args[1];

        if (StringUtils.isStringBoolean(sender, bool)) {
            boolean b = Boolean.parseBoolean(bool);
            PluginConfigManager.setDefaultBlockRefill(b);
            PluginConfigManager.setDefaultConsumablesRefill(b);
            PluginConfigManager.setDefaultBreakableRefill(b);
            String allRefillsProperty = "all refills";
            MessageSystem.sendChangedValue(sender, allRefillsProperty, String.valueOf(b));
        }
    }

    private void setChatNotification(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String bool = tuple.args[1];

        if (StringUtils.isStringBoolean(sender, bool)) {
            boolean b = Boolean.parseBoolean(bool);
            PluginConfigManager.setDefaultChatNotificationBoolean(b);
            MessageSystem.sendChangedValue(sender, chatNotificationProperty, String.valueOf(b));
        }
    }

    private void setSound(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String bool = tuple.args[1];

        if (StringUtils.isStringBoolean(sender, bool)) {
            boolean b = Boolean.parseBoolean(bool);
            PluginConfigManager.setDefaultSortingSoundBoolean(b);
            String soundProperty = "sorting sound";
            MessageSystem.sendChangedValue(sender, soundProperty, String.valueOf(b));
        }
    }

    private void setDefaultAutoSort(CommandTuple tuple) {
        CommandSender sender = tuple.sender;
        String bool = tuple.args[1];

        if (!StringUtils.isStringTrueOrFalse(bool)) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
        } else {

            boolean b = Boolean.parseBoolean(bool);
            PluginConfigManager.setDefaultAutoSort(b);
            MessageSystem.sendChangedValue(sender, autosortProperty, String.valueOf(b));
        }
    }

    private void setDefaultPattern(CommandTuple tuple) {
        CommandSender sender = tuple.sender;
        String patternName = tuple.args[1];
        SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);

        if (pattern == null) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_PATTERN_ID, sender);
        } else {
            PluginConfigManager.setDefaultPattern(pattern);
            MessageSystem.sendChangedValue(sender, patternProperty, pattern.name());
        }
    }

    private void setDefaultCategories(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String commaSeparatedCategories = tuple.args[2];

        List<String> categories = Arrays.asList(commaSeparatedCategories.split(","));

        if (!CategorizerManager.validateExists(categories)) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME, sender);
        } else {
            PluginConfigManager.setCategoryOrder(categories);
            MessageSystem.sendChangedValue(sender, categoriesProperty, categories.toString());
        }
    }

    private void removeCategory(CommandTuple tuple) {
        CategorizerManager.removeCategoryAndSave(tuple.args[2], tuple.sender);
    }

    private void addFromBook(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        Player player = getPlayer(sender);

        if (player == null) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
        } else {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType().equals(Material.WRITABLE_BOOK)
                    || itemInHand.getType().equals(Material.WRITTEN_BOOK)) {

                String name = CategorizerManager.addFromBook(
                        ((BookMeta) Objects.requireNonNull(itemInHand.getItemMeta())).getPages(), sender);

                MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CATEGORY_NEW, sender,
                        name);
            } else {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_BOOK, sender);
            }
        }
    }

    private void getBook(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        Player player = getPlayer(sender);
        String categoryString = tuple.args[2];

        if (player == null) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
        } else {
            Category<?> category = PluginConfigManager.getCategoryByName(categoryString);
            if (category == null) {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME, sender);
            } else {
                ItemStack book = category.getAsBook();
                player.getWorld().dropItem(player.getLocation(), book);
            }
        }
    }

    private void setCooldownTime(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String arg = tuple.args[2];

        if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_COOLDOWN.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_COOLDOWN);
        } else {

            try {
                int time = Integer.parseInt(arg);
                PluginConfigManager.setCooldown(time);
                MessageSystem.sendChangedValue(sender, cooldownProperty, String.valueOf(time));

            } catch (NumberFormatException ex) {
                MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_VALIDATION_INTEGER,
                        sender, arg);
            }
        }
    }

    private void setCooldownActive(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String arg = tuple.args[2];

        if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_COOLDOWN.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_COOLDOWN);
        } else if (!StringUtils.isStringTrueOrFalse(arg)) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
        } else {
            boolean state = Boolean.parseBoolean(arg);
            PluginConfigManager.setCooldownActive(state);
            MessageSystem.sendChangedValue(sender, activeProperty, String.valueOf(state));
        }

    }

    public enum RefillType {
        BLOCKS, CONSUMABLES, BREAKABLES;

        public static RefillType getByName(String str) {
            for (RefillType type : values()) {
                if (str.equalsIgnoreCase(type.toString())) {
                    return type;
                }
            }
            return null;
        }
    }

}
