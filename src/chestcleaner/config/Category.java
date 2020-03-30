package chestcleaner.config;

public interface Category<T> {
    String getName();
    T getValue();
}
