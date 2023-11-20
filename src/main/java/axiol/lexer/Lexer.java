package axiol.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final List<TokenRule> tokenRules;
    private final List<Token> tokens;

    private int currentLine;
    private int currentColumn;

    public Lexer() {
        this.tokenRules = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.currentLine = 1;
        this.currentColumn = 1;
    }

    public void addTokenRule(String type, String pattern) {
        tokenRules.add(new TokenRule(type, pattern));
    }

    public List<Token> tokenize(String input) {
        tokens.clear();

        int currentIndex = 0;
        while (currentIndex < input.length()) {
            boolean matched = false;

            for (TokenRule rule : tokenRules) {
                Matcher matcher = rule.pattern.matcher(input.substring(currentIndex));

                if (matcher.find() && matcher.start() == 0) {
                    String value = matcher.group();
                    tokens.add(new Token(rule.type, value, currentLine, currentColumn));
                    updatePosition(value);
                    currentIndex += value.length();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                throw new IllegalStateException("invalid token for '%s'".formatted(input.charAt(currentIndex)));
            }
        }

        return tokens;
    }

    private void updatePosition(String value) {
        for (int i = 0; i < value.length(); i++) {
            char currentChar = value.charAt(i);
            if (currentChar == '\n') {
                currentLine++;
                currentColumn = 1;
            } else {
                currentColumn++;
            }
        }
    }

    private static class TokenRule {
        private final String type;
        private final Pattern pattern;

        public TokenRule(String type, String pattern) {
            this.type = type;
            this.pattern = Pattern.compile("^" + pattern);
        }
    }
}