package axiol.linker;

import axiol.lexer.Lexer;
import axiol.parser.LanguageParser;
import axiol.parser.scope.ScopeStash;
import axiol.parser.tree.RootNode;
import axiol.parser.tree.statements.LinkedNoticeStatement;
import axiol.parser.util.SourceFile;
import axiol.parser.util.error.LanguageException;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Linker {

    private static final String SUFFIX = ".ax";

    private final Map<String, RootNode> importedFiles = new HashMap<>();

    private final LanguageParser languageParser;
    private final File sourceFolder;

    private SourceFile mainFile;
    private ScopeStash scopeStash;

    public Linker(LanguageParser languageParser, File sourceFolder) {
        this.languageParser = languageParser;
        this.sourceFolder = sourceFolder;
    }

    public void linkFiles(final RootNode rootNode) {
        this.importedFiles.clear();
        this.scopeStash = rootNode.getScopeStash();
        this.mainFile = rootNode.getSourceFile();

        this.resolveImports(rootNode);
        this.checkFileCompatability();
    }

    public void resolveImports(RootNode rootNode) {
        List<String> paths = rootNode.getStatements()
                .stream()
                .filter(statement -> statement instanceof LinkedNoticeStatement)
                .map(statement -> (LinkedNoticeStatement) statement)
                .map(LinkedNoticeStatement::getLinkedName)
                .toList();

        for (String path : paths) {
            path = path.replace('.', '/') + SUFFIX;
            if (!importedFiles.containsKey(path)) {
                RootNode current = importFile(sourceFolder, path);
                importedFiles.put(path, current);

                this.resolveImports(current);
            }
        }
    }

    public void checkFileCompatability() {
        this.importedFiles.forEach((s, rootNode) -> rootNode.getScopeStash().getAllReferences().forEach(reference -> {
            Reference blocking = this.scopeStash.containsReference(reference);
            if (blocking != null) {

                // todo implement global access modifier for global defined constants!
                if (blocking.getType() == ReferenceType.VAR)
                    return;

                System.out.println(blocking);
                new LanguageException("file '%s' contains duplicated methode in namespace '%s' with name '%s'!", s,
                        blocking.getLocation().getPath(), blocking.getName()).throwError();
            }
        }));

    }

    public RootNode importFile(File folder, String file) {
        final File sourceFile = new File(folder, file);

        if (!sourceFile.exists() || !sourceFile.isFile()) {
            new LanguageException("file for import: '%s' not found!", sourceFile.toPath()).throwError();
        }

        try {
            return languageParser.parseFile(sourceFile);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
