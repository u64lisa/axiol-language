package axiol.analyses;

import java.util.ArrayList;
import java.util.List;

public class AnalyseContext {

    private final List<String> mangelElements = new ArrayList<>();

    private final List<String> functions = new ArrayList<>();
    private final List<String> classes = new ArrayList<>();

    private final List<String> structures = new ArrayList<>();
    private final List<String> structuresFields = new ArrayList<>();

    private final List<String> variable = new ArrayList<>();
    private final List<String> udt = new ArrayList<>();

    public boolean checkMangel(String mangel) {
        if (this.mangelElements.contains(mangel)) {
            return false;
        }
        this.mangelElements.add(mangel);
        return true;
    }

    @Override
    public String toString() {
        return "AnalyseContext{" +
                "mangelElements=" + mangelElements +
                ", functions=" + functions +
                ", classes=" + classes +
                ", structures=" + structures +
                ", structuresFields=" + structuresFields +
                ", variable=" + variable +
                '}';
    }

    public List<String> getClasses() {
        return classes;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public List<String> getVariable() {
        return variable;
    }

    public List<String> getStructures() {
        return structures;
    }

    public List<String> getStructuresFields() {
        return structuresFields;
    }

    public List<String> getUdt() {
        return udt;
    }
}
