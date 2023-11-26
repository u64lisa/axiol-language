package axiol.parser.util.error;

import axiol.lexer.Token;

import java.util.List;

public class LanguageException extends IllegalStateException {

    // file
    private final String content;
    private final String path;

    // error itself
    private final TokenPosition position;
    private final String message;

    public LanguageException(String content, TokenPosition position, String path, String message, Object... arguments) {
        this.content = content;
        this.path = path;
        this.position = position;
        this.message = message.formatted(arguments);
    }

    public LanguageException(String content, Position from, Position to, String path, String message) {
        this.content = content;
        this.path = path;
        this.position = new TokenPosition(from, to);
        this.message = message;
    }

    public LanguageException(String content, Token token, String path, String message) {
        this.content = content;
        this.path = path;
        this.position = token.getTokenPosition();
        this.message = message;
    }

    public LanguageException(String content, Token token, String path, String message, Object... arguments) {
        this.content = content;
        this.path = path;
        this.position = token.getTokenPosition();
        this.message = message.formatted(arguments);
    }

    public void throwError() {
        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append("(")
                .append(path != null ? path : "internal")
                .append(") (line: ")
                .append(position.getStart().line() + 1)
                .append(", column: ")
                .append(position.getStart().column() + 1)
                .append("): ");

        appendHighlighter(errorMessage);

        System.err.println(errorMessage);

        throw this;
    }

    void appendHighlighter(StringBuilder stringBuilder) {
        int errorLine = position.getEnd().line();
        int errorStart = position.getStart().column() - 1;
        int errorEnd = position.getEnd().column() - 1;
        int columns = Math.max(1, errorEnd - errorStart);
        int padSize = Math.max(1, (int) Math.floor(Math.log10(errorLine)) + 1);

        String numPadding = " ".repeat(padSize);
        String numFormat = "%" + padSize + "d";
        String errPadding = " ".repeat(errorStart);

        if (content != null) {
            List<String> lines = content.lines().toList();
            String errString = lines.get(errorLine - 1);

            stringBuilder.append('\n').append("%s |".formatted(numPadding)).append('\n');
            stringBuilder.append("%s | %s\n".formatted(numFormat.formatted(errorLine), errString));
            stringBuilder.append("%s | %s%s\n".formatted(numPadding, errPadding, "^".repeat(columns)));
            stringBuilder.append("%s | %s%s".formatted(numPadding, errPadding, message));
            stringBuilder.append('\n').append("%s |".formatted(numPadding));
        } else {
            stringBuilder.append('\n').append("%s |".formatted(numPadding)).append('\n');
            stringBuilder.append("%s | %s".formatted(numFormat.formatted(errorLine), message));
            stringBuilder.append('\n').append("%s |".formatted(numPadding));
        }
    }

}
