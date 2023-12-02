package axiol.analyses;

import axiol.parser.util.SourceFile;
import axiol.parser.util.error.LanguageException;
import axiol.parser.util.error.TokenPosition;

public enum ValidationException {

    DUPLICATE("duplicated element '%s' with name '%s' found!"),
    UNMATCHED_STATEMENT("statement '%s' for wrong category '%s' found!"),
    DUPLICATED_VAR("variable with name '%s' already defined in in current scope!")
    ;

    private final String format;

    ValidationException(String format) {
        this.format = format;
    }

    public void throwException(SourceFile sourceFile, TokenPosition position, Object... arguments) {
        LanguageException languageException = new LanguageException(sourceFile.getContent(), position,
                sourceFile.getFilePath(), this.format, arguments);

        languageException.throwError();
    }
}
