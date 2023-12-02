package axiol.analyses;

import axiol.parser.util.SourceFile;
import axiol.parser.util.error.LanguageException;
import axiol.parser.util.error.TokenPosition;

public enum ValidationException {

    // finished
    DUPLICATE("duplicated element '%s' with name '%s' found!"),
    UNMATCHED_STATEMENT("statement '%s' for wrong category '%s' found!"),
    DUPLICATED_VAR("variable with name '%s' already defined in in current scope!"),

    // todo expr
    UNDECLARED_VAR("tried to use variable '%s' but couldn't find any declared var!"),
    INCOMPATIBLE_TYPES("incompatible types of '%s' tried to assign with: '%s'"),
    MISSING_RETURN_TYPE("missing return type for function '%s' return type '%s'"),
    INVALID_CAST("can't cast type from '%s' to '%s'!"),

    // todo class
    INVALID_MEMBER_ACCESS("tried to access private member '%s' of '%s'!"),
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
