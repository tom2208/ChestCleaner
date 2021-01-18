package chestcleaner.utils.datastructures;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

/**
 * A tree structure representing a command. A node holds an alias and a lambda Consumer.
 * It offers many useful methods that reduces the work which needs to be done creating new spigot command.
 */
public class CommandTree {

    private TreeNode<Tuple<String, Consumer<CommandTuple>>> root;
    private final String placeholderPreamble = "placeholder:";

    /**
     * Generates an empty tree. The root is clearly determined by its Tuple entries which are null.
     * Clearly, because every search algorithm in this class starts at the root.
     */
    public CommandTree() {
        root = new TreeNode<>();
    }

    /**
     * Adds a new node into the CommandTree. Make sure there exists a node
     * that can be found under the String {@code node} (which represents the subcommand name).
     * The algorithm first searches for a normal node after that for a placeholder node.
     * <p>
     * You can refer to the root by setting {@code node} to null.
     *
     * @param node            the alias of a subcommand or {@code null}
     *                        if you want the to be the first argument of your command.
     * @param subCommandLabel the alias of the sub-subcommand
     *                        (the subcommand of the subcommand with the alias {@code node}).
     * @param consumer        the Consumer of the sub-subcommand.
     */
    public void addChildToNode(String node, String subCommandLabel, Consumer<CommandTuple> consumer) {
        TreeNode<Tuple<String, Consumer<CommandTuple>>> newnode;
        Tuple<String, Consumer<CommandTuple>> t = new Tuple<>(subCommandLabel, consumer);
        newnode = new TreeNode<>(t);

        if (node == null) {
            root.addChild(newnode);
        } else {
            TreeNode<Tuple<String, Consumer<CommandTuple>>> n = getNamednode(node);
            if (n == null) n = getNamednode(placeholderPreamble + node);
            if (n == null) {
                throw new NullPointerException("There is no node with its first element String being: "
                        + node + ", the node could be added.");
            }
            n.addChild(newnode);
        }
    }

    /**
     * Adds a placeholder node into the CommandTree. A placeholder tree is a note with its Consumer being null.
     * A placeholder will not be executed by running the command.
     *
     * @param node  the parent of the placeholder node (use {@code null} for the root).
     * @param label the alias of the placeholder node.
     */
    public void addPlaceHolderNode(String node, String label) {
        TreeNode<Tuple<String, Consumer<CommandTuple>>> newNode;
        Tuple<String, Consumer<CommandTuple>> t = new Tuple<>("placeholder:" + label, null);
        newNode = new TreeNode<>(t);
        if (node == null) {
            root.addChild(newNode);
        } else {
            getNamednode(node).addChild(newNode);
        }
    }

    /**
     * Executes the command with the alias {@code node}
     * or more detailed said: lets the consumer associated with the alias {@code node} accept the CommandTuple {@code ct}.
     *
     * @param node the alias of the subcommand you want to execute.
     * @param ct   all parameters of the onCommand method of the implemented CommandExecutor interface.
     */
    public void executeSubCommand(String node, CommandTuple ct) {
        if (!node.startsWith(placeholderPreamble)) {
            TreeNode<Tuple<String, Consumer<CommandTuple>>> n = getNamednode(node);
            n.getValue().e2.accept(ct);
        }
    }

    private TreeNode<Tuple<String, Consumer<CommandTuple>>> getNamednode(String name) {
        return root.getNode(tuple -> tuple.e1 != null && tuple.e1.equalsIgnoreCase(name));
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

    private class Tuple<T1, T2> {

        T1 e1;
        T2 e2;

        public Tuple(T1 e1, T2 e2) {
            this.e1 = e1;
            this.e2 = e2;
        }

    }

}
