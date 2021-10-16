import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Math.*;

public class Main {
    private static final String RESET = "\u001B[0m";
    private static final String FNT_ITALIC = "\u001B[3m";
    private static final String CLR_RED = "\u001B[91m";
    private static final String CLR_YELLOW = "\u001B[93m";
    private static final String CLR_GREEN = "\u001B[92m";
    private static final String CLR_BLUE = "\u001B[94m";
    private static final String CLR_GRAY = "\u001B[90m";
//    private static final String CLR_WHITE = "\u001B[37m";
//    private static final String CLR_BLACK = "\u001B[30m";
//    private static final String BCK_BLACK = "\u001B[49m";
//    private static final String BCK_YELLOW = "\u001B[43m";
//    private static final String BCK_GREEN = "\u001B[42m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nType full source file name: " + CLR_BLUE);
        String sourceFileName = scanner.nextLine();
        int threshold;

        if (args.length > 0) {
            try {
                threshold = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.print(RESET + "Type minimal string length: " + CLR_BLUE);
                threshold = scanner.nextInt();
            }
        } else {
            System.out.print(RESET + "Type minimal string length: " + CLR_BLUE);
            threshold = scanner.nextInt();
        }
        scanner.close();
        File sourceFile = new File(sourceFileName);

        try {
            parse(sourceFile, threshold);
        } catch (FileNotFoundException e) {
            System.out.println(CLR_RED + "\n\tFile or directory not found." + RESET + "\n");
        } catch (IOException e) {
            System.out.println(CLR_RED + "\n\tUnable to read/write file." + RESET + "\n");
        }
    }

    private static void parse(File sourceFile, int threshold) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> unique = new ArrayList<>();
        HashMap<String, Integer> all = new HashMap<>();
        Scanner scanner = new Scanner(sourceFile);
        int duplicates = 0;
        int maxLength = 0;
        int maxCount = 0;

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            lines.add(line);

            if (!unique.contains(line)) {
                unique.add(line);
            } else if (line.startsWith("//") || line.length() < threshold) {
                unique.add(line);
            }

            if (all.containsKey(line) && line.length() >= threshold) {
                all.put(line, all.get(line) + 1);

                if (all.get(line) > maxCount) {
                    maxCount = all.get(line);
                }

                if (line.length() > maxLength) {
                    maxLength = line.length();
                }
            } else {
                all.put(line, 1);
            }
        }
        scanner.close();

        for (HashMap.Entry<String, Integer> entry : all.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates += entry.getValue();
            }
        }

        if (duplicates != 0) {
            String cleanFileName = sourceFile.getPath() + "_clean";
            BufferedWriter writer = new BufferedWriter(new FileWriter(cleanFileName));

            for (String line : unique) {
                writer.write(line + "\n");
            }
            writer.close();
            maxCount = (int) (log10(maxCount)) + 1;
            String separator = CLR_GRAY + "\t" + repeat("-", 11 + max(maxCount, 6) + max(maxLength, 5));
            System.out.println("\n" + separator + "\n\t|" + RESET + FNT_ITALIC + "  Times:" + (maxCount - 4 < 2 ? "  " : repeat(" ", maxCount - 4)) + RESET + CLR_GRAY + "|"
                    + RESET + FNT_ITALIC + "  Line:" + repeat(" ", max(maxLength - 3, 2)) + RESET + CLR_GRAY + "|\n" + separator + RESET);

            for (Map.Entry<String, Integer> entry : all.entrySet()) {
                if (entry.getValue() > 1) {
                    String output = CLR_GRAY + "\t|  " + RESET + entry.getValue()
                            + repeat(" ", max(8 - ((int) log10(entry.getValue()) + 1), maxCount + 2 - ((int) log10(entry.getValue()) + 1)))
                            + CLR_GRAY + "|  " + RESET + entry.getKey() + repeat(" ", max(maxLength - entry.getKey().length() + 2, 7 - entry.getKey().length())) + CLR_GRAY + "|";
                    System.out.println(output);
                }
            }
            System.out.println(CLR_GRAY + separator + "\n"
                    + CLR_YELLOW + "\n\t" + duplicates
                    + " duplicated lines" + RESET + " of " + lines.size() + " were found.     "
                    + "\n\tUnique lines: " + unique.size() + String.format(" (%,.2f", (double) unique.size() / lines.size() * 100) + "%)\n"
                    + "\nFile with unique lines has been written with name " + sourceFile.getName() + CLR_YELLOW + "_clean" + RESET + ".\n");
        } else {
            System.out.println(CLR_GREEN + "\n\tThere is no duplicates in the file.     " + RESET + "\n");
        }
    }

    private static String repeat(String pattern, int count) {
        return pattern.repeat(count);
    }
}
