import java.io.*;
import java.util.*;

import static java.lang.Math.*;

public class Main {
    private static final String RESET = "\u001B[0m";
    private static final String FNT_ITALIC = "\u001B[3m";
    private static final String FG_RED = "\u001B[91m";
    private static final String FG_YELLOW = "\u001B[38;5;208m";
    private static final String FG_GREEN = "\u001B[92m";
    private static final String FG_BLUE = "\u001B[38;5;33m";
    private static final String FG_DK_GRAY = "\u001B[90m";
    private static final String FG_GRAY = "\u001B[38;5;237m";
    private static final String BG_HEADER = "\u001B[48;5;234m";
    private static final String BG_EVEN = "\u001B[48;5;232m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String sourceFileName = null;
        System.out.print("\nType full source file name: " + FG_BLUE);

        try {
            sourceFileName = scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println(RESET + FG_RED + "\n\tIncorrect input." + RESET + "\n");
            System.exit(0);
        }
        File sourceFile = new File(sourceFileName);

        if (!sourceFile.exists() || sourceFile.isDirectory()) {
            System.out.println(RESET + FG_RED + "\n\tFile or directory not found." + RESET + "\n");
        } else {
            int threshold = 0;

            try {
                if (args.length > 0) {
                    try {
                        threshold = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        System.out.print(RESET + "Type sensitivity:           " + FG_BLUE);
                        threshold = scanner.nextInt();
                    }
                } else {
                    System.out.print(RESET + "Type sensitivity:           " + FG_BLUE);
                    threshold = scanner.nextInt();
                }
                scanner.close();
            } catch (InputMismatchException e) {
                System.out.println(RESET + FG_RED + "\n\tIncorrect input." + RESET + "\n");
                System.exit(0);
            }

            try {
                parse(sourceFile, threshold);
            } catch (FileNotFoundException e) {
                System.out.println(RESET + FG_RED + "\n\tFile or directory not found." + RESET + "\n");
            } catch (IOException e) {
                System.out.println(RESET + FG_RED + "\n\tUnable to read/write file." + RESET + "\n");
            }
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
            int lineParity = 0;
            String separator = FG_GRAY + "\t" + repeat("-", 11 + max(maxCount, 6) + max(maxLength, 5));
            System.out.println("\n" + separator + "\n\t|" + RESET + BG_HEADER + FNT_ITALIC + "  Times:" + (maxCount - 4 < 2 ? "  " : repeat(" ", maxCount - 4)) + RESET + FG_GRAY + "|"
                    + RESET + BG_HEADER + FNT_ITALIC + "  Line:" + repeat(" ", max(maxLength - 3, 2)) + RESET + FG_GRAY + "|\n" + separator + RESET);

            for (Map.Entry<String, Integer> entry : all.entrySet()) {
                if (entry.getValue() > 1) {
                    String output = FG_GRAY + "\t|" + (lineParity % 2 == 0 ? RESET : RESET + BG_EVEN) + "  " + entry.getValue()
                            + repeat(" ", max(8 - ((int) log10(entry.getValue()) + 1), maxCount + 2 - ((int) log10(entry.getValue()) + 1))) + RESET
                            + FG_GRAY + "|" + (lineParity % 2 == 0 ? RESET : RESET + BG_EVEN) + "  " + entry.getKey() + repeat(" ", max(maxLength - entry.getKey().length() + 2, 7 - entry.getKey().length())) + RESET + FG_GRAY + "|";
                    System.out.println(output);
                    lineParity++;
                }
            }
            System.out.println(FG_DK_GRAY + separator + "\n"
                    + FG_YELLOW + "\n\t" + duplicates
                    + " duplicated lines" + RESET + " of " + lines.size() + " were found.     "
                    + "\n\tUnique lines: " + unique.size() + String.format(" (%,.2f", (double) unique.size() / lines.size() * 100) + "%)\n"
                    + "\nFile with unique lines has been written with name " + sourceFile.getName() + FG_YELLOW + "_clean" + RESET + ".\n");
        } else {
            System.out.println(FG_GREEN + "\n\tThere is no duplicates in the file.     " + RESET + "\n");
        }
    }

    private static String repeat(String pattern, int count) {
        return pattern.repeat(count);
    }
}
