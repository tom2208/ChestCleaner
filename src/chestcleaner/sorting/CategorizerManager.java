package chestcleaner.sorting;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.config.serializable.Category;
import chestcleaner.config.serializable.ListCategory;
import chestcleaner.config.serializable.MasterCategory;
import chestcleaner.config.serializable.WordCategory;
import chestcleaner.sorting.categorizer.Categorizer;
import chestcleaner.sorting.categorizer.ListCategoryCategorizer;
import chestcleaner.sorting.categorizer.MasterCategorizer;
import chestcleaner.sorting.categorizer.PredicateCategorizer;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategorizerManager {

	private static List<Categorizer> availableCategorizers = new ArrayList<>();

	public static List<ItemStack> sort(List<ItemStack> items, List<String> categorizerNames) {

		List<Categorizer> categorizers = categorizerNames.stream().map(CategorizerManager::getByName)
				.collect(Collectors.toList());

		List<List<ItemStack>> categories = new ArrayList<>();
		categories.add(items);

		for (Categorizer categorizer : categorizers) {
			if (categorizer != null) {
				categories = categorizer.categorize(categories);
			}
		}

		return categories.stream().flatMap(List::stream).collect(Collectors.toList());
	}

	public static void addCategorizer(Categorizer categorizer) {
		if (getByName(categorizer.getName()) == null) {
			availableCategorizers.add(categorizer);
		} else {
			MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME);
		}
	}

	public static void removeCategoryAndSave(String category, CommandSender sender) {

		if (category != null) {
			Categorizer categorizer = getCategorizerByName(category);
			if (categorizer == null) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME, sender);
			} else {
				boolean success = PluginConfigManager.removeCategory(categorizer);

				if (!success) {
					MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
							MessageID.ERROR_CATEGORY_NOT_IN_CONFIG, sender, categorizer.getName());
				} else {

					availableCategorizers.remove(categorizer);
					MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CATEGORY_REMOVED,
							sender, categorizer.getName());
				}
			}
		}
	}

	private static Categorizer getCategorizerByName(String name) {
		for (Categorizer cat : availableCategorizers) {
			if (cat.getName().equalsIgnoreCase(name)) {
				return cat;
			}
		}
		return null;
	}

	public static void addCategoryAndSave(Category<?> category, CommandSender sender) {
		if (category != null) {
			availableCategorizers.removeIf(cat -> cat.getName().equals(category.getName()));
			if (category instanceof MasterCategory) {
				availableCategorizers.add(new MasterCategorizer((MasterCategory) category));
				PluginConfigManager.addMasterCategory((MasterCategory) category);
			} else if (category instanceof ListCategory) {
				availableCategorizers.add(new ListCategoryCategorizer((ListCategory) category));
				PluginConfigManager.addListCategory((ListCategory) category);
			} else if (category instanceof WordCategory) {
				availableCategorizers.add(new PredicateCategorizer((WordCategory) category));
				PluginConfigManager.addWordCategory((WordCategory) category);
			}
		} else {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_BOOK, sender);
		}
	}

	public static String addFromBook(List<String> pages, CommandSender sender) {
		if (pages.isEmpty() || pages.get(0).isEmpty()) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_BOOK, sender);
		}

		String allPages = String.join("\n", pages);
		String[] allLines = allPages.split("\n");
		for (int i = 0; i < allLines.length; i++) {
			allLines[i] = "  " + allLines[i].replace("§0", "");
		}
		allPages = "category:\n" + String.join("\n", allLines);

		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new StringReader(allPages));
		Object category = yamlConfiguration.get("category");
		addCategoryAndSave((Category<?>) category, sender);
		assert category != null;
		return ((Category<?>) category).getName();
	}

	public static boolean validateExists(List<String> strings) {
		return strings.stream().map(CategorizerManager::getByName).filter(Objects::nonNull).count() == strings.size();
	}

	public static Categorizer getByName(String name) {
		return availableCategorizers.stream().filter(categorizer -> categorizer.getName().equalsIgnoreCase(name))
				.findFirst().orElse(null);
	}

	public static List<String> getAllNames() {
		return availableCategorizers.stream().map(Categorizer::getName).collect(Collectors.toList());
	}
}
