package net.spacive.bigfilefinder.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileNode implements TreeTraverser.TreeNode {

    private File file;

    public FileNode(File file) {
        this.file = file;
    }

    @Override
    public boolean hasChildren() {
        return file.isDirectory() && (file.listFiles() != null);
    }

    @Override
    public List<TreeTraverser.TreeNode> getChildren() {

        List<TreeTraverser.TreeNode> fileNodes = new ArrayList<>();

        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {
                fileNodes.add(new FileNode(f));
            }
        }

        return fileNodes;
    }

    public File getData() {
        return file;
    }
}
