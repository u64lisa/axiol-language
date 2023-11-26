package axiol.parser.util.stream;

import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.util.SourceFile;

import java.util.List;

public class TokenStream {

    private final SourceFile sourceFile;
    private final List<Token> tokens;
    private int index;

    public TokenStream(SourceFile sourceFile, List<Token> tokens) {
        this.sourceFile = sourceFile;
        this.tokens = tokens;
        this.index = 0;
    }

    public boolean hasMoreTokens() {
        if (tokens.size() <= index)
            return false;

        Token token = tokens.get(index);
        if (token == null)
            return false;

        return token.getType() != TokenType.EOF;
    }

    public boolean matches(TokenType type) {
        Token current = this.current();
        return current != null && current.getType() == type;
    }

    public boolean matchesValue(String value) {
        Token current = this.current();
        return current != null && current.getValue().equals(value);
    }

    public Token current() {
        return hasMoreTokens() ? tokens.get(index) : null;
    }

    public Token prev() {
        return hasMoreTokens() ? tokens.get(index - 1) : null;
    }

    public Token peak(int amount) {
        return hasMoreTokens() ? tokens.get(index + amount) : null;
    }

    public void advance() {
        this.index++;
    }

    public void reverse() {
        this.index--;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }
}
