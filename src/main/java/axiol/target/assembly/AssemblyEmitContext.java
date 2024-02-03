package axiol.target.assembly;

public class AssemblyEmitContext {

    private String source = "";
    private String documentation = "";

    public void append(AssemblyEmitElement element) {
        this.source += element.export();
    }
    public void append(String  element) {
        this.source += element;
    }

    public void lineBreak() {
        this.source += "\n";
    }

    public void appendDocumentation(String documentation) {
        this.documentation += documentation;
    }

    public String build() {
        return source;
    }


}
