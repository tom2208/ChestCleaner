package chestcleaner.utils.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TreeNode<T> {

    private List<TreeNode<T>> children;
    private T value;

    public TreeNode(T entry) {
        children = new ArrayList<>();
        value = entry;
    }

    public TreeNode() {
        children = new ArrayList<>();
    }

    public void removeChild(TreeNode<T> node) {
        children.remove(node);
    }

    public void addChild(TreeNode<T> node) {
        children.add(node);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * Searches through the whole graph to return the first node it finds with the requested {@code value}.
     * Returns {@code null} if it can not find a node with the specific value.
     *
     * @param predicate the Predicate you want to be satisfied.
     * @return a node carrying the specific value if it finds one. Otherwise it returns {@code null}.
     */
    public TreeNode<T> getNode(Predicate<T> predicate) {
        if (getValue() != null && predicate.test(value)) {
            return this;
        } else {
            for (TreeNode<T> node : children) {
                TreeNode<T> n = node.getNode(predicate);
                if (n != null) {
                    return n;
                }
            }
        }
        return null;
    }

}
