package axiol.analyses;

import axiol.types.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyseContext {

    private final List<String> mangelElements = new ArrayList<>();

    private final Map<String, Reference> functions = new HashMap<>();
    private final Map<String, Reference> classes = new HashMap<>();

    private final Map<String, Reference> structures = new HashMap<>();
    private final Map<String, Reference> structuresFields = new HashMap<>();

    private final Map<String, Reference> variable = new HashMap<>();
    private final Map<String, Reference> udt = new HashMap<>();

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

    public List<String> getMangelElements() {
        return mangelElements;
    }

    public Map<String, Reference> getClasses() {
        return classes;
    }

    public Map<String, Reference> getFunctions() {
        return functions;
    }

    public Map<String, Reference> getStructures() {
        return structures;
    }

    public Map<String, Reference> getStructuresFields() {
        return structuresFields;
    }

    public Map<String, Reference> getUdt() {
        return udt;
    }

    public Map<String, Reference> getVariable() {
        return variable;
    }

}
