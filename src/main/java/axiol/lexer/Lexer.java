package axiol.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final List<LexerRule> tokenRules;
    private final List<Token> tokens;

    private int currentLine;
    private int currentColumn;

    public Lexer addRule(TokenType type, Consumer<LexerRule> ruleConsumer) {
        LexerRule rule = new LexerRule(type.ordinal());
        ruleConsumer.accept(rule);

        this.tokenRules.add(rule);
        return this;
    }

    public Lexer() {
        this.tokenRules = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }


    public List<Token> tokenize(String input) {
        return null;
    }

}