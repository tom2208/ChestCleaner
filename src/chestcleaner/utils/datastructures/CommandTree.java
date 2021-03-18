package chestcleaner.utils.datastructures;

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A acyclic directed tree structure representing a command. A node holds an alias and a lambda Consumer.
 * It offers many useful methods that reduces the work which needs to be done creating new spigot command.
 */
public class CommandTree extends Tree<CommandTree.Triple> {

    public CommandTree(String commandAlias) {
        super(new Triple(commandAlias, null, null));
    }


    public void execute(CommandSender sender, Command command, String alias, String[] args) {

        GraphNode<Triple> node = getRoot();

        for (int i = 0; i < args.length; i++) {
            boolean isLast = i == args.length - 1;
            GraphNode<Triple> nextNode = getChildNodeByStr(node, args[i]);
            if (nextNode == null) {
                for (GraphNode<Triple> child : node.getChildren()) {
                    if (isTypeNode(child) && getInterpretedObjByNodeType(args[i], child) != null) {
                        node = child;
                        break;
                    }
                }
            } else {
                node = nextNode;
            }
            if (isLast) {
                executeNode(node, sender, command, alias, args);
                return;
            }
        }
        MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, buildSyntax(node), sender);
    }

    private void executeNode(GraphNode<Triple> node, CommandSender sender, Command command, String alias, String[] args) {
        if (node.getValue().consumer != null) {
            CommandTuple tuple = new CommandTuple(sender, command, alias, args);
            node.getValue().consumer.accept(tuple);
        } else {
            MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, buildSyntax(node), sender);
        }
    }

    private Object getInterpretedObjByNodeType(String str, GraphNode<Triple> node) {

        //String
        if (node.getValue().type.equals(String.class)) {
            return str;
        }

        // Material
        Material material = Material.getMaterial(str.toUpperCase());
        if (node.getValue().type.equals(Material.class)) {
            return material;
        }

        //Blacklist
        BlacklistCommand.BlacklistType blacklistType = BlacklistCommand.BlacklistType.getBlackListTypeByString(str);
        if (blacklistType != null && node.getValue().type.equals(BlacklistCommand.BlacklistType.class)) {
            return blacklistType;
        }

        //Integer
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ignored) {
        }

        return null;
    }

    private boolean isTypeNode(GraphNode<Triple> node) {
        return node != null && node.getValue().type != null;
    }


    /**
     * Adds a node into the command tree.
     *
     * @param path     like the ingame command. Example: "/cmd subCmd arg".
     * @param consumer the consumer which gets get executed if this command gets executed.
     *                 Use {@code null} if you dont want this to be an executable path.
     * @param c        This is null if the path directs to a node which is not an argument
     *                 otherwise it determines the type of the argument.
     */
    public void addPath(String path, Consumer<CommandTree.CommandTuple> consumer, Class<?> c) {
        String[] args = path.split(" ");
        GraphNode<Triple> node = getRoot();

        for (int i = 1; i < args.length; i++) {
            final int finalI = i;
            GraphNode<Triple> tempNode
                    = getNodeFormChildren(node, t -> t.label.equalsIgnoreCase(args[finalI]));

            boolean isLastElement = !(i + 1 < args.length);

            if (tempNode == null) {
                Triple tuple = new Triple(args[i], null, null);
                tempNode = new GraphNode<>(tuple);
                node.addChild(tempNode);
            }

            if (isLastElement) {
                tempNode.getValue().consumer = consumer;
                tempNode.getValue().type = c;
            }
            node = tempNode;

        }

    }

    /**
     * Generates a Syntax message for the subcommand with the node {@code node}.
     *
     * @param node the node which determines the subcommand and thus its syntax.
     * @return the syntax string.
     */
    public String buildSyntax(GraphNode<Triple> node) {
        StringBuilder syntax = new StringBuilder();
        if (node.hasChild()) {
            syntax = new StringBuilder("<");
            for (GraphNode<Triple> child : node.getChildren()) {
                syntax.append(child.getValue().label).append(", ");
            }
            syntax = new StringBuilder(syntax.substring(0, syntax.length() - 2).concat(">"));
        }

        GraphNode<Triple> nextNode = node;

        do {
            if (nextNode.getValue().type == null) {
                syntax.insert(0, nextNode.getValue().label + " ");
            } else {
                syntax.insert(0, "<" + nextNode.getValue().label + "> ");
            }
            if (nextNode.hasParent()) nextNode = nextNode.getParents().get(0);
            else nextNode = null;
        } while (nextNode != null);
        System.out.println(1);

        return "/" + syntax;
    }

    private boolean hasArgumentAValidTypeOtherwiseSendMsg(String arg, GraphNode<Triple> node, CommandSender cs) {
        if (node.getValue().type == null) {
            return true;

        } else if (node.getValue().type.toString().equals(BlacklistCommand.BlacklistType.class.toString())) {
            for (BlacklistCommand.BlacklistType type : BlacklistCommand.BlacklistType.values()) {
                if (type.toString().equalsIgnoreCase(arg)) {
                    return true;
                } else {
                    MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_BLACKLIST_NOT_EXISTS, cs);
                    return false;
                }
            }

        } else if (node.getValue().type.toString().equals(Material.class.toString())) {
            for (Material material : Material.values()) {
                if (material.toString().equalsIgnoreCase(arg)) {
                    return true;
                }
            }
        }
        return true;
    }

    private GraphNode<Triple> getChildNodeByStr(GraphNode<Triple> node, String string) {

        for (GraphNode<Triple> n : node.getChildren()) {

            if (n.getValue().label.equals(string) && n.getValue().type == null) {
                return n;
            }

        }
        return null;
    }

    public static class CommandTuple {

        public CommandSender sender;
        public Command cmd;
        public String label;
        public String[] args;

        public CommandTuple(CommandSender sender, Command cmd, String label, String[] args) {
            this.sender = sender;
            this.cmd = cmd;
            this.label = label;
            this.args = args;
        }

    }

    static class Triple {

        String label;
        Consumer<CommandTuple> consumer;
        Class<?> type;

        public Triple(String label, Consumer<CommandTuple> consumer, Class<?> type) {
            this.label = label;
            this.consumer = consumer;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Triple triple = (Triple) o;
            return Objects.equals(label, triple.label) && Objects.equals(consumer, triple.consumer) && Objects.equals(type, triple.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, consumer, type);
        }

        public String toString() {
            return "(" + label + "," + consumer + "," + type + ")";
        }

    }

}
