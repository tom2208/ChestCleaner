package chestcleaner.config.serializable;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public interface Category<T> extends ConfigurationSerializable {
    String getName();
    T getValue();
    void setValue(T value);
    ItemStack getAsBook();
}
