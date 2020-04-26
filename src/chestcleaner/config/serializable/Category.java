package chestcleaner.config.serializable;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

/**
 * Configurable Category Interface.
 * Every Configurable Category must implement this interface.
 * It defines how the Category gets serialized and how it gets written into a minecraft book
 * @param <T>
 */
public interface Category<T> extends ConfigurationSerializable {
    /**
     *
     * @return the name of the category
     */
    String getName();

    /**
     * The value of the category. This is what defines the category.
     */
    T getValue();

    /**
     * Change the Value of the category
     */
    void setValue(T value);

    /**
     * This method returns the Category as a minecraft book, that can be read and edited in-game.
     * It should use the StringUtils.getAsBook(string) helper method
     */
    ItemStack getAsBook();
}
