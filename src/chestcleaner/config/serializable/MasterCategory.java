package chestcleaner.config.serializable;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@SerializableAs("MasterCategory")
public class MasterCategory extends ListCategory {
    public MasterCategory(String name, List<String> list) {
        super(name, list);
    }

    // not "unused". Needed for ConfigurationSerializable e.g. To read as Object from config.yml
    public static MasterCategory deserialize(Map<String, Object> map) {
        ListCategory temp = ListCategory.deserialize(map);
        return new MasterCategory(temp.getName(), temp.getValue());
    }

    @Override
    public ItemStack getAsBook() {
        return getAsBook("==: MasterCategory");
    }
}
