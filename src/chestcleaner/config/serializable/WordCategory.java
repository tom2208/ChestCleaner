package chestcleaner.config.serializable;

import chestcleaner.utils.StringUtils;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("WordCategory")
public class WordCategory implements Category<String> {

    private String name;
    private String word;

    private static final String nameKey = "name";
    private static final String wordKey = "word";

    public WordCategory(String name, String word) {
        this.name = name;
        this.word = word;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return word;
    }

    public void setValue(String value) {
         this.word = value;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put(nameKey, this.name);
        result.put(wordKey, this.word);
        return result;
    }

    // not "unused". Needed for ConfigurationSerializable e.g. To read as Object from config.yml
    public static WordCategory deserialize(Map<String, Object> map) {
        String name = map.containsKey(nameKey) ? ((String)map.get(nameKey)) : "";
        String word = map.containsKey(wordKey) ? ((String)map.get(wordKey)) : "";

        return new WordCategory(name, word);
    }

    @Override
    public ItemStack getAsBook() {
        String book = "==: WordCategory\n"
                + nameKey + ": " + name + "\n"
                + wordKey + ": " + word;
        return StringUtils.getAsBook(book);
    }
}
