package axiol.linker;

import axiol.parser.LanguageParser;
import axiol.parser.scope.ScopeStash;
import axiol.parser.tree.RootNode;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.LinkedNoticeStatement;
import axiol.parser.util.SourceFile;
import axiol.parser.util.error.LanguageException;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public LinkedSources linkFiles(final RootNode rootNode) {
        this.importedFiles.clear();
        this.scopeStash = rootNode.getScopeStash();
        this.mainFile = rootNode.getSourceFile();

        this.resolveImports(rootNode);
        this.checkFileCompatability();

        List<Statement> statements = rootNode.getStatements();
        this.importedFiles.forEach((s, current) -> statements.addAll(current.getStatements()));

        return new LinkedSources(mainFile, statements, scopeStash);
    }

    public void resolveImports(RootNode rootNode) {
        List<String> paths = rootNode.getStatements()
                .stream()
                .filter(statement -> statement instanceof LinkedNoticeStatement)
                .map(statement -> (LinkedNoticeStatement) statement)
                .map(LinkedNoticeStatement::getLinkedName)
                .toList();

        for (String path : paths) {
            path = sourceFolder.getAbsolutePath() + "\\" + path.replace('.', '\\') + SUFFIX;

            if (!importedFiles.containsKey(path) && !path.equals(mainFile.asFile().getAbsolutePath())) {
                RootNode current = importFile(path);
                importedFiles.put(path, current);

                current.getScopeStash().getAllReferences().forEach(reference -> {
                    switch (reference.getType()) {
                        case NAMESPACE -> this.scopeStash
                                .importNamespace(reference.getLocation());
                        case VAR -> this.scopeStash.getLocalScope()
                                .addLocalVariable(reference.getLocation(), reference.getValueType(), reference.isConstant(), reference.getName());
                        case FUNCTION -> this.scopeStash.getFunctionScope()
                                .importFunction(reference);
                    }
                });

                this.resolveImports(current);
            }
        }
    }

    public void checkFileCompatability() {
        this.importedFiles.forEach((importedFilePath, importedRootNode) -> {
            importedRootNode.getScopeStash().getAllReferences().forEach(importedReference -> {
                Reference blockingReference = this.scopeStash.containsReference(importedReference);

                if (blockingReference != null) {
                    // todo implement global access modifier for global defined constants!
                    if (blockingReference.getType() == ReferenceType.VAR) {
                        return;
                    }

                    new LanguageException("File '%s' contains duplicated %s in namespace '%s' with name '%s'!",
                            importedFilePath, importedReference.getType(), importedReference.getLocation().getPath(),
                            importedReference.getName()).throwError();
                }

                this.importedFiles.forEach((otherFilePath, otherRootNode) -> {
                    if (!otherFilePath.equals(importedFilePath)) {
                        Reference conflictingReference = otherRootNode.getScopeStash().containsReference(importedReference);

                        if (conflictingReference != null) {
                            new LanguageException("File '%s' conflicts with file '%s'. Both contain %s in namespace '%s' with name '%s'!",
                                    importedFilePath, otherFilePath, importedReference.getType(),
                                    importedReference.getLocation().getPath(), importedReference.getName()).throwError();
                        }
                    }
                });
            });
        });
    }

    public RootNode importFile(String file) {
        final File sourceFile = new File(file);

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
