package chestcleaner.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.BlockIterator;

public class BlockDetector implements Listener {

	private static final int RANGE = 12;
	private static final Material[] passableBlocks = { Material.AIR, Material.CAVE_AIR, Material.ITEM_FRAME, Material.GRASS,
			Material.TORCH };

	/**
	 * Return the first Block the vector of view hits. This method ignores air.
	 * 
	 * @param player The player you want to get the view vector from.
	 * @param range  The range you want to search for a non-air block.
	 * @return Returns the block the player is looking at, if there is no in range
	 *         it returns an air block.
	 */
	public static Block getTargetBlock(Player player) {
		BlockIterator iter = new BlockIterator(player, RANGE);
		Block lastBlock = iter.next();

		while (iter.hasNext()) {

			lastBlock = iter.next();
			if (isBlockPassable(lastBlock) || lastBlock.getType().name().contains("SIGN")
					|| lastBlock.getType().name().contains("CARPET") || lastBlock.isLiquid()) {
				continue;
			}
			break;
		}

		return lastBlock;
	}

	/**
	 * Returns the Block at the Location {@code loc}.
	 * 
	 * @param loc The Location of the block.
	 * @return Returns the Block at the Location {@code loc}.
	 */
	public static Block getBlockByLocation(Location loc) {
		return loc.getWorld().getBlockAt(loc);
	}

	private static boolean isBlockPassable(Block block) {
		for (Material m : passableBlocks) {
			if (block.getType().equals(m))
				return true;
		}

		return false;
	}

}
