package chestcleaner.commands.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GraphNode<T> {

    private List<GraphNode<T>> children;
    private List<GraphNode<T>> parents;
    private T value;
    private boolean visited = false;

    public GraphNode(T entry) {
        children = new ArrayList<>();
        parents = new ArrayList<>();
        value = entry;
    }

    public GraphNode() {
        children = new ArrayList<>();
        parents = new ArrayList<>();
    }

    public void removeParent(GraphNode<T> node) {
        parents.remove(node);
    }

    public void removeChild(GraphNode<T> node) {
        children.remove(node);
    }

    public void addParent(GraphNode<T> node) {
        parents.add(node);
    }

    public void addChild(GraphNode<T> node) {
        children.add(node);
        node.addParent(this);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean hasChild() {
        return children.size() > 0;
    }

    public boolean hasParent() {
        return parents.size() > 0;
    }

    public List<GraphNode<T>> getChildren() {
        return children;
    }

    public List<GraphNode<T>> getParents() {
        return parents;
    }

    public boolean gotVisited() {
        return visited;
    }

    public void makeUnvisited() {
        visited = false;
    }

    public void visit() {
        visited = true;
    }

    /**
     * Searches through the whole graph to return the first node it finds with the requested {@code value}.
     * Returns {@code null} if it can not find a node with the specific value.
     *
     * @param predicate the Predicate you want to be satisfied.
     * @return a node carrying the specific value if it finds one. Otherwise it returns {@code null}.
     */
    public GraphNode<T> getNode(Predicate<T> predicate) {
        if (getValue() != null && predicate.test(value)) {
            return this;
        } else {
            for (GraphNode<T> node : children) {
                GraphNode<T> n = node.getNode(predicate);
                if (n != null) {
                    return n;
                }
            }
        }
        return null;
    }

    public String toString() {
        return "Node:" + value.toString();
    }

}
