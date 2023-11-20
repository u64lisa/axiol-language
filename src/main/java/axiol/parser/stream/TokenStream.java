package axiol.parser.stream;

import axiol.lexer.Token;
import axiol.lexer.TokenType;

import java.util.List;

public class TokenStream {

    private final List<Token> tokens;
    private int index;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
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
        return tokens.get(index);
    }

    public Token prev() {
        return tokens.get(index - 1);
    }

    public Token peak(int amount) {
        return tokens.get(index + amount);
    }

    public void advance() {
        this.index++;
    }

    public void reverse() {
        this.index--;
    }

}
