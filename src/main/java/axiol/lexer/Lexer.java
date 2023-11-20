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
        LexerRule rule = tokenRules.stream()
                .filter(lexerRule -> lexerRule.getHead() == type.ordinal()).findFirst().orElse(new LexerRule(type.ordinal()));
        ruleConsumer.accept(rule);

        this.tokenRules.add(rule);
        return this;
    }

    public Lexer() {
        this.tokenRules = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }


    public List<Token> tokenize(String input, boolean skipWhiteSpace) {
        while (!input.isEmpty()) {
            int matchedLength = 0;
            TokenType matchedType = null;

            for (LexerRule rule : tokenRules) {
                List<Pattern> patterns = rule.getPatterns();
                for (Pattern pattern : patterns) {
                    Matcher matcher = pattern.matcher(input);
                    if (matcher.lookingAt() && matcher.start() == 0) {
                        int length = matcher.end();
                        if (length > matchedLength) {
                            matchedLength = length;
                            matchedType = TokenType.values()[rule.getHead()];
                        }
                    }
                }
            }

            if (matchedLength > 0) {
                String substring = input.substring(0, matchedLength);
                Token token = new Token(matchedType, substring, currentLine, currentColumn);
                tokens.add(token);
                for (char c : substring.toCharArray()) {
                    if (c == '\n') {
                        currentLine++;
                        currentColumn = 1;
                    } else {
                        currentColumn++;
                    }
                }
                input = input.substring(matchedLength);
            } else {
                throw new UnknownTokenException("Unknown token encountered at line %s, column %s, token: %s"
                        .formatted(currentLine, currentColumn, input.charAt(0)));
            }
        }

        tokens.removeIf(token -> skipWhiteSpace && token.getType().equals(TokenType.WHITESPACE));
        return tokens;
    }

}