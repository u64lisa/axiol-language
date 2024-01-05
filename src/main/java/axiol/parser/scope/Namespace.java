package axiol.parser.scope;

public record Namespace(Namespace parent, String... path) {
    public String getName() {
        StringBuilder namespacePath = new StringBuilder();
        for (String element : path) {
            namespacePath.append(element).append("::");
        }
        return namespacePath.toString();
    }
}