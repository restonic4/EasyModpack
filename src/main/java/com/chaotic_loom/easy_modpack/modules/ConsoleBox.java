package com.chaotic_loom.easy_modpack.modules;

public class ConsoleBox {
    /**
     * Creates a box with the given title and content.
     * The content may include newline characters ('\n') to separate lines.
     * The box adjusts its width based on the longest text (title or any content line).
     *
     * @param title   The title text.
     * @param content The content text, which can include newline characters.
     * @return A String representing the formatted box.
     */
    public static String createBox(String title, String content) {
        content = " \n" + content + "\n ";

        // Split the content into lines based on newline characters.
        String[] lines = content.split("\n");

        // Calculate the maximum width needed by comparing the title length with each content line.
        int maxWidth = title.length();
        for (String line : lines) {
            if (line.length() > maxWidth) {
                maxWidth = line.length();
            }
        }

        // Define the inner width of the box:
        // Adding 2 for a one-space margin on each side.
        int innerWidth = maxWidth + 2;
        // The total width includes the vertical borders (| on each side).
        int totalWidth = innerWidth + 2;

        // Create the border line using the '/' character.
        String border = "/".repeat(totalWidth);

        StringBuilder sb = new StringBuilder();
        // Top border of the box.
        sb.append(border).append("\n");

        // Title line: Center the title within the inner width.
        int leftPadding = (innerWidth - title.length()) / 2;
        int rightPadding = innerWidth - title.length() - leftPadding;
        sb.append("|")
                .append(" ".repeat(leftPadding))
                .append(title)
                .append(" ".repeat(rightPadding))
                .append("|\n");

        // Separator border between the title and content.
        sb.append(border).append("\n");

        // Content lines: Left-align each line.
        for (String line : lines) {
            sb.append("| ");
            sb.append(line);
            // Fill the remaining space with spaces.
            sb.append(" ".repeat(innerWidth - 1 - line.length()));
            sb.append("|\n");
        }

        // Bottom border of the box.
        sb.append(border);

        return sb.toString();
    }

    public static String createBoxForMinecraftLogger(String title, String content) {
        return " \n" + createBox(title, content);
    }

    // Main method to test the function.
    public static void main(String[] args) {
        String title = "Epic Title";
        String content = "Here goes the text\nwith informative lines\nand such.\nWhat if i try doing a huge text because yes?";
        System.out.println(createBox(title, content));
    }
}
