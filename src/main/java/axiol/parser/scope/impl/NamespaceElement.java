package axiol.parser.scope.impl;

import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.parser.scope.Namespace;
import axiol.parser.scope.ScopeElement;
import axiol.types.Type;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class NamespaceElement implements ScopeElement {
    private final Namespace treeRoot;
    private final Reference rootReference;

    private final Map<String, Reference> namespaces;
    private final LinkedList<Reference> scopeTree;

    public NamespaceElement(final Namespace nameSpace) {
        this.rootReference = createNamespaceReference(nameSpace);

        this.treeRoot = nameSpace;
        this.scopeTree = new LinkedList<>();

        this.namespaces = new HashMap<>();
        this.namespaces.put("", rootReference);
        this.scopeTree.add(rootReference);
    }

    public void push(String name) {
        Namespace child = new Namespace(scopeTree.getLast().getLocation(), name);
        Reference reference = namespaces.getOrDefault(child.getName(), createNamespaceReference(child));
        if (!namespaces.containsKey(child.getName()))
            this.namespaces.put(child.getName(), reference);

        this.scopeTree.add(reference);
    }

    @Override
    public void pop() {
        this.scopeTree.pollLast();
    }

    @Override
    public void clear() {
        scopeTree.clear();
        namespaces.clear();
    }

    public Reference createNamespaceReference(Namespace name) {
        return new Reference(ReferenceType.NAMESPACE, name.getName(), name);
    }

    public Namespace getRelative(Namespace start, Namespace path) {
        Reference reference = path.path().length == 0 ? namespaces.get(start.getName()) : start.path().length == 0 ?
                namespaces.get(path.getName()) : namespaces.get("%s::%s".formatted(start.getName(), path.getName()));
        return reference == null ? null : reference.getLocation();
    }

    public Namespace createNamespace(String... path) {
        Namespace namespace = new Namespace(this.treeRoot, path);
        namespaces.put(namespace.getName(), this.createNamespaceReference(namespace));
        return namespace;
    }

    public Namespace byPath(String... path) {
        Namespace buffer = new Namespace(null, path);
        Reference reference = this.namespaces.get(buffer.getName());
        return reference == null && path.length == 0 ? treeRoot :
                reference != null  ? reference.getLocation() : null;
    }

    public LinkedList<Reference> getScopeTree() {
        return scopeTree;
    }

    public Map<String, Reference> getNamespaces() {
        return namespaces;
    }

    public Namespace getTreeRoot() {
        return treeRoot;
    }

    public Reference getRootReference() {
        return rootReference;
    }
}
