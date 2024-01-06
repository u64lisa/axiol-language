package axiol.parser.scope.impl;

import axiol.parser.scope.*;
import axiol.parser.statement.Parameter;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.types.Type;
import axiol.utils.SuppliedStack;

import java.util.List;

public class FunctionScope extends ScopeReferenceStorage {

    private final ScopeStash scopeStash;
    private final SuppliedStack<FunctionScopeLayer> functionScope;

    public FunctionScope(ScopeStash scopeStash) {
        this.scopeStash = scopeStash;
        this.functionScope = new SuppliedStack<>(FunctionScopeLayer::new);
    }

    @Override
    public void clear() {
        functionScope.clear();
    }

    @Override
    public void pushBlock() {
        functionScope.push();
    }

    @Override
    public void popBlock() {
        functionScope.pop();
    }

    public Reference addFunction(Type type, Namespace namespace, String name, List<Parameter> parameters) {
        return functionScope.getLast().addFunction(type, namespace, name, parameters.stream().map(Parameter::getReference).toList());
    }

    public Reference addFunctionReferenceParams(Type type, Namespace namespace, String name, List<Reference> parameters) {
        return functionScope.getLast().addFunction(type, namespace, name, parameters);
    }

    public Reference getFunctionBlocking(Namespace namespace, String name, Type returnType, List<Parameter> parameters) {
        return functionScope.getLast().getFunctionBlocking(namespace, returnType, name, parameters.stream().map(Parameter::getReference).toList());
    }

    public Reference importFunction(Namespace namespace, String name, List<Reference> parameters) {
        Reference reference = addFunctionReferenceParams(Type.MERGED, namespace, name, parameters);
        reference.setImported(true);
        reference.setIdent(Mangler.mangleFunction(Type.MERGED, namespace, name, parameters));
        return reference;
    }

    public Reference getGlobalFunction(Namespace namespace, String name, List<Reference> parameters) {
        return functionScope.getElements().getFirst().getFunction(namespace, name, parameters);
    }

    public Reference getLocalFunction(Namespace namespace, String name, List<Reference> parameters) {
        return functionScope.getLast().getFunction(namespace, name, parameters);
    }

    public Reference getFunction(Namespace namespace, String name, List<Reference> parameters) {
        Reference reference;
        if ((reference = getLocalFunction(namespace, name, parameters)) != null) {
            return reference;
        }

        Namespace relativeNamespace = scopeStash.getRelativeNamespace(scopeStash.getNamespace(), namespace);
        if (relativeNamespace != null) {
            if ((reference = getLocalFunction(relativeNamespace, name, parameters)) != null) {
                return reference;
            }

            if ((reference = getGlobalFunction(relativeNamespace, name, parameters)) != null) {
                return reference;
            }
        }

        return getGlobalFunction(namespace, name, parameters);
    }

    public class FunctionScopeLayer extends ScopedReferenceMap {

        public Reference addFunction(Type returnType, Namespace namespace, String name, List<Reference> parameters) {
            String mangledName = Mangler.mangleFunction(returnType, namespace, name, parameters);

            if (this.hasMangledName(mangledName)) {
                return null;
            }

            Reference reference = new Reference(ReferenceType.FUNCTION, name, namespace, returnType);
            reference.setIdent(mangledName);
            reference.setIdentId(scopeStash.count++);

            FunctionScopeLayer global = functionScope.getElements().getFirst();
            if (global != this && global.hasMangledName(mangledName)) {
                throw new RuntimeException("Function override");
            }

            this.insertNew(mangledName, reference);
            scopeStash.getAllReferences().add(reference);
            return reference;
        }

        public Reference getFunctionBlocking(Namespace namespace, Type type, String name, List<Reference> parameters) {
            String mangledName = Mangler.mangleFunction(type, namespace, name, parameters);
            return this.getReference(mangledName);
        }

        public Reference getFunction(Namespace namespace, String name, List<Reference> parameters) {
            String mangledName = Mangler.mangleFunction(Type.MERGED, namespace, name, parameters);
            return this.getReference(mangledName);
        }

    }
}
