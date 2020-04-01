package chestcleaner.config;

import org.bukkit.configuration.serialization.SerializableAs;

import java.util.List;

@SerializableAs("MasterCategory")
public class MasterCategory extends ListCategory {
    public MasterCategory(String name, List<String> list) {
        super(name, list);
    }
}
