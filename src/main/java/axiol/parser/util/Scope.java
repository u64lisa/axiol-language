package axiol.parser.util;

public class Scope {

    private final StringBuilder scopePath;

    public Scope() {
        this.scopePath = new StringBuilder();
    }

    public Scope(String value) {
        this.scopePath = new StringBuilder(value);
    }

    public void appendScope(String next) {
        this.scopePath.append("::").append(next);
    }

    public String dumpPath() {
        String path = scopePath.toString();
        if (path.startsWith("::")) {
            return path.substring(2);
        }
        return path;
    }

    @SuppressWarnings("all")
    public Scope clone() {
        return new Scope(dumpPath());
    }
}
