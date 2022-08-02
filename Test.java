import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    public List<String> path = new ArrayList<>();

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        int maxValue = 100;
        int findValue = 785;
        System.out.println("Generating nodes up to: " + maxValue);
        generateNodes(root, maxValue);
        try {
            int maxExists = root.getRightNode().getLeftNode().getLeftNode().getRightNode().getLeftNode()
                    .getLeftNodeValue();
            System.out.println("Resulting max value: " + maxExists);
            int doesOneMoreExist = root.getRightNode().getLeftNode().getLeftNode().getRightNode().getLeftNode()
                    .getRightNodeValue();
            System.out.println(doesOneMoreExist);
        } catch (NullPointerException np) {
            System.out.println("If this line shows, the following value doesn't exist!");
        }
        System.out.println(String.format("Finding the path to value %d", findValue));
        root.findPath(findValue, new ArrayList<Boolean>());
        System.out.println(root.getReadablePathToValue());
        System.out.println(root.getFoundValue());
    }

    /**
     * a recursive class to generate children within a boundary.
     * 
     * @param node the base node.
     * @param max  the value that cannot be exceeded.
     */
    public static void generateNodes(TreeNode node, int max) {
        HashMap<String, Integer> preview = node.previewValues();
        if (preview.get("left") <= max && preview.get("right") <= max) {
            node.generateChildren();
        }

        if (preview.get("left") > max || preview.get("right") > max) {
            if (preview.get("left") <= max) {
                node.generateChildNode("left");
            }
            if (preview.get("right") <= max) {
                node.generateChildNode("right");
            }
        }

        if (preview.get("left") > max) {
            return;
        }
        if (preview.get("right") > max) {
            return;
        }

        if (!node.getLeftNodeValue().equals(null)) {
            generateNodes(node.getLeftNode(), max);
        }

        if (!node.getRightNodeValue().equals(null)) {
            generateNodes(node.getRightNode(), max);
        }
    }
}

/**
 * A class for a node of a binary tree.
 * Written entirely from scratch.
 * Thanks, Derek.
 * 
 * @author aangar
 */
class TreeNode {
    private Integer val;
    private TreeNode left;
    private TreeNode right;
    private Integer foundValue;
    private List<String> readablePathToValue;

    /**
     * default empty constructor
     */
    public TreeNode() {
    }

    /**
     * Constructor with a passed value. This will NOT generate anything for left /
     * right nodes.
     * 
     * @param value int value for this node.
     */
    public TreeNode(int value) {
        this.val = value;
    }

    /**
     * constructor that can determine which node to generate.
     * 
     * @param value          the root value.
     * @param nodeToGenerate the string ( either left or right ) of the node to
     *                       generate.
     */
    public TreeNode(int value, String nodeToGenerate) {
        this.val = value;
        if (nodeToGenerate.toLowerCase().equals("left")) {
            this.left = new TreeNode(this.val * 2);
        }
        if (nodeToGenerate.toLowerCase().equals("right")) {
            this.right = new TreeNode((this.val * 2) + 1);
        }
    }

    /**
     * Generates values for the children nodes,
     * not inlcuding their following nodes.
     */
    public void generateChildren() {
        this.left = new TreeNode(this.val * 2);
        this.right = new TreeNode((this.val * 2) + 1);
    }

    public void generateChildNode(String node) {
        switch (node) {
            case "left":
                this.left = new TreeNode(this.val * 2);
                break;
            case "right":
                this.right = new TreeNode((this.val * 2) + 1);
                break;
            default:
                System.err.println("Node name not recognized!");
        }
    }

    public HashMap<String, Integer> previewValues() {
        HashMap<String, Integer> values = new HashMap<>();
        values.put("left", this.val * 2);
        values.put("right", (this.val * 2) + 1);
        return values;
    }

    /**
     * corrects the found order via searching: RECURSIVE
     * 
     * @param input the initial order
     * @return the corrected order
     */
    private List<Boolean> correctOrder(List<Boolean> input) {
        List<Boolean> corrected = new ArrayList<Boolean>(input.size());
        for (int i = 0; i <= input.size() - 1; i++) {
            int index = input.size() - (1 + i);
            corrected.add(input.get(index));
        }
        return corrected;
    }

    private void generateReadablePath(List<Boolean> input) {
        List<String> readable = new ArrayList<String>();
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i)) {
                readable.add("Right");
            }
            if (!input.get(i)) {
                readable.add("Left");
            }
        }
        this.readablePathToValue = readable;
    }

    /**
     * Determines the path to a number - EXCLUSIVE METHOD && RECURSIVE
     * false means the left node
     * true means the right node
     * 
     * @param number the current number
     * @param pathTo the list for the path
     */
    public void findPath(int number, List<Boolean> pathTo) {
        List<Boolean> newPath = pathTo.size() > 0 ? pathTo : new ArrayList<Boolean>();

        if (number % 2 > 0) {
            newPath.add(true);
            int num = (number - 1) / 2;
            if (num <= 1) {
                TreeNode initialNode = new TreeNode(1);
                initialNode.generateChildren();
                List<Boolean> correctedOrder = correctOrder(newPath);
                generateReadablePath(correctedOrder);
                followPath(0, initialNode, correctedOrder);
                return;
            }
            findPath((number - 1) / 2, newPath);
        }

        if (number % 2 == 0) {
            newPath.add(false);
            int num = number / 2;
            if (num <= 1) {
                TreeNode initialNode = new TreeNode(1);
                initialNode.generateChildren();
                List<Boolean> correctedOrder = correctOrder(newPath);
                generateReadablePath(correctedOrder);
                followPath(0, initialNode, correctedOrder);
                return;
            }
            findPath(number / 2, newPath);
        }
    }

    /**
     * follows the found path regardless of node creation
     * 
     * @param step the current step.
     * @param node the node its on.
     */
    public void followPath(int step, TreeNode node, List<Boolean> path) {
        if (step > path.size() - 1) {
            this.foundValue = node.getNodeValue();
            return;
        }

        if (path.get(step)) {
            TreeNode rightNode = new TreeNode(node.getRightNodeValue());
            rightNode.generateChildren();
            followPath(step + 1, rightNode, path);
        }

        if (!path.get(step)) {
            TreeNode leftNode = new TreeNode(node.getLeftNodeValue());
            leftNode.generateChildren();
            followPath(step + 1, leftNode, path);
        }
    }

    public void followPathIfExists(int step, TreeNode node, List<Boolean> path) throws NullPointerException {
        // thiis will follow the path from the current Tree.
        // pass the root node and follow the path, probably going to need to set this
        // once again.
        // man--- sucks i deleted that just a minute ago
    }

    /**
     * gets the target node value.
     * 
     * @return the value of the node.
     */
    public Integer getNodeValue() {
        return this.val;
    }

    /**
     * gets the value for the node to the right of the current node.
     * 
     * @return the value of the right node.
     */
    public Integer getRightNodeValue() {
        return this.right.getNodeValue();
    }

    /**
     * gets the value for the node to the left of the current node.
     * 
     * @return the value of the left node.
     */
    public Integer getLeftNodeValue() {
        return this.left.getNodeValue();
    }

    /**
     * Gets the complete left node.
     * 
     * @return Left Node.
     */
    public TreeNode getLeftNode() {
        return this.left;
    }

    /**
     * Gets the complete Right Node.
     * 
     * @return Right Node.
     */
    public TreeNode getRightNode() {
        return this.right;
    }

    /**
     * getter for the found value from the followed path.
     * 
     * @return the found number.
     */
    public int getFoundValue() {
        return this.foundValue;
    }

    /**
     * getter for the human readable path to the previous specified value
     * @return list of the steps to follow
     */
    public List<String> getReadablePathToValue() {
        return this.readablePathToValue;
    }

    /**
     * sets the value for the target node
     * 
     * @param value the set value of the node.
     */
    public void setValue(int value) {
        this.val = value;
    }
}