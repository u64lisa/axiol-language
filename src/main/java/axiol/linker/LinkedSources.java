package axiol.linker;

import axiol.parser.scope.ScopeStash;
import axiol.parser.tree.Statement;
import axiol.parser.util.SourceFile;

import java.util.List;

public class LinkedSources {

    private final SourceFile mainFile;
    private final List<Statement> statements;
    private final ScopeStash scopeStash;

    public LinkedSources(SourceFile mainFile, List<Statement> statements, ScopeStash scopeStash) {
        this.mainFile = mainFile;
        this.statements = statements;
        this.scopeStash = scopeStash;
    }

    public ScopeStash getScopeStash() {
        return scopeStash;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public SourceFile getMainFile() {
        return mainFile;
    }
}
