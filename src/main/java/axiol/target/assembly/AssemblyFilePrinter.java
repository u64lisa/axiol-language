package axiol.target.assembly;

public abstract class AssemblyFilePrinter {
    public static final String NEXT_LINE = "\n";
    public static final String TAB = "    ";

    public abstract void init(int bitSize);
    public abstract void createEntryPoint(String main);
    public abstract void createCodeSection(String code);
    public abstract void createDataSection(String data);

    public abstract String print();
}
