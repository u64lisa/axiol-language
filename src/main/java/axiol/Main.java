package axiol;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionGenerator;
import axiol.instruction.InstructionSet;
import axiol.linker.LinkedSources;
import axiol.linker.Linker;
import axiol.parser.LanguageParser;
import axiol.parser.tree.RootNode;
import axiol.target.AssemblyGenerator;
import axiol.target.TargetFormat;
import axiol.utils.Profiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
            "bug",
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

            // assembly generation
            PROFILER.startProfilingSection("asm", "ASM gen. '%s'".formatted(testingCase));
            exportCode(testingCase, instructionSet);


            PROFILER.startProfilingSection("asm", "ASM gen. '%s'".formatted(testingCase));
        }

    }

    static final TargetFormat[] formats = {
            TargetFormat.X86,
            TargetFormat.ARM,
    };
    public static void exportCode(String name, InstructionSet instructionSet) {
        File file = new File("./test/build/");
        file.mkdirs();

        for (TargetFormat format : formats) {
            AssemblyGenerator<?> generator = format.generatorClass;

            String code = new String(generator.getAssembler(instructionSet));

            writeFile("/test/build/%s_%s.asm".formatted(name, format.name()), code);
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
    public static void writeFile(String name, String content) {
        File file = new File("./" + name);

        if (file.exists()) {
            file.delete();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);

            writer.write(content);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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