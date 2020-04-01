package chestcleaner.sorting.v2;

import java.util.ArrayList;
import java.util.List;

public class CategorizerManager {

	private List<Categorizer> availableCategorizers = new ArrayList<>();

	public void registerCategorizer(Categorizer categorizer) {
		availableCategorizers.add(categorizer);
	}

	/**
	 * Returns the registered instance of the Categorizer with the name {@code name}
	 * if it isn't registered it returns null.
	 * 
	 * @param name the name of the Categorizer
	 * @return the registered instance of the Categorizer or null if it's not
	 *         registered.
	 */
	public Categorizer getByName(String name) {
		Categorizer result = availableCategorizers.stream()
				.filter(categorizer -> categorizer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		return result;
	}
	
	/**
	 * Returns a List of all names as Strings of the registered Categorizers.
	 * @return the List.
	 */
	public List<String> getNameList(){
		List<String> names = new ArrayList<>();
		for(Categorizer categorizer : availableCategorizers) {
			names.add(categorizer.getName());
		}
		return names;
	}
	
}
