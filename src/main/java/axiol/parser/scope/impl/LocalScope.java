package axiol.parser.scope.impl;

import axiol.parser.scope.*;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.types.Type;
import axiol.utils.SuppliedStack;

import java.util.Iterator;
import java.util.LinkedList;

public class LocalScope extends ScopeReferenceStorage {
    private final ScopeStash scopeStash;
    private final LinkedList<SuppliedStack<LocalScopeLayer>> localScope;

    public LocalScope(ScopeStash scopeStash) {
        this.scopeStash = scopeStash;
        this.localScope = new LinkedList<>();
    }

    @Override
    public void clear() {
        localScope.clear();
    }

    @Override
    public void pushBlock() {
        localScope.addLast(new SuppliedStack<>(LocalScopeLayer::new));
    }

    @Override
    public void popBlock() {
        localScope.removeLast();
    }

    public void pushLocals() {
        localScope.getLast().push();
    }

    public void popLocals() {
        localScope.getLast().pop();
    }

    public Reference importVariable(Namespace namespace, String name) {
        Reference reference = addLocalVariable(namespace, Type.NONE, false, name);
        reference.setImported(true);
        reference.setIdent(Mangler.mangleVariable(namespace, name));
        return reference;
    }

    public Reference addLocalVariable(Namespace namespace, Type valueType, boolean constant, String name) {
        return localScope.getLast().getLast().addLocal(valueType, namespace, constant, name);
    }

    public Reference getVariable(Namespace namespace, String name) {
        Iterator<SuppliedStack<LocalScopeLayer>> iter = localScope.descendingIterator();

        while (iter.hasNext()) {
            Iterator<LocalScopeLayer> iter2 = iter.next().getElements().descendingIterator();
            while (iter2.hasNext()) {
                Reference reference = iter2.next().getLocal(namespace, name);
                if (reference != null) {
                    return reference;
                }
            }
        }

        return null;
    }

    public Reference getLocal(Namespace namespace, String name) {
        Iterator<LocalScopeLayer> iter = localScope.getLast().getElements().descendingIterator();
        while (iter.hasNext()) {
            Reference reference = iter.next().getLocal(namespace, name);
            if (reference != null) {
                return reference;
            }
        }

        return null;
    }

    public class LocalScopeLayer extends ScopedReferenceMap {

        public Reference addLocal(Type valueType, Namespace namespace, boolean constant, String name) {
            String mangledName = Mangler.mangleVariable(namespace, name);
            if (this.hasMangledName(mangledName)) {
                return null;
            }

            Reference reference = new Reference(ReferenceType.VAR, name, namespace, valueType);
            reference.setConstant(constant);
            reference.setIdentId(scopeStash.count++);
            reference.setIdent(mangledName);
            insertNew(mangledName, reference);
            scopeStash.getAllReferences().add(reference);
            return reference;
        }

        public Reference getLocal(Namespace namespace, String name) {
            String mangledName = Mangler.mangleVariable(namespace, name);
            Reference reference = this.getReference(mangledName);
            if (reference != null && reference.getLocation() != namespace) {
                throw new RuntimeException("scope miss matched: (%s) (%s)".formatted(reference, namespace));
            }
            return reference;
        }

    }
}
