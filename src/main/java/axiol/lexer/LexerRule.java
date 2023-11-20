package axiol.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LexerRule {
    private static final String ESCAPED_PATTERN = "%s.*?%s";
    private static final String UNESCAPED_PATTERN = "%s(?:%s(?:%s|%s|(?!%s).)|(?!%s|%s).)*%s";

    private final int head;
    private final List<Pattern> patterns;

    public LexerRule(int head) {
        this.head = head;

        this.patterns = new ArrayList<>();
    }

    public void addString(String... values) {
        for (String value : values) {
            this.patterns.add(Pattern.compile(this.regexEscape(value)));
        }

    }
    public void addRegexes(String... regexes) {
        for (String regex : regexes) {
            this.patterns.add(Pattern.compile(regex));
        }

    }

    public void addMultiline(String open, String escape, String close) {
        addDelimiter(open, escape, close, Pattern.DOTALL);
    }
    public void addMultiline(String open, String close) {
        this.addMultiline(open, "", close);
    }

    private void addDelimiter(String open, String escape, String close, int flags) {
        String startingSymbol = regexEscape(open);
        String closingSymbol = regexEscape(close);
        String escapeRegex = regexEscape(escape);

        String regex = escape.isEmpty() ? ESCAPED_PATTERN.formatted(
                startingSymbol, closingSymbol
        ) :
                UNESCAPED_PATTERN.formatted(
                        startingSymbol, escapeRegex, escapeRegex, closingSymbol,
                        closingSymbol, escapeRegex, closingSymbol, closingSymbol
                );

        this.patterns.add(Pattern.compile(regex, flags));
    }

    String regexEscape(String string) {
        if (string == null) return null;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            switch (c) {
                case '\0', '\n', '\r', '\t', '\\', '^', '$', '?', '|', '*', '/', '+', '.', '(', ')', '[', ']', '{', '}':
                    sb.append('\\').append(c);
                    continue;
            }

            if (c > 0xff) sb.append("\\u").append(String.format("%0" + 4 + "x", c));
            else if (Character.isISOControl(c)) sb.append("\\x").append(String.format("%0" + 2 + "x", c));
            else sb.append(c);
        }

        return sb.toString();
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public int getHead() {
        return head;
    }


}
