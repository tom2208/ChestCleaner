package chestcleaner.commands.datastructures;

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.commands.SortingAdminCommand;
import chestcleaner.sorting.CategorizerManager;
import chestcleaner.sorting.categorizer.Categorizer;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A acyclic directed tree structure representing a command. A node holds an alias and a lambda Consumer.
 * It offers many useful methods that reduces the work which needs to be done creating new spigot command.
 */
public class CommandTree extends Tree<CommandTree.Quadruple> {

    public CommandTree(String commandAlias) {
        super(new Quadruple(commandAlias, null, null, false));
    }

    public void execute(CommandSender sender, Command command, String alias, String[] args) {

        //TODO reorder so int types etc comes before string

        GraphNode<Quadruple> node = getRoot();

        if ((args.length == 0 && node.getValue().consumer != null) || node.getValue().definiteExecute) {
            executeNode(node, sender, command, alias, args);
            return;
        }

        for (int i = 0; i < args.length; i++) {
            boolean isLast = i == args.length - 1;
            GraphNode<Quadruple> nextNode = getChildNodeByStr(node, args[i]);
            if (nextNode == null) {
                for (GraphNode<Quadruple> child : node.getChildren()) {
                    if (isTypeNode(child) && getInterpretedObjByNodeType(args[i], child) != null) {
                        nextNode = child;
                        node = child;
                        break;
                    }
                }
            } else {
                node = nextNode;
            }
            System.out.println(nextNode);
            if ((isLast || node.getValue().definiteExecute) && nextNode != null) {
                executeNode(node, sender, command, alias, args);
                return;
            }
        }
        MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, buildSyntax(node), sender);
    }

    private void executeNode(GraphNode<Quadruple> node, CommandSender sender, Command command, String alias, String[] args) {
        if (node.getValue().consumer != null) {
            CommandTuple tuple = new CommandTuple(sender, command, alias, args);
            node.getValue().consumer.accept(tuple);
        } else {
            MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, buildSyntax(node), sender);
        }
    }

    private Object getInterpretedObjByNodeType(String str, GraphNode<Quadruple> node) {

        if (node == null || node.getValue().type == null) {
            return null;
        }

        //Player
        if (node.getValue().type.equals(Player.class)) {
            List<Player> list = Bukkit.getOnlinePlayers().stream().filter(
                    e -> e.getDisplayName().equalsIgnoreCase(str)).collect(Collectors.toList());
            if (list.size() == 1) {
                return list.get(0);
            }
        }

        // String
        if (node.getValue().type.equals(String.class)) {
            return str;
        }

        // Material
        if (node.getValue().type.equals(Material.class)) {
            return Material.getMaterial(str.toUpperCase());
        }

        // Categorizer
        if (node.getValue().type.equals(Categorizer.class)) {
            return CategorizerManager.getByName(str);
        }

        // RefillType
        if (node.getValue().type.equals(SortingAdminCommand.RefillType.class)) {
            return SortingAdminCommand.RefillType.getByName(str);
        }

        // Blacklist
        if (node.getValue().type.equals(BlacklistCommand.BlacklistType.class)) {
            return BlacklistCommand.BlacklistType.getBlackListTypeByString(str);
        }
        // Integer
        if (node.getValue().type.equals(Integer.class)) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ignored) {
            }
        }

        // Boolean
        if (node.getValue().type.equals(Boolean.class)) {
            if (str.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if (str.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
        }

        return null;
    }

    private boolean isTypeNode(GraphNode<Quadruple> node) {
        return node != null && node.getValue().type != null;
    }

    /**
     * Adds a node into the command tree. This node has no definite execution.
     *
     * @param path     like the ingame command. Example: "/cmd subCmd arg".
     * @param consumer the consumer which gets get executed if this command gets executed.
     *                 Use {@code null} if you don't want this to be an executable path.
     * @param type     This is null if the path directs to a node which is not an argument
     *                 otherwise it determines the type of the argument.
     */
    public void addPath(String path, Consumer<CommandTuple> consumer, Class<?> type) {
        addPath(path, consumer, type, false);
    }

    /**
     * Adds a node into the command tree. This node has no definite execution and no type.
     *
     * @param path     like the ingame command. Example: "/cmd subCmd arg".
     * @param consumer the consumer which gets get executed if this command gets executed.
     *                 Use {@code null} if you don't want this to be an executable path.
     */
    public void addPath(String path, Consumer<CommandTuple> consumer) {
        addPath(path, consumer, null, false);
    }

    /**
     * Adds a node into the command tree.
     *
     * @param path            like the ingame command. Example: "/cmd subCmd arg".
     * @param consumer        the consumer which gets get executed if this command gets executed.
     *                        Use {@code null} if you don't want this to be an executable path.
     * @param type            This is null if the path directs to a node which is not an argument
     *                        otherwise it determines the type of the argument.
     * @param definiteExecute if true the the command will always execute on this node,
     *                        otherwise they can but don't have to execute.
     */
    public void addPath(String path, Consumer<CommandTuple> consumer, Class<?> type, boolean definiteExecute) {
        String[] args = path.split(" ");
        GraphNode<Quadruple> node = getRoot();
        if (path.equalsIgnoreCase("/" + getRoot().getValue().label)) {
            getRoot().getValue().consumer = consumer;
            getRoot().getValue().type = type;
            getRoot().getValue().definiteExecute = definiteExecute;
            return;
        }

        for (int i = 1; i < args.length; i++) {
            final int finalI = i;
            GraphNode<Quadruple> tempNode
                    = getNodeFormChildren(node, t -> t.label.equalsIgnoreCase(args[finalI]));

            boolean isLastElement = i + 1 >= args.length;

            if (tempNode == null) {
                Quadruple tuple = new Quadruple(args[i], null, null, false);
                tempNode = new GraphNode<>(tuple);
                node.addChild(tempNode);
            }

            if (isLastElement) {
                tempNode.getValue().consumer = consumer;
                tempNode.getValue().type = type;
                tempNode.getValue().definiteExecute = definiteExecute;
            }
            node = tempNode;
        }

    }

    /**
     * Returns a sorted list of suggestions for TabCompletion with partial matches for the last argument.
     *
     * @param args the arguments of the command.
     * @return the list.
     */
    public List<String> getListForTabCompletion(String[] args) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], getListOfCandidates(args), completions);
        Collections.sort(completions);
        return completions;
    }

    private List<String> getListOfCandidates(String[] args) {

        List<String> list = new ArrayList<>();
        List<GraphNode<Quadruple>> nodes = getPathNodeList(args);
        GraphNode<Quadruple> lastNode = nodes.get(nodes.size() - 1);
        if (nodes.size() == args.length) {
            for (GraphNode<Quadruple> child : lastNode.getChildren()) {
                list.addAll(genNodeCompletions(child));
            }
        }
        if(lastNode.getValue().definiteExecute){
            list.addAll(genNodeCompletions(lastNode));
        }
        return list;
    }

    /**
     * Returns a List of all Nodes that exists in the continuous {@code path}. The root is included.
     *
     * @param args the arguments of which you want to get the Node-List from.
     * @return the Iterator.
     */
    private List<GraphNode<Quadruple>> getPathNodeList(String[] args) {
        List<GraphNode<Quadruple>> elements = new ArrayList<>();
        GraphNode<Quadruple> node = getRoot();
        elements.add(node);
        for (String arg : args) {
            boolean found = false;
            for (GraphNode<Quadruple> child : node.getChildren()) {
                if (child.getValue().label.equalsIgnoreCase(arg)) {
                    found = true;
                    elements.add(child);
                    node = child;
                    break;
                }
            }

            if (!found) {
                for (GraphNode<Quadruple> child : node.getChildren()) {
                    if (getInterpretedObjByNodeType(arg, child) != null) {
                        found = true;
                        elements.add(child);
                        node = child;
                        break;
                    }
                }
            }

            if (!found) {
                break;
            }
        }
        return elements;
    }

    private List<String> genNodeCompletions(GraphNode<Quadruple> node) {
        List<String> list = new ArrayList<>();
        Predicate<Class<?>> isType = c -> c.equals(node.getValue().type);
        if (node.getValue().type == null) {
            list.add(node.getValue().label);
        } else if (isType.test(Player.class)) {
            list = Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).collect(Collectors.toList());
        } else if (isType.test(BlacklistCommand.BlacklistType.class)) {
            list = Arrays.stream(BlacklistCommand.BlacklistType.values()).map(Enum::toString).collect(Collectors.toList());
        } else if (isType.test(Boolean.class)) {
            list.add("true");
            list.add("false");
        } else if (isType.test(Material.class)) {
            list = Arrays.stream(Material.values()).map(Enum::toString).collect(Collectors.toList());
        } else if (isType.test(SortingAdminCommand.RefillType.class)) {
            list = Arrays.stream(SortingAdminCommand.RefillType.values()).map(Enum::toString).collect(Collectors.toList());
        } else if (isType.test(Categorizer.class)) {
            list = CategorizerManager.getAllNames();
        }
        return list;
    }

    /**
     * Generates a Syntax message for the subcommand with the node {@code node}.
     *
     * @param node the node which determines the subcommand and thus its syntax.
     * @return the syntax string.
     */
    public String buildSyntax(GraphNode<Quadruple> node) {
        StringBuilder syntax = new StringBuilder();
        if (node.hasChild()) {
            syntax = new StringBuilder("<");
            for (GraphNode<Quadruple> child : node.getChildren()) {
                syntax.append(child.getValue().label).append(", ");
            }
            syntax = new StringBuilder(syntax.substring(0, syntax.length() - 2).concat(">"));
        }

        GraphNode<Quadruple> nextNode = node;

        do {
            if (nextNode.getValue().type == null) {
                syntax.insert(0, nextNode.getValue().label + " ");
            } else {
                syntax.insert(0, "<" + nextNode.getValue().label + "> ");
            }
            if (nextNode.hasParent()) nextNode = nextNode.getParents().get(0);
            else nextNode = null;
        } while (nextNode != null);

        return "/" + syntax;
    }

    private GraphNode<Quadruple> getChildNodeByStr(GraphNode<Quadruple> node, String string) {

        for (GraphNode<Quadruple> n : node.getChildren()) {

            if (n.getValue().label.equals(string) && n.getValue().type == null) {
                return n;
            }

        }
        return null;
    }

    static class Quadruple {

        String label;
        Consumer<CommandTuple> consumer;
        Class<?> type;
        boolean definiteExecute;

        public Quadruple(String label, Consumer<CommandTuple> consumer, Class<?> type, boolean definiteExecute) {
            this.label = label;
            this.consumer = consumer;
            this.type = type;
            this.definiteExecute = definiteExecute;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Quadruple quadruple = (Quadruple) o;
            return definiteExecute == quadruple.definiteExecute && Objects.equals(label, quadruple.label) &&
                    Objects.equals(consumer, quadruple.consumer) && Objects.equals(type, quadruple.type);
        }

        public String toString() {
            return "(" + label + "," + consumer + "," + type + "," + definiteExecute + ")";
        }

    }

}
