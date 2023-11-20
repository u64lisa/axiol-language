package axiol.lexer;

import axiol.parser.error.Position;

public class Token {
    private final TokenType type;
    private final String value;
    private final Position position;
    private final Position end;

    public Token(TokenType type, String value, Position position) {
        this.type = type;
        this.value = value;
        this.position = position;
        this.end = new Position(position.line(), position.column() + value.length());
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

    public Position getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "[" + type + " at line " + position.line() + ", column " + position.column() + "]";
    }
}