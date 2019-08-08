package net.spacive.bigfilefinder.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeTraverser {

    public interface TreeNode {
        boolean hasChildren();

        List<TreeNode> getChildren();

        Object getData();
    }

    public interface NodeAction {
        void doNodeAction(TreeNode treeNode);
    }

    private TreeNode rootNode;

    public TreeTraverser(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public void traverse(NodeAction action) {
        List<TreeNode> nextLayer = Arrays.asList(rootNode);

        do {
            List<TreeNode> currentLayer = nextLayer;
            nextLayer = new ArrayList<>();

            for (TreeNode node: currentLayer) {
                for (TreeNode children: node.getChildren()) {
                    if (children.hasChildren()) {
                        nextLayer.add(children);
                    }
                    action.doNodeAction(children);
                }
            }
        } while (!nextLayer.isEmpty());
    }
}
