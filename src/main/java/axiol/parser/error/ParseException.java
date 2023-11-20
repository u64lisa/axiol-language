package axiol.parser.error;

import axiol.lexer.Token;

import java.util.List;
import java.util.Objects;

public class ParseException extends IllegalStateException {

    // file
    private final String content;
    private final String path;

    // error itself
    private final Position from, to;
    private final String message;

    public ParseException(String content, Position from, Position to, String path,  String message, Object... arguments) {
        this.content = content;
        this.path = path;
        this.from = from;
        this.to = to;
        this.message = message.formatted(arguments);
    }

    public ParseException(String content, Position from, Position to, String path, String message) {
        this.content = content;
        this.path = path;
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public ParseException(String content, Token token, String path, String message) {
        this.content = content;
        this.path = path;
        this.from = token.getPosition();
        this.to = token.getEnd();
        this.message = message;
    }

    public ParseException(String content, Token token, String path, String message, Object... arguments) {
        this.content = content;
        this.path = path;
        this.from = token.getPosition();
        this.to = token.getEnd();
        this.message = message;
    }

    public void throwError() {
        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append("(")
                .append(path != null ? path : "internal")
                .append(") (line: ")
                .append(from.line() + 1)
                .append(", column: ")
                .append(from.column() + 1)
                .append("): ");

        appendHighlighter(errorMessage);

        System.err.println(errorMessage);

        throw this;
    }

    void appendHighlighter(StringBuilder stringBuilder) {
        int errorLine = to.line();
        int errorStart = from.column() - 1;
        int errorEnd = to.column() - 1;
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
