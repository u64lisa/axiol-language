package axiol.parser.util.error;

public class TokenPosition {

    private final Position start, end;

    public TokenPosition(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    public Position getEnd() {
        return end;
    }

    public Position getStart() {
        return start;
    }
}
