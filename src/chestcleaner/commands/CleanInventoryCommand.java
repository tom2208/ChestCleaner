package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chestcleaner.commands.datastructures.CommandTree;
import chestcleaner.commands.datastructures.CommandTuple;
import chestcleaner.cooldown.CMRegistry;
import chestcleaner.utils.SortingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.BlockDetector;
import chestcleaner.utils.InventoryDetector;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

/**
 * A command class representing the CleanInventory command. CleanInventory
 * Command explained:
 * https://github.com/tom2208/ChestCleaner/wiki/Command--cleaninventory
 *
 * @author Tom2208
 */
public class CleanInventoryCommand implements CommandExecutor, TabCompleter {

    private final String ownSubCommand = "own";
    private final CommandTree cmdTree;

    public static final String COMMAND_ALIAS = "cleaninventory";


    public CleanInventoryCommand() {
        cmdTree = new CommandTree(COMMAND_ALIAS);

        cmdTree.addPath("/cleaninventory x", null, Integer.class);
        cmdTree.addPath("/cleaninventory x y", null, Integer.class);
        cmdTree.addPath("/cleaninventory x y z", this::sortInvAtLocation, Integer.class);
        cmdTree.addPath("/cleaninventory x y z world", this::sortInvInWorld, String.class);

        cmdTree.addPath("/cleaninventory", this::sortInvForPlayer);
        cmdTree.addPath("/cleaninventory ".concat(ownSubCommand), this::sortPlayerInventory);
        cmdTree.addPath("/cleaninventory player", this::sortPlayerInventory, Player.class);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission(PluginPermissions.CMD_INV_CLEAN.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_INV_CLEAN);
            return true;
        }

        cmdTree.execute(sender, cmd, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return cmdTree.getListForTabCompletion(args);
    }

    /**
     * The player {@code player} sorts the inventory of the player with the name
     * {@code playerName}. He needs the correct permissions.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void sortPlayerInventory(CommandTuple tuple) {
        Player player = getPlayer(tuple.sender);
        if (player == null) return;
        String playerName = tuple.args[0];
        if (playerName.equalsIgnoreCase(ownSubCommand) || playerName.equalsIgnoreCase(player.getName())) {
            if (!player.hasPermission(PluginPermissions.CMD_INV_CLEAN_OWN.getString())) {
                MessageSystem.sendPermissionError(player, PluginPermissions.CMD_INV_CLEAN_OWN);
            }
            if (InventorySorter.sortPlayerInventory(player)) {
                MessageSystem.sendSortedMessage(player);
                InventorySorter.playSortingSound(player);
            }
        } else {
            if (!player.hasPermission(PluginPermissions.CMD_INV_CLEAN_OTHERS.getString())) {
                MessageSystem.sendPermissionError(player, PluginPermissions.CMD_INV_CLEAN_OTHERS);
            }

            Player player2 = Bukkit.getPlayer(playerName);

            if (player2 == null) {
                MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_PLAYER_NOT_ONLINE, player);
            } else {
                if (InventorySorter.sortInventory(player2.getInventory(), player,
                        InventoryDetector.getPlayerInventoryList(player2))) {
                    MessageSystem.sendSortedMessage(player);
                    MessageSystem.sendSortedMessage(player2);
                    InventorySorter.playSortingSound(player);
                    InventorySorter.playSortingSound(player2);
                }
            }

        }
    }

    private Player getPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        return null;
    }

    /**
     * The player {@code p} sorts a blocks inventory.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void sortInvForPlayer(CommandTuple tuple) {
        Player p = getPlayer(tuple.sender);
        Block block = BlockDetector.getTargetBlock(p);
        sortBlock(block, p, p);
    }

    /**
     * Sorts an inventory of a block if it has one.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void sortInvInWorld(CommandTuple tuple) {

        String worldStr = tuple.args[3];
        CommandSender cs = tuple.sender;

        World world = Bukkit.getWorld(worldStr);
        if (world == null) {
            MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_WORLD_NAME, cs, worldStr);
        } else {
            sortInvAtLocation(tuple);
        }
    }

    /**
     * Sorts an inventory of a block if it has one.
     *
     * @param tuple the tuple the sub-command should run on.
     */
    private void sortInvAtLocation(CommandTuple tuple) {
        String xStr = tuple.args[0];
        String yStr = tuple.args[1];
        String zStr = tuple.args[2];
        CommandSender sender = tuple.sender;
        Player player = getPlayer(sender);
        World world;
        if (tuple.args.length >= 4) {
            world = Bukkit.getWorld(tuple.args[3]);
        } else {
            world = player.getWorld();
        }
        int x = (int) Double.parseDouble(xStr);
        int y = (int) Double.parseDouble(yStr);
        int z = (int) Double.parseDouble(zStr);
        Block block = BlockDetector.getBlockByLocation(new Location(world, x, y, z));
        sortBlock(block, player, sender);
    }

    /**
     * Sorts the inventory of a block if it has one.
     *
     * @param block  the block which may have an inventory.
     * @param p      the player who is sorting.
     * @param sender the sender which executed the command.
     */
    private void sortBlock(Block block, Player p, CommandSender sender) {
        if (!SortingUtils.isOnInventoryBlacklist(block, sender)) {
            if (!CMRegistry.isOnCooldown(CMRegistry.CMIdentifier.SORTING, p)) {
                if (InventorySorter.sortPlayerBlock(block, p)) {
                    MessageSystem.sendSortedMessage(sender);
                } else {
                    MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_BLOCK_NO_INVENTORY, sender,
                            "(" + block.getX() + " / " + block.getY() + " / " + block.getZ() + ", " + block.getType().name()
                                    + ")");
                }
            }
        }
    }
}
