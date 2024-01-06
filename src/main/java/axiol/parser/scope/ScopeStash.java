package axiol.parser.scope;

import axiol.parser.scope.impl.FunctionScope;
import axiol.parser.scope.impl.LocalScope;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.types.Type;

import java.util.*;

public class ScopeStash {
	private final FunctionScope functionScope;
	private final LocalScope localScope;

	protected final Map<Reference, TokenPosition> firstReferencePosition;
	protected final Map<String, Reference> importedReference;
	protected final List<Reference> allReferences;

	private final Namespace namespaceRoot;
	private final LinkedList<Reference> scopes;
	private final ScopedReferenceMap referenceMap;

	public int count;
	protected int tempCount;
	
	public ScopeStash() {
		this.firstReferencePosition = new HashMap<>();
		this.importedReference = new HashMap<>();
		this.allReferences = new ArrayList<>();

		this.scopes = new LinkedList<>();
		this.referenceMap = new ScopedReferenceMap();
		this.namespaceRoot = new Namespace();

		Reference rootReference = createNamespaceReference(namespaceRoot);
		this.referenceMap.insertNew("", rootReference);
		this.scopes.add(rootReference);

		this.functionScope = new FunctionScope(this);
		this.localScope = new LocalScope(this);

		this.functionScope.pushBlock();
		this.localScope.pushBlock();
	}
	
	public void clear() {
		functionScope.clear();
		localScope.clear();
		scopes.clear();
		referenceMap.clear();
	}
	
	public FunctionScope getFunctionScope() {
		return functionScope;
	}
	
	public LocalScope getLocalScope() {
		return localScope;
	}

	public List<Reference> getAllReferences() {
		return allReferences;
	}
	
	public Map<String, Reference> getImportedReferences() {
		return importedReference;
	}

	public TokenPosition getFirstReferencePosition(Reference reference) {
		return firstReferencePosition.get(reference);
	}
	
	public void setReferencePosition(Reference reference, TokenPosition syntaxPosition) {
		if (firstReferencePosition.putIfAbsent(reference, syntaxPosition) != null) {
			throw new RuntimeException("Reference was added multiple times");
		}
	}

	public void pushNamespace(String name) {
		Namespace parent = scopes.getLast().getLocation();
		Namespace child = new Namespace(parent, name);

		Reference ref;
		if (referenceMap.hasMangledName(child.getPath())) {
			ref = referenceMap.getReference(child.getPath());
		} else {
			ref = createNamespaceReference(child);
			referenceMap.insertNew(child.getPath(), ref);
		}

		scopes.add(ref);
	}

	public void popNamespace() {
		scopes.pollLast();
	}

	public Namespace getNamespaceRoot() {
		return namespaceRoot;
	}

	public Namespace getNamespace() {
		return getNamespaceReference().getLocation();
	}

	public Reference getNamespaceReference() {
		return scopes.getLast();
	}

	public Namespace getRelativeNamespace(Namespace base, Namespace path) {
		Reference reference;
		if (path.isRoot()) {
			reference = referenceMap.getReference(base.getPath());
		} else {
			// Path is not root
			if (base.isRoot()) {
				reference = referenceMap.getReference(path.getPath());
			} else {
				reference = referenceMap.getReference(base.getPath() + "::" + path.getPath());
			}
		}

		if (reference != null) {
			return reference.getLocation();
		}

		return null;
	}

	public Namespace importNamespace(List<String> parts) {
		Namespace namespace = new Namespace(this.namespaceRoot, String.join("::", parts));
		Reference reference = createNamespaceReference(namespace);
		referenceMap.insertNew(namespace.getPath(), reference);
		return namespace;
	}

	public Namespace resolveNamespace(List<String> parts) {
		if (parts.isEmpty()) {
			return getNamespaceRoot();
		}

		Reference ref = referenceMap.getReference(String.join("::", parts));

		if (ref != null) {
			return ref.getLocation();
		}

		return null;
	}

	public Reference createImportedReference(String name) {
		Reference reference = importedReference.get(name);
		if (reference != null) {
			return reference;
		}
		
		reference = new Reference(ReferenceType.EMPTY, name, getNamespace(), Type.NONE);
		reference.setImported(true);
		reference.setIdentId(count++);
		importedReference.put(name, reference);
		allReferences.add(reference);
		return reference;
	}
	
	public Reference createEmptyReference(String name) {
		Reference reference = new Reference(ReferenceType.EMPTY, name, getNamespace(), Type.NONE);
		allReferences.add(reference);
		return reference;
	}
	
	protected Reference createNamespaceReference(Namespace namespace) {
		Reference reference = new Reference(ReferenceType.NAMESPACE, namespace.getPath(), namespace, Type.NONE);
		reference.setIdentId(-1 - (tempCount++));
		allReferences.add(reference);
		return reference;
	}
}
