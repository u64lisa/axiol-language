package axiol;

import axiol.linker.LinkedSources;
import axiol.linker.Linker;
import axiol.parser.LanguageParser;
import axiol.parser.tree.RootNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static final File TESTING_FOLDER = new File("test/");

    public static final String[] testingCases = {
            //"enum_syntax",
            //"lambda_syntax",
            //"override_syntax",
            "namespace_syntax",
            //"attribute_syntax",
            //"main_syntax",

            //"syntax"
    };

    public static void main(String[] args) {
        LanguageParser languageParser = new LanguageParser();

        for (String testingCase : testingCases) {
            RootNode rootNode = languageParser.parseSource(TESTING_FOLDER, "%s.ax".formatted(testingCase),
                    readFile("/test/%s.ax".formatted(testingCase)));

            //StaticAnalysis staticAnalysis = new StaticAnalysis();
            //rootNode = staticAnalysis.process(rootNode);

            //InstructionGenerator instructionGenerator = new InstructionGenerator();
            //InstructionSet instructionSet = instructionGenerator.emit(rootNode);

            Linker linker = new Linker(languageParser, TESTING_FOLDER);
            LinkedSources linkedSources = linker.linkFiles(rootNode);
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