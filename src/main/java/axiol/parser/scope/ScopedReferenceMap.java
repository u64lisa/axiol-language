package axiol.parser.scope;

import axiol.parser.util.reference.Reference;

import java.util.LinkedHashMap;

public class ScopedReferenceMap {

    private final LinkedHashMap<String, Reference> referenceMap;

    public ScopedReferenceMap() {
        referenceMap = new LinkedHashMap<>();
    }

    public Reference getReference(String mangled) {
        return referenceMap.get(mangled);
    }

    public boolean hasMangledName(String mangled) {
        return referenceMap.containsKey(mangled);
    }

    public Reference insertNew(String mangled, Reference reference) {
        assert mangled != null && !mangled.isEmpty();
        return referenceMap.put(mangled, reference);
    }

    public void clear() {
        this.referenceMap.clear();
    }

    public boolean isEmpty() {
        return referenceMap.isEmpty();
    }

}
