package axiol.lexer;

import axiol.parser.error.Position;

public class Token {
    private final TokenType type;
    private final String value;
    private final Position position;

    public Token(TokenType type, String value, Position position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "[" + type + " at line " + position.line() + ", column " + position.column() + "]";
    }
}