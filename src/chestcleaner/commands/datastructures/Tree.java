package chestcleaner.commands.datastructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tree<T> implements Iterable<GraphNode<T>> {

    private GraphNode<T> root;

    public Tree(T rootValue) {
        root = new GraphNode<>(rootValue);
    }

    public Tree() {
        root = new GraphNode<>();
    }

    public Tree(GraphNode<T> node) {
        root = node;
    }

    /**
     * Adds a new child to a parent with the value {@code valueOfParent}. The parent has to exist.
     *
     * @param valueOfParent the value of the parent.
     * @param valueOfChild  the value of the new child.
     */
    public void addChild(T valueOfParent, T valueOfChild) {
        GraphNode<T> parent = getNode(valueOfParent);
        if (parent != null) {
            GraphNode<T> child = new GraphNode<>(valueOfChild);
            child.addParent(parent);
            parent.addChild(child);
        } else {
            throw new IllegalArgumentException("A node with the value of the argument should exist in the tree.");
        }
    }

    /**
     * Searches through the whole graph to return the first node it finds with the requested {@code value}.
     * Returns {@code null} if it can not find a node with the specific value.
     *
     * @param value the value of the node you want to get returned.
     * @return returns the node which value matches with {@code value}
     * if this node does not exist it returns {@code null}.
     */
    public GraphNode<T> getNode(T value) {
        return root.getNode(value::equals);
    }

    /**
     * Returns the root of the tree.
     *
     * @return the root of the tree.
     */
    public GraphNode<T> getRoot() {
        return root;
    }

    /**
     * Returns the level of the {@code node}. The root has the level 0,
     * its children the level 1, their children the level 2 and so on.
     *
     * @param node the not which level you want to get returned.
     * @return the level of the {@code node}.
     */
    public int getLevel(GraphNode<?> node) {
        if (node == null) {
            throw new NullPointerException("The node must be not null.");
        }

        if (node.getParents().size() == 0) {
            return 0;
        } else {
            return 1 + getLevel(node.getParents().get(0));
        }
    }

    public GraphNode<T> getNodeFormChildren(GraphNode<T> parent, Predicate<T> predicate) {
        for (GraphNode<T> child : parent.getChildren()) {
            if (predicate.test(child.getValue())) {
                return child;
            }
        }
        return null;
    }

    public List<GraphNode<T>> toList(GraphNode<T> node) {
        List<GraphNode<T>> list = new ArrayList<>();
        list.add(node);
        for (GraphNode<T> child : node.getChildren()) {
            List<GraphNode<T>> childList = toList(child);
            list = Stream.concat(list.stream(), childList.stream()).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public String toString() {
        return buildStringForNode(getRoot(), "");
    }

    private String buildStringForNode(GraphNode<T> node, String tabs) {
        StringBuilder builder = new StringBuilder();
        builder.append(tabs).append("├ ").append(node);
        boolean hasChild = node.hasChild();
        if (hasChild) builder.append("\n");

        for (GraphNode<T> n : node.getChildren()) {
            builder.append(buildStringForNode(n, tabs + "|\t")).append("\n");
        }
        if (hasChild) builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<GraphNode<T>> iterator() {
        return toList(getRoot()).iterator();
    }

    // STATIC METHODS

    /**
     * Checks if a directed graph is acyclic.
     *
     * @param tempRoot the root of the graph you want to check.
     * @return {@code true} if it is acyclic otherwise {@code false}.
     */
    public static boolean isGraphAcyclic(GraphNode<?> tempRoot) {
        boolean returnValue = isAcyclic(tempRoot);
        makeGraphUnvisited(tempRoot);
        return returnValue;
    }

    private static void makeGraphUnvisited(GraphNode<?> tempRoot) {
        tempRoot.makeUnvisited();
        for (GraphNode<?> child : tempRoot.getChildren()) {
            makeGraphUnvisited(child);
        }
    }

    private static boolean isAcyclic(GraphNode<?> tempRoot) {
        if (tempRoot.gotVisited()) {
            return false;
        }
        tempRoot.visit();

        for (GraphNode<?> child : tempRoot.getChildren()) {
            if (!isAcyclic(child)) {
                return false;
            }
        }
        return true;
    }

}
