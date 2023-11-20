package axiol.lexer;

public class Token {
    private final int type;
    private final String value;
    private final int line;
    private final int column;

    public Token(int type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "[" + type + ": " + value + " at line " + line + ", column " + column + "]";
    }
}