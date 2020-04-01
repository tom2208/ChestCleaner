package chestcleaner.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("ListCategory")
public class ListCategory implements Category<List<String>>, ConfigurationSerializable {

    public String name;
    public List<String> list;

    private static final String nameKey = "name";
    private static final String listKey = "list";

    public ListCategory(String name, List<String> list) {
        this.name = name;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public List<String> getValue() {
        return list;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put(nameKey, this.name);
        result.put(listKey, this.list);
        return result;
    }

    // not "unused". Needed for ConfigurationSerializable e.g. To read as Object from config.yml
    public static ListCategory deserialize(Map<String, Object> map) {
        String name = map.containsKey(nameKey) ? ((String)map.get(nameKey)) : "";
        List<String> list = map.containsKey(listKey) ? ((List<String>)map.get(listKey)) : null;

        return new ListCategory(name, list);
    }
}
