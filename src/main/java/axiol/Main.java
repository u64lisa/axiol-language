package axiol;

import axiol.instruction.InstructionGenerator;
import axiol.instruction.InstructionSet;
import axiol.linker.LinkedSources;
import axiol.linker.Linker;
import axiol.parser.LanguageParser;
import axiol.parser.tree.RootNode;
import axiol.utils.Profiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static final Profiler PROFILER = new Profiler();

    public static final File TESTING_FOLDER = new File("test/");

    public static final String[] testingCases = {
            //"enum_syntax",
            //"lambda_syntax",
            //"override_syntax",
            "namespace_syntax",
            //"attribute_syntax",
            "main_syntax",

            "syntax"
    };

    public static void main(String[] args) {
        LanguageParser languageParser = new LanguageParser();

        for (String testingCase : testingCases) {
            System.out.println("#".repeat(122));
            // lexer / parsing
            PROFILER.startProfilingSection("parsing", "parsing '%s'".formatted(testingCase));
            RootNode rootNode = languageParser.parseSource(TESTING_FOLDER, "%s.ax".formatted(testingCase),
                    readFile("/test/%s.ax".formatted(testingCase)));

            System.out.printf("root contains '%d' statements%n", rootNode.getStatements().size());
            System.out.printf("root contains '%d' references%n", rootNode.getScopeStash().getAllReferences().size());

            PROFILER.endProfilingSection("parsing", "parsing of '" + testingCase + "' took %sms");

            // linking
            PROFILER.startProfilingSection("linking", "linking '%s'".formatted(testingCase));
            Linker linker = new Linker(languageParser, TESTING_FOLDER);
            LinkedSources linkedSources = linker.linkFiles(rootNode);

            System.out.printf("linked-element contains '%d' statements%n", linkedSources.getStatements().size());
            System.out.printf("linked-element contains '%d' references%n", linkedSources.getScopeStash().getAllReferences().size());

            PROFILER.endProfilingSection("linking", "linking of '" + testingCase + "' took %sms");

            // instruction generation
            PROFILER.startProfilingSection("instruction", "instruction gen. '%s'".formatted(testingCase));
            InstructionGenerator instructionGenerator = new InstructionGenerator();
            InstructionSet instructionSet = instructionGenerator.emit(linkedSources);
            System.out.printf("generated instruction-set with '%s' internal instructions%n", instructionSet.getInstructions().size());

            PROFILER.endProfilingSection("instruction", "instruction gen. '" + testingCase + "' took %sms");
        }

    }

    public static String readFile(String name) {
        File file = new File("./" + name);

        if (file.exists()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            StringBuilder fileContents = new StringBuilder();

            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            return fileContents.toString();
        }
        throw new RuntimeException("file not found in source!");
    }

    public static String readFileFromJar(String file) {
        Scanner scanner = new Scanner(Objects.requireNonNull(Main.class.getResourceAsStream(file)));
        StringBuilder fileContents = new StringBuilder();

        while (scanner.hasNextLine()) {
            fileContents.append(scanner.nextLine()).append("\n");
        }

        scanner.close();
        return fileContents.toString();
    }

}