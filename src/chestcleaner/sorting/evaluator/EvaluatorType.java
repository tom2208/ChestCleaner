package chestcleaner.sorting.evaluator;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public enum EvaluatorType {

	BACK_BEGIN_STRING, BEGIN_BACK_STRING;

	/**
	 * Returns the enum object if it's is equal to an existing entry.
	 * 
	 * @param str the name of the enum entry.
	 * @return the enum entry object or {@code null} if does not exist.
	 */
	public static EvaluatorType getEvaluatorTypByName(String str) {
		if (str == null)
			return null;

		if (str.equalsIgnoreCase(BACK_BEGIN_STRING.name())) {
			return BACK_BEGIN_STRING;
		} else if (str.equalsIgnoreCase(BEGIN_BACK_STRING.name())) {
			return BEGIN_BACK_STRING;
		}
		return null;
	}

	public static List<String> getIDList() {
		List<String> list = new ArrayList<String>();
		list.add(BACK_BEGIN_STRING.name());
		list.add(BEGIN_BACK_STRING.name());
		return list;
	}

	public static EvaluatorType DEFAULT = EvaluatorType.BACK_BEGIN_STRING;

}
