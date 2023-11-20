package axiol.lexer;

public class UnknownTokenException extends IllegalArgumentException {
    public UnknownTokenException(String s) {
        super(s);
    }
}
