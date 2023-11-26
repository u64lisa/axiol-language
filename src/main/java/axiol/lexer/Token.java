package axiol.lexer;

import axiol.parser.util.error.Position;
import axiol.parser.util.error.TokenPosition;

public class Token {
    private final TokenType type;
    private final String value;

    private final TokenPosition tokenPosition;

    public Token(TokenType type, String value, Position position) {
        this.type = type;
        this.value = value;
        this.tokenPosition = new TokenPosition(position,
                new Position(position.line(), position.column() + value.length()));
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public TokenPosition getTokenPosition() {
        return tokenPosition;
    }

    @Override
    public String toString() {
        return "[" + type + " at line " + tokenPosition.getStart().line() + ", column " + tokenPosition.getStart().column() + "]";
    }
}