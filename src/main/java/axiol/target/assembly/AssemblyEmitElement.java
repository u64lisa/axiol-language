package axiol.target.assembly;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class AssemblyEmitElement {

    public static final AssemblyEmitElement EMPTY = new AssemblyEmitElement();

    public String content = "";

    public AssemblyEmitElement(String content) {
        this.content = content;
    }

    public AssemblyEmitElement() {

    }

    public void add(String raw, Object... args) {
        content = content + raw.formatted(args) + '\n';
    }

    public void add(String raw) {
        content = content + raw + '\n';
    }

    public void lineBeak() {
        content = content + '\n';
    }

    public String export() {
        return Arrays.stream(content.split("\n"))
                .reduce("", (a, b) -> a + '\n' + b).indent(4).stripTrailing();
    }

    public String exportRaw() {
        return content;
    }

    public Stream<String> stream() {
        return Arrays.stream(content.split("\n"));
    }
}
