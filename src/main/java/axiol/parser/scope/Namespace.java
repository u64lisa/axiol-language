package axiol.parser.scope;

public class Namespace {

    private final String[] parts;

    public Namespace() {
        // Empty namespace
        this.parts = new String[0];
    }

    public Namespace(String pathname) {
        if (pathname.isEmpty()) {
            this.parts = new String[0];
        } else {
            this.parts = pathname.split("::");
        }
    }

    public Namespace(Namespace parent, String name) {
        this.parts = new String[parent.parts.length + 1];

        System.arraycopy(parent.parts, 0, parts, 0, parent.parts.length);
        this.parts[parent.parts.length] = name;
    }

    public boolean isRoot() {
        return parts.length == 0;
    }

    public String getPath() {
        return String.join("::", parts);
    }

    @Override
    public String toString() {
        if (parts.length == 0)
            return "{ count: 0, parts: [] }";

        return "{ count: " + parts.length + ", parts: [\"" + String.join("\", \"", parts) + "\"] }";
    }
}