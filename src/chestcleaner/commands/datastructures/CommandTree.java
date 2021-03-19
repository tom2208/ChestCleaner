package chestcleaner.commands.datastructures;

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A acyclic directed tree structure representing a command. A node holds an alias and a lambda Consumer.
 * It offers many useful methods that reduces the work which needs to be done creating new spigot command.
 */
public class CommandTree extends Tree<CommandTree.Quadruple> {

    public CommandTree(String commandAlias) {
        super(new Quadruple(commandAlias, null, null, false));
    }


    public void execute(CommandSender sender, Command command, String alias, String[] args) {

        GraphNode<Quadruple> node = getRoot();

        for (int i = 0; i < args.length; i++) {
            boolean isLast = i == args.length - 1;
            GraphNode<Quadruple> nextNode = getChildNodeByStr(node, args[i]);
            if (nextNode == null) {
                for (GraphNode<Quadruple> child : node.getChildren()) {
                    if (isTypeNode(child) && getInterpretedObjByNodeType(args[i], child) != null) {
                        node = child;
                        break;
                    }
                }
            } else {
                node = nextNode;
            }
            if (isLast || node.getValue().definiteExecute) {
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

        //String
        if (node.getValue().type.equals(String.class)) {
            return str;
        }

        // Material
        if (node.getValue().type.equals(Material.class)) {
            return Material.getMaterial(str.toUpperCase());
        }

        //Blacklist
        if (node.getValue().type.equals(BlacklistCommand.BlacklistType.class)) {
            BlacklistCommand.BlacklistType blacklistType = BlacklistCommand.BlacklistType.getBlackListTypeByString(str);
            if (blacklistType != null) {
                return blacklistType;
            }
        }
        //Integer
        if (node.getValue().type.equals(Integer.class)) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ignored) {
            }
        }

        //Boolean
        if (node.getValue().type.equals(Boolean.class)) {
            return Boolean.parseBoolean(str);
        }

        return null;
    }

    private boolean isTypeNode(GraphNode<Quadruple> node) {
        return node != null && node.getValue().type != null;
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
    public void addPath(String path, Consumer<CommandTree.CommandTuple> consumer, Class<?> type, boolean definiteExecute) {
        String[] args = path.split(" ");
        GraphNode<Quadruple> node = getRoot();

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
