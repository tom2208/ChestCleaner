package chestcleaner.config.serializable;

public interface Category<T> {
    String getName();
    T getValue();
}
