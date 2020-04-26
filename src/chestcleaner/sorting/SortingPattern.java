package chestcleaner.sorting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SortingPattern {

	LEFT_TO_RIGHT_TOP_TO_BOTTOM, RIGHT_TO_LEFT_BOTTOM_TO_TOP, TOP_TO_BOTTOM_LEFT_TO_RIGHT, BOTTOM_TO_TOP_LEFT_TO_RIGHT;

	/**
	 * Returns the enum object if it's is equal to an existing entry.
	 * 
	 * @param str the name of the enum entry.
	 * @return the enum entry object or {@code null} if does not exist.
	 */
	public static SortingPattern getSortingPatternByName(String str) {

		if (str == null)
			return null;

		if (str.equalsIgnoreCase(SortingPattern.BOTTOM_TO_TOP_LEFT_TO_RIGHT.name())) {
			return BOTTOM_TO_TOP_LEFT_TO_RIGHT;
		} else if (str.equalsIgnoreCase(SortingPattern.LEFT_TO_RIGHT_TOP_TO_BOTTOM.name())) {
			return LEFT_TO_RIGHT_TOP_TO_BOTTOM;
		} else if (str.equalsIgnoreCase(SortingPattern.RIGHT_TO_LEFT_BOTTOM_TO_TOP.name())) {
			return SortingPattern.RIGHT_TO_LEFT_BOTTOM_TO_TOP;
		} else if (str.equalsIgnoreCase(SortingPattern.TOP_TO_BOTTOM_LEFT_TO_RIGHT.name())) {
			return SortingPattern.TOP_TO_BOTTOM_LEFT_TO_RIGHT;
		}
		return null;
	}

	public static List<String> getIDList() {
		return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
	}
}
