package chestcleaner.commands;

import chestcleaner.commands.datastructures.CommandTuple;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.cooldown.CMRegistry;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.StringUtils;
import chestcleaner.commands.datastructures.CommandTree;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * A command class representing the CleaningItem command. CleaningItem Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-cleaningitem
 *
 * @author Tom2208
 */
public class CleaningItemCommand implements CommandExecutor, TabCompleter {

    public static final String COMMAND_ALIAS = "cleaningitem";
    private final CommandTree cmdTree;
    private final String command = "cleaningitem";
    /* sub-commands */
    private final String getSubCommand = "get";
    private final String setSubCommand = "set";
    private final String giveSubCommand = "give";
    private final String nameSubCommand = "name";
    private final String loreSubCommand = "lore";
    private final String activeSubCommand = "active";
    private final String durabilityLossSubCommand = "durabilityLoss";
    private final String openEventSubCommand = "openEvent";

    private final String nameProperty = command.concat(" ").concat(nameSubCommand);
    private final String loreProperty = command.concat(" ").concat(loreSubCommand);
    private final String activeProperty = command.concat(" ").concat(activeSubCommand);
    private final String durabilityProperty = command.concat(" ").concat(durabilityLossSubCommand);
    private final String openEventProperty = openEventSubCommand;

    private final String[] strCommandList = {getSubCommand, setSubCommand, giveSubCommand, nameSubCommand,
            loreSubCommand, activeSubCommand, durabilityLossSubCommand, openEventSubCommand};

    public CleaningItemCommand() {
        cmdTree = new CommandTree(COMMAND_ALIAS);

        cmdTree.addPath("/cleaningitem get", this::getCleaningItem, null, false);

        cmdTree.addPath("/cleaningitem give @a", this::giveCleaningItem, null, false);
        cmdTree.addPath("/cleaningitem give player", this::giveCleaningItem, Player.class, false);

        cmdTree.addPath("/cleaningitem set", this::setCleaningItem, null, false);

        cmdTree.addPath("/cleaningitem name", this::getConfig, null, false);
        cmdTree.addPath("/cleaningitem lore", this::getConfig, null, false);
        cmdTree.addPath("/cleaningitem active", this::getConfig, null, false);
        cmdTree.addPath("/cleaningitem durabilityLoss", this::getConfig, null, false);
        cmdTree.addPath("/cleaningitem openEvent", this::getConfig, null, false);

        cmdTree.addPath("/cleaningitem name name", this::setItemName, String.class, true);

        cmdTree.addPath("/cleaningitem lore lore", this::setItemLore, String.class, true);

        cmdTree.addPath("/cleaningitem active true/false", this::setCleaningItemActive, Boolean.class, false);

        cmdTree.addPath("/cleaningitem durabilityLoss true/false", this::setDurabilityLoss, Boolean.class, false);

        cmdTree.addPath("/cleaningitem openEvent true/false", this::setOpenEventMode, Boolean.class, false);


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        cmdTree.execute(sender, command, alias, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return cmdTree.getListForTabCompletion(args);
    }

    /**
     * Sends a value change message of the state of the {@code command} form the
     * config to the {@code sender}.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void getConfig(CommandTuple tuple) {
        String command = tuple.args[0];

        String key = "";
        String value = "";

        if (nameSubCommand.equalsIgnoreCase(command)) {
            key = nameProperty;
            value = Objects.requireNonNull(PluginConfigManager.getCleaningItem().getItemMeta()).hasDisplayName()
                    ? PluginConfigManager.getCleaningItem().getItemMeta().getDisplayName()
                    : "<null>";
        } else if (loreSubCommand.equalsIgnoreCase(command)) {
            key = loreProperty;
            value = Objects.requireNonNull(PluginConfigManager.getCleaningItem().getItemMeta()).hasLore()
                    ? Objects.requireNonNull(PluginConfigManager.getCleaningItem().getItemMeta().getLore()).toString()
                    : "<null>";
        } else if (activeSubCommand.equalsIgnoreCase(command)) {
            key = activeProperty;
            value = String.valueOf(PluginConfigManager.isCleaningItemActive());
        } else if (durabilityLossSubCommand.equalsIgnoreCase(command)) {
            key = durabilityProperty;
            value = String.valueOf(PluginConfigManager.isDurabilityLossActive());
        } else if (openEventSubCommand.equalsIgnoreCase(command)) {
            key = openEventProperty;
            value = String.valueOf(PluginConfigManager.isOpenEvent());
        }

        MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, tuple.sender, key,
                value);
    }

    private boolean checkPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
            return false;
        }
    }

    /**
     * Gives the {@code player} the cleaning item if he has the permission for that.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void getCleaningItem(CommandTuple tuple) {
        if (checkPlayer(tuple.sender)) {
            Player player = (Player) tuple.sender;
            if(!CMRegistry.isOnCooldown(CMRegistry.CMIdentifier.CLEANING_ITEM_GET, player)) {
                if (!player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_GET.getString())) {
                    MessageSystem.sendPermissionError(player, PluginPermissions.CMD_CLEANING_ITEM_GET);
                } else {

                    player.getInventory().addItem(PluginConfigManager.getCleaningItem());
                    MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_CLEANITEM_YOU_GET, player);
                }
            }
        }
    }

    /**
     * Sets the cleaning item to the item the player is holding if the
     * {@code player} has the correct permission.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void setCleaningItem(CommandTuple tuple) {

        if (checkPlayer(tuple.sender)) {

            Player player = (Player) tuple.sender;

            if (!player.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET.getString())) {
                MessageSystem.sendPermissionError(player, PluginPermissions.CMD_ADMIN_ITEM_SET);
            } else {

                ItemStack item = player.getInventory().getItemInMainHand().clone();

                if (item.getType() == Material.AIR) {
                    MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_HOLD_ITEM, player);
                } else {

                    ItemMeta itemMeta = item.getItemMeta();
                    Damageable damageable = ((Damageable) itemMeta);
                    assert damageable != null;
                    damageable.setDamage(0);
                    item.setItemMeta(itemMeta);
                    item.setAmount(1);

                    PluginConfigManager.setCleaningItem(item);
                    MessageSystem.sendChangedValue(player, command, item.toString());
                }
            }
        }
    }

    /**
     * Gives the player with the name {@code playerName} a cleaning item.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void giveCleaningItem(CommandTuple tuple) {
        CommandSender sender = tuple.sender;
        String playerName = tuple.args[1];
        if (!sender.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_GIVE.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_CLEANING_ITEM_GIVE);
        } else {

            Player player2 = Bukkit.getPlayer(playerName);

            if (player2 != null) {
                player2.getInventory().addItem(PluginConfigManager.getCleaningItem());
                MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CLEANITEM_PLAYER_GET,
                        sender, player2.getName());

            } else {
                if (playerName.equalsIgnoreCase("@a")) {
                    Object[] players = Bukkit.getOnlinePlayers().toArray();
                    for (Object p : players) {
                        Player pl = (Player) p;
                        pl.getInventory().addItem(PluginConfigManager.getCleaningItem());
                        MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
                                MessageID.INFO_CLEANITEM_PLAYER_GET, sender, pl.getName());
                    }
                } else {

                    MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_PLAYER_NOT_ONLINE,
                            sender, playerName);
                }
            }
        }
    }

    /**
     * Activates or deactivates the cleaning item depending on the String
     * {@code value} if the {@code sender} has the correct permission.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void setCleaningItemActive(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String value = tuple.args[1];

        if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET_ACTIVE.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_SET_ACTIVE);

        } else if (StringUtils.isStringNotTrueOrFalse(value)) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
        } else {
            boolean b = Boolean.parseBoolean(value);
            PluginConfigManager.setCleaningItemActive(b);
            MessageSystem.sendChangedValue(sender, activeProperty, String.valueOf(b));
        }
    }

    /**
     * Activates or deactivates the durability loss for the cleaning item depending
     * on the String {@code value} if the {@code sender} has the correct permission.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void setDurabilityLoss(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String value = tuple.args[1];

        if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET_DURABILITYLOSS.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_SET_DURABILITYLOSS);
        } else if (StringUtils.isStringNotTrueOrFalse(value)) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
        } else {

            boolean b = Boolean.parseBoolean(value);
            PluginConfigManager.setDurabilityLossActive(b);
            MessageSystem.sendChangedValue(sender, durabilityProperty, String.valueOf(b));
        }
    }

    /**
     * Activates or deactivates the open event mode for the cleaning item depending
     * on the String {@code value} if the {@code sender} has the correct permission.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void setOpenEventMode(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String value = tuple.args[1];

        if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET_EVENT_MODE.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_SET_EVENT_MODE);
        } else {
            boolean b = Boolean.parseBoolean(value);
            PluginConfigManager.setOpenEvent(b);
            MessageSystem.sendChangedValue(sender, openEventProperty, String.valueOf(b));
        }
    }

    /**
     * Sets the lore for the cleaning item the {@code sender} has the correct
     * permission.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void setItemLore(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String[] args = tuple.args;

        if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET_LORE.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_SET_LORE);
        } else {

            String lore = args[1];
            for (int i = 2; i < args.length; i++) {
                lore = lore.concat(" ").concat(args[i]);
            }

            String[] lorearray = lore.split("/n");

            ArrayList<String> lorelist = new ArrayList<>();

            for (String obj : lorearray) {
                obj = obj.replace("&", "\u00A7");
                lorelist.add(obj);
            }

            ItemStack is = PluginConfigManager.getCleaningItem();
            ItemMeta im = is.getItemMeta();
            assert im != null;
            im.setLore(lorelist);
            is.setItemMeta(im);
            PluginConfigManager.setCleaningItem(is);

            MessageSystem.sendChangedValue(sender, loreProperty, lorelist.toString());
        }
    }

    /**
     * Sets the name of the cleaning item if the {@code sender} has the correct
     * permission.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void setItemName(CommandTuple tuple) {

        CommandSender sender = tuple.sender;
        String[] args = tuple.args;

        if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_RENAME.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_RENAME);
        } else {

            String newname = args[1];
            for (int i = 2; i < args.length; i++) {
                newname = newname.concat(" ").concat(args[i]);
            }

            newname = newname.replace("&", "\u00A7");

            ItemStack is = PluginConfigManager.getCleaningItem();
            ItemMeta im = is.getItemMeta();
            assert im != null;
            im.setDisplayName(newname);
            is.setItemMeta(im);
            PluginConfigManager.setCleaningItem(is);

            MessageSystem.sendChangedValue(sender, nameProperty, newname);
        }
    }

}
