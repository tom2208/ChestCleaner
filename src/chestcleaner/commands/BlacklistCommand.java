package chestcleaner.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import chestcleaner.commands.datastructures.CommandTuple;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.commands.datastructures.CommandTree;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

/**
 * A command class representing the blacklist command. Blacklist Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-blacklist
 *
 * @author Tom2208
 */
public class BlacklistCommand implements CommandExecutor, TabCompleter {

    private final CommandTree cmdTree;

    // The lineNumbers of a page when the list gets displayed in the in game chat.
    private final int LIST_PAGE_LENGTH = 8;

    public static final String COMMAND_ALIAS = "blacklist";

    private final String stackingSubCommand = "stacking";
    private final String inventorySubCommand = "inventory";
    private final String autoRefillSubCommand = "autoRefill";

    public BlacklistCommand() {
        cmdTree = new CommandTree(COMMAND_ALIAS);
        /* subcommands */
        cmdTree.addPath("/blacklist blacklist", null, BlacklistType.class, false);
        // ADD
        cmdTree.addPath("/blacklist blacklist add", this::addSubCommand, null, false);
        cmdTree.addPath("/blacklist blacklist add materialId", this::addSubCommand, Material.class, false);
        //REMOVE
        cmdTree.addPath("/blacklist blacklist remove", this::removeSubCommand, null, false);
        cmdTree.addPath("/blacklist blacklist remove materialId", this::removeSubCommand, Material.class, false);
        //LIST
        cmdTree.addPath("/blacklist blacklist list", this::listSubCommand, null, false);
        //CLEAR
        cmdTree.addPath("/blacklist blacklist clear", this::clearSubCommand, null, false);
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

    private BlacklistType getListType(String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase(stackingSubCommand)) {
                return BlacklistType.STACKING;
            } else if (args[0].equalsIgnoreCase(inventorySubCommand)) {
                return BlacklistType.INVENTORY;
            } else if (args[0].equalsIgnoreCase(autoRefillSubCommand)) {
                return BlacklistType.AUTOREFILL;
            }
        }
        return null;
    }

    private List<Material> getList(String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase(stackingSubCommand)) {
                return PluginConfigManager.getBlacklistStacking();
            } else if (args[0].equalsIgnoreCase(inventorySubCommand)) {
                return PluginConfigManager.getBlacklistInventory();
            } else if (args[0].equalsIgnoreCase(autoRefillSubCommand)) {
                return PluginConfigManager.getBlacklistAutoRefill();
            }
        }
        return null;
    }

    private Player checkForPlayer(CommandSender cs) {
        if (cs instanceof Player) {
            return (Player) cs;
        }
        return null;
    }

    /**
     * A method representing the sub-command <b>add</b>
     * that runs all actions with the data of the {@code tuple} when called.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    public void addSubCommand(CommandTuple tuple) {
        Player player = checkForPlayer(tuple.sender);
        if (player != null && tuple.args.length == 2) {
            addMaterial(tuple.sender, getListType(tuple.args), getList(tuple.args), getMaterialFromPlayerHand(player));
        } else if (tuple.args.length == 3) {
            addMaterialName(tuple.sender, getListType(tuple.args), getList(tuple.args), tuple.args[2]);
        }
    }

    /**
     * A method representing the sub-command <b>remove</b>
     * that runs all actions with the data of the {@code tuple} when called.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    public void removeSubCommand(CommandTuple tuple) {
        Player player = checkForPlayer(tuple.sender);
        if (player != null && tuple.args.length == 2) {
            removeMaterial(tuple.sender, getListType(tuple.args),
                    Objects.requireNonNull(getList(tuple.args)), getMaterialFromPlayerHand(player));
        } else if (tuple.args.length == 3) {
            removeMaterialName(tuple.sender, getListType(tuple.args), getList(tuple.args), tuple.args[2]);
        }
    }

    /**
     * A method representing the sub-command <b>list</b>
     * that runs all actions with the data of the {@code tuple} when called.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    public void listSubCommand(CommandTuple tuple) {
        if (tuple.args.length == 2) {
            printBlacklist(tuple.sender, "1", Objects.requireNonNull(getList(tuple.args)));
        } else if (tuple.args.length == 3) {
            printBlacklist(tuple.sender, tuple.args[2], Objects.requireNonNull(getList(tuple.args)));
        }
    }

    /**
     * A method representing the sub-command <b>clear</b>
     * that runs all actions with the data of the {@code tuple} when called.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    public void clearSubCommand(CommandTuple tuple) {
        clearBlacklist(tuple.sender, getListType(tuple.args));
    }

    private void addMaterialName(CommandSender sender, BlacklistType type, List<Material> list, String name) {
        Material material = Material.getMaterial(name.toUpperCase());
        if (material == null) {
            MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_MATERIAL_NAME, sender,
                    name);
        } else {
            addMaterial(sender, type, list, material);
        }
    }

    /**
     * Adds {@code material} to the blacklist.
     *
     * @param sender   the sender which executed the command.
     * @param type     the list on which the material gets added.
     * @param list     a list which gets modified and set to the config.
     * @param material the material that gets added to the list.
     */
    private void addMaterial(CommandSender sender, BlacklistType type, List<Material> list, Material material) {
        if (list == null) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_BLACKLIST_LIST_NOT_EXIST, sender);
            return;
        }

        if (list.contains(material)) {
            MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_BLACKLIST_EXISTS, sender,
                    material.name().toLowerCase());
        } else {

            list.add(material);
            saveList(type, list);

            MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_BLACKLIST_ADD, sender,
                    material.name().toLowerCase());
        }
    }

    /**
     * Removes a material with the name {@code name} form a blacklist.
     *
     * @param sender the sender which executed the command.
     * @param type   the type of blacklist form which you want to remove the
     *               material.
     * @param list   the list which gets modified and then set to the config.
     * @param name   the name of the material you want to remove.
     */
    private void removeMaterialName(CommandSender sender, BlacklistType type, List<Material> list, String name) {
        Material material = Material.getMaterial(name.toUpperCase());

        if (material == null) {
            try {
                int index = Integer.parseInt(name);
                // expect 1 based index input, bc of list and not all players are programmers
                if (index > 0 && index <= list.size()) {
                    material = list.get(index - 1);
                } else {
                    MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
                            MessageID.ERROR_VALIDATION_INDEX_BOUNDS, sender, String.valueOf(index));
                    return;
                }
            } catch (NumberFormatException ex) {
                MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_MATERIAL_NAME, sender,
                        name);
                return;
            }

        }
        removeMaterial(sender, type, list, material);
    }

    /**
     * Removes a {@code material} form a blacklist.
     *
     * @param sender   the sender which executed the command.
     * @param type     the type of blacklist form which you want to remove the
     *                 material.
     * @param list     the list which gets modified and then set to the config.
     * @param material the material you want to remove.
     */
    private void removeMaterial(CommandSender sender, BlacklistType type, List<Material> list, Material material) {
        if (!list.contains(material)) {
            MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_BLACKLIST_NOT_EXISTS,
                    sender, material.name().toLowerCase());
        } else {

            list.remove(material);
            saveList(type, list);

            MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_BLACKLIST_DEL, sender,
                    material.name().toLowerCase());
        }
    }

    /**
     * Sends a page with the page number {@code pageString} of the list to the
     * {@code sender}.
     *
     * @param sender     the sender which will receive the the list.
     * @param pageString the page of the list which gets sent.
     * @param list       The list which contains the page/part of the list you want
     *                   to send.
     */
    private void printBlacklist(CommandSender sender, String pageString, List<Material> list) {
        if (list.size() == 0) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_BLACKLIST_EMPTY, sender);
        } else {

            List<String> names = list.stream().map(item -> item.name().toLowerCase()).collect(Collectors.toList());
            MessageSystem.sendListPageToCS(names, sender, pageString, LIST_PAGE_LENGTH);
        }
    }

    /**
     * Clears the specific blacklist in the config.
     *
     * @param sender the sender which executed the command. It becomes a success
     *               message.
     * @param type   the type of the list. It determines the blacklist which gets
     *               cleared.
     */
    private void clearBlacklist(CommandSender sender, BlacklistType type) {
        List<Material> list = new ArrayList<>();
        saveList(type, list);
        MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_BLACKLIST_CLEARED, sender);
    }

    /**
     * Returns the Material of the item in a hand (prefers the main hand, if it's
     * empty it take the off handF).
     *
     * @param p the player of the hand.
     * @return it returns the material of you main hand if it is not AIR otherwise
     * the Material of your off hand.
     */
    private Material getMaterialFromPlayerHand(Player p) {
        if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            if (!p.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
                return p.getInventory().getItemInOffHand().getType();
            }
        }
        return p.getInventory().getItemInMainHand().getType();
    }

    /**
     * Saves the list in to the config.yml.
     *
     * @param type  the type of blacklist you want to save.
     * @param items the list which gets set to the config.
     */
    private void saveList(BlacklistType type, List<Material> items) {
        if (type.equals(BlacklistType.STACKING)) {
            PluginConfigManager.setBlacklistStacking(items);
        } else if (type.equals(BlacklistType.INVENTORY)) {
            PluginConfigManager.setBlacklistInventory(items);
        } else if (type.equals(BlacklistType.AUTOREFILL)) {
            PluginConfigManager.setBlacklistAutoRefill(items);
        }
    }


    public enum BlacklistType {
        STACKING, INVENTORY, AUTOREFILL;

        public static BlacklistType getBlackListTypeByString(String str) {
            for (BlacklistType type : values()) {
                if (str.equalsIgnoreCase(type.toString())) return type;
            }
            return null;
        }

    }

}
