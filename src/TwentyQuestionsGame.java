import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

class TreeNode {
    String data;
    TreeNode left;
    TreeNode right;

    public TreeNode(String data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}

public class TwentyQuestionsGame {
    private static final int MAX_QUESTIONS = 20;
    private static final String TREE_FILE_PATH = "tree.txt";
    private static TreeNode root;

    public static void main(String[] args) {
        loadTreeFromFile();

        Scanner scanner = new Scanner(System.in);
        String input;

        do {
            System.out.println("Welcome to 20 Questions Game!");
            System.out.println("Think of an object, and I'll try to guess it.");
            playGame(root, scanner);

            System.out.println("Do you want to play again? (yes/no)");
            input = scanner.nextLine().toLowerCase();
        } while (input.equals("yes"));

        saveTreeToFile(root);
        System.out.println("Thanks for playing!");
    }

    private static void playGame(TreeNode node, Scanner scanner) {
        int questionCount = 0;
        while (node.left != null && node.right != null) {
            if (questionCount >= MAX_QUESTIONS) {
                System.out.println("I've reached the maximum limit of questions. I give up!");
                return;
            }
            System.out.println(node.data);
            System.out.println("Enter 'yes' or 'no':");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("yes")) {
                node = node.left;
            } else {
                node = node.right;
            }
            questionCount++;
        }

        System.out.println("Is it a " + node.data + "?");
        System.out.println("Enter 'yes' if correct, 'no' otherwise:");
        String input = scanner.nextLine().toLowerCase();

        if (input.equals("yes")) {
            System.out.println("I win!");
        } else {
            System.out.println("I give up. What is it?");
            String newAnswer = scanner.nextLine();
            System.out.println("Give me a question to distinguish between " + node.data + " and " + newAnswer + ":");
            String newQuestion = scanner.nextLine();

            System.out.println("For " + newAnswer + ", what is the answer to your question? (yes/no)");
            String answerToQuestion = scanner.nextLine().toLowerCase();

            TreeNode oldAnswerNode = new TreeNode(node.data);
            node.data = newQuestion;
            if (answerToQuestion.equals("yes")) {
                node.left = new TreeNode(newAnswer);
                node.right = oldAnswerNode;
            } else {
                node.left = oldAnswerNode;
                node.right = new TreeNode(newAnswer);
            }
        }
    }

    private static void loadTreeFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TREE_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                root = constructTreeFromString(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading tree from file: " + e.getMessage());
            initializeTree();
        }
    }

    private static TreeNode constructTreeFromString(String line) {
        String[] parts = line.split("::");
        return constructTree(parts);
    }

    private static TreeNode constructTree(String[] parts) {
        if (parts.length == 0) {
            return null;
        }

        TreeNode node = new TreeNode(parts[0]);
        if (parts.length > 1 && parts[1].equals("yes")) {
            node.left = constructTree(Arrays.copyOfRange(parts, 2, parts.length));
        } else if (parts.length > 1 && parts[1].equals("no")) {
            node.right = constructTree(Arrays.copyOfRange(parts, 2, parts.length));
        }
        return node;
    }

    private static void initializeTree() {
        // left: yes, right: no
        root = new TreeNode("Is it an animal?");
        root.left = new TreeNode("Does it have fur?");
        root.left.left = new TreeNode("cat?");
        root.left.right = new TreeNode("Does it fly?");
        root.left.right.left = new TreeNode("Bird?");
        root.right = new TreeNode("Is it a vehicle?");
        root.right.left = new TreeNode("car");
    }

    private static void saveTreeToFile(TreeNode node) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TREE_FILE_PATH))) {
            writer.write(serializeTreeToString(node));
        } catch (IOException e) {
            System.out.println("Error saving tree to file: " + e.getMessage());
        }
    }

    private static String serializeTreeToString(TreeNode node) {
        StringBuilder sb = new StringBuilder();
        serializeTree(node, sb);
        return sb.toString();
    }

    private static void serializeTree(TreeNode node, StringBuilder sb) {
        if (node == null) {
            return;
        }

        sb.append(node.data);
        if (node.left != null) {
            sb.append("::yes::");
            serializeTree(node.left, sb);
        } else if (node.right != null) {
            sb.append("::no::");
            serializeTree(node.right, sb);
        }
    }
}
