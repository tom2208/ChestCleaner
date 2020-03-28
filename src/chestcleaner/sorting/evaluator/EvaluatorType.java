package chestcleaner.sorting.evaluator;

import java.util.ArrayList;
import java.util.List;

public enum EvaluatorType {

	BACK_BEGIN_STRING, BEGIN_BACK_STRING, TWEAKED_CREATIVE;

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
		} else if (str.equalsIgnoreCase(TWEAKED_CREATIVE.name())) {
			return TWEAKED_CREATIVE;
		}
		return null;
	}

	public static List<String> getIDList() {
		List<String> list = new ArrayList<>();
		list.add(BACK_BEGIN_STRING.name());
		list.add(BEGIN_BACK_STRING.name());
		list.add(TWEAKED_CREATIVE.name());
		return list;
	}

	public static Evaluator getEvaluator(EvaluatorType type) {

		if (type == null)
			return null;

		if (type.equals(BACK_BEGIN_STRING)) {
			return new BackBeginStringEvaluator();
		} else if (type.equals(BEGIN_BACK_STRING)) {
			return new BeginBackStringEvaluator();
		} else if (type.equals(TWEAKED_CREATIVE)) {
			return new TweakedCreativeEvaluator();
		}
		return null;
	}

	public static EvaluatorType DEFAULT = EvaluatorType.TWEAKED_CREATIVE;

}
