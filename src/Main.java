import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String CLR_RESET = "\u001B[0m";
    private static final String CLR_RED = "\u001B[91m";
    private static final String CLR_YELLOW = "\u001B[93m";
    private static final String CLR_GREEN = "\u001B[92m";
    private static final String CLR_BLUE = "\u001B[94m";
    private static final String CLR_GRAY = "\u001B[90m";
    private static final String CLR_WHITE = "\u001B[37m";
    private static final String CLR_BLACK = "\u001B[30m";
    private static final String BCK_BLACK = "\u001B[49m";
    private static final String BCK_YELLOW = "\u001B[43m";
    private static final String BCK_GREEN = "\u001B[42m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nType full source file name: " + CLR_BLUE);
        String sourceFileName = scanner.nextLine();
        int threshold;

        if (args.length > 0) {
            try {
                threshold = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.print(CLR_RESET + "Type minimal string length: " + CLR_BLUE);
                threshold = scanner.nextInt();
            }
        } else {
            System.out.print(CLR_RESET + "Type minimal string length: " + CLR_BLUE);
            threshold = scanner.nextInt();
        }
        scanner.close();
        File sourceFile = new File(sourceFileName);

        try {
            parse(sourceFile, threshold);
        } catch (FileNotFoundException e) {
            System.out.println(CLR_RED + "\n     File or directory not found." + CLR_RESET + "\n");
        } catch (IOException e) {
            System.out.println(CLR_RED + "\n     Unable to read/write file." + CLR_RESET + "\n");
        }
    }

    private static void parse(File sourceFile, int threshold) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> unique = new ArrayList<>();
        HashMap<String, Integer> all = new HashMap<>();
        Scanner scanner = new Scanner(sourceFile);
        int duplicates = 0;

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            lines.add(line);

            if (!unique.contains(line)) {
                unique.add(line);
            } else if (line.startsWith("//") || line.length() < threshold) {
                unique.add(line);
            }

            if (all.containsKey(line)) {
                all.put(line, all.get(line) + 1);
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
            System.out.println(CLR_GRAY + "\n---------------------------------------"
                    + CLR_RESET + "\n Times:\t" + CLR_GRAY + "|" + CLR_RESET + "\tLine:\n"
                    + CLR_GRAY + "---------------------------------------" + CLR_RESET);

            for (Map.Entry<String, Integer> entry : all.entrySet()) {
                if (entry.getValue() > 1) {
                    System.out.println(" " + entry.getValue() + "\t" + CLR_GRAY
                            + "|" + CLR_RESET + "\t" + entry.getKey());
                }
            }

            System.out.println(CLR_GRAY + "---------------------------------------\n"
                    + CLR_YELLOW + "\n     " + duplicates
                    + " duplicated lines of " + lines.size() + " were found.     "
                    + CLR_RESET + "\n     Unique lines: " + unique.size()
                    + String.format(" (%,.2f", (double) unique.size() / lines.size() * 100) + "%)\n"
                    + "\nFile with unique lines has been written with name "
                    + sourceFile.getName() + CLR_YELLOW + "_clean" + CLR_RESET + ".\n");
        } else {
            System.out.println(CLR_GREEN + "\n     There is no duplicates in the file.     "
                    + CLR_RESET + "\n");
        }
    }
}
