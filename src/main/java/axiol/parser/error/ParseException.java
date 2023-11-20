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

    public ParseException(String content, String path, Position from, Position to, String message, Object... arguments) {
        this.content = content;
        this.path = path;
        this.from = from;
        this.to = to;
        this.message = message.formatted(arguments);
    }

    public ParseException(String content, String path, Position from, Position to, String message) {
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

    public void throwError() {
        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append("(")
                .append(path != null ? path : "internal")
                .append(") (line: ")
                .append(from.line() + 1)
                .append(", column: ")
                .append(from.column() + 1)
                .append("): ");

        errorMessage = appendHighlighter(errorMessage);

        System.err.println(errorMessage);
    }

    StringBuilder appendHighlighter(StringBuilder stringBuilder) {
        int errorLine = to.line() + 1;
        int errorStart = from.column();
        int errorEnd = to.column();
        int columns = Math.max(1, errorEnd - errorStart);
        int padSize = Math.max(1, (int) Math.floor(Math.log10(errorLine)) + 1);

        String numPadding = " ".repeat(padSize);
        String numFormat = "%" + padSize + "d";
        String errPadding = " ".repeat(errorStart);

        StringBuilder sb = new StringBuilder();

        if (content != null) {
            List<String> lines = content.lines().toList();
            String errString = lines.get(errorLine - 1);

            sb.append('\n').append("%s |".formatted(numPadding)).append('\n');
            sb.append("%s | %s\n".formatted(numFormat.formatted(errorLine), errString));
            sb.append("%s | %s%s\n".formatted(numPadding, errPadding, "^".repeat(columns)));
            sb.append("%s | %s%s".formatted(numPadding, errPadding, message));
            sb.append('\n').append("%s |".formatted(numPadding));
        } else {
            sb.append('\n').append("%s |".formatted(numPadding)).append('\n');
            sb.append("%s | %s".formatted(numFormat.formatted(errorLine), message));
            sb.append('\n').append("%s |".formatted(numPadding));
        }

        return sb;
    }

}
