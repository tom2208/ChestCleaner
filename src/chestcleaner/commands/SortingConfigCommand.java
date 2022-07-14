package chestcleaner.commands;

import chestcleaner.commands.datastructures.CommandTree;
import chestcleaner.commands.datastructures.CommandTuple;
import chestcleaner.config.PlayerDataManager;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.CategorizerManager;
import chestcleaner.sorting.categorizer.Categorizer;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.StringUtils;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A command class representing the SortingConfig command. SortingConfig Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-sortingconfig
 */
public class SortingConfigCommand implements CommandExecutor, TabCompleter {

    private final int MAX_LINES_PER_PAGE = 8;
    /* sub-commands */
    private final String autosortSubCommand = "autosort";
    private final String categoriesSubCommand = "categories";
    private final String patternSubCommand = "pattern";
    private final String chatNotificationSubCommand = "chatNotification";
    private final String sortingSoundSubCommand = "sortingSound";
    private final String clickSortSubCommand = "clickSort";

    private final String blocksSubCommand = "blocks";
    private final String consumablesSubCommand = "consumables";
    private final String breakablesSubCommand = "breakables";

    private final String autosortProperty = "autosort";
    private final String categoriesProperty = "categoryOrder";
    private final String patternProperty = "sortingpattern";
    private final String chatNotificationProperty = "chat sorting notification";
    private final String sortingSoundProperty = "sorting sound";

    public static final String COMMAND_ALIAS = "sortingconfig";
    private final CommandTree cmdTree;

    public SortingConfigCommand() {
        cmdTree = new CommandTree(COMMAND_ALIAS);

        // autoSort
        cmdTree.addPath("/sortingconfig autosort", this::getConfig);
        cmdTree.addPath("/sortingconfig autosort true/false", this::setAutoSort, Boolean.class);
        // categories
        cmdTree.addPath("/sortingconfig categories", this::getConfig);
        cmdTree.addPath("/sortingconfig categories list", this::getCategoryList);
        cmdTree.addPath("/sortingconfig categories list page", this::getCategoryList, Integer.class);
        cmdTree.addPath("/sortingconfig categories reset", this::resetCategories);
        cmdTree.addPath("/sortingconfig categories set names", this::setCategories, Categorizer.class, true);
        // pattern
        cmdTree.addPath("/sortingconfig pattern", this::getConfig);
        cmdTree.addPath("/sortingconfig pattern pattern", this::setPattern, SortingPattern.class);
        // chatNotification
        cmdTree.addPath("/sortingconfig chatNotification", this::getConfig);
        cmdTree.addPath("/sortingconfig chatNotification true/false", this::setChatNotificationBool, Boolean.class);
        // refill
        cmdTree.addPath("/sortingconfig refill type", null, SortingAdminCommand.RefillType.class);
        cmdTree.addPath("/sortingconfig refill type true/false", this::setRefill, Boolean.class);
        cmdTree.addPath("/sortingconfig refill true/false", this::setAllRefills, Boolean.class);
        // sortingSound
        cmdTree.addPath("/sortingconfig sortingSound", this::getConfig);
        cmdTree.addPath("/sortingconfig sortingSound true/false", this::setSortingSoundBool, Boolean.class);
        // clickSort
        cmdTree.addPath("/sortingconfig clickSort", this::getConfig);
        cmdTree.addPath("/sortingconfig clickSort true/false", this::setClickSort, Boolean.class);
        // reset
        cmdTree.addPath("/sortingconfig reset", this::resetConfiguration);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player == null) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
            return true;
        }

        cmdTree.execute(sender, cmd, label, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        return cmdTree.getListForTabCompletion(args);
    }

    private void getConfig(CommandTuple tuple) {

        Player p = (Player) tuple.sender;
        String command = tuple.args[0];

        String key = "";
        String value = "";

        if (command.equalsIgnoreCase(autosortSubCommand)) {
            key = autosortProperty;
            value = String.valueOf(PlayerDataManager.isAutoSort(p));

        } else if (command.equalsIgnoreCase(categoriesSubCommand)) {
            key = categoriesProperty;
            value = PlayerDataManager.getCategoryOrder(p).toString();

        } else if (command.equalsIgnoreCase(patternSubCommand)) {
            key = patternProperty;
            value = PlayerDataManager.getSortingPattern(p).name();

        } else if (command.equalsIgnoreCase(chatNotificationSubCommand)) {
            key = chatNotificationProperty;
            value = String.valueOf(PlayerDataManager.isNotification(p));

        } else if (command.equalsIgnoreCase(sortingSoundSubCommand)) {
            key = sortingSoundProperty;
            value = String.valueOf(PlayerDataManager.isSortingSound(p));

        } else if (command.equalsIgnoreCase(clickSortSubCommand)) {
            key = clickSortSubCommand;
            value = String.valueOf(PlayerDataManager.isClickSort(p));
        }

        if (!key.equals("") && !value.equals("")) {
            MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, p, key,
                    value);
        }
    }


    private void resetCategories(CommandTuple tuple) {

        Player player = (Player) tuple.sender;

        if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES_RESET)) {
            PlayerDataManager.resetCategories(player);
            MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_CATEGORY_RESETED, player);
        }
    }


    private void setClickSort(CommandTuple tuple) {

        Player player = (Player) tuple.sender;
        String bool = tuple.args[1];

        if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_CLICKSORT)) {
            if (StringUtils.isStringNotTrueOrFalse(bool)) {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
            } else {
                boolean b = Boolean.parseBoolean(bool);
                PlayerDataManager.setClickSort(player, b);
                MessageSystem.sendChangedValue(player, clickSortSubCommand, String.valueOf(b));
            }
        }
    }

    /**
     * Sets the configuration for a refill option.
     *
     * @param tuple the tuple the sub-command should run on.
     * @return True if the command can get parsed, otherwise false.
     */
    private boolean setRefill(CommandTuple tuple) {

        Player player = (Player) tuple.sender;
        String arg = tuple.args[1];
        String bool = tuple.args[2];

        if (StringUtils.isStringBoolean(player, bool)) {

            boolean b = Boolean.parseBoolean(bool);
            String property;

            if (arg.equalsIgnoreCase(blocksSubCommand) && checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_REFILL_BLOCKS)) {
                PlayerDataManager.setRefillBlocks(player, b);
                property = blocksSubCommand;
            } else if (arg.equalsIgnoreCase(consumablesSubCommand) && checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_REFILL_CONSUMABLES)) {
                PlayerDataManager.setRefillConumables(player, b);
                property = consumablesSubCommand;
            } else if (arg.equalsIgnoreCase(breakablesSubCommand) && checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_REFILL_BREAKABLES)) {
                PlayerDataManager.setRefillBreakables(player, b);
                property = breakablesSubCommand;
            } else {
                return false;
            }

            MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, player,
                    property, b);

        }

        return true;
    }

    private void setAllRefills(CommandTuple tuple) {

        Player player = (Player) tuple.sender;
        String bool = tuple.args[1];

        if (StringUtils.isStringNotTrueOrFalse(bool)) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
        } else {
            boolean change = false;
            boolean b = Boolean.parseBoolean(bool);
            if (player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_REFILL_BLOCKS.getString())) {
                PlayerDataManager.setRefillBlocks(player, b);
                MessageSystem.sendChangedValue(player, blocksSubCommand, String.valueOf(b));
                change = true;
            }
            if (player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_REFILL_CONSUMABLES.getString())) {
                PlayerDataManager.setRefillConumables(player, b);
                MessageSystem.sendChangedValue(player, consumablesSubCommand, String.valueOf(b));
                change = true;
            }
            if (player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_REFILL_BREAKABLES.getString())) {
                PlayerDataManager.setRefillBreakables(player, b);
                MessageSystem.sendChangedValue(player, breakablesSubCommand, String.valueOf(b));
                change = true;
            }

            if (!change) {
                MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_REFILL_GENERIC);
            }

        }
    }

    private void resetConfiguration(CommandTuple tuple) {

        Player player = (Player) tuple.sender;

        if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_RESET)) {
            PlayerDataManager.reset(player);
            MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_RESET_CONFIG, player);
        }
    }

    private void setChatNotificationBool(CommandTuple tuple) {

        Player player = (Player) tuple.sender;
        String bool = tuple.args[1];

        if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_SET_NOTIFICATION_BOOL)) {
            if (StringUtils.isStringNotTrueOrFalse(bool)) {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
            } else {
                boolean b = Boolean.parseBoolean(bool);
                PlayerDataManager.setNotification(player, b);
                MessageSystem.sendChangedValue(player, chatNotificationProperty, String.valueOf(b));
            }
        }
    }

    private void setSortingSoundBool(CommandTuple tuple) {

        Player player = (Player) tuple.sender;
        String bool = tuple.args[1];

        if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_SET_SOUND_BOOL)) {
            if (StringUtils.isStringNotTrueOrFalse(bool)) {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
            } else {
                boolean b = Boolean.parseBoolean(bool);
                PlayerDataManager.setSortingSound(player, b);
                MessageSystem.sendChangedValue(player, sortingSoundProperty, String.valueOf(b));
            }
        }
    }

    private void getCategoryList(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String pageString = "1";
        if (tuple.args.length == 3) pageString = tuple.args[2];

        List<String> names = CategorizerManager.getAllNames();
        MessageSystem.sendListPageToCS(names, sender, pageString, MAX_LINES_PER_PAGE);
    }

    private void setPattern(CommandTuple tuple) {

        Player player = (Player) tuple.sender;
        String patternName = tuple.args[1];

        if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_PATTERN)) {

            SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);
            if (pattern != null) {
                PlayerDataManager.setSortingPattern(player, pattern);
                MessageSystem.sendChangedValue(player, patternProperty, pattern.name());
            } else {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_PATTERN_ID, player);
            }
        }
    }

    private void setAutoSort(CommandTuple tuple) {

        Player player = (Player) tuple.sender;
        String bool = tuple.args[1];

        if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT)) {
            if (StringUtils.isStringNotTrueOrFalse(bool)) {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
            } else {

                boolean b = Boolean.parseBoolean(bool);
                PlayerDataManager.setAutoSort(player, b);
                MessageSystem.sendChangedValue(player, autosortProperty, String.valueOf(b));
            }
        }
    }

    private boolean checkPermission(Player player, PluginPermissions permission) {

        if (!player.hasPermission(permission.getString())) {
            MessageSystem.sendPermissionError(player, permission);
            return false;
        } else {
            return true;
        }

    }

    private void setCategories(CommandTuple tuple) {

        Player player = (Player) tuple.sender;
        String commaSeparatedCategories = tuple.args[2];

        List<String> categories = SortingAdminCommand.getCategoriesFromArguments(tuple.args);

        if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES.getString())) {
            MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES);
        } else if (!CategorizerManager.validateExists(categories)) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME, player);
        } else {
            PlayerDataManager.setCategoryOrder(player, categories);
            MessageSystem.sendChangedValue(player, categoriesProperty, categories.toString());
        }
    }
}
