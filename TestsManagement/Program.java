package TestsManagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Program {

    // Test generator singleton 
    private static Generator _generator;

      @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        System.out.println("____/Tests Management Console \\____");
        Scanner input = new Scanner(System.in);
        do {
            System.out.print("Command > ");
            Scanner parser = new Scanner(input.nextLine().trim());
            if (!parser.hasNext()) {
                continue;
            }
            String cmd = parser.next().toLowerCase();
            if (cmd.isEmpty()) {
                continue;
            }
            if (cmd.equals("exit") || cmd.equals("quit")) {
                break;
            }
            try {
                switch(cmd.toLowerCase()) {
                    case "help":
                    case "?":
                        processHelp();
                        break;
                    case "root":
                        processSetRoot(parser.nextLine());
                        break;
                    case "root?":
                        processGetRoot();
                        break;
                    case "questions?":
                        processGetQuestions();
                        break;
                    case "gen-root":
                        processGenRoot(true);
                        break;
                    case "regen-root":
                        processGenRoot(false);
                        break;
                    case "gen-test":
                        processGenTest(parser, true);
                        break;
                    case "regen-test":
                        processGenTest(parser, false);
                        break;
                    case "gen-variants":
                        processGenVariants(parser, true);
                        break;
                    case "regen-variants":
                        processGenVariants(parser, false);
                        break;
                    case "debug":
                        Path phIndex = Paths.get("D:/ODrive/Admin@Inproted/TEALS_LWHS/LWHS CS.23-24 - Documents/General/Data Structures/ds/unit1/.template/.index.html");
                        WebDoc wd = new WebDoc(Files.readAllLines(phIndex));
                        System.out.println(wd);
                    default:
                        throw new RuntimeException("Unrecognized command!");
                }
            } catch (Exception e) {
                System.out.printf("##ERR##: %s\n", e.getMessage());
                e.printStackTrace();             
            }
            parser.close();
        } while(true);
        input.close();
        System.out.println("____________           ____________");
        System.out.println("            \\ Goodbye /");
    }

    private static void processHelp() {
        System.out.println("? | help:\n  This help.");
        System.out.println("exit | quit:\n    Exits the program.");
        System.out.println("root {path_to_folder}:\n    Targets the test generator to path_to_folder.");
        System.out.println("root?:\n    Prints the current test generator folder.");
        System.out.println("questions?\n    Prints out stats on questions loaded from the root\\.template.");
        System.out.println("gen-root:\n    Generates the .meta and index.html for the all questions in root\\.template.");
        System.out.println("regen-root:\n    Loads pre-existing generator .meta file and reconstructs the index.html.");
        System.out.println("gen-test {testName} {Questions_csv}:\n    Generates .meta and index.html files for the given questions in root\\test\\...");
        System.out.println("regen-test {testName}:\n    Loads pre-existing .meta and reconstructs the index.html in given root\\test\\...");
        System.out.println("gen-variants {testName} {Variants_csv}:\n    Generates .meta and index.html files for {Variants_csv} of {testName}");
        System.out.println("regen-variants {testName} {Variants_csv}:\n    Loads pre-existing .meta and reconstructs the index.html for the given {Variants_csv}");
    }

    private static void processSetRoot(String root) throws IOException {
        root = root.trim();
        if (root.startsWith("\"")) {
            root = root.substring(1, root.length()-1);
        }
        _generator = new Generator(root);
        System.out.println("DONE");
    }

    private static void processGetRoot() {
        if (_generator == null) {
            System.out.println("(null)");
        } else {
            System.out.println(_generator.getRoot());
        }
    }

    private static void processGetQuestions() {
        if (_generator == null) {
            System.out.printf("No questions loaded from root.\n");
        } else {
            System.out.printf("Questions loaded from root:\n%s\n", _generator.getQuestions());
        }
    }

    private static void processGenRoot(boolean regenMeta) throws IOException {
        _generator.genRoot(regenMeta);
        System.out.println("DONE");
    }

    private static void processGenTest(Scanner argParser, boolean regenMeta) throws IOException {
        if (!argParser.hasNext()) {
            throw new IllegalArgumentException("Missing or invalid test name!");
        }
        String testName = argParser.next();
        String[] qIDs = {};
        if (argParser.hasNext()) {
            qIDs = argParser.next().split(",");
        }
        _generator.genTest(testName, qIDs, regenMeta);
        System.out.println("DONE");
    }

    private static void processGenVariants(Scanner argParser, boolean regenMeta) throws IOException {
        if (!argParser.hasNext()) {
            throw new IllegalArgumentException("Missing or invalid test name!");
        }
        String testName = argParser.next();

        String[] vIDs = {};
        if (!argParser.hasNext()) {
            throw new IllegalArgumentException("Missing or invalid variant arguments!");
        }
        vIDs = argParser.next().split(",");

        List<String> excFRQs = new ArrayList<String>();
        if (argParser.hasNext()) {
            excFRQs = Arrays.asList(argParser.next().split(","));
        }
        
        _generator.genTestVariants(testName, vIDs, excFRQs, regenMeta);
        System.out.println("DONE");
    }
}