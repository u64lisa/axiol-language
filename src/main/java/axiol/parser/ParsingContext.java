package axiol.parser;

import java.util.ArrayList;
import java.util.List;

public class ParsingContext {

    private final List<String> structureNames = new ArrayList<>();
    private final List<String> globalVarNames = new ArrayList<>();
    private final List<String> functionNames =  new ArrayList<>();
    private final List<String> classNames =     new ArrayList<>();
    private final List<String> importedNames =  new ArrayList<>();

    public List<String> getStructureNames() {
        return structureNames;
    }

    public List<String> getGlobalVarNames() {
        return globalVarNames;
    }

    public List<String> getFunctionNames() {
        return functionNames;
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public List<String> getImportedNames() {
        return importedNames;
    }
}
